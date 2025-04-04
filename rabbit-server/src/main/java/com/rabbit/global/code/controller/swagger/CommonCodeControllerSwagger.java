package com.rabbit.global.code.controller.swagger;

import com.rabbit.global.code.domain.dto.request.CommonCodeRequestDTO;
import com.rabbit.global.code.domain.dto.request.CommonCodeUpdateRequestDTO;
import com.rabbit.global.code.domain.dto.response.CommonCodeResponseDTO;
import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.global.response.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Tag(name = "CommonCode", description = "공통 코드 관리 API - 시스템 공통 코드와 일반 공통 코드 관리")
public interface CommonCodeControllerSwagger {
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "코드 타입별 코드 목록 조회",
            description = "코드 타입에 따라 시스템 공통 코드(enum 기반, 읽기 전용) 또는 일반 공통 코드(DB 기반, 수정 가능)를 반환합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            parameters = {
                    @Parameter(name = "lang", description = "언어 코드 (ko, en, jp)", in = ParameterIn.QUERY,
                            schema = @Schema(type = "string", defaultValue = "ko", allowableValues = {"ko", "en", "jp"}))
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "코드 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "경매 상태 코드 목록",
                                                    summary = "AUCTION_STATUS 코드 타입 조회 결과",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": [\n    {\n      \"codeType\": \"AUCTION_STATUS\",\n      \"code\": \"ING\",\n      \"codeName\": \"진행중\",\n      \"description\": \"경매가 활성화되어 입찰을 받는 중\",\n      \"displayOrder\": 1,\n      \"activeFlag\": true\n    },\n    {\n      \"codeType\": \"AUCTION_STATUS\",\n      \"code\": \"COMPLETED\",\n      \"codeName\": \"완료\",\n      \"description\": \"경매가 종료되어 낙찰이 완료됨\",\n      \"displayOrder\": 2,\n      \"activeFlag\": true\n    },\n    {\n      \"codeType\": \"AUCTION_STATUS\",\n      \"code\": \"FAILED\",\n      \"codeName\": \"실패\",\n      \"description\": \"경매가 실패하여 종료됨\",\n      \"displayOrder\": 3,\n      \"activeFlag\": true\n    },\n    {\n      \"codeType\": \"AUCTION_STATUS\",\n      \"code\": \"CANCELED\",\n      \"codeName\": \"취소\",\n      \"description\": \"경매가 취소됨\",\n      \"displayOrder\": 4,\n      \"activeFlag\": true\n    }\n  ],\n  \"error\": null\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "입찰 상태 코드 목록",
                                                    summary = "BID_STATUS 코드 타입 조회 결과",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": [\n    {\n      \"codeType\": \"BID_STATUS\",\n      \"code\": \"PENDING\",\n      \"codeName\": \"대기중\",\n      \"description\": \"경매가 진행중인 입찰\",\n      \"displayOrder\": 1,\n      \"activeFlag\": true\n    },\n    {\n      \"codeType\": \"BID_STATUS\",\n      \"code\": \"WON\",\n      \"codeName\": \"낙찰\",\n      \"description\": \"경매에서 낙찰됨\",\n      \"displayOrder\": 2,\n      \"activeFlag\": true\n    },\n    {\n      \"codeType\": \"BID_STATUS\",\n      \"code\": \"LOST\",\n      \"codeName\": \"유찰\",\n      \"description\": \"경매에서 유찰됨\",\n      \"displayOrder\": 3,\n      \"activeFlag\": true\n    }\n  ],\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 코드 타입",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 응답 - 유효하지 않은 코드 타입",
                                                    summary = "코드 타입이 유효하지 않을 때",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"유효하지 않은 코드 타입입니다: INVALID_TYPE\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 응답 - 인증 실패",
                                                    summary = "인증 토큰이 유효하지 않을 때",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"유효하지 않은 인증입니다. 다시 로그인해주세요.\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface GetCodesByTypeApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "활성화된 코드 목록 조회",
            description = "지정된 코드 타입의 활성화된 코드만 조회합니다. 시스템 공통 코드는 항상 활성화 상태로 간주됩니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            parameters = {
                    @Parameter(name = "codeType", description = "코드 타입", required = true, in = ParameterIn.PATH,
                            schema = @Schema(type = "string", example = "AUCTION_STATUS",
                                    allowableValues = {"AUCTION_STATUS", "BID_STATUS", "EMAIL_LOG_STATUS", "PROMISSORY_NOTE_STATUS", "COIN_LOG_TYPE", "CONTRACT_STATUS", "REPAYMENT_TYPE", "NOTIFICATION_TYPE", "NOTIFICATION_RELATED_TYPE"}))
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "활성화된 코드 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 응답",
                                                    summary = "활성화된 코드 목록",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": [\n    {\n      \"codeType\": \"AUCTION_STATUS\",\n      \"code\": \"ING\",\n      \"codeName\": \"진행중\",\n      \"description\": \"경매가 활성화되어 입찰을 받는 중\",\n      \"displayOrder\": 1,\n      \"activeFlag\": true\n    },\n    {\n      \"codeType\": \"AUCTION_STATUS\",\n      \"code\": \"COMPLETED\",\n      \"codeName\": \"완료\",\n      \"description\": \"경매가 종료되어 낙찰이 완료됨\",\n      \"displayOrder\": 2,\n      \"activeFlag\": true\n    }\n  ],\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 코드 타입",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 응답",
                                                    summary = "유효하지 않은 코드 타입",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"유효하지 않은 코드 타입입니다: INVALID_TYPE\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 응답 - 인증 실패",
                                                    summary = "인증 토큰이 유효하지 않을 때",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"유효하지 않은 인증입니다. 다시 로그인해주세요.\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface GetActiveFlagCodesByTypeApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "단일 코드 조회",
            description = "코드 타입과 코드 값으로 단일 코드를 조회합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            parameters = {
                    @Parameter(name = "codeType", description = "코드 타입", required = true, in = ParameterIn.PATH,
                            schema = @Schema(type = "string", example = "AUCTION_STATUS",
                                    allowableValues = {"AUCTION_STATUS", "BID_STATUS", "EMAIL_LOG_STATUS", "PROMISSORY_NOTE_STATUS", "COIN_LOG_TYPE", "CONTRACT_STATUS", "REPAYMENT_TYPE", "NOTIFICATION_TYPE", "NOTIFICATION_RELATED_TYPE"})),
                    @Parameter(name = "code", description = "코드 값", required = true, in = ParameterIn.PATH,
                            schema = @Schema(type = "string", example = "ING"))
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "코드 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 응답",
                                                    summary = "단일 코드 조회 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"codeType\": \"AUCTION_STATUS\",\n    \"code\": \"ING\",\n    \"codeName\": \"진행중\",\n    \"description\": \"경매가 활성화되어 입찰을 받는 중\",\n    \"displayOrder\": 1,\n    \"activeFlag\": true\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 응답 - 인증 실패",
                                                    summary = "인증 토큰이 유효하지 않을 때",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"유효하지 않은 인증입니다. 다시 로그인해주세요.\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "코드를 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 응답",
                                                    summary = "코드를 찾을 수 없음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"코드를 찾을 수 없습니다: 타입=AUCTION_STATUS, 코드=INVALID_CODE\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface GetCodeApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "모든 코드 타입 조회",
            description = "시스템에 등록된 모든 코드 타입 목록을 조회합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "코드 타입 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 응답",
                                                    summary = "코드 타입 목록",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": [\n    \"AUCTION_STATUS\",\n    \"BID_STATUS\",\n    \"EMAIL_LOG_STATUS\",\n    \"PROMISSORY_NOTE_STATUS\",\n    \"COIN_LOG_TYPE\"\n  ],\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 응답 - 인증 실패",
                                                    summary = "인증 토큰이 유효하지 않을 때",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"유효하지 않은 인증입니다. 다시 로그인해주세요.\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface GetCodeTypesApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "일반 공통 코드 생성",
            description = "일반 공통 코드를 생성합니다. 코드 타입과 코드 값 조합은 고유해야 합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            deprecated = true,
            requestBody = @RequestBody(
                    description = "생성할 코드 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonCodeRequestDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "코드 생성 요청",
                                            summary = "정상 요청 예시",
                                            value = "{\n  \"codeType\": \"CUSTOM_CODE\",\n  \"code\": \"NEW_CODE\",\n  \"codeName\": \"새 코드\",\n  \"description\": \"사용자 정의 코드\",\n  \"displayOrder\": 1,\n  \"activeFlag\": true\n}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "코드 생성 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 응답",
                                                    summary = "코드 생성 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"codeId\": 10,\n    \"codeType\": \"CUSTOM_CODE\",\n    \"code\": \"NEW_CODE\",\n    \"codeName\": \"새 코드\",\n    \"description\": \"사용자 정의 코드\",\n    \"displayOrder\": 1,\n    \"activeFlag\": true,\n    \"createdAt\": \"2025-03-30 12:34:56\",\n    \"updatedAt\": \"2025-03-30 12:34:56\"\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "유효하지 않은 코드 타입 또는 값",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 응답 - 시스템 코드 타입 사용",
                                                    summary = "시스템 코드 타입으로 생성 시도",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"시스템 공통 코드는 API를 통해 생성할 수 없습니다. StatusCode enum에 정의해주세요.\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "에러 응답 - 유효성 검사 실패",
                                                    summary = "필수 필드 누락",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"코드 타입은 필수입니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 응답 - 인증 실패",
                                                    summary = "인증 토큰이 유효하지 않을 때",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"유효하지 않은 인증입니다. 다시 로그인해주세요.\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "이미 존재하는 코드",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 응답 - 코드 중복",
                                                    summary = "이미 존재하는 코드",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 409,\n    \"message\": \"이미 존재하는 코드입니다: 타입=CUSTOM_CODE, 코드=NEW_CODE\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface CreateCodeApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "일반 공통 코드 수정",
            description = "일반 공통 코드를 수정합니다. 변경하려는 필드만 전송할 수 있습니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            deprecated = true,
            requestBody = @RequestBody(
                    description = "수정할 코드 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonCodeUpdateRequestDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "전체 수정 요청",
                                            summary = "모든 필드 변경",
                                            value = "{\n  \"codeName\": \"수정된 코드명\",\n  \"description\": \"수정된 설명\",\n  \"displayOrder\": 2,\n  \"activeFlag\": false\n}"
                                    ),
                                    @ExampleObject(
                                            name = "부분 수정 요청",
                                            summary = "코드명만 변경",
                                            value = "{\n  \"codeName\": \"수정된 코드명\"\n}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "코드 수정 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 응답",
                                                    summary = "코드 수정 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"codeId\": 10,\n    \"codeType\": \"CUSTOM_CODE\",\n    \"code\": \"EXISTING_CODE\",\n    \"codeName\": \"수정된 코드명\",\n    \"description\": \"수정된 설명\",\n    \"displayOrder\": 2,\n    \"activeFlag\": false,\n    \"createdAt\": \"2025-03-30 12:34:56\",\n    \"updatedAt\": \"2025-03-30 13:45:12\"\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "유효하지 않은 코드 타입 또는 값",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 응답 - 시스템 코드 수정 시도",
                                                    summary = "시스템 코드 수정 시도",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"시스템 공통 코드는 API를 통해 업데이트할 수 없습니다. StatusCode enum에 정의해주세요.\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "에러 응답 - 유효성 검사 실패",
                                                    summary = "표시 순서 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"표시 순서는 0 이상이어야 합니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 응답 - 인증 실패",
                                                    summary = "인증 토큰이 유효하지 않을 때",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"유효하지 않은 인증입니다. 다시 로그인해주세요.\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "코드를 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 응답 - 코드 없음",
                                                    summary = "존재하지 않는 코드",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"코드를 찾을 수 없습니다: 타입=CUSTOM_CODE, 코드=UNKNOWN_CODE\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface UpdateCodeApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "일반 공통 코드 삭제",
            description = "일반 공통 코드를 삭제합니다. 삭제된 코드는 복구할 수 없습니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            deprecated = true,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "코드 삭제 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 응답",
                                                    summary = "코드 삭제 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"message\": \"코드가 삭제되었습니다: 타입=CUSTOM_CODE, 코드=EXISTING_CODE\"\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "유효하지 않은 코드 타입",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 응답 - 시스템 코드 삭제 시도",
                                                    summary = "시스템 코드 삭제 시도",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"시스템 공통 코드는 API를 통해 삭제할 수 없습니다. StatusCode enum에서 관리됩니다.\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 응답 - 인증 실패",
                                                    summary = "인증 토큰이 유효하지 않을 때",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"유효하지 않은 인증입니다. 다시 로그인해주세요.\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "코드를 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 응답 - 코드 없음",
                                                    summary = "존재하지 않는 코드",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"코드를 찾을 수 없습니다: 타입=CUSTOM_CODE, 코드=UNKNOWN_CODE\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface DeleteCodeApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "코드 캐시 새로고침",
            description = "지정된 코드 타입의 캐시를 새로고침합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            deprecated = true,
            parameters = {
                    @Parameter(name = "codeType", description = "코드 타입", required = true, in = ParameterIn.PATH,
                            schema = @Schema(type = "string", example = "CUSTOM_CODE"))
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "캐시 새로고침 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 응답",
                                                    summary = "캐시 새로고침 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"message\": \"코드 타입 [CUSTOM_CODE]의 캐시가 새로고침되었습니다\"\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 응답 - 인증 실패",
                                                    summary = "인증 토큰이 유효하지 않을 때",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"유효하지 않은 인증입니다. 다시 로그인해주세요.\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 코드 타입",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 응답 - 유효하지 않은 코드 타입",
                                                    summary = "코드 타입이 유효하지 않을 때",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"유효하지 않은 코드 타입입니다: INVALID_TYPE\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface RefreshCacheApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "모든 코드 캐시 새로고침",
            description = "모든 코드 타입의 캐시를 새로고침합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            deprecated = true,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "모든 캐시 새로고침 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 응답",
                                                    summary = "모든 캐시 새로고침 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"message\": \"모든 공통 코드 캐시가 새로고침되었습니다\"\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 응답 - 인증 실패",
                                                    summary = "인증 토큰이 유효하지 않을 때",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"유효하지 않은 인증입니다. 다시 로그인해주세요.\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface RefreshAllCachesApi {
    }

    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Parameter(
            name = "codeType",
            description = "조회할 코드 타입 (예: AUCTION_STATUS, BID_STATUS, EMAIL_LOG_STATUS)",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "string", example = "AUCTION_STATUS",
                    allowableValues = {"AUCTION_STATUS", "BID_STATUS", "EMAIL_LOG_STATUS", "PROMISSORY_NOTE_STATUS", "COIN_LOG_TYPE", "CONTRACT_STATUS", "REPAYMENT_TYPE", "NOTIFICATION_TYPE", "NOTIFICATION_RELATED_TYPE"})
    )
    @interface CodeTypeParam {
    }

    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Parameter(
            name = "code",
            description = "조회할 코드 값 (예: ING, COMPLETED, FAILED)",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "string", example = "ING")
    )
    @interface CodeParam {
    }
}