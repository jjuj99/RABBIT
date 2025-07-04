package com.rabbit.auth.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "로그인 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    @Schema(description = "메타마스크 지갑 주소", example = "0x1234abcd5678efgh")
    @NotBlank(message = "지갑 주소를 입력하지 않았습니다.")
    private String walletAddress;

    @Schema(description = "서명 데이터", example = "0xdddd")
    @NotBlank(message = "서명을 입력하지 않았습니다.")
    private String signature;

    @Schema(description = "Nonce 데이터", example = "0xdddd")
    @NotBlank(message = "Nonce 정보가 누락되었습니다.")
    private String nonce;
}
