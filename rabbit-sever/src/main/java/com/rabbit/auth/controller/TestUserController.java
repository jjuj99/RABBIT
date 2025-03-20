package com.rabbit.auth.controller;

import com.rabbit.auth.domain.entity.TestUser;
import com.rabbit.auth.domain.response.TestUserResponseDTO;
import com.rabbit.auth.service.TestUserService;
import com.rabbit.global.response.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "TestUser", description = "JPA 연결 확인 관련 API")
@RestController
@RequestMapping("/api/v1/test/users")
@RequiredArgsConstructor
@Slf4j
public class TestUserController {

    private final TestUserService testUserService;

    @GetMapping("/{id}")
    @Operation(summary = "특정 테스트 사용자 조회", description = "ID로 특정 테스트 사용자를 조회합니다.")
    public ResponseEntity<CustomApiResponse<TestUserResponseDTO>> getUserById(@PathVariable("id") int id) {
        log.debug("[테스트 사용자 조회 요청] id: {}", id);

        TestUser user = testUserService.getUserById(id);

        TestUserResponseDTO responseDTO = TestUserResponseDTO.builder()
                .id(user.getTestUserId())
                .name(user.getName())
                .build();

        return ResponseEntity.ok(CustomApiResponse.success(responseDTO));
    }

    @GetMapping
    @Operation(summary = "모든 테스트 사용자 조회", description = "데이터베이스에 저장된 모든 테스트 사용자를 조회합니다.")
    public ResponseEntity<CustomApiResponse<List<TestUserResponseDTO>>> getAllUsers() {
        log.debug("[테스트 사용자 전체 조회 요청]");

        List<TestUserResponseDTO> users = testUserService.getAllUsers()
                .stream()
                .map(user -> TestUserResponseDTO.builder()
                        .id(user.getTestUserId())
                        .name(user.getName())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(CustomApiResponse.success(users));
    }
}
