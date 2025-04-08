package com.rabbit.user.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "로그인한 회원 간단 정보 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginInfoResponseDTO {

    private String userName;
    private String nickname;
    private String email;
    private Integer bankId;
    private String bankName;
    private String refundAccount;
}
