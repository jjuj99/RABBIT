package com.rabbit.contract.service;

import java.math.BigInteger;

import com.rabbit.blockchain.service.RabbitCoinService;
import com.rabbit.contract.domain.dto.ContractProcessingInfoDTO;
import com.rabbit.promissorynote.domain.dto.UserContractInfoDto;
import com.rabbit.user.domain.entity.User;
import com.rabbit.user.service.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rabbit.promissorynote.service.PromissoryNoteBusinessService;
import com.rabbit.contract.domain.entity.Contract;
import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.global.ipfs.PinataUploader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

/**
 * 계약 완료 처리, NFT 생성, 자금 전송 등의 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractProcessingService {

    private final ContractPdfService contractPdfService;
    private final DarkThemePdfService darkThemePdfService;
    private final WhiteThemePdfService whiteThemePdfService;
    private final PinataUploader pinataUploader;
    private final ContractImageService contractImageService;
    private final PromissoryNoteBusinessService promissoryNoteBusinessService;
    private final WalletService walletService;
    private final SignatureService signatureService;
    private final HashingService hashingService;
    private final RabbitCoinService rabbitCoinService;

    /**
     * 계약 완료 처리를 위한 준비 작업 (트랜잭션 밖에서 실행)
     * @param contract 계약 엔티티
     * @return 계약 처리에 필요한 정보
     */
    public ContractProcessingInfoDTO prepareContractProcessing(Contract contract) {
        // 시작 시간 측정
        long startTime = System.currentTimeMillis();

        try {
            // 1. 사용자 정보 준비 (지갑 주소 등)
            UserContractInfoDto userInfo = prepareUserContractInfo(contract);

            // 2. PDF 생성
            byte[] encryptedPdfBytes = whiteThemePdfService.generateEncryptedWhiteThemeContractPdf(contract);

            // 3. PDF를 IPFS에 업로드
            String pdfFileName = "contract_" + contract.getContractId() + ".pdf";
            String pdfUrl = pinataUploader.uploadContent(encryptedPdfBytes, pdfFileName, "application/pdf");
            log.info("[IPFS 업로드 완료] 계약 ID: {}, URL: {}", contract.getContractId(), pdfUrl);

            // 4. NFT 이미지 생성 및 IPFS 업로드
            byte[] nftImageBytes = contractImageService.generateContractImage(contract);
            String imageFileName = "contract_image_" + contract.getContractId() + ".png";
            String imgUrl = pinataUploader.uploadContent(nftImageBytes, imageFileName, "image/png");
            log.info("[이미지 IPFS 업로드 완료] 계약 ID: {}, 이미지 URL: {}", contract.getContractId(), imgUrl);

            // 5. 준비 정보 반환
            return ContractProcessingInfoDTO.builder()
                    .userInfo(userInfo)
                    .pdfUrl(pdfUrl)
                    .imgUrl(imgUrl).build();
        } catch (Exception e) {
            log.error("[계약 준비 실패] 계약 ID: {}, 오류: {}", contract.getContractId(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "계약 완료 준비 중 오류가 발생했습니다: " + e.getMessage());
        } finally {
            // 준비 시간 측정 및 소요 시간 로깅
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            log.info("[성능 측정] 계약 ID: {}, 계약 준비 소요 시간: {}ms", contract.getContractId(), elapsedTime);
        }
    }

    /**
     * 계약 완료 처리 (트랜잭션 내에서 실행)
     * @param contract 계약 엔티티
     * @param processingInfo 준비된 처리 정보
     */
    @Transactional
    public void completeContractProcessing(Contract contract, ContractProcessingInfoDTO processingInfo) {
        // 시작 시간 측정
        long startTime = System.currentTimeMillis();

        try {
            // 1. NFT 생성 - 트랜잭션 전에 이미 생성된 상태
            BigInteger tokenId = promissoryNoteBusinessService.mintPromissoryNoteNFT(
                    contract,
                    processingInfo.getPdfUrl(),
                    processingInfo.getImgUrl(),
                    processingInfo.getUserInfo()
            );

            // 2. 자금 전송
            transferFunds(contract, processingInfo.getUserInfo());

            // 3. 계약 상태 업데이트 및 NFT 정보 설정
            contract.updateStatus(SysCommonCodes.Contract.CONTRACTED);
            contract.setNftInfo(tokenId, processingInfo.getImgUrl());

            log.info("[계약 완료 처리 성공] 계약 ID: {}, 토큰 ID: {}", contract.getContractId(), tokenId);

        } catch (BusinessException e) {
            // 비즈니스 예외는 그대로 전파
            throw e;
        } catch (Exception e) {
            log.error("[계약 완료 처리 실패] 계약 ID: {}, 오류: {}", contract.getContractId(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "계약 완료 처리 중 오류가 발생했습니다: " + e.getMessage());
        } finally {
            // 종료 시간 측정 및 소요 시간 로깅
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            log.info("[성능 측정] 계약 ID: {}, NFT 생성 총 소요 시간: {}ms", contract.getContractId(), elapsedTime);
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
     * 자금 전송
     * @param contract 계약 엔티티
     * @param userInfo 사용자 정보
     */
    private void transferFunds(Contract contract, UserContractInfoDto userInfo) {
        try {
            // 대출 금액
            BigInteger loanAmount = contract.getLoanAmount().toBigInteger();

            log.info("[자금 전송 시작] {} -> {}, 금액: {}, 채권자 지갑: {}, 채무자 지갑: {}",
                    contract.getCreditor().getNickname(),
                    contract.getDebtor().getNickname(),
                    contract.getLoanAmount(),
                    userInfo.getCreditorWalletAddress(),
                    userInfo.getDebtorWalletAddress());

            // 실제 자금 전송 - RabbitCoinService 사용
            TransactionReceipt receipt = rabbitCoinService.transferFrom(
                    userInfo.getCreditorWalletAddress(),  // 채권자 지갑 주소 (from)
                    userInfo.getDebtorWalletAddress(),    // 채무자 지갑 주소 (to)
                    loanAmount              // 송금 금액
            );

            if (!receipt.isStatusOK()) {
                throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR, "자금 전송에 실패했습니다.");
            }

            log.info("[자금 전송 완료] 트랜잭션 해시: {}", receipt.getTransactionHash());
        } catch (Exception e) {
            log.error("[자금 전송 실패] 오류: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR, "자금 전송 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}