package com.rabbit.auth.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "닉네임 중복 확인 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckNicknameRequestDTO {

    @Schema(description = "닉네임", example = "열정두배")
    @NotBlank(message = "닉네임을 입력하지 않았습니다.")
    @Size(max = 12, message = "닉네임은 12자 이하로 입력해야 합니다.")
    private String nickname;
}
