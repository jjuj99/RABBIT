package com.rabbit.contract.controller.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.rabbit.contract.domain.dto.request.ContractRejectRequestDTO;
import com.rabbit.contract.domain.dto.request.ContractRequestDTO;
import com.rabbit.contract.domain.dto.request.ContractSearchRequestDTO;
import com.rabbit.global.response.CustomApiResponse;

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

/**
 * ContractController의 Swagger 문서화를 위한 인터페이스
 */
@Tag(name = "Contract", description = "차용증 계약 API - 차용증 계약의 CRUD 작업을 수행합니다")
public interface ContractControllerSwagger {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "계약 설정 정보 조회",
            description = "차용증 계약 작성 시 필요한 설정 정보를 조회합니다 (법정 최고 이자율, 기본값 등)",
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
                                                    summary = "계약 설정 정보 조회 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"maxLegalIr\": 20.0,\n    \"defDir\": 15.0,\n    \"defDefCnt\": 3,\n    \"defEarlypayFee\": 2.0,\n    \"defGraceDays\": 7,\n    \"minLa\": 10000,\n    \"maxLa\": 10000000,\n    \"minLtDays\": 30,\n    \"maxLtDays\": 365,\n    \"defAddTerms\": \"본 계약은 다음과 같은 조건으로 체결됩니다...\"\n  },\n  \"error\": null\n}"
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
                                                    name = "인증 오류",
                                                    summary = "인증 정보 없음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"인증이 필요합니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface GetContractConfigApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "계약 목록 조회",
            description = "자신과 관련된 차용증 계약 목록을 조회합니다. 요청 파라미터의 searchCondition.type으로 보낸 계약(sent)과 받은 계약(received)을 구분할 수 있습니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            parameters = {
                    @Parameter(name = "searchCondition.type", description = "계약 유형 (sent: 보낸 계약, received: 받은 계약)",
                            in = ParameterIn.QUERY, schema = @Schema(type = "string", allowableValues = {"sent", "received"}, defaultValue = "sent")),
                    @Parameter(name = "pageNumber", description = "페이지 번호 (0부터 시작)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
                    @Parameter(name = "pageSize", description = "페이지 크기", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10")),
                    @Parameter(name = "sortBy", description = "정렬 기준", in = ParameterIn.QUERY,
                            schema = @Schema(type = "string", allowableValues = {"createdAt", "la", "matDt", "ir", "lt"}, defaultValue = "createdAt")),
                    @Parameter(name = "sortDirection", description = "정렬 방향", in = ParameterIn.QUERY,
                            schema = @Schema(type = "string", allowableValues = {"ASC", "DESC"}, defaultValue = "DESC"))
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 응답 - 계약 목록",
                                                    summary = "페이징된 계약 목록",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"content\": [\n      {\n        \"id\": 1,\n        \"la\": 5000000,\n        \"contractDt\": \"2025-04-01 00:00:00\",\n        \"createdAt\": \"2025-04-01 10:30:00\",\n        \"name\": \"관리자\",\n        \"walletAddress\": \"0xdbd1cb333978e1f6774e759d9742e8955cad3f6b\",\n        \"ir\": 5.5,\n        \"lt\": 12,\n        \"matDt\": \"2026-04-01 00:00:00\",\n        \"mpDt\": 15,\n        \"repayType\": \"EPP\",\n        \"repayTypeName\": \"원금균등상환\",\n        \"contractStatus\": \"CONTRACTED\",\n        \"contractStatusName\": \"체결\"\n      }\n    ],\n    \"pageNumber\": 0,\n    \"pageSize\": 2147483647,\n    \"totalElements\": 1,\n    \"totalPages\": 1,\n    \"last\": true,\n    \"hasNext\": false\n  },\n  \"error\": null\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "성공 응답 - 빈 목록",
                                                    summary = "계약이 없는 경우",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"content\": [],\n    \"pageNumber\": 0,\n    \"pageSize\": 2147483647,\n    \"totalElements\": 0,\n    \"totalPages\": 0,\n    \"last\": true,\n    \"hasNext\": false\n  },\n  \"error\": null\n}"
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
                                                    summary = "페이지 번호 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"페이지 번호는 0 이상이어야 합니다\"\n  }\n}"
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
                                                    summary = "인증 토큰 없음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"인증이 필요합니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface GetContractsApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "계약 검색",
            description = "계약을 다양한 조건으로 검색합니다. 페이징과 정렬을 지원합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "검색 조건",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ContractSearchRequestDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "검색 요청 예시",
                                            summary = "계약 검색 요청",
                                            value = "{\n  \"pageNumber\": 0,\n  \"pageSize\": 10,\n  \"sortBy\": \"createdAt\",\n  \"sortDirection\": \"DESC\",\n  \"searchCondition\": {\n    \"type\": \"sent\",\n    \"keyword\": \"홍길동\",\n    \"contractStatus\": \"REQUESTED\",\n    \"startDate\": \"2025-04-01\",\n    \"endDate\": \"2025-04-30\"\n  }\n}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "검색 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "검색 결과",
                                                    summary = "검색 결과 예시",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"content\": [\n      {\n        \"contractId\": 1,\n        \"crId\": 1,\n        \"crName\": \"홍길동\",\n        \"drId\": 2,\n        \"drName\": \"김철수\",\n        \"la\": 1000000,\n        \"ir\": 5.0,\n        \"contractDt\": \"2025-04-10 00:00:00\",\n        \"matDt\": \"2025-10-10 00:00:00\",\n        \"contractStatus\": \"REQUESTED\",\n        \"contractStatusName\": \"요청됨\",\n        \"createdAt\": \"2025-04-01 12:00:00\"\n      }\n    ],\n    \"pageNumber\": 0,\n    \"pageSize\": 10,\n    \"totalElements\": 1,\n    \"totalPages\": 1,\n    \"last\": true,\n    \"hasNext\": false\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class)
                            )
                    )
            }
    )
    @interface SearchContractsApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "계약 상세 정보 조회",
            description = "특정 계약의 상세 정보를 조회합니다. 계약 조항, 상환 금액 계산, 남은 기간 등 상세 정보가 포함됩니다.",
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
                                                    name = "성공 응답 - 계약 상세 정보",
                                                    summary = "계약 상세 정보 조회 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"contractId\": 1,\n    \"crId\": 1,\n    \"crName\": \"홍길동\",\n    \"crWallet\": \"0x123...\",\n    \"drId\": 2,\n    \"drName\": \"김철수\",\n    \"drWallet\": \"0x456...\",\n    \"la\": 1000000,\n    \"ir\": 5.0,\n    \"contractDt\": \"2025-04-10 00:00:00\",\n    \"matDt\": \"2025-10-10 00:00:00\",\n    \"lt\": 12,\n    \"earlypayFee\": 2.0,\n    \"repayType\": \"EPIP\",\n    \"repayTypeName\": \"원금균등상환\",\n    \"mpDt\": 15,\n    \"dir\": 15.0,\n    \"defCnt\": 3,\n    \"earlypay\": true,\n    \"pnTransFlag\": true,\n    \"addTerms\": \"본 계약은 다음 조건으로 체결됩니다...\",\n    \"contractStatus\": \"REQUESTED\",\n    \"contractStatusName\": \"요청됨\",\n    \"createdAt\": \"2025-04-01 12:00:00\",\n    \"updatedAt\": \"2025-04-01 12:00:00\",\n    \"remainingDays\": 120,\n    \"matAmt\": 1025000,\n    \"message\": \"잘 부탁드립니다\",\n    \"rejectMessage\": null,\n    \"message\": null,\n    \"rejectedAt\": null\n  },\n  \"error\": null\n}"
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
                                                    name = "인증 오류",
                                                    summary = "인증 정보 없음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"인증이 필요합니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "접근 권한 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 권한 없음",
                                                    summary = "해당 계약에 접근할 권한이 없음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 403,\n    \"message\": \"해당 계약에 접근할 권한이 없습니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "계약을 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 계약 없음",
                                                    summary = "존재하지 않는 계약 ID",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"존재하지 않는 계약입니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface GetContractDetailApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "계약 생성",
            description = "새로운 차용증 계약을 작성합니다. 계약 작성자는 채권자(Creditor)로 설정되며, 채무자(Debtor) 정보를 지정해야 합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "계약 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ContractRequestDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "계약 생성 요청",
                                            summary = "새 계약 생성 요청 예시",
                                            value = "{\n  \"drPhone\": \"01012345678\",\n  \"drName\": \"김철수\",\n  \"drWallet\": \"0x1234567890abcdef\",\n  \"crEmail\": \"creditor@example.com\",\n  \"crName\": \"홍길동\",\n  \"crWallet\": \"0xabcdef1234567890\",\n  \"la\": 1000000,\n  \"ir\": 5.0,\n  \"lt\": 12,\n  \"repayType\": \"EPIP\",\n  \"mpDt\": 15,\n  \"dir\": 15.0,\n  \"contractDt\": \"2025-04-10 00:00:00\",\n  \"defCnt\": 3,\n  \"pnTransFlag\": true,\n  \"earlypay\": true,\n  \"earlypayFee\": 2.0,\n  \"addTerms\": \"본 계약은 다음 조건으로 체결됩니다...\",\n  \"message\": \"잘 부탁드립니다.\",\n  \"passAuthToken\": \"token123\",\n  \"txId\": \"tx_abc123\",\n  \"authResultCode\": \"SUCCESS\",\n  \"passAuthToken\": \"token123\",\n  \"txId\": \"tx_abc123\",\n  \"authResultCode\": \"SUCCESS\"\n}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "계약 생성 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 응답 - 계약 생성",
                                                    summary = "계약 생성 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"contractId\": 3,\n    \"crId\": 2,\n    \"crName\": \"clapsheep\",\n    \"drId\": 1,\n    \"drName\": \"열정두배\",\n    \"la\": 1000000,\n    \"ir\": 5,\n    \"contractDt\": \"2025-04-10 00:00:00\",\n    \"matDt\": \"2026-04-09 00:00:00\",\n    \"lt\": 12,\n    \"earlypayFee\": 2,\n    \"repayType\": \"EPIP\",\n    \"repayTypeName\": \"원리금균등상환\",\n    \"mpDt\": 15,\n    \"dir\": 15,\n    \"defCnt\": 3,\n    \"earlypay\": true,\n    \"pnTransFlag\": true,\n    \"graceLineDays\": 7,\n    \"addTerms\": \"본 계약은 다음 조건으로 체결됩니다...\",\n    \"contractStatus\": \"REQUESTED\",\n    \"contractStatusName\": \"요청\",\n    \"createdAt\": \"2025-04-04 17:01:34\"\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            ),
                            headers = {
                                    @Header(name = "Location", description = "생성된 계약의 URI", schema = @Schema(type = "string"))
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
                                                    name = "실패 응답 - 유효성 검사 실패",
                                                    summary = "요청 데이터 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"대출 만기일은 미래 날짜여야 합니다\"\n  }\n}"
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
                                                    name = "인증 오류",
                                                    summary = "인증 정보 없음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"인증이 필요합니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "채무자를 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 채무자 없음",
                                                    summary = "존재하지 않는 채무자 정보",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"존재하지 않는 사용자입니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface CreateContractApi {
    }


    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "계약 취소",
            description = "요청됨 상태의 계약을 취소합니다. 채무자만 계약을 취소할 수 있습니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "취소 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 응답 - 계약 취소",
                                                    summary = "계약 취소 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"contractId\": 1,\n    \"crId\": 1,\n    \"crName\": \"홍길동\",\n    \"drId\": 2,\n    \"drName\": \"김철수\",\n    \"la\": 1000000,\n    \"ir\": 5.0,\n    \"contractDt\": \"2025-04-10 00:00:00\",\n    \"matDt\": \"2025-10-10 00:00:00\",\n    \"contractStatus\": \"CANCELED\",\n    \"contractStatusName\": \"취소됨\",\n    \"updatedAt\": \"2025-04-06 09:15:00\",\n    \"passAuthToken\": \"token123\",\n    \"txId\": \"tx_abc123\",\n    \"authResultCode\": \"SUCCESS\",\n    \"passAuthToken\": \"token123\",\n    \"txId\": \"tx_abc123\",\n    \"authResultCode\": \"SUCCESS\"\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 또는 상태 전이 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 상태 전이 오류",
                                                    summary = "허용되지 않는 상태 변경",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"체결된 계약의 상태는 변경할 수 없습니다\"\n  }\n}"
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
                                                    name = "인증 오류",
                                                    summary = "인증 정보 없음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"인증이 필요합니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "권한 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 권한 없음",
                                                    summary = "상태 변경 권한 없음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 403,\n    \"message\": \"채무자만 계약을 취소할 수 있습니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "계약을 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 계약 없음",
                                                    summary = "존재하지 않는 계약 ID",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"존재하지 않는 계약입니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface CancelContractApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "계약 체결 처리",
            description = "요청된 계약을 체결 상태로 변경하고 NFT 생성 및 자금 전송을 처리합니다. 채권자만 계약을 체결할 수 있습니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "계약 체결 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 응답 - 계약 체결",
                                                    summary = "계약 체결 처리 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"contractId\": 1,\n    \"crId\": 1,\n    \"crName\": \"홍길동\",\n    \"drId\": 2,\n    \"drName\": \"김철수\",\n    \"la\": 1000000,\n    \"ir\": 5.0,\n    \"contractDt\": \"2025-04-10 00:00:00\",\n    \"matDt\": \"2025-10-10 00:00:00\",\n    \"contractStatus\": \"CONTRACTED\",\n    \"contractStatusName\": \"체결됨\",\n    \"tokenId\": \"nft-1681234567890\",\n    \"nftImageUrl\": \"https://example.com/nft/nft-1681234567890.png\",\n    \"updatedAt\": \"2025-04-07 10:15:00\"\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 또는 상태 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 상태 오류",
                                                    summary = "체결할 수 없는 상태",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"요청됨 상태의 계약만 체결할 수 있습니다\"\n  }\n}"
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
                                                    name = "인증 오류",
                                                    summary = "인증 정보 없음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"인증이 필요합니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "권한 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 권한 없음",
                                                    summary = "체결 권한 없음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 403,\n    \"message\": \"채권자만 계약을 체결할 수 있습니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "계약을 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 계약 없음",
                                                    summary = "존재하지 않는 계약",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"존재하지 않는 계약입니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface CompleteContractApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "계약 반려(수정요청)",
            description = "계약을 반려하고 수정을 요청합니다. 채권자만 계약을 반려할 수 있습니다. 반려 사유를 입력하고 취소 또는 수정 요청 여부를 선택할 수 있습니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "반려 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ContractRejectRequestDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "계약 반려 요청",
                                            summary = "반려 요청 예시",
                                            value = "{\n  \"rejectMessage\": \"계약 조건 재검토 필요\",\n  \"isCanceled\": false\n}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "반려 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 응답 - 계약 반려",
                                                    summary = "계약 반려 처리 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"contractId\": 1,\n    \"crId\": 2,\n    \"crName\": \"clapsheep\",\n    \"drId\": 1,\n    \"drName\": \"열정두배\",\n    \"la\": 1000000,\n    \"ir\": 5.0,\n    \"contractStatus\": \"MODIFICATION_REQUESTED\",\n    \"contractStatusName\": \"수정 요청\",\n    \"message\": \"잘 부탁드립니다\",\n    \"rejectMessage\": \"계약 조건 재검토 필요\",\n    \"rejectedAt\": \"2025-04-07 10:15:00\",\n    \"updatedAt\": \"2025-04-07 10:15:00\"\n  },\n  \"error\": null\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "성공 응답 - 계약 취소",
                                                    summary = "계약 취소 처리 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"contractId\": 1,\n    \"crId\": 2,\n    \"crName\": \"clapsheep\",\n    \"drId\": 1,\n    \"drName\": \"열정두배\",\n    \"la\": 1000000,\n    \"ir\": 5.0,\n    \"contractStatus\": \"CANCELED\",\n    \"contractStatusName\": \"취소\",\n    \"message\": \"잘 부탁드립니다\",\n    \"rejectMessage\": \"수정이 불가능하여 취소 처리합니다\",\n    \"rejectedAt\": \"2025-04-07 10:15:00\",\n    \"updatedAt\": \"2025-04-07 10:15:00\"\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 또는 상태 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 상태 오류",
                                                    summary = "반려할 수 없는 상태",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"요청 상태의 계약만 반려할 수 있습니다\"\n  }\n}"
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
                                                    name = "인증 오류",
                                                    summary = "인증 정보 없음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"인증이 필요합니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "권한 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 권한 없음",
                                                    summary = "반려 권한 없음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 403,\n    \"message\": \"채권자만 계약을 반려할 수 있습니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "계약을 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 계약 없음",
                                                    summary = "존재하지 않는 계약",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"존재하지 않는 계약입니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface RejectContractApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "계약 삭제",
            description = "계약을 삭제합니다 (논리적 삭제). 체결된 계약은 삭제할 수 없으며, 관리자만 삭제할 수 있습니다.",
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
                                                    name = "성공 응답 - 삭제 성공",
                                                    summary = "계약 삭제 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"message\": \"계약이 성공적으로 삭제되었습니다\"\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "삭제할 수 없는 상태",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 상태 오류",
                                                    summary = "체결된 계약 삭제 시도",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"체결된 계약은 삭제할 수 없습니다\"\n  }\n}"
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
                                                    name = "인증 오류",
                                                    summary = "인증 정보 없음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"인증이 필요합니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "권한 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 권한 없음",
                                                    summary = "삭제 권한 없음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 403,\n    \"message\": \"해당 계약을 삭제할 권한이 없습니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "계약을 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 계약 없음",
                                                    summary = "존재하지 않는 계약 ID",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"존재하지 않는 계약입니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface DeleteContractApi {
    }

    /**
     * 계약 ID 경로 변수에 대한 파라미터 정보
     */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Parameter(
            name = "contractId",
            description = "계약 ID",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "integer", format = "int32", minimum = "1")
    )
    @interface ContractIdParam {
    }

    /**
     * 계약 유형 쿼리 파라미터에 대한 정보
     */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Parameter(
            name = "searchCondition.type",
            description = "계약 유형 (sent: 보낸 계약, received: 받은 계약)",
            required = false,
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string", defaultValue = "sent", allowableValues = {"sent", "received"})
    )
    @interface ContractTypeParam {
    }

    /**
     * 페이지 번호 쿼리 파라미터에 대한 정보
     */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Parameter(
            name = "pageNumber",
            description = "페이지 번호 (0부터 시작)",
            required = false,
            in = ParameterIn.QUERY,
            schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")
    )
    @interface PageNumberParam {
    }

    /**
     * 페이지 크기 쿼리 파라미터에 대한 정보
     */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Parameter(
            name = "pageSize",
            description = "페이지 크기",
            required = false,
            in = ParameterIn.QUERY,
            schema = @Schema(type = "integer", defaultValue = "10", minimum = "1", maximum = "100")
    )
    @interface PageSizeParam {
    }

    /**
     * 정렬 기준 쿼리 파라미터에 대한 정보
     */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Parameter(
            name = "sortBy",
            description = "정렬 기준 필드",
            required = false,
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string", defaultValue = "createdAt", allowableValues = {"createdAt", "la", "matDt", "ir", "lt"})
    )
    @interface SortByParam {
    }

    /**
     * 정렬 방향 쿼리 파라미터에 대한 정보
     */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Parameter(
            name = "sortDirection",
            description = "정렬 방향",
            required = false,
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string", defaultValue = "DESC", allowableValues = {"ASC", "DESC"})
    )
    @interface SortDirectionParam {
    }

    /**
     * 계약 검색 요청 바디에 대한 정보
     */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Parameter(
            description = "계약 검색 조건",
            required = true
    )
    @interface ContractSearchRequestParam {
    }

    /**
     * 계약 생성 요청 바디에 대한 정보
     */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Parameter(
            description = "계약 생성 정보",
            required = true
    )
    @interface ContractRequestParam {
    }

    /**
     * 계약 반려 요청 바디에 대한 정보
     */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Parameter(
            description = "계약 반려 정보",
            required = true
    )
    @interface ContractRejectRequestParam {
    }
}