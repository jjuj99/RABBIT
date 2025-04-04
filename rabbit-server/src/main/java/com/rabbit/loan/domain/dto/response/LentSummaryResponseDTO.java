package com.rabbit.loan.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Schema(description = "채권 정보 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LentSummaryResponseDTO {

    private int totalIncomingLa;
    private int monthlyIncomingLa;
    private String nextIncomingDt;
}
