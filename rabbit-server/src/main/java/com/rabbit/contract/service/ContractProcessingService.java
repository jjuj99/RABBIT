package com.rabbit.contract.service;

import java.math.BigInteger;

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

/**
 * 계약 완료 처리, NFT 생성, 자금 전송 등의 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractProcessingService {

    private final ContractPdfService contractPdfService;
    private final PinataUploader pinataUploader;
    private final ContractImageService contractImageService;
    private final PromissoryNoteBusinessService promissoryNoteBusinessService; // 추가된 의존성

    /**
     * 계약 완료 처리 (NFT 생성, 자금 전송 등)
     * @param contract 계약 엔티티
     */
    @Transactional
    public void completeContractProcessing(Contract contract) {
        // 시작 시간 측정
        long startTime = System.currentTimeMillis();
        try {
            // 1. NFT 생성
            BigInteger tokenId = generateNFT(contract);

            // 2. 자금 전송
            transferFunds(contract);

            // 3. 상태 업데이트
            contract.updateStatus(SysCommonCodes.Contract.CONTRACTED);

            log.info("[계약 완료 처리 성공] 계약 ID: {}, 토큰 ID: {}",
                    contract.getContractId(), tokenId);
        } catch (Exception e) {
            log.error("[계약 완료 처리 실패] 계약 ID: {}, 오류: {}",
                    contract.getContractId(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR,
                    "계약 완료 처리 중 오류가 발생했습니다: " + e.getMessage());
        } finally {
            // 종료 시간 측정 및 소요 시간 로깅
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            log.info("[성능 측정] 계약 ID: {}, NFT 생성 총 소요 시간: {}ms", contract.getContractId(), elapsedTime);
        }
    }

    /**
     * NFT 생성
     * @param contract 계약 엔티티
     * @return 생성된 NFT 토큰 ID
     */
    private BigInteger generateNFT(Contract contract) {
        try {
            // 1. 개인정보가 암호화된 PDF 생성 (IPFS용)
            byte[] encryptedPdfBytes = contractPdfService.generateEncryptedContractPdf(contract);

            // 2. 암호화된 PDF를 IPFS에 업로드
            String pdfFileName = "contract_" + contract.getContractId() + ".pdf";
            String pdfUrl = pinataUploader.uploadContent(encryptedPdfBytes, pdfFileName, "application/pdf");
            log.info("[IPFS 업로드 완료] 계약 ID: {}, URL: {}", contract.getContractId(), pdfUrl);

            // 3. NFT 이미지 생성 및 IPFS 업로드 추가
            byte[] nftImageBytes = contractImageService.generateContractImage(contract);
            String imageFileName = "contract_image_" + contract.getContractId() + ".png";
            String imgUrl = pinataUploader.uploadContent(nftImageBytes, imageFileName, "image/png");
            log.info("[이미지 IPFS 업로드 완료] 계약 ID: {}, 이미지 URL: {}", contract.getContractId(), imgUrl);

            // 4. 블록체인에 트랜잭션 전송하여 NFT 발행
            // PDF URL은 contractTermsHash로 사용, imgUrl은 이미지 URL로 사용
            BigInteger tokenId = promissoryNoteBusinessService.mintPromissoryNoteNFT(contract, pdfUrl, imgUrl);
            log.info("[NFT 발행 완료] 계약 ID: {}, 토큰 ID: {}", contract.getContractId(), tokenId);

            // 5. 생성된 NFT 정보 설정 - nftImageUrl에 이미지 URL 설정
            contract.setNftInfo(tokenId, imgUrl);

            return tokenId;
        } catch (Exception e) {
            log.error("[NFT 생성 실패] 계약 ID: {}, 오류: {}", contract.getContractId(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR, "NFT 생성 중 오류가 발생했습니다");
        }
    }

    /**
     * 자금 전송
     * @param contract 계약 엔티티
     */
    private void transferFunds(Contract contract) {
        // 실제 자금 전송 로직 구현 필요
        // 예: 블록체인 트랜잭션, 내부 계좌 이체 등
        log.info("[자금 전송 - 시뮬레이션] {} -> {}, 금액: {}",
                contract.getCreditor().getNickname(),
                contract.getDebtor().getNickname(),
                contract.getLoanAmount());

        // 여기에 실제 자금 전송 로직 구현
        // 예: blockchainService.transferFunds(
        //     walletService.getUserPrimaryWalletAddress(contract.getCreditor()),
        //     walletService.getUserPrimaryWalletAddress(contract.getDebtor()),
        //     contract.getLoanAmount()
        // );
    }
}