package com.rabbit.example.controller.swagger;

import com.rabbit.example.domain.dto.request.ExampleRequestDTO;
import com.rabbit.example.domain.dto.response.ExampleResponseDTO;
import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.global.response.MessageResponse;
import com.rabbit.global.response.PageResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
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
 * ExampleController의 Swagger 문서화를 위한 인터페이스
 * 이 인터페이스는 ExampleController의 API 메서드에 대한 Swagger 문서화 정보만 포함합니다.
 */
@Tag(name = "Example", description = "예시 API - 예시 데이터의 CRUD 작업을 수행합니다 - 테스트")
public interface ExampleControllerSwagger {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "예시 항목 생성",
            description = "새로운 예시 항목을 생성합니다. 생성된 항목의 ID가 응답에 포함됩니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "생성할 예시 항목 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExampleRequestDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "기본 생성 요청",
                                            summary = "모든 필드가 포함된 요청",
                                            value = "{\n  \"title\": \"새 예시 제목\",\n  \"content\": \"새 예시 내용입니다.\",\n  \"categoryCode\": \"CATEGORY_A\",\n  \"isActive\": true\n}"
                                    ),
                                    @ExampleObject(
                                            name = "최소 생성 요청",
                                            summary = "필수 필드만 포함한 요청",
                                            value = "{\n  \"title\": \"새 예시 제목\"\n}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "생성 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 응답",
                                                    summary = "항목 생성 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"id\": 5,\n    \"title\": \"새 예시 제목\",\n    \"content\": \"새 예시 내용입니다\",\n    \"categoryCode\": \"CATEGORY_A\",\n    \"isActive\": true,\n    \"createdAt\": \"2023-01-01T12:00:00\",\n    \"updatedAt\": \"2023-01-01T12:00:00\"\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            ),
                            headers = {
                                    @Header(name = "Location", description = "생성된 리소스의 URI", schema = @Schema(type = "string"))
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 필수 필드 누락",
                                                    summary = "제목 누락",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"제목은 필수 입력값입니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "실패 응답 - 유효성 검사 실패",
                                                    summary = "제목 길이 초과",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"제목은 2~100자 사이여야 합니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 인증 실패",
                                                    summary = "인증 토큰 없음 또는 만료",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"인증이 필요합니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface InsertExampleApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "예시 항목 조회",
            description = "ID를 기준으로 예시 항목을 조회합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 응답",
                                                    summary = "항목 조회 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"id\": 1,\n    \"title\": \"예시 제목입니다\",\n    \"content\": \"예시 내용입니다\",\n    \"categoryCode\": \"CATEGORY_A\",\n    \"isActive\": true,\n    \"createdAt\": \"2023-01-01T12:00:00\",\n    \"updatedAt\": \"2023-01-02T12:00:00\"\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 잘못된 파라미터",
                                                    summary = "ID가 유효하지 않음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"올바르지 않은 타입입니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "예시 항목을 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 존재하지 않는 항목",
                                                    summary = "리소스를 찾을 수 없음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"존재하지 않는 예시 항목입니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface SelectExampleApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "예시 항목 목록 조회",
            description = "예시 항목의 전체 목록을 페이지네이션하여 조회합니다. 검색 조건 및 정렬 기준을 지정할 수 있습니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            parameters = {
                    // 페이지네이션 파라미터
                    @Parameter(name = "pageNo", description = "페이지 번호 (0부터 시작)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
                    @Parameter(name = "pageSize", description = "페이지당 항목 수", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10")),
                    @Parameter(name = "sortBy", description = "정렬 필드", in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "createdAt")),
                    @Parameter(name = "sortDirection", description = "정렬 방향 (ASC, DESC)", in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "DESC")),

                    // 검색 조건 파라미터 - 점 표기법으로 중첩 객체 표현
                    @Parameter(name = "searchCondition.keyword", description = "검색 키워드 (제목, 내용에서 검색)", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
                    @Parameter(name = "searchCondition.categoryCode", description = "카테고리 코드 필터", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
                    @Parameter(name = "searchCondition.isActive", description = "활성화 상태 필터", in = ParameterIn.QUERY, schema = @Schema(type = "boolean")),
                    @Parameter(name = "searchCondition.startDate", description = "등록일 시작 범위 (yyyy-MM-dd)", in = ParameterIn.QUERY, schema = @Schema(type = "string", format = "date")),
                    @Parameter(name = "searchCondition.endDate", description = "등록일 종료 범위 (yyyy-MM-dd)", in = ParameterIn.QUERY, schema = @Schema(type = "string", format = "date"))
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class, subTypes = {PageResponseDTO.class}),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 응답",
                                                    summary = "페이지네이션된 결과",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"content\": [\n      {\n        \"id\": 1,\n        \"title\": \"예시 제목 1\",\n        \"content\": \"예시 내용입니다\",\n        \"categoryCode\": \"CATEGORY_A\",\n        \"isActive\": true,\n        \"createdAt\": \"2023-01-01T12:00:00\",\n        \"updatedAt\": \"2023-01-02T12:00:00\"\n      },\n      {\n        \"id\": 2,\n        \"title\": \"예시 제목 2\",\n        \"content\": \"예시 내용입니다\",\n        \"categoryCode\": \"CATEGORY_B\",\n        \"isActive\": true,\n        \"createdAt\": \"2023-01-03T12:00:00\",\n        \"updatedAt\": \"2023-01-04T12:00:00\"\n      }\n    ],\n    \"pageNo\": 0,\n    \"pageSize\": 10,\n    \"totalElements\": 50,\n    \"totalPages\": 5,\n    \"first\": true,\n    \"last\": false,\n    \"empty\": false\n  },\n  \"error\": null\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "빈 응답",
                                                    summary = "검색 결과 없음",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"content\": [],\n    \"pageNo\": 0,\n    \"pageSize\": 10,\n    \"totalElements\": 0,\n    \"totalPages\": 0,\n    \"first\": true,\n    \"last\": true,\n    \"empty\": true\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 유효하지 않은 페이지 파라미터",
                                                    summary = "페이지 번호 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"페이지 번호는 0 이상이어야 합니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "실패 응답 - 날짜 형식 오류",
                                                    summary = "날짜 형식이 잘못됨",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"날짜 형식이 올바르지 않습니다. 'yyyy-MM-dd' 형식으로 입력해주세요.\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface SelectExampleListApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "예시 항목 수정",
            description = "ID를 기준으로 예시 항목을 수정합니다. 요청 본문에 수정할 내용을 포함해야 합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 예시 항목 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExampleRequestDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "기본 수정 요청",
                                            summary = "모든 필드 수정",
                                            value = "{\n  \"title\": \"수정된 예시 제목\",\n  \"content\": \"수정된 예시 내용입니다.\",\n  \"categoryCode\": \"CATEGORY_B\",\n  \"isActive\": true\n}"
                                    ),
                                    @ExampleObject(
                                            name = "부분 수정 요청",
                                            summary = "일부 필드만 수정",
                                            value = "{\n  \"title\": \"수정된 제목\",\n  \"isActive\": false\n}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "수정 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 응답",
                                                    summary = "항목이 성공적으로 수정됨",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"message\": \"수정 성공\",\n    \"seq\": null,\n    \"value\": null\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 잘못된 요청",
                                                    summary = "필수 파라미터 누락",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"필수 파라미터가 누락되었습니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "실패 응답 - 유효성 검사 실패",
                                                    summary = "제목 길이 제한 초과",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"제목은 2~100자 사이여야 합니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "예시 항목을 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 존재하지 않는 항목",
                                                    summary = "리소스를 찾을 수 없음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"존재하지 않는 예시 항목입니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface UpdateExampleApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "예시 항목 삭제",
            description = "ID를 기준으로 예시 항목을 삭제합니다. 삭제된 항목은 복구할 수 없습니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "삭제 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 응답",
                                                    summary = "항목이 성공적으로 삭제됨",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"message\": \"삭제 성공\",\n    \"seq\": null,\n    \"value\": null\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 잘못된 요청",
                                                    summary = "유효하지 않은 ID 형식",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"올바르지 않은 타입입니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "예시 항목을 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 존재하지 않는 항목",
                                                    summary = "리소스를 찾을 수 없음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"존재하지 않는 예시 항목입니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface RemoveExampleApi {
    }

    /**
     * 예시 항목 ID 경로 변수에 대한 파라미터 정보
     */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Parameter(name = "example-id", description = "예시 ID", required = true, in = ParameterIn.PATH, schema = @Schema(type = "integer", format = "int64", minimum = "1"))
    @interface ExampleIdParam {
    }

    /**
     * 페이지 번호 쿼리 파라미터에 대한 정보
     */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Parameter(name = "pageNo", description = "페이지 번호 (0부터 시작)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0", minimum = "0"))
    @interface PageNoParam {
    }

    /**
     * 페이지 크기 쿼리 파라미터에 대한 정보
     */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Parameter(name = "pageSize", description = "페이지 크기", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10", minimum = "1", maximum = "100"))
    @interface PageSizeParam {
    }

    /**
     * 정렬 기준 쿼리 파라미터에 대한 정보
     */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Parameter(name = "sortBy", description = "정렬 기준 필드명", in = ParameterIn.QUERY, schema = @Schema(type = "string", allowableValues = {"id", "title", "createdAt", "updatedAt"}))
    @interface SortByParam {
    }

    /**
     * 정렬 방향 쿼리 파라미터에 대한 정보
     */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Parameter(name = "sortDirection", description = "정렬 방향 (ASC 또는 DESC)", in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "DESC", allowableValues = {"ASC", "DESC"}))
    @interface SortDirectionParam {
    }

    /**
     * 요청 바디 파라미터에 대한 정보
     */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Parameter(description = "예시 정보", required = true)
    @interface RequestBodyParam {
    }
}