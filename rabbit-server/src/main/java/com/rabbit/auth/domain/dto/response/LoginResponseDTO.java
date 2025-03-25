package com.rabbit.auth.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "로그인 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String userName;
    private String nickname;
    private String accessToken;
}
