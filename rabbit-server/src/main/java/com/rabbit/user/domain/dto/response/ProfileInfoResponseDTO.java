package com.rabbit.user.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "마이페이지 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileInfoResponseDTO {

    private int userId;
    private String email;
    private String userName;
    private String nickname;
    private String bankName;
    private String refundAccount;
    private String walletAddress;
}
