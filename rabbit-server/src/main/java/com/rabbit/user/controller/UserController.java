package com.rabbit.user.controller;

import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.user.controller.swagger.UserControllerSwagger;
import com.rabbit.user.domain.dto.response.*;
import com.rabbit.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @UserControllerSwagger.getProfileInfoApi
    @GetMapping("/me/profile")
    public ResponseEntity<CustomApiResponse<ProfileInfoResponseDTO>> getProfileInfo(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        ProfileInfoResponseDTO response = userService.getProfileInfo(Integer.parseInt(userId));

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @UserControllerSwagger.searchUserByEmailApi
    @GetMapping
    public ResponseEntity<CustomApiResponse<SearchUserResponseDTO>> searchUserByEmail(@RequestParam
                                                                      @NotBlank(message = "이메일을 입력하지 않았습니다.")
                                                                      String searchEmail) {
        SearchUserResponseDTO response = userService.searchUserByEmail(searchEmail);

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }
}
