package com.rabbit.contract.domain.dto.response;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rabbit.contract.domain.entity.Contract;

import com.rabbit.global.code.domain.enums.SysCommonCodes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "차용증 계약 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractResponseDTO {

    @Schema(description = "계약 ID", example = "1")
    private Integer contractId;

    @Schema(description = "채권자 ID", example = "1")
    private Integer crId;

    @Schema(description = "채권자 이름", example = "홍길동")
    private String crName;

    @Schema(description = "채무자 ID", example = "2")
    private Integer drId;

    @Schema(description = "채무자 이름", example = "김철수")
    private String drName;

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
    private String repayType;

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

    @Schema(description = "납부 유예 일수", example = "7")
    private Integer graceLineDays;

    @Schema(description = "계약 조항", example = "이 계약의 조항은...")
    private String addTerms;

    @Schema(description = "NFT 토큰 ID", example = "0x123...")
    private String tokenId;

    @Schema(description = "NFT 이미지 URL", example = "https://example.com/nft.png")
    private String nftImageUrl;

    @Schema(description = "계약 상태", example = "PENDING")
    private SysCommonCodes.Contract contractStatus;

    @Schema(description = "계약 상태명", example = "서명 대기중")
    private String contractStatusName;

    @Schema(description = "반려 메시지", example = "이자율이 너무 낮습니다.")
    private String rejectMessage;

    @Schema(description = "반려 메시지 생성일", example = "2025-04-05 15:30:00")
    private ZonedDateTime rejectedAt;

    @Schema(description = "생성일", example = "2025-04-01 12:00:00")
    private ZonedDateTime createdAt;

    @Schema(description = "수정일", example = "2025-04-02 12:00:00")
    private ZonedDateTime updatedAt;

    /**
     * Contract 엔티티를 ContractResponseDTO로 변환
     * @param contract 계약 엔티티
     * @return 계약 응답 DTO
     */
    public static ContractResponseDTO from(Contract contract) {
        if (contract == null) {
            return null;
        }

        return ContractResponseDTO.builder()
                .contractId(contract.getContractId())
                .crId(contract.getCreditor().getUserId())
                .crName(contract.getCreditor().getNickname())
                .drId(contract.getDebtor().getUserId())
                .drName(contract.getDebtor().getNickname())
                .la(contract.getLoanAmount())
                .ir(contract.getInterestRate())
                .contractDt(contract.getContractDate())
                .matDt(contract.getMaturityDate())
                .lt(contract.getLoanTerm())
                .earlypayFee(contract.getPrepaymentInterestRate())
                .repayType(contract.getRepaymentType())
                .mpDt(contract.getMonthlyPaymentDate())
                .dir(contract.getDefaultInterestRate())
                .defCnt(contract.getDefaultCount())
                .earlypay(contract.getEarlyPayment())
                .pnTransFlag(contract.getPromissoryNoteTransferabilityFlag())
                .graceLineDays(contract.getGraceLineDays())
                .addTerms(contract.getContractTerms())
                .tokenId(contract.getTokenId())
                .nftImageUrl(contract.getNftImageUrl())
                .contractStatus(contract.getContractStatus())
                .contractStatusName(contract.getContractStatus().getCodeName())
                .rejectMessage(contract.getRejectMessage())
                .rejectedAt(contract.getRejectedAt())
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .build();
    }
}