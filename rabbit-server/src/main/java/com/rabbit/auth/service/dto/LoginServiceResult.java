package com.rabbit.auth.service.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginServiceResult {

    private String userName;
    private String nickname;
    private String accessToken;
    private String refreshToken;
}
