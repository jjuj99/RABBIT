package com.rabbit.auth.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "1원 인증 검증 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountAuthVerifyRequestDTO {

    String email;

    @NotBlank(message = "인증 번호를 입력하지 않았습니다.")
    String authCode;
}
