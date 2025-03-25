package com.rabbit.auth.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Schema(description = "로그인 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    @Schema(description = "메타마스크 지갑 주소", example = "0xdb...f6b")
    @NotBlank(message = "지갑 주소를 입력하지 않았습니다.")
    private String walletAddress;

    @Schema(description = "서명 데이터", example = "0xdddd")
    @NotBlank(message = "서명을 입력하지 않았습니다.")
    private String signature;

    @Schema(description = "난수 데이터", example = "0xdddd")
    @NotBlank(message = "난수 정보가 누락되었습니다.")
    private String nonce;
}
