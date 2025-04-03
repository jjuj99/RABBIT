package com.rabbit.loan.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "부채 정보 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowSummaryResponseDTO {

    private int totalOutgoingLa;
    private int monthlyOutgoingLa;
    private String nextOutgoingDt;
}
