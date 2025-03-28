package com.rabbit.coin.controller.swagger;

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
}
