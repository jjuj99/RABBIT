package com.rabbit.promissorynote.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.rabbit.blockchain.service.PromissoryNoteService;
import com.rabbit.blockchain.wrapper.PromissoryNote;
import com.rabbit.blockchain.wrapper.RepaymentScheduler;
import com.rabbit.promissorynote.domain.dto.UserContractInfoDto;
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
import com.rabbit.user.service.WalletService;
import com.rabbit.contract.service.SignatureService;
import com.rabbit.contract.service.HashingService;

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
    private final WalletService walletService;
    private final SignatureService signatureService;
    private final HashingService hashingService;

    /**
     * 차용증 NFT 발행 및 데이터베이스 저장
     *
     * @param contract 계약 정보
     * @param nftPdFUri NFT PDF URI
     * @param nftImageUri NFT 이미지 URI
     * @return 생성된 토큰 ID
     */
    @Transactional
    public BigInteger mintPromissoryNoteNFT(Contract contract, String nftPdFUri, String nftImageUri) {
        log.info("[블록체인][시작] 차용증 NFT 발행 시작 - 계약 ID: {}", contract.getContractId());

        try {
            // 사용자 정보 한 번만 조회하여 캐싱
            UserContractInfoDto userInfo = prepareUserContractInfo(contract);

            // 1. 메타데이터 생성
            PromissoryNote.PromissoryMetadata metadata = createPromissoryMetadata(contract, userInfo, nftPdFUri, nftImageUri);

            // 2. NFT 민팅 (PromissoryNoteService 사용)
            BigInteger tokenId = promissoryNoteService.mintPromissoryNote(metadata, userInfo.getCreditorWalletAddress());

            // 3. DB에 저장
            savePromissoryNoteToDatabase(tokenId, contract, userInfo, nftPdFUri, nftImageUri);

            // 4. 계약 정보 업데이트
            contract.setNftInfo(tokenId, nftImageUri);

            log.info("[블록체인][완료] 차용증 NFT 발행 성공 - 계약 ID: {}, 토큰 ID: {}",
                    contract.getContractId(), tokenId);

            return tokenId;
        } catch (Exception e) {
            log.error("[블록체인][예외] 차용증 NFT 발행 중 예외 발생", e);
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR,
                    "NFT 발행 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 사용자 계약 정보(지갑 주소, 서명, 해시 등)를 미리 준비하는 메서드
     *
     * @param contract 계약 정보
     * @return 사용자 계약 정보 DTO
     */
    private UserContractInfoDto prepareUserContractInfo(Contract contract) {
        User creditor = contract.getCreditor();
        User debtor = contract.getDebtor();

        // 지갑 주소 가져오기 (외부 서비스에서 조회)
        String creditorWalletAddress = walletService.getUserPrimaryWalletAddressById(creditor.getUserId());
        if (creditorWalletAddress == null || creditorWalletAddress.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "채권자 지갑 주소가 유효하지 않습니다.");
        }

        String debtorWalletAddress = walletService.getUserPrimaryWalletAddressById(debtor.getUserId());
        if (debtorWalletAddress == null || debtorWalletAddress.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "채무자 지갑 주소가 유효하지 않습니다.");
        }

        // 서명 정보 가져오기
        String creditorSign = signatureService.getSignature(creditor.getUserId(), contract.getContractId());
        String debtorSign = signatureService.getSignature(debtor.getUserId(), contract.getContractId());

        // 정보 해시 생성
        String creditorInfoHash = hashingService.hashUserInfo(creditor);
        String debtorInfoHash = hashingService.hashUserInfo(debtor);

        // DTO 생성 및 반환
        return UserContractInfoDto.builder()
                .creditorWalletAddress(creditorWalletAddress)
                .debtorWalletAddress(debtorWalletAddress)
                .creditorSign(creditorSign)
                .debtorSign(debtorSign)
                .creditorInfoHash(creditorInfoHash)
                .debtorInfoHash(debtorInfoHash)
                .build();
    }

    /**
     * 상환 일정 등록
     *
     * @param tokenId 토큰 ID
     * @param contract 계약 정보
     * @param userInfo 사용자 계약 정보
     */
    private void registerRepaymentSchedule(BigInteger tokenId, Contract contract, UserContractInfoDto userInfo) throws Exception {
        log.info("[블록체인] 상환 일정 등록 시작 - 토큰 ID: {}", tokenId);

        TransactionReceipt receipt = repaymentScheduler.registerRepaymentSchedule(tokenId).send();
        if (!receipt.isStatusOK()) {
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR, "상환 일정 등록 트랜잭션이 실패했습니다.");
        }

        // 상환 정보 조회
        RepaymentScheduler.RepaymentInfo repaymentInfo = repaymentScheduler.getRepaymentInfo(tokenId).send();

        // 데이터베이스에 상환 일정 저장
        saveRepaymentScheduleToDatabase(tokenId, contract, repaymentInfo, userInfo);

        log.info("[블록체인] 상환 일정 등록 완료 - 토큰 ID: {}, 트랜잭션: {}",
                tokenId, receipt.getTransactionHash());
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
     * 데이터베이스에 상환 일정 정보 저장
     *
     * @param tokenId 토큰 ID
     * @param contract 계약 정보
     * @param repaymentInfo 블록체인에서 받아온 상환 정보
     * @param userInfo 사용자 계약 정보
     */
    private void saveRepaymentScheduleToDatabase(
            BigInteger tokenId,
            Contract contract,
            RepaymentScheduler.RepaymentInfo repaymentInfo,
            UserContractInfoDto userInfo) {

        // 블록체인 타임스탬프를 Instant로 변환
        Instant nextPaymentInstant = Instant.ofEpochSecond(repaymentInfo.nextMpDt.longValue());

        RepaymentSchedule entity = RepaymentSchedule.builder()
                .tokenId(tokenId)
                .initialPrincipal(repaymentInfo.initialPrincipal.longValue())
                .remainingPrincipal(repaymentInfo.remainingPrincipal.longValue())
                .interestRate(repaymentInfo.ir.intValue())
                .defaultInterestRate(repaymentInfo.dir.intValue())
                .monthlyPaymentDate(contract.getMonthlyPaymentDate())
                .nextMonthlyPaymentDate(nextPaymentInstant)
                .totalPayments(repaymentInfo.totalPayments.intValue())
                .remainingPayments(repaymentInfo.remainingPayments.intValue())
                .fixedPaymentAmount(repaymentInfo.fixedPaymentAmount.longValue())
                .repaymentType(repaymentInfo.repayType)
                .debtorWalletAddress(userInfo.getDebtorWalletAddress())
                .activeFlag(repaymentInfo.activeFlag)
                .overdueFlag(repaymentInfo.overdueInfo.overdueFlag)
                .overdueStartDate(null) // 초기에는 연체가 없으므로 null
                .overdueDays(repaymentInfo.overdueInfo.overdueDays.intValue())
                .accumulatedOverdueInterest(repaymentInfo.overdueInfo.aoi.longValue())
                .defaultCount(repaymentInfo.overdueInfo.defCnt.intValue())
                .accelerationClause(contract.getDefaultCount())
                .currentInterestRate(repaymentInfo.overdueInfo.currentIr.intValue())
                .totalDefaultCount(repaymentInfo.overdueInfo.totalDefCnt.intValue())
                .build();

        repaymentScheduleRepository.save(entity);
        log.info("[데이터베이스] 상환 일정 정보 저장 완료 - 토큰 ID: {}", tokenId);
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