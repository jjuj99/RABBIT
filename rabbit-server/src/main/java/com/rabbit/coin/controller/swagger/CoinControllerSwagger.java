package com.rabbit.coin.controller.swagger;

import com.rabbit.coin.domain.dto.request.CoinWithdrawRequestDTO;
import com.rabbit.coin.domain.dto.request.TossConfirmRequestDTO;
import com.rabbit.global.response.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Tag(name = "Coin", description = "코인 관련 API입니다.")
public interface CoinControllerSwagger {
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "토스 결제 승인",
            description = "프론트에서 Toss 결제 성공 후, 해당 결제를 최종 확정합니다.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Toss 결제 승인 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TossConfirmRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "결제 승인 요청 예시",
                                    summary = "정상 결제 요청",
                                    value = "{\n  \"paymentKey\": \"pay_abcdef123456\",\n  \"orderId\": \"order_001\",\n  \"amount\": 10000\n}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "결제 승인 및 성공 처리됨",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "성공 응답",
                                            summary = "결제 성공",
                                            value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"message\": \"계좌 이체 성공했습니다.\"\n  },\n  \"error\": null\n}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "결제 실패 또는 유효하지 않은 요청",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "실패 응답",
                                            summary = "결제 실패",
                                            value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"결제 승인에 실패했습니다.\"\n  }\n}"
                                    )
                            )
                    )
            }
    )
    @interface TossConfirmApi {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "코인 출금",
            description = "사용자의 RAB 코인을 출금하여 등록된 환불 계좌로 입금합니다.",
            requestBody = @RequestBody(
                    required = true,
                    description = "코인 출금 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CoinWithdrawRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "출금 요청 예시",
                                    summary = "정상 출금 요청",
                                    value = "{\n  \"name\": \"밀키스\", \n  \"accountNumber\": \"1234567890\",\n  \"amount\": 10000\n}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "출금 처리 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "성공 응답",
                                            summary = "출금 성공",
                                            value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"message\": \"출금이 완료되었습니다\"\n  },\n  \"error\": null\n}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "출금 실패 - 잔액 부족",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "잔액 부족",
                                                    summary = "RAB 잔액 부족",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"보유한 RAB이 출금 요청액보다 부족합니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "리소스를 찾을 수 없음 - 지갑 또는 계좌 정보 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "계좌 정보 없음",
                                                    summary = "환불 계좌 정보 조회 불가",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"환불 계좌 정보를 찾을 수 없습니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "지갑 정보 없음",
                                                    summary = "메타마스크 지갑 정보 조회 불가",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"사용자의 주 지갑을 찾을 수 없습니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "블록체인 트랜잭션 실패 또는 서버 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "블록체인 트랜잭션 실패",
                                                    summary = "RAB 전송 실패",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 500,\n    \"message\": \"RAB 전송 중 오류가 발생했습니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface WithdrawAPI {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "코인 거래 내역 조회",
            description = "사용자의 RAB 코인 입출금 거래 내역을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "거래 내역 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "성공 응답",
                                            summary = "거래 내역 목록",
                                            value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": [\n    {\n      \"type\": \"DEPOSIT\",\n      \"amount\": 10000,\n      \"createdAt\": \"2025-04-07T14:30:00+09:00\"\n    },\n    {\n      \"type\": \"WITHDRAW\",\n      \"amount\": 5000,\n      \"createdAt\": \"2025-04-06T09:15:00+09:00\"\n    }\n  ],\n  \"error\": null\n}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "200",
                            description = "거래 내역이 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "빈 응답",
                                            summary = "거래 내역 없음",
                                            value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": null,\n  \"error\": null\n}"
                                    )
                            )
                    )
            }
    )
    @interface GetTransactionsAPI {}
}
