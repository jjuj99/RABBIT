package com.rabbit.contract.service;

import java.math.BigInteger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rabbit.contract.domain.entity.Contract;
import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.global.ipfs.PinataUploader;
import com.rabbit.user.service.WalletService;

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
    private final WalletService walletService;
    // private final BlockchainService blockchainService; // 실제 구현 시 필요

    /**
     * 계약 완료 처리 (NFT 생성, 자금 전송 등)
     * @param contract 계약 엔티티
     */
    @Transactional
    public void completeContractProcessing(Contract contract) {
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

            // 아직 미구현
            BigInteger tokenId = new BigInteger("1"); // 실제로는 블록체인 트랜잭션 결과에서 추출
//            // 3. 스마트 컨트랙트 상호작용을 위한 PromissoryMetadata 객체 생성
//            PromissoryNote.PromissoryMetadata metadata = createPromissoryMetadata(contract, pdfUrl);
//
//            // 4. 블록체인에 트랜잭션 전송하여 NFT 발행
//            BigInteger tokenId = blockchainService.mintPromissoryNoteNFT(
//                    metadata,
//                    walletService.getUserPrimaryWalletAddress(contract.getCreditor())
//            );

            // 5. 생성된 NFT 정보 설정
            contract.setNftInfo(tokenId, pdfUrl);

            return tokenId;
        } catch (Exception e) {
            log.error("[NFT 생성 실패] 계약 ID: {}, 오류: {}", contract.getContractId(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR, "NFT 생성 중 오류가 발생했습니다");
        }
    }

    /**
     * NFT 메타데이터 JSON 생성 (개인정보 암호화 적용)
     * @deprecated 차용증 NFT 가 존재함으로 사용하지 않음
     * @param contract 계약 엔티티
     * @param pdfUrl PDF 파일의 IPFS URL
     * @return NFT 메타데이터 JSON 문자열
     */
    private String createNftMetadata(Contract contract, String pdfUrl) {
        // 메타데이터에 사용될 정보 암호화
        String encryptedCreditorName = contractPdfService.encryptForMetadata(contract, contract.getCreditor().getNickname());
        String encryptedDebtorName = contractPdfService.encryptForMetadata(contract, contract.getDebtor().getNickname());

        // 간단한 JSON 생성 예시 (실제로는 Jackson 등 사용 권장)
        return String.format(
                "{\n" +
                        "  \"name\": \"Loan Contract #%d\",\n" +
                        "  \"description\": \"Loan contract between %s and %s\",\n" +
                        "  \"image\": \"%s\",\n" +
                        "  \"attributes\": [\n" +
                        "    {\n" +
                        "      \"trait_type\": \"Contract Type\",\n" +
                        "      \"value\": \"Loan\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"trait_type\": \"Loan Amount\",\n" +
                        "      \"value\": \"%s\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"trait_type\": \"Interest Rate\",\n" +
                        "      \"value\": \"%s%%\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"trait_type\": \"Term\",\n" +
                        "      \"value\": \"%d months\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"trait_type\": \"Maturity Date\",\n" +
                        "      \"value\": \"%s\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"trait_type\": \"Contract ID\",\n" +
                        "      \"value\": \"%d\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}",
                contract.getContractId(),
                encryptedCreditorName,
                encryptedDebtorName,
                pdfUrl,
                contract.getLoanAmount(),
                contract.getInterestRate(),
                contract.getLoanTerm(),
                contract.getMaturityDate().toString(),
                contract.getContractId()
        );
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