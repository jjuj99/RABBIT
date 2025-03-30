package com.rabbit.sse.controller.swagger;

import com.rabbit.global.response.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Tag(name = "SSE", description = "서버 센트 이벤트(SSE) 관련 API입니다.")
public interface SseControllerSwagger {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "SSE 구독",
            description = "클라이언트가 SSE 이벤트를 구독합니다. 유저 ID 또는 경매 ID 기반으로 연결 키를 생성하고, 해당 키로 이벤트를 수신합니다.",
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "구독할 사용자 ID (또는 경매 ID 등)",
                            required = true,
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string"),
                            example = "user123"
                    ),
                    @Parameter(
                            name = "type",
                            description = "구독 타입 (예: USER, AUCTION 등)",
                            required = true,
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string"),
                            example = "USER"
                    ),
                    @Parameter(
                            name = "Last-Event-Id",
                            description = "이전에 수신한 마지막 이벤트 ID (재연결 시 사용)",
                            required = false,
                            in = ParameterIn.HEADER,
                            schema = @Schema(type = "string"),
                            example = "event123"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "SSE 구독 성공 (이벤트 스트림 반환)",
                            content = @Content(
                                    mediaType = "text/event-stream",
                                    schema = @Schema(type = "string"),
                                    examples = @ExampleObject(
                                            name = "이벤트 스트림 예시",
                                            summary = "입찰 이벤트 수신 예시",
                                            value = "event: bid\n" +
                                                    "id: event456\n" +
                                                    "data: {\n" +
                                                    "  \"bidId\": 101,\n" +
                                                    "  \"bidAmount\": 150000,\n" +
                                                    "  \"createdAt\": \"2025-03-30T14:00:00Z\"\n" +
                                                    "}\n\n"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (파라미터 누락 또는 형식 오류)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "파라미터 오류",
                                            summary = "필수 파라미터 누락",
                                            value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"userId 또는 type은 필수입니다.\"\n  }\n}"
                                    )
                            )
                    )
            }
    )
    @interface SubscribeSseApi {}
}
