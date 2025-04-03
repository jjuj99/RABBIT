package com.rabbit.bankApi.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "계좌 입금 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDepositRequestDTO {

    @Schema(example = "a9b501b9-dcec-4eaf-9f98-3b9bf5393ef0")
    private String userKey;

    @Schema(example = "0015479883663992")
    private String accountNo;

    @Schema(example = "100000")
    private String transactionBalance;

    @Schema(example = "(수시입출금) : 입금")
    private String transactionSummary;
}
