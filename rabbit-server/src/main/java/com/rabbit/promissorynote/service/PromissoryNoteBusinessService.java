package com.rabbit.promissorynote.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.rabbit.blockchain.service.PromissoryNoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.rabbit.promissorynote.domain.entity.PromissoryNoteEntity;
import com.rabbit.blockchain.domain.entity.RepaymentSchedule;
import com.rabbit.promissorynote.repository.PromissoryNoteRepository;
import com.rabbit.blockchain.repository.RepaymentScheduleRepository;
import com.rabbit.blockchain.wrapper.PromissoryNote;
import com.rabbit.blockchain.wrapper.RepaymentScheduler;
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
     * @param nftImageUri NFT 이미지 URI
     * @return 생성된 토큰 ID
     */
    @Transactional
    public BigInteger mintPromissoryNoteNFT(Contract contract, String nftPdFUri, String nftImageUri) {
        log.info("[블록체인][시작] 차용증 NFT 발행 시작 - 계약 ID: {}", contract.getContractId());

        try {
            // 1. 메타데이터 생성
            PromissoryNote.PromissoryMetadata metadata = createPromissoryMetadata(contract, nftPdFUri, nftImageUri);

            // 2. 채권자 지갑 주소 확인
            String creditorWalletAddress = walletService.getUserPrimaryWalletAddressById(contract.getCreditor().getUserId());
            if (creditorWalletAddress == null || creditorWalletAddress.isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "채권자 지갑 주소가 유효하지 않습니다.");
            }

            // 3. NFT 민팅 (PromissoryNoteService 사용)
            BigInteger tokenId = promissoryNoteService.mintPromissoryNote(metadata, creditorWalletAddress);

            // 4. DB에 저장
            savePromissoryNoteToDatabase(tokenId, contract, nftPdFUri, nftImageUri);

            // 5. 계약 정보 업데이트
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
     * 상환 일정 등록
     *
     * @param tokenId 토큰 ID
     * @param contract 계약 정보
     */
    private void registerRepaymentSchedule(BigInteger tokenId, Contract contract) throws Exception {
        log.info("[블록체인] 상환 일정 등록 시작 - 토큰 ID: {}", tokenId);

        TransactionReceipt receipt = repaymentScheduler.registerRepaymentSchedule(tokenId).send();
        if (!receipt.isStatusOK()) {
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR, "상환 일정 등록 트랜잭션이 실패했습니다.");
        }

        // 상환 정보 조회
        RepaymentScheduler.RepaymentInfo repaymentInfo = repaymentScheduler.getRepaymentInfo(tokenId).send();

        // 데이터베이스에 상환 일정 저장
        saveRepaymentScheduleToDatabase(tokenId, contract, repaymentInfo);

        log.info("[블록체인] 상환 일정 등록 완료 - 토큰 ID: {}, 트랜잭션: {}",
                tokenId, receipt.getTransactionHash());
    }

    /**
     * NFT 발행 트랜잭션에서 토큰 ID 추출
     *
     * @param receipt 트랜잭션 영수증
     * @return 생성된 토큰 ID
     */
    private BigInteger extractTokenIdFromMintReceipt(TransactionReceipt receipt) {
        // 트랜잭션 로그에서 PromissoryNoteMinted 이벤트 찾기
        var events = PromissoryNote.getPromissoryNoteMintedEvents(receipt);
        if (events.isEmpty()) {
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR, "NFT 발행 이벤트를 찾을 수 없습니다.");
        }

        return events.get(0).tokenId;
    }

    /**
     * 차용증 메타데이터 생성
     *
     * @param contract 계약 정보
     * @param nftImageUri NFT 이미지 URI
     * @return 메타데이터 객체
     */
    private PromissoryNote.PromissoryMetadata createPromissoryMetadata(Contract contract, String nftPdFUri, String nftImageUri) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 채권자/채무자 정보 준비
        User creditor = contract.getCreditor();
        User debtor = contract.getDebtor();

        // 지갑 주소와 서명 정보 가져오기 (외부 서비스에서 조회)
        String creditorWalletAddress = walletService.getUserPrimaryWalletAddressById(creditor.getUserId());
        String debtorWalletAddress = walletService.getUserPrimaryWalletAddressById(debtor.getUserId());
        String creditorSign = signatureService.getSignature(creditor.getUserId(), contract.getContractId());
        String debtorSign = signatureService.getSignature(debtor.getUserId(), contract.getContractId());

        // 정보 해시 생성 -> 차용증별 솔트 추가해서 db 저장 예정
        String creditorInfoHash = hashingService.hashUserInfo(creditor);
        String debtorInfoHash = hashingService.hashUserInfo(debtor);

        // CrInfo (채권자 정보) 생성
        PromissoryNote.CrInfo crInfo = new PromissoryNote.CrInfo(
                creditorSign,
                creditor.getUserName(),
                creditorWalletAddress,
                creditorInfoHash
        );

        // DrInfo (채무자 정보) 생성
        PromissoryNote.DrInfo drInfo = new PromissoryNote.DrInfo(
                debtorSign,
                debtor.getUserName(),
                debtorWalletAddress,
                debtorInfoHash
        );

        // 추가 조항 해시 생성
