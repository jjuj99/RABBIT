package com.rabbit.auth.controller;

import com.rabbit.auth.controller.swagger.AuthControllerSwagger;
import com.rabbit.auth.domain.dto.request.LoginRequestDTO;
import com.rabbit.auth.domain.dto.request.NonceRequestDTO;
import com.rabbit.auth.domain.dto.request.SignupRequestDTO;
import com.rabbit.auth.domain.dto.response.CheckNicknameResponseDTO;
import com.rabbit.auth.domain.dto.response.LoginResponseDTO;
import com.rabbit.auth.domain.dto.response.NonceResponseDTO;
import com.rabbit.auth.domain.dto.response.RefreshResponseDTO;
import com.rabbit.auth.service.AuthService;
import com.rabbit.auth.service.dto.LoginServiceResult;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.global.response.MessageResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@Tag(name = "Auth", description = "계정 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @AuthControllerSwagger.nonceApi
    @PostMapping("/nonce")
    public ResponseEntity<CustomApiResponse<NonceResponseDTO>> nonce(@RequestBody @Valid NonceRequestDTO request) {
        NonceResponseDTO response = authService.nonce(request);

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @AuthControllerSwagger.loginApi
    @PostMapping("/login")
    public ResponseEntity<CustomApiResponse<LoginResponseDTO>> login(@RequestBody @Valid LoginRequestDTO request, HttpServletResponse httpResponse) {
        LoginServiceResult result = authService.login(request);

        LoginResponseDTO response = LoginResponseDTO.builder()
                .userName(result.getUserName())
                .nickname(result.getNickname())
                .accessToken(result.getAccessToken())
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 60 * 24 * 7)
                .sameSite("Strict")
                .build();

        httpResponse.addHeader("Set-Cookie", refreshCookie.toString());

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @AuthControllerSwagger.signupApi
    @PostMapping("/sign-up")
    public ResponseEntity<CustomApiResponse<MessageResponse>> signup(@RequestBody @Valid SignupRequestDTO request) {
        authService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomApiResponse.success(MessageResponse.of("회원가입에 성공했습니다.")));
    }

    @AuthControllerSwagger.logoutApi
    @PostMapping("/logout")
    public ResponseEntity<CustomApiResponse<MessageResponse>> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookie(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.JWT_REQUIRED));

        authService.logout(refreshToken);

        ResponseCookie emptyCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0) // 쿠키 만료
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", emptyCookie.toString());

        return ResponseEntity.ok(CustomApiResponse.success(MessageResponse.of("로그아웃에 성공했습니다.")));
    }

    @AuthControllerSwagger.checkNicknameApi
    @GetMapping("/check-nickname")
    public ResponseEntity<CustomApiResponse<CheckNicknameResponseDTO>> checkNickname(@RequestParam
                                                                                         @NotBlank(message = "닉네임을 입력하지 않았습니다.")
                                                                                         @Size(max = 12, message = "닉네임은 12자 이하로 입력해야 합니다.")
                                                                                         String nickname) {
        CheckNicknameResponseDTO response = authService.checkNickname(nickname);

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @AuthControllerSwagger.refreshApi
    @PostMapping("/refresh")
    public ResponseEntity<CustomApiResponse<?>> refresh(HttpServletRequest request) {
        String refreshToken = getRefreshTokenFromCookie(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.JWT_REQUIRED));

        RefreshResponseDTO response = authService.refresh(refreshToken);

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    private Optional<String> getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return Optional.empty();

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}