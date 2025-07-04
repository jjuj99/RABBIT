package com.rabbit.user.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "이메일로 유저 검색 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchUserResponseDTO {

    private int userId;
    private String email;
    private String userName;
    private String nickname;
    private String walletAddress;
}
