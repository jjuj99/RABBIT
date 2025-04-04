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

@Schema(description = "차용증 계약 목록 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractListResponseDTO {

    @Schema(description = "계약 ID", example = "3")
    private Integer id;

    @Schema(description = "대출 금액", example = "2500000")
    private BigDecimal la;

    @Schema(description = "대출 시작일", example = "2024-04-15")
    private ZonedDateTime contractDt;

    @Schema(description = "생성일", example = "2024-04-05")
    private ZonedDateTime createdAt;

    @Schema(description = "채무자, 채권자 이름", example = "이영희")
    private String name;

    @Schema(description = "채무자, 채권자 지갑 주소", example = "0x10045789abcdef0123")
    private String walletAddress;

    @Schema(description = "연 이자율 (%)", example = "4.2")
    private BigDecimal ir;

    @Schema(description = "대출 기간 (개월)", example = "12")
    private Integer lt;

    @Schema(description = "대출 만기일", example = "2024-05-01")
    private ZonedDateTime matDt;

    @Schema(description = "월 납입일", example = "10")
    private Integer mpDt;

    @Schema(description = "상환방식", example = "BP")
    private SysCommonCodes.Repayment repayType;

    @Schema(description = "상환방식명", example = "원리금균등상환")
    private String repayTypeName;

    @Schema(description = "계약 상태", example = "REQUESTED")
    private SysCommonCodes.Contract contractStatus;

    @Schema(description = "계약 상태명", example = "요청됨")
    private String contractStatusName;

    /**
     * Contract 엔티티를 ContractListResponseDTO로 변환
     * @param contract 계약 엔티티
     * @return 계약 목록 응답 DTO
     */
    public static ContractListResponseDTO from(Contract contract) {
        if (contract == null) {
            return null;
        }

        return ContractListResponseDTO.builder()
                .id(contract.getContractId())
                .la(contract.getLoanAmount())
                .contractDt(contract.getContractDate())
                .createdAt(contract.getCreatedAt())
                .ir(contract.getInterestRate())
                .lt(contract.getLoanTerm())
                .matDt(contract.getMaturityDate())
                .mpDt(contract.getMonthlyPaymentDate())
                .repayType(contract.getRepaymentType())
                .repayTypeName(contract.getRepaymentType().getCodeName())
                .contractStatus(contract.getContractStatus())
                .contractStatusName(contract.getContractStatus().getCodeName())
                .build();
    }
}