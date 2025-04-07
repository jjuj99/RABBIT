package com.rabbit.auth.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "1원 인증 검증 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountAuthVerifyResponseDTO {

    private boolean isVerified;
    private String message;
}
