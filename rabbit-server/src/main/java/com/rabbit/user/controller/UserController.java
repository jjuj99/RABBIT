package com.rabbit.user.controller;

import com.rabbit.auth.controller.swagger.AuthControllerSwagger;
import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.global.response.MessageResponse;
import com.rabbit.user.controller.swagger.UserControllerSwagger;
import com.rabbit.user.domain.dto.response.LoginInfoResponseDTO;
import com.rabbit.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Tag(name = "User", description = "유저 관련 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @UserControllerSwagger.getLoginInfoApi
    @GetMapping("/me")
    public ResponseEntity<CustomApiResponse<LoginInfoResponseDTO>> getLoginInfo(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        LoginInfoResponseDTO response = userService.getLoginInfo(Integer.parseInt(userId));

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }
}
