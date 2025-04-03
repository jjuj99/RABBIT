package com.rabbit.bankApi.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "계좌 내역 조회 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquireTransactionRequestDTO {

    @Schema(example = "a9b501b9-dcec-4eaf-9f98-3b9bf5393ef0")
    private String userKey;

    @Schema(example = "0015479883663992")
    private String accountNo;

    @Schema(example = "20250301")
    private String startDate;

    @Schema(example = "A")
    private String transactionType;

    @Schema(example = "DESC")
    private String orderByType;
}
