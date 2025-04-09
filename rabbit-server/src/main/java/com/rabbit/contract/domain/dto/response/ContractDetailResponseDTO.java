package com.rabbit.contract.domain.dto.response;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.BigInteger;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rabbit.contract.domain.entity.Contract;

import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.user.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "차용증 계약 상세 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractDetailResponseDTO {

    @Schema(description = "계약 ID", example = "1")
    private Integer contractId;

    @Schema(description = "채권자 ID", example = "1")
    private Integer crId;

    @Schema(description = "채권자 이름", example = "홍길동")
    private String crName;

    @Schema(description = "채권자 이메일", example = "123@naver.com")
    private String crEmail;
    
    @Schema(description = "채권자 지갑 주소", example = "0x123...")
    private String crWallet;

    @Schema(description = "채무자 ID", example = "2")
    private Integer drId;

    @Schema(description = "채무자 이름", example = "김철수")
    private String drName;

    @Schema(description = "채무자 지갑 주소", example = "0x456...")
    private String drWallet;

    @Schema(description = "대출 금액", example = "1000000")
    private BigDecimal la;

    @Schema(description = "연 이자율 (%)", example = "5.0")
    private BigDecimal ir;

    @Schema(description = "대출 시작일", example = "2025-04-10 00:00:00")
    private ZonedDateTime contractDt;

    @Schema(description = "대출 만기일", example = "2025-10-10 00:00:00")
    private ZonedDateTime matDt;

    @Schema(description = "대출 기간 (개월)", example = "12")
    private Integer lt;

    @Schema(description = "중도상환 이자율 (%)", example = "2.0")
    private BigDecimal earlypayFee;

    @Schema(description = "상환방식", example = "EPIP")
    private SysCommonCodes.Repayment repayType;

    @Schema(description = "상환방식명", example = "원리금균등상환")
    private String repayTypeName;

    @Schema(description = "월 납입일", example = "15")
    private Integer mpDt;

    @Schema(description = "연체 이자율 (%)", example = "15.0")
    private BigDecimal dir;

    @Schema(description = "기한이익상실 연체 횟수", example = "3")
    private Integer defCnt;

    @Schema(description = "중도 상환 가능 여부", example = "true")
    private Boolean earlypay;

    @Schema(description = "차용증 양도 가능 여부", example = "true")
    private Boolean pnTransFlag;

    @Schema(description = "계약 조항", example = "이 계약의 조항은...")
    private String addTerms;

    @Schema(description = "NFT 토큰 ID", example = "123456")
    private BigInteger tokenId;

    @Schema(description = "NFT 이미지 URL", example = "https://example.com/nft.png")
    private String nftImageUrl;

    @Schema(description = "계약 상태", example = "REQUESTED")
    private SysCommonCodes.Contract contractStatus;

    @Schema(description = "계약 상태명", example = "요청됨")
    private String contractStatusName;

    @Schema(description = "생성일", example = "2025-04-01 12:00:00")
    private ZonedDateTime createdAt;

    @Schema(description = "수정일", example = "2025-04-02 12:00:00")
    private ZonedDateTime updatedAt;

    @Schema(description = "남은 대출 일수", example = "120")
    private Long remainingDays;

    @Schema(description = "만기 시 총 상환 금액", example = "1050000")
    private BigDecimal matAmt;

    @Schema(description = "메시지", example = "잘 부탁드립니다.")
    private String message;

    @Schema(description = "반려 메시지", example = "이자율이 너무 낮습니다.")
    private String rejectMessage;

    @Schema(description = "반려 메시지 생성일", example = "2025-04-05 15:30:00")
    private ZonedDateTime rejectedAt;

    /**
     * Contract 엔티티를 ContractDetailResponseDTO로 변환
     * @param contract 계약 엔티티
     * @return 계약 상세 응답 DTO
     */
    public static ContractDetailResponseDTO from(Contract contract) {
        if (contract == null) {
            return null;
        }

        // 남은 대출 일수 계산
        long remainingDays = 0;
        if(contract.getMaturityDate()!=null) {
            remainingDays = java.time.Duration.between(
                    ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0),
                    contract.getMaturityDate().withHour(0).withMinute(0).withSecond(0)
            ).toDays();
        }
