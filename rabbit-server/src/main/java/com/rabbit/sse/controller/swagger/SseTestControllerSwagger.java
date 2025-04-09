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
import java.util.Map;

@Tag(name = "SSE", description = "서버 센트 이벤트(SSE) 관련 API입니다.")
public interface SseTestControllerSwagger {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "테스트용 SSE 이벤트 발행",
            description = "개발 및 테스트 목적으로 임의의 SSE 이벤트를 발행합니다. 실제 운영 환경에서는 사용하지 않아야 합니다.",
            parameters = {
                    @Parameter(
                            name = "type",
                            description = "이벤트 타입 (예: BID_CREATED, AUCTION_ENDED 등)",
                            required = true,
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string"),
                            example = "BID_CREATED"
                    ),
                    @Parameter(
                            name = "key",
                            description = "이벤트 발행 키 (예: auction-13, user-42 등)",
                            required = true,
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string"),
                            example = "auction-13"
                    ),
                    @Parameter(
                            name = "message",
                            description = "이벤트 메시지 내용",
                            required = true,
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string"),
                            example = "새 입찰이 생성되었습니다."
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "이벤트 발행 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": \"이벤트 발행 성공\",\n  \"error\": null\n}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "이벤트 발행 실패",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 500,\n    \"message\": \"이벤트 발행 실패: Redis 연결 오류\"\n  }\n}"
                                    )
                            )
                    )
            }
    )
    @interface PublishTestEventApi {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "활성 SSE 구독자 조회",
            description = "현재 활성화된 모든 SSE 구독자 정보를 조회합니다. 키별 구독자 수가 반환됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "구독자 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"auction-13\": 2,\n    \"user-42\": 1\n  },\n  \"error\": null\n}"
                                    )
                            )
                    )
            }
    )
    @interface GetActiveSubscribersApi {}
}