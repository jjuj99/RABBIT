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
 * BankAuthController의 Swagger 문서화를 위한 인터페이스
 * 이 인터페이스는 BankAuthController의 API 메서드에 대한 Swagger 문서화 정보만 포함합니다.
 */
@Tag(name = "Bank Auth", description = "계좌 검증 관련 API - 1원 인증을 수행합니다")
public interface BankAuthControllerSwagger {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "은행 리스트 정보",
            description = "은행 리스트를 가져옵니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "전송 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "리스트 조회 성공",
                                                    summary = "리스트 조회 성공",
                                                    value = """
                                                            {
                                                              "status": "SUCCESS",
                                                              "data": [
                                                                {
                                                                  "bankId": 1,
                                                                  "bankName": "국민은행"
                                                                },
                                                                {
                                                                  "bankId": 2,
                                                                  "bankName": "신한은행"
                                                                }
                                                              ],
                                                              "error": null
                                                            }
                                                            """
                                            )
                                    }
                            )
                    )
            }
    )
    @interface getAllBanksApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "계좌 인증번호 전송",
            description = "사용자의 이메일을 기반으로 싸피 은행 계좌를 생성하고 해당 계좌로 인증번호를 전송합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "전송 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "인증 번호 전송 성공",
                                                    summary = "인증 번호 전송 성공",
                                                    value = "{ \"status\": \"SUCCESS\", \"data\": { \"message\": \"해당 계좌로 인증 번호를 전송했습니다.\" }, \"error\": null }"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "API 호출 중 서버 오류 발생",
                                                    summary = "API 호출 중 서버 오류 발생",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 500,\n    \"message\": \"외부 API 호출 중 오류가 발생했습니다.\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface accountAuthSendApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "계좌 인증번호 검증",
            description = "전송받은 인증번호를 입력하여 계좌 인증을 검증합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "검증 결과 반환",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "인증 성공",
                                                    summary = "인증 성공",
                                                    value = "{ \"status\": \"SUCCESS\", \"data\": { \"isVerified\": true, \"message\": \"계좌 인증에 성공했습니다\" }, \"error\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "인증 실패",
                                                    summary = "인증 실패",
                                                    value = "{ \"status\": \"SUCCESS\", \"data\": { \"isVerified\": false, \"message\": \"계좌 인증에 실패했습니다\" }, \"error\": null }"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 - 유효성 검사 실패",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "인증번호 누락",
                                                    summary = "authCode가 비어있을 때",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"인증 번호를 입력하지 않았습니다.\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "API 호출 중 서버 오류 발생",
                                                    summary = "API 호출 중 서버 오류 발생",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 500,\n    \"message\": \"외부 API 호출 중 오류가 발생했습니다.\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface accountAuthVerifyApi {
    }

}
