package com.rabbit.auth.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LoginServiceResult {

    private String userName;
    private String nickname;
    private String accessToken;
    private String refreshToken;
}
