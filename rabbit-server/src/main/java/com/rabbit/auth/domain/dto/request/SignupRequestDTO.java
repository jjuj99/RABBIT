package com.rabbit.auth.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "회원가입 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDTO {

    @Schema(description = "이메일", example = "user@example.com")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일을 입력하지 않았습니다.")
    @Size(max = 50, message = "이메일은 50자 이하로 입력해야 합니다.")
    private String email;

    @Schema(description = "이름", example = "김싸피")
    @NotBlank(message = "이름을 입력하지 않았습니다.")
    @Size(max = 30, message = "이름은 30자 이하로 입력해야 합니다.")
    private String userName;

    @Schema(description = "닉네임", example = "열정두배")
    @NotBlank(message = "닉네임을 입력하지 않았습니다.")
    @Size(max = 30, message = "닉네임은 30자 이하로 입력해야 합니다.")
    private String nickname;

    @Schema(description = "은행 ID", example = "1")
    @NotNull(message = "은행 정보를 입력하지 않았습니다.")
    private Integer bankId;

    @Schema(description = "환불 계좌", example = "123-45678-9012-34")
    @NotBlank(message = "환불 계좌 정보를 입력하지 않았습니다.")
    @Size(max = 30, message = "계좌 번호는 30자 이하로 입력해야 합니다.")
    private String refundAccount;

    @Schema(description = "메타마스크 지갑 주소", example = "0x1234abcd5678ef901234abcd5678ef901234abcd")
    @NotBlank(message = "메타마스크 지갑 주소를 입력하지 않았습니다.")
    private String walletAddress;
}