//        String contractTermsHash = null;
//        if (contract.getContractTerms() != null && !contract.getContractTerms().isEmpty()) {
//            contractTermsHash = hashingService.hashText(contract.getContractTerms());
//        }

        // 추가 조항 해시 생성 부분 수정 => nftPdFUri 기입으로 대체
        String contractTermsHash = nftPdFUri; // PDF URL을 contractTermsHash에 설정

        // AddTerms (추가 조항) 생성
        PromissoryNote.AddTerms addTerms = new PromissoryNote.AddTerms(
                contract.getContractTerms() != null ? contract.getContractTerms() : "",
//                contractTermsHash != null ? contractTermsHash : ""
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
     * @param nftImageUri NFT 이미지 URI
     */
    private void savePromissoryNoteToDatabase(BigInteger tokenId, Contract contract, String nftPdFUri, String nftImageUri) {
        // 채권자/채무자 정보
        User creditor = contract.getCreditor();
        User debtor = contract.getDebtor();

        // 지갑 주소와 서명 정보
        String creditorWalletAddress = walletService.getUserPrimaryWalletAddressById(creditor.getUserId());
        String debtorWalletAddress = walletService.getUserPrimaryWalletAddressById(debtor.getUserId());
        String creditorSign = signatureService.getSignature(creditor.getUserId(), contract.getContractId());
        String debtorSign = signatureService.getSignature(debtor.getUserId(), contract.getContractId());

        // 정보 해시 생성
        String creditorInfoHash = hashingService.hashUserInfo(creditor);
        String debtorInfoHash = hashingService.hashUserInfo(debtor);

        // 추가 조항 해시 생성
//        String contractTermsHash = null;
//        if (contract.getContractTerms() != null && !contract.getContractTerms().isEmpty()) {
//            contractTermsHash = hashingService.hashText(contract.getContractTerms());
//        }

        // 추가 조항 해시 생성 부분 수정 => nftPdFUri 기입으로 대체
        String contractTermsHash = nftPdFUri; // PDF URL을 contractTermsHash에 설정

        // LocalDate로 변환
        LocalDate maturityDate = contract.getMaturityDate().toLocalDate();
        LocalDate contractDate = contract.getContractDate().toLocalDate();

        PromissoryNoteEntity entity = PromissoryNoteEntity.builder()
                .tokenId(tokenId)
                .creditorName(creditor.getUserName())
                .creditorWalletAddress(creditorWalletAddress)
                .creditorSign(creditorSign)
                .creditorInfoHash(creditorInfoHash)
                .debtorName(debtor.getUserName())
                .debtorWalletAddress(debtorWalletAddress)
                .debtorSign(debtorSign)
                .debtorInfoHash(debtorInfoHash)
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
                .addTermsHash(contractTermsHash)
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
     */
    private void saveRepaymentScheduleToDatabase(BigInteger tokenId, Contract contract,
                                                 RepaymentScheduler.RepaymentInfo repaymentInfo) {
        // 블록체인 타임스탬프를 Instant로 변환
        Instant nextPaymentInstant = Instant.ofEpochSecond(repaymentInfo.nextMpDt.longValue());

        // 채무자 지갑 주소
        String debtorWalletAddress = walletService.getUserPrimaryWalletAddressById(contract.getDebtor().getUserId());

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
                .debtorWalletAddress(debtorWalletAddress)
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