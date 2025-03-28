package com.rabbit.user.controller.swagger;

import com.rabbit.global.response.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * UserController의 Swagger 문서화를 위한 인터페이스
 * 이 인터페이스는 UserController의 API 메서드에 대한 Swagger 문서화 정보만 포함합니다.
 */
@Tag(name = "User", description = "유저 API - 유저 관련 기능을 수행합니다")
public interface UserControllerSwagger {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "로그인 정보 조회",
            description = "토큰을 통해 로그인 한 유저의 이름, 닉네임 정보를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그인 정보 조회",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "로그인 유저 정보 조회",
                                                    summary = "로그인 유저 정보 조회",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n  \"userName\": \"김싸피\",\n  \"nickname\": \"열정두배\"\n  },\n  \"error\": null\n}"
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
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "잘못된 요청 - 존재하지 않는 데이터",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "잘못된 요청 예시",
                                                    summary = "존재하지 않는 회원",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"존재하지 않는 회원입니다.\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface getLoginInfoApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "이메일로 사용자 검색",
            description = "파라미터를 통해 사용자를 이메일로 검색합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "유저 검색 결과",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "유저 검색 성공",
                                                    summary = "유저 검색 성공",
                                                    value = """
                                                            {
                                                              "status": "SUCCESS",
                                                              "data": {
                                                                "userId": 1,
                                                                "email": "ssafy@ssafy.com",
                                                                "name": "김싸피",
                                                                "nickname": "열정두배"
                                                              },
                                                              "error": null
                                                            }"""
                                            ),
                                            @ExampleObject(
                                                    name = "유저 검색 실패",
                                                    summary = "유저 검색 실패",
                                                    value = """
                                                            {
                                                              "status": "SUCCESS",
                                                              "data": null,
                                                              "error": null
                                                            }"""
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 - 파라미터 누락",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "검색 이메일 누락",
                                                    summary = "검색 이메일 누락",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"이메일을 입력하지 않았습니다.\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface searchUserByEmailApi {
    }
}