//        remainingDays = Math.max(0, remainingDays);

        // 만기 시 총 상환 금액 계산 (원금 + 이자)
        BigDecimal totalRepayment = calculateTotalRepayment(contract);

        return ContractDetailResponseDTO.builder()
                .contractId(contract.getContractId())
                .crId(contract.getCreditor().getUserId())
                .crName(contract.getCreditor().getNickname())
                .crEmail(contract.getCreditor().getEmail())
                .drId(contract.getDebtor().getUserId())
                .drName(contract.getDebtor().getNickname())
                .la(contract.getLoanAmount())
                .ir(contract.getInterestRate())
                .contractDt(contract.getContractDate())
                .matDt(contract.getMaturityDate())
                .lt(contract.getLoanTerm())
                .earlypayFee(contract.getPrepaymentInterestRate())
                .repayType(contract.getRepaymentType())
                .repayTypeName(contract.getRepaymentType().getCodeName())
                .mpDt(contract.getMonthlyPaymentDate())
                .dir(contract.getDefaultInterestRate())
                .defCnt(contract.getDefaultCount())
                .earlypay(contract.getEarlyPayment())
                .pnTransFlag(contract.getPromissoryNoteTransferabilityFlag())
                .addTerms(contract.getContractTerms())
                .tokenId(contract.getTokenId())
                .nftImageUrl(contract.getNftImageUrl())
                .contractStatus(contract.getContractStatus())
                .contractStatusName(contract.getContractStatus().getCodeName())
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .remainingDays(remainingDays)
                .matAmt(totalRepayment)
                .message(contract.getMessage())
                .rejectMessage(contract.getRejectMessage())
                .rejectedAt(contract.getRejectedAt())
                .build();
    }

    /**
     * 사용자의 메타마스크 지갑 주소를 조회
     * @param user 사용자 엔티티
     * @return 지갑 주소
     */
    private static String getWalletAddress(User user) {
        // User 클래스 구조에 맞게 수정
        try {
            if (user != null) {
                // MetamaskWallet이 접근 가능한 필드로 노출되어 있다면 직접 접근
//                if (user.getMetamaskWallet() != null) {
//                    return user.getMetamaskWallet().getAddress();
//                }

                // 또는 객체를 리플렉션으로 접근하는 대신, 관련 ID나 다른 식별자를 반환
                // return "wallet-" + user.getId();
            }
        } catch (Exception e) {
            // 로깅 추가할 수 있음
        }
        return null;
    }

    /**
     * 사용자의 프로필 이미지 URL을 조회
     * @param user 사용자 엔티티
     * @return 프로필 이미지 URL
     */
    private static String getUserProfileImage(User user) {
        // User 클래스 구조에 맞게 수정
        try {
            if (user != null) {
                // 직접 필드에 접근할 수 있으면 접근
                // 예: user.profileImage 또는 user.profileImageUrl

                // 또는 기본 이미지 URL 반환
                return "/api/images/profile/default.png";
            }
        } catch (Exception e) {
            // 로깅 추가할 수 있음
        }
        return null;
    }

    /**
     * 만기 시 총 상환 금액 계산 (원금 + 이자)
     * @param contract 계약 엔티티
     * @return 총 상환 금액
     */
    private static BigDecimal calculateTotalRepayment(Contract contract) {
        BigDecimal principal = contract.getLoanAmount();
        BigDecimal interestRate = contract.getInterestRate().divide(BigDecimal.valueOf(100));

        // 대출 기간(년)
        long loanDays = java.time.Duration.between(
                contract.getContractDate().withHour(0).withMinute(0).withSecond(0),
                contract.getMaturityDate().withHour(0).withMinute(0).withSecond(0)
        ).toDays();
        BigDecimal loanYears = BigDecimal.valueOf(loanDays).divide(BigDecimal.valueOf(365), 10, java.math.RoundingMode.HALF_UP);

        // 이자 계산 (단리)
        BigDecimal interest = principal.multiply(interestRate).multiply(loanYears);

        // 원금 + 이자
        return principal.add(interest).setScale(0, java.math.RoundingMode.HALF_UP);
    }
}