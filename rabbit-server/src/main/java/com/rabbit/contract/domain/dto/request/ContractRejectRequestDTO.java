package com.rabbit.contract.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Schema(description = "차용증 계약 반려 요청 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractRejectRequestDTO {
    @Schema(description = "반려 메시지", example = "계약 조건 재검토 필요", maxLength = 500)
    private String rejectMessage;

    @Schema(description = "계약 취소 여부", example = "false")
    @Builder.Default
    private boolean isCanceled = false;

//    @Schema(description = "패스 인증 토큰", example = "token123")
//    private String passAuthToken;
//
//    @Schema(description = "트랜잭션 ID", example = "tx_abc123")
//    private String txId;
//
//    @Schema(description = "인증 결과 코드", example = "SUCCESS")
//    private String authResultCode;

    @Size(max = 500, message = "반려 메시지는 500자 이내로 작성해야 합니다.")
    public String getRejectMessage() {
        return rejectMessage;
    }
}
