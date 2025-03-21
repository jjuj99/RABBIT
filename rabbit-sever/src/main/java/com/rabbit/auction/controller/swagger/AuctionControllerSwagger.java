package com.rabbit.auction.controller.swagger;

import com.rabbit.auction.domain.dto.request.AuctionRequestDTO;
import com.rabbit.global.response.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
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
}
