package com.rabbit.promissorynote.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.rabbit.blockchain.service.PromissoryNoteService;
import com.rabbit.blockchain.wrapper.PromissoryNote;
import com.rabbit.blockchain.wrapper.RepaymentScheduler;
import com.rabbit.promissorynote.domain.dto.UserContractInfoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.rabbit.promissorynote.domain.entity.PromissoryNoteEntity;
import com.rabbit.blockchain.domain.entity.RepaymentSchedule;
import com.rabbit.promissorynote.repository.PromissoryNoteRepository;
import com.rabbit.blockchain.repository.RepaymentScheduleRepository;
import com.rabbit.contract.domain.entity.Contract;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.user.domain.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 블록체인 상호작용 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PromissoryNoteBusinessService {

    private final PromissoryNote promissoryNote;
    private final RepaymentScheduler repaymentScheduler;
    private final PromissoryNoteService promissoryNoteService;
    private final PromissoryNoteRepository promissoryNoteRepository;
    private final RepaymentScheduleRepository repaymentScheduleRepository;

    // 블록체인 작업 타임아웃 설정
    @Value("${blockchain.transaction.timeout:60}")
    private int blockchainTransactionTimeoutSeconds;

    /**
     * 차용증 NFT 발행 - 트랜잭션 밖에서 호출되는 메소드
     *
     * @param contract 계약 정보
     * @param nftPdFUri NFT PDF URI
     * @param nftImageUri NFT 이미지 URI
     * @return 생성된 토큰 ID
     */
    public BigInteger mintPromissoryNoteNFT(Contract contract, String nftPdFUri, String nftImageUri, UserContractInfoDto userInfo) {
        log.info("[블록체인][시작] 차용증 NFT 발행 시작 - 계약 ID: {}", contract.getContractId());

        // 1. 메타데이터 생성
        PromissoryNote.PromissoryMetadata metadata = createPromissoryMetadata(contract, userInfo, nftPdFUri, nftImageUri);

        // 2. 비동기 작업으로 NFT 민팅 실행 및 타임아웃 설정
        CompletableFuture<BigInteger> mintFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return promissoryNoteService.mintPromissoryNote(metadata, userInfo.getCreditorWalletAddress());
            } catch (Exception e) {
                log.error("[블록체인][민팅 예외] NFT 민팅 중 오류: {}", e.getMessage(), e);
                throw new CompletionException(e);
            }
        });

        // 3. 설정된 타임아웃으로 결과 대기
        try {
            BigInteger tokenId = mintFuture.get(blockchainTransactionTimeoutSeconds, TimeUnit.SECONDS);
            log.info("[블록체인][민팅 성공] 토큰 ID: {}, 계약 ID: {}", tokenId, contract.getContractId());

            // 4. DB에 저장 및 토큰 ID 반환
            savePromissoryNoteToDatabaseInNewTransaction(tokenId, contract, userInfo, nftPdFUri, nftImageUri);
            return tokenId;

        } catch (TimeoutException e) {
            // 타임아웃 발생 시 실패로 처리
            log.error("[블록체인][타임아웃] NFT 민팅 타임아웃 - 계약 ID: {}", contract.getContractId());
            throw new BusinessException(ErrorCode.BLOCKCHAIN_TIMEOUT, "블록체인 트랜잭션이 타임아웃되었습니다. 나중에 다시 시도해주세요.");
        } catch (InterruptedException | ExecutionException e) {
            // 다른 예외는 일반적인 블록체인 에러로 변환
            log.error("[블록체인][예외] NFT 민팅 실행 오류: {}", e.getCause() != null ? e.getCause().getMessage() : e.getMessage(), e);
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR,
                    "NFT 발행 중 오류가 발생했습니다: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
        }
    }

    /**
     * 새 트랜잭션에서 DB에 차용증 NFT 정보 저장
     */
    @Transactional
    public void savePromissoryNoteToDatabaseInNewTransaction(BigInteger tokenId, Contract contract,
                                                             UserContractInfoDto userInfo, String nftPdFUri, String nftImageUri) {
        savePromissoryNoteToDatabase(tokenId, contract, userInfo, nftPdFUri, nftImageUri);
    }

    /**
     * 데이터베이스에 차용증 NFT 정보 저장
     *
     * @param tokenId 토큰 ID
     * @param contract 계약 정보
     * @param userInfo 사용자 계약 정보
     * @param nftPdFUri NFT PDF URI
     * @param nftImageUri NFT 이미지 URI
     */
    private void savePromissoryNoteToDatabase(
            BigInteger tokenId,
            Contract contract,
            UserContractInfoDto userInfo,
            String nftPdFUri,
            String nftImageUri) {

        // 채권자/채무자 정보
        User creditor = contract.getCreditor();
        User debtor = contract.getDebtor();

        // LocalDate로 변환
        LocalDate maturityDate = contract.getMaturityDate().toLocalDate();
        LocalDate contractDate = contract.getContractDate().toLocalDate();

        PromissoryNoteEntity entity = PromissoryNoteEntity.builder()
                .tokenId(tokenId)
                .creditorName(creditor.getUserName())
                .creditorWalletAddress(userInfo.getCreditorWalletAddress())
                .creditorSign(userInfo.getCreditorSign())
                .creditorInfoHash(userInfo.getCreditorInfoHash())
                .debtorName(debtor.getUserName())
                .debtorWalletAddress(userInfo.getDebtorWalletAddress())
                .debtorSign(userInfo.getDebtorSign())
                .debtorInfoHash(userInfo.getDebtorInfoHash())
                .loanAmount(contract.getLoanAmount().longValue())
                .interestRate(contract.getInterestRate().multiply(BigDecimal.valueOf(100)).intValue())
                .loanTerm(contract.getLoanTerm())
                .repaymentType(contract.getRepaymentType().getCode())
                .maturityDate(maturityDate)
                .monthlyPaymentDate(contract.getMonthlyPaymentDate())
                .defaultInterestRate(contract.getDefaultInterestRate().multiply(BigDecimal.valueOf(100)).intValue())
                .contractDate(contractDate)
                .earlypayFlag(contract.getEarlyPayment())
                .earlypayFee(contract.getPrepaymentInterestRate() != null ?
                        contract.getPrepaymentInterestRate().multiply(BigDecimal.valueOf(100)).intValue() : 0)
                .accelerationClause(contract.getDefaultCount())
                .addTerms(contract.getContractTerms())
                .addTermsHash(nftPdFUri)
                .nftImage(nftImageUri)
                .deletedFlag(false)
                .build();

        promissoryNoteRepository.save(entity);
        log.info("[데이터베이스] 차용증 NFT 정보 저장 완료 - 토큰 ID: {}", tokenId);
    }

    /**
     * 차용증 메타데이터 생성
     *
     * @param contract 계약 정보
     * @param userInfo 사용자 계약 정보
     * @param nftPdFUri NFT PDF URI
     * @param nftImageUri NFT 이미지 URI
     * @return 메타데이터 객체
     */
    private PromissoryNote.PromissoryMetadata createPromissoryMetadata(
            Contract contract,
            UserContractInfoDto userInfo,
            String nftPdFUri,
            String nftImageUri) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 채권자/채무자 정보 준비
        User creditor = contract.getCreditor();
        User debtor = contract.getDebtor();

        // CrInfo (채권자 정보) 생성
        PromissoryNote.CrInfo crInfo = new PromissoryNote.CrInfo(
                userInfo.getCreditorSign(),
                creditor.getUserName(),
                userInfo.getCreditorWalletAddress(),
                userInfo.getCreditorInfoHash()
        );

        // DrInfo (채무자 정보) 생성
        PromissoryNote.DrInfo drInfo = new PromissoryNote.DrInfo(
                userInfo.getDebtorSign(),
                debtor.getUserName(),
                userInfo.getDebtorWalletAddress(),
                userInfo.getDebtorInfoHash()
        );

        // 추가 조항 해시 생성 부분 수정 => nftPdFUri 기입으로 대체
        String contractTermsHash = nftPdFUri; // PDF URL을 contractTermsHash에 설정

        // AddTerms (추가 조항) 생성
        PromissoryNote.AddTerms addTerms = new PromissoryNote.AddTerms(
                contract.getContractTerms() != null ? contract.getContractTerms() : "",
                contractTermsHash // PDF URL을 addTermsHash에 설정
        );

        // 메타데이터 생성
        return new PromissoryNote.PromissoryMetadata(
                nftImageUri,
                crInfo,
                drInfo,
                contract.getLoanAmount().toBigInteger(),
                contract.getInterestRate().multiply(BigDecimal.valueOf(100)).toBigInteger(), // 백분율로 변환 (예: 0.05 -> 5)
                BigInteger.valueOf(contract.getLoanTerm()),
                contract.getRepaymentType().getCode(),
                contract.getMaturityDate().format(formatter),
                BigInteger.valueOf(contract.getMonthlyPaymentDate()),
                contract.getDefaultInterestRate().multiply(BigDecimal.valueOf(100)).toBigInteger(), // 백분율로 변환
                contract.getContractDate().format(formatter),
                contract.getEarlyPayment(),
                contract.getPrepaymentInterestRate() != null ?
                        contract.getPrepaymentInterestRate().multiply(BigDecimal.valueOf(100)).toBigInteger() :
                        BigInteger.ZERO,
                BigInteger.valueOf(contract.getDefaultCount()),
                addTerms
        );
    }

    /**
     * 토큰 ID로 차용증 PDF URI(addTermsHash) 조회
     *
     * @param tokenId 토큰 ID
     * @return PDF URI(addTermsHash) 값
     * @throws BusinessException 차용증이 존재하지 않을 경우 예외 발생
     */
    @Transactional(readOnly = true)
    public String getPromissoryNotePdfUriByTokenId(BigInteger tokenId) {
        log.info("[서비스] 토큰 ID로 차용증 PDF URI 조회 시작 - 토큰 ID: {}", tokenId);

        Optional<String> pdfUri = promissoryNoteRepository.findAddTermsHashByTokenId(tokenId);

        if (pdfUri.isEmpty()) {
            log.error("[서비스] 토큰 ID에 해당하는 차용증을 찾을 수 없음 - 토큰 ID: {}", tokenId);
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                    String.format("토큰 ID %s에 해당하는 차용증을 찾을 수 없습니다.", tokenId));
        }

        log.info("[서비스] 토큰 ID로 차용증 PDF URI 조회 성공 - 토큰 ID: {}", tokenId);
        return pdfUri.get();
    }
}