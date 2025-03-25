package com.rabbit.auction.controller.swagger;

import com.rabbit.auction.domain.dto.request.BidRequestDTO;
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Tag(name = "Bid", description = "입찰 관련 api입니다.")
public interface BidControllerSwagger {
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "경매 입찰",
            description = "특정 경매에 대해 입찰을 진행합니다. 입찰 금액은 현재 경매가보다 커야 합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "입찰 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BidRequestDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "입찰 요청 예시",
                                            summary = "정상적인 입찰 요청",
                                            value = "{\n  \"bidAmount\": 50000\n}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "입찰 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 응답",
                                                    summary = "입찰 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": { \"message\": \"입찰 성공했습니다.\" },\n  \"error\": null\n}"
                                            )
                                    }
                            ),
                            headers = {
                                    @Header(name = "Location", description = "입찰 성공 후 리소스 URI", schema = @Schema(type = "string"))
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "경매 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 - 경매 없음",
                                                    summary = "경매 ID로 조회 실패",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": { \"statusCode\": 404, \"message\": \"경매를 찾을 수 없습니다.\" }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "비즈니스 로직 위반",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "에러 - 경매 마감",
                                                    summary = "마감된 경매",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": { \"statusCode\": 422, \"message\": \"이미 마감된 경매입니다.\" }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "에러 - 입찰 금액 부족",
                                                    summary = "입찰 금액이 낮음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": { \"statusCode\": 422, \"message\": \"입찰 금액이 현재 경매가보다 낮습니다.\" }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface AddBidApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "경매 입찰 내역 조회",
            description = "특정 경매 ID에 대한 입찰 내역을 최신순으로 조회합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            parameters = {
                    @Parameter(
                            name = "auctionId",
                            description = "입찰 내역을 조회할 경매 ID",
                            required = true,
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "integer", format = "int32", minimum = "1")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "입찰 내역 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "성공 응답",
                                            summary = "입찰 내역 리스트",
                                            value = "{\n" +
                                                    "  \"status\": \"SUCCESS\",\n" +
                                                    "  \"data\": [\n" +
                                                    "    {\n" +
                                                    "      \"bid_id\": 2,\n" +
                                                    "      \"bid_amount\": 15050,\n" +
                                                    "      \"created_at\": \"2025-03-12T15:00:00\"\n" +
                                                    "    },\n" +
                                                    "    {\n" +
                                                    "      \"bid_id\": 1,\n" +
                                                    "      \"bid_amount\": 10020,\n" +
                                                    "      \"created_at\": \"2025-03-12T14:30:00\"\n" +
                                                    "    }\n" +
                                                    "  ],\n" +
                                                    "  \"error\": null\n" +
                                                    "}"
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
                                            name = "실패 응답 - 경매 없음",
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
    @interface GetBidListApi {
    }

}
