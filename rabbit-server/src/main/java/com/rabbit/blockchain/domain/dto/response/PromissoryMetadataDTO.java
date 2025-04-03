package com.rabbit.blockchain.domain.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
public class PromissoryMetadataDTO {
    private Long tokenId;                // 토큰 ID
    private String nftImage;             // NFT 이미지 URI

    // 채권자 정보
    private String creditorName;
    private String creditorWalletAddress;

    // 채무자 정보
    private String debtorName;
    private String debtorWalletAddress;

    // 대출 정보
    private BigInteger loanAmount;        // 대출 금액
    private Double interestRate;          // 이자율 (%)
    private Integer loanTerm;             // 대출 기간 (개월)
    private String repaymentType;         // 상환 방식 (EPIP, EPP, BP)
    private String maturityDate;          // 만기일
    private Integer monthlyPaymentDay;    // 월 납부일
    private Double defaultInterestRate;   // 연체 이자율 (%)
    private String contractDate;          // 계약일

    // 중도상환 정보
    private Boolean earlyPaymentAllowed;  // 중도상환 가능 여부
    private Double earlyPaymentFee;       // 중도상환 수수료 (%)
}
