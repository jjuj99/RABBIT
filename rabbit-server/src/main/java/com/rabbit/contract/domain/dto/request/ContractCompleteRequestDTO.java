package com.rabbit.contract.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 차용증 계약 체결 요청 DTO
 */
@Schema(description = "차용증 계약 체결 요청 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractCompleteRequestDTO {
    @Schema(description = "패스 인증 토큰", example = "token123")
    private String passAuthToken;

    @Schema(description = "트랜잭션 ID", example = "tx_abc123")
    private String txId;

    @Schema(description = "인증 결과 코드", example = "SUCCESS")
    private String authResultCode;
}