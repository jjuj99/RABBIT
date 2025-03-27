package com.rabbit.auction.controller.swagger;

import com.rabbit.auction.domain.dto.request.AuctionRequestDTO;
import com.rabbit.auction.domain.dto.response.AuctionDetailResponseDTO;
import com.rabbit.auction.domain.dto.response.MyAuctionResponseDTO;
import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.global.response.PageResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
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

@Tag(name = "Auction", description = "마켓 관련 api입니다.")
public interface AuctionControllerSwagger {
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "NFT 경매 등록",
            description = "NFT에 대한 새로운 경매를 등록합니다. 최소 입찰가, 경매 종료 시간, 토큰 ID, 서명값을 필수로 입력해야 합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            requestBody = @RequestBody(
                    description = "경매 등록 요청",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuctionRequestDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "경매 등록 요청 예시",
                                            summary = "정상 요청 예시",
                                            value = "{\n  \"minimumBid\": 1000,\n  \"endDate\": \"2025-03-30T23:59:59\",\n  \"tokenId\": \"NFT_TOKEN_001\",\n  \"sellerSign\": \"SIGNATURE_ABC\"\n}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "경매 등록 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "성공 응답",
                                            summary = "등록 성공",
                                            value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"message\": \"경매 등록 성공했습니다.\"\n  },\n  \"error\": null\n}"
                                    )
                            ),
                            headers = @Header(name = "Location", description = "생성된 경매의 URI")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (입찰가 0 이하, 종료시간 과거, 파라미터 누락 등)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 입찰가 오류",
                                                    summary = "입찰가 0 이하",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"최소 입찰가는 0보다 커야 합니다.\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "실패 응답 - 시간 오류",
                                                    summary = "경매 종료 시간이 현재보다 과거",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"경매 종료 시간은 현재 시간 이후여야 합니다.\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "실패 응답 - 파라미터 누락",
                                                    summary = "필수 파라미터 없음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"필수 파라미터가 누락되었습니다.\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "이미 진행중인 경매가 있을 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "실패 응답 - 경매 중복",
                                            summary = "이미 해당 NFT로 경매가 진행 중",
                                            value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 500,\n    \"message\": \"해당 NFT는 이미 경매가 진행 중입니다.\"\n  }\n}"
                                    )
                            )
                    )
            }
    )
    @interface InsertAuctionApi {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "경매 목록 검색",
            description = "필터 조건 및 페이징 기준에 맞는 경매 목록을 검색합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            parameters = {
                    // 페이지네이션 파라미터
                    @Parameter(name = "pageNo", description = "페이지 번호 (0부터 시작)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
                    @Parameter(name = "pageSize", description = "페이지당 항목 수", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10")),
                    @Parameter(name = "sortBy", description = "정렬 필드", in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "price")),
                    @Parameter(name = "sortDirection", description = "정렬 방향 (ASC, DESC)", in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "ASC")),

                    // 검색 조건 파라미터
                    @Parameter(name = "minPrice", description = "최소 금액", in = ParameterIn.QUERY, schema = @Schema(type = "integer", example = "10000")),
                    @Parameter(name = "maxPrice", description = "최대 금액", in = ParameterIn.QUERY, schema = @Schema(type = "integer", example = "50000")),
                    @Parameter(name = "minIr", description = "최소 수익률", in = ParameterIn.QUERY, schema = @Schema(type = "number", format = "double", example = "3.0")),
                    @Parameter(name = "maxIr", description = "최대 수익률", in = ParameterIn.QUERY, schema = @Schema(type = "number", format = "double", example = "10.0")),
                    @Parameter(name = "repayType", description = "상환 방식 (원리금 균등, 원금 균등, 만기 일시 상환)", in = ParameterIn.QUERY, schema = @Schema(type = "number", example = "1")),
                    @Parameter(name = "matTerm", description = "만기 조건 (1개월, 3개월, 6개월, 1년, custom)", in = ParameterIn.QUERY, schema = @Schema(type = "number", example = "3")),
                    @Parameter(name = "matStart", description = "만기 시작일 (custom일 경우)", in = ParameterIn.QUERY, schema = @Schema(type = "string", format = "date-time", example = "2025-01-01T00:00:00Z")),
                    @Parameter(name = "matEnd", description = "만기 종료일 (custom일 경우)", in = ParameterIn.QUERY, schema = @Schema(type = "string", format = "date-time", example = "2025-12-31T00:00:00Z"))
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "경매 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class, subTypes = {PageResponseDTO.class}),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 응답",
                                                    summary = "경매 목록 조회 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"content\": [\n      {\n        \"auctionId\": 1,\n        \"price\": 15000,\n        \"endDate\": \"2025-05-01T12:00:00\",\n        \"ir\": 5.5,\n        \"createdAt\": \"2025-03-22T10:00:00\",\n        \"repayType\": \"원리금 균등\",\n        \"totalAmount\": 1000000,\n        \"matDt\": \"2025-12-31T14:30:00\",\n        \"dir\": 1.05,\n   \"la\": 1000000000,\n   \"earlypayFlag\": true,\n      \"earlypayFee\": 1.03,\n        \"creditScore\": 800,\n        \"defCnt\": 0\n      }\n    ],\n    \"pageNo\": 0,\n    \"pageSize\": 10,\n    \"totalElements\": 1,\n    \"totalPages\": 1,\n    \"first\": true,\n    \"last\": true,\n    \"empty\": false\n  },\n  \"error\": null\n}"
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
                            description = "잘못된 요청 (금액/수익률/날짜 범위 오류)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 응답 - 금액 범위 오류",
                                                    summary = "최소 금액이 최대 금액보다 큼",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"최소 금액이 최대 금액보다 클 수 없습니다.\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "실패 응답 - 수익률 범위 오류",
                                                    summary = "최소 수익률이 최대 수익률보다 큼",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"최소 수익률이 최대 수익률보다 클 수 없습니다.\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "실패 응답 - 날짜 범위 오류",
                                                    summary = "시작일이 종료일보다 늦음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"시작일이 종료일보다 늦을 수 없습니다.\"\n  }\n}"
                                            )
                                    }
                            )
                    )

            }
    )
    @interface SearchAuctionApi {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "경매 취소",
            description = "특정 경매를 취소 처리합니다. 단, 입찰자가 존재하면 취소할 수 없습니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            parameters = {
                    @Parameter(
                            name = "auctionId",
                            description = "취소할 경매 ID",
                            required = true,
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "integer", format = "int32")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "경매 취소 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "성공 응답",
                                            summary = "정상 취소 처리",
                                            value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n \"message\": \"경매가 취소되었습니다.\"\n  },\n  \"error\": null\n}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "경매를 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "에러 응답 - 경매 없음",
                                            summary = "존재하지 않는 경매 ID",
                                            value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"해당 경매를 찾을 수 없습니다.\"\n  }\n}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "입찰자가 있어 취소 불가",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "에러 응답 - 입찰자 존재",
                                            summary = "취소 불가",
                                            value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 422,\n    \"message\": \"입찰자가 존재해 경매를 취소할 수 없습니다.\"\n  }\n}"
                                    )
                            )
                    )
            }
    )
    public @interface CancelAuctionApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "경매 상세 조회",
            description = "경매 ID를 통해 경매의 상세 정보를 조회합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            parameters = {
                    @Parameter(
                            name = "auctionId",
                            description = "조회할 경매 ID",
                            required = true,
                            example = "1",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "integer")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class,
                                            subTypes = {AuctionDetailResponseDTO.class}),
                                    examples = @ExampleObject(
                                            name = "성공 응답",
                                            summary = "경매 상세 조회 성공",
                                            value = "{\n" +
                                                    "  \"status\": \"SUCCESS\",\n" +
                                                    "  \"data\": {\n" +
                                                    "    \"auctionId\": 1,\n" +
                                                    "    \"price\": 15000,\n" +
                                                    "    \"endDate\": \"2025-05-01T12:00:00Z\",\n" +
                                                    "    \"ir\": 5.5,\n" +
                                                    "    \"createdAt\": \"2025-03-22T10:00:00Z\",\n" +
                                                    "    \"repayType\": \"원리금 균등\",\n" +
                                                    "    \"totalAmount\": 1000000,\n" +
                                                    "    \"matDt\": \"2025-12-31T14:30:00Z\",\n" +
                                                    "    \"dir\": 1.05,\n" +
                                                    "    \"la\": 1000000000,\n" +
                                                    "    \"earlypayFlag\": true,\n" +
                                                    "    \"earlypayFee\": 1.03,\n" +
                                                    "    \"creditScore\": 800,\n" +
                                                    "    \"defCnt\": 0\n" +
                                                    "  },\n" +
                                                    "  \"error\": null\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 경매를 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "조회 실패",
                                            summary = "존재하지 않는 경매 ID",
                                            value = "{\n" +
                                                    "  \"status\": \"ERROR\",\n" +
                                                    "  \"data\": null,\n" +
                                                    "  \"error\": {\n" +
                                                    "    \"statusCode\": 404,\n" +
                                                    "    \"message\": \"해당 경매를 찾을 수 없습니다.\"\n" +
                                                    "  }\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    public @interface GetAuctionDetailApi {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "내 입찰 경매 목록 조회",
            description = "사용자가 입찰한 경매 목록을 페이징 기준에 따라 조회합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            parameters = {
                    @Parameter(name = "pageNo", description = "페이지 번호 (0부터 시작)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
                    @Parameter(name = "pageSize", description = "페이지당 항목 수", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10"))
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "내 입찰 경매 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class, subTypes = {MyAuctionResponseDTO.class}),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 응답",
                                                    summary = "조회 성공",
                                                    value = "{\n" +
                                                            "  \"status\": \"SUCCESS\",\n" +
                                                            "  \"data\": {\n" +
                                                            "    \"content\": [\n" +
                                                            "      {\n" +
                                                            "        \"auctionId\": 1,\n" +
                                                            "        \"bidDate\": \"2025-03-20T15:00:00Z\",\n" +
                                                            "        \"auctionStatus\": \"ING\",\n" +
                                                            "        \"price\": 100000,\n" +
                                                            "        \"bidAmount\": 50000,\n" +
                                                            "        \"bidStatus\": \"PENDING\",\n" +
                                                            "        \"bidderNum\": 5\n" +
                                                            "      }\n" +
                                                            "    ],\n" +
                                                            "    \"pageNo\": 0,\n" +
                                                            "    \"pageSize\": 10,\n" +
                                                            "    \"totalElements\": 1,\n" +
                                                            "    \"totalPages\": 1,\n" +
                                                            "    \"first\": true,\n" +
                                                            "    \"last\": true,\n" +
                                                            "    \"empty\": false\n" +
                                                            "  },\n" +
                                                            "  \"error\": null\n" +
                                                            "}"
                                            ),
                                            @ExampleObject(
                                                    name = "빈 응답",
                                                    summary = "입찰 내역 없음",
                                                    value = "{\n" +
                                                            "  \"status\": \"SUCCESS\",\n" +
                                                            "  \"data\": {\n" +
                                                            "    \"content\": [],\n" +
                                                            "    \"pageNo\": 0,\n" +
                                                            "    \"pageSize\": 10,\n" +
                                                            "    \"totalElements\": 0,\n" +
                                                            "    \"totalPages\": 0,\n" +
                                                            "    \"first\": true,\n" +
                                                            "    \"last\": true,\n" +
                                                            "    \"empty\": true\n" +
                                                            "  },\n" +
                                                            "  \"error\": null\n" +
                                                            "}"
                                            )
                                    }
                            )
                    )
            }
    )
    public @interface GetMyBidAuctionsApi {}


    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Parameter(
            name = "auctionId",
            description = "입찰할 경매 ID",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "integer", format = "int32")
    )
    @interface AuctionIdParam {
    }
}
