package com.rabbit.auth.controller.swagger;

import com.rabbit.global.response.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * AuthController의 Swagger 문서화를 위한 인터페이스
 * 이 인터페이스는 AuthController의 API 메서드에 대한 Swagger 문서화 정보만 포함합니다.
 */
@Tag(name = "Auth", description = "계정 API - 회원 인증 및 회원 가입을 수행합니다")
public interface AuthControllerSwagger {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Nonce 발급",
            description = "메타마스크 지갑 주소를 전달하면, 해당 주소에 대한 서명 검증용 1회성 난수(Nonce)를 발급합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Nonce 생성 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Nonce 생성 성공",
                                                    summary = "난수 생성 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n  \"nonce\": \"3f9zq1x7k2s98f2mv5\"\n  },\n  \"error\": null\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "Nonce 생성 실패",
                                                    summary = "회원이 존재하지 않아 난수 생성 실패",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": null,\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 - 유효하지 않은 지갑 주소",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "잘못된 요청 예시 1",
                                                    summary = "지갑 주소 입력 누락",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"지갑 주소를 입력하지 않았습니다.\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "잘못된 요청 예시 2",
                                                    summary = "잘못된 형식의 지갑 주소",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"지갑 주소 형식이 올바르지 않습니다.\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface nonceApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "로그인",
            description = "지갑 주소, 서명, 난수를 전달받아 서명 검증을 수행하고 accessToken, refreshToken을 발급합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그인 요청",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "로그인 성공",
                                                    summary = "로그인 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n  \"username\": \"김싸피\",\n  \"nickname\": \"열정두배\",\n  \"accessToken\": \"aido3owfid...\"\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 - 유효하지 않은 지갑 주소",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "잘못된 요청 예시 1",
                                                    summary = "서명 입력 누락",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"서명을 입력하지 않았습니다.\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "서명 검증 실패",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "서명 검증 실패",
                                                    summary = "서명 검증 실패",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 403,\n    \"message\": \"서명이 일치하지 않습니다.\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface loginApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "회원가입",
            description = "이메일, 이름, 닉네임, 은행 정보, 메타마스크 지갑 주소를 포함한 회원가입을 수행합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "회원가입 요청",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "로그인 성공",
                                                    summary = "로그인 성공",
                                                    value = """
                                                            {
                                                              "status": "SUCCESS",
                                                              "data": {
                                                                "message": "회원가입에 성공했습니다."
                                                              },
                                                              "error": null
                                                            }"""
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 - 유효하지 않은 데이터",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "잘못된 요청 예시",
                                                    summary = "이메일 형식 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"이메일을 입력하지 않았습니다.\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "잘못된 요청 - 중복 데이터 존재",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "이메일 중복",
                                                    summary = "이메일 중복",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 409,\n    \"message\": \"이미 등록된 이메일입니다.\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface signupApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Access Token 갱신",
            description = "Refresh Token을 통해 Access Token을 갱신합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "AccessToken 갱신",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Access Token 갱신 성공",
                                                    summary = "Access Token 갱신 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n  \"username\": \"김싸피\",\n  \"nickname\": \"열정두배\",\n  \"accessToken\": \"aido3owfid...\"\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "잘못된 요청 - 존재하지 않는 데이터",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "잘못된 요청 예시 1",
                                                    summary = "만료된 토큰",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"토큰이 만료되었습니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "잘못된 요청 예시 2",
                                                    summary = "토큰이 존재하지 않음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"로그인 후 이용해주세요\"\n  }\n}"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "잘못된 요청 - 유효하지 않은 토큰",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "잘못된 요청 예시",
                                                    summary = "유효하지 않은 토큰",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 403,\n    \"message\": \"유효하지 않은 토큰입니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface refreshApi {
    }
}
