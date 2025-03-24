package com.rabbit.auth.controller;

import com.rabbit.auth.controller.swagger.AuthControllerSwagger;
import com.rabbit.auth.domain.dto.request.*;
import com.rabbit.auth.domain.dto.response.*;
import com.rabbit.auth.service.AuthService;
import com.rabbit.auth.service.dto.LoginServiceResult;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.global.response.CustomApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @AuthControllerSwagger.nonceApi
    @PostMapping("/nonce")
    public ResponseEntity<CustomApiResponse<NonceResponseDTO>> nonce(@RequestBody @Valid NonceRequestDTO request,
                                                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            throw new BusinessException(ErrorCode.INVALID_TYPE_VALUE, errorMessage);
        }

        NonceResponseDTO response = authService.nonce(request);

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @AuthControllerSwagger.loginApi
    @PostMapping("/login")
    public ResponseEntity<CustomApiResponse<LoginResponseDTO>> login(@RequestBody @Valid LoginRequestDTO request,
                                                                     HttpServletResponse httpResponse,BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            throw new BusinessException(ErrorCode.INVALID_TYPE_VALUE, errorMessage);
        }

        LoginServiceResult result = authService.login(request);

        LoginResponseDTO response = LoginResponseDTO.builder()
                .nickname(result.getNickname())
                .accessToken(result.getAccessToken())
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 7)
                .sameSite("Strict")
                .build();

        httpResponse.addHeader("Set-Cookie", refreshCookie.toString());

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }
}