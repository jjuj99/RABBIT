package com.rabbit.bankApi.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "1원 인증 송금 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAccountRequestDTO {

    @Schema(example = "a9b501b9-dcec-4eaf-9f98-3b9bf5393ef0")
    private String userKey;

    @Schema(example = "0015479883663992")
    private String accountNo;

    @Schema(example = "RABBIT")
    private String authText;
}
