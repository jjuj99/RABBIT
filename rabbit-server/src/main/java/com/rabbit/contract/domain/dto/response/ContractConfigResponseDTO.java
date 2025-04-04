package com.rabbit.contract.domain.dto.response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "계약 설정 정보 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractConfigResponseDTO {

    @Schema(description = "법정 최고 이자율", example = "20.0")
    private BigDecimal maxLegalIr;

    @Schema(description = "기본 연체 이자율", example = "15.0")
    private BigDecimal defDir;

    @Schema(description = "기한이익상실 연체 횟수", example = "3")
    private Integer defDefCnt;

    @Schema(description = "중도상환 이자율", example = "2.0")
    private BigDecimal defEarlypayFee;

    @Schema(description = "기본 납부 유예 일수", example = "7")
    private Integer defGraceDays;

    @Schema(description = "최소 대출 금액", example = "10000")
    private BigDecimal minLa;

    @Schema(description = "최대 대출 금액", example = "10000000")
    private BigDecimal maxLa;

    @Schema(description = "대출 가능 최소 기간(일)", example = "30")
    private Integer minLtDays;

    @Schema(description = "대출 가능 최대 기간(일)", example = "365")
    private Integer maxLtDays;

    @Schema(description = "기본 계약 조항 템플릿", example = "본 계약은 다음과 같은 조건으로 체결됩니다...")
    private String defAddTerms;
}