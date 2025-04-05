package com.rabbit.notification.controller.swagger;

import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.notification.domain.dto.response.NotificationResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface NotificationControllerSwagger {
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "사용자 알림 목록 조회",
            description = "특정 사용자(userId)의 알림 내역을 조회합니다. 최신순 정렬이며, 읽음 여부에 관계없이 전체 조회합니다.",
            parameters = {
                    @Parameter(name = "userId", description = "조회할 사용자 ID", required = true, in = ParameterIn.QUERY, schema = @Schema(type = "integer"))
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "알림 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class, subTypes = {NotificationResponseDTO.class}),
                                    examples = @ExampleObject(
                                            name = "알림 조회 성공",
                                            summary = "알림 리스트 반환",
                                            value = "{\n" +
                                                    "  \"status\": \"SUCCESS\",\n" +
                                                    "  \"data\": [\n" +
                                                    "    {\n" +
                                                    "      \"notificationId\": 15,\n" +
                                                    "      \"type\": \"BID_FAILED\",\n" +
                                                    "      \"title\": \"입찰 실패\",\n" +
                                                    "      \"content\": \"다른 입찰자가 더 높은 금액을 입찰했습니다.\",\n" +
                                                    "      \"readFlag\": false,\n" +
                                                    "      \"relatedId\": 25,\n" +
                                                    "      \"relatedType\": \"AUCTION\",\n" +
                                                    "      \"createdAt\": \"2025-04-03T15:32:14+09:00\"\n" +
                                                    "    }\n" +
                                                    "  ],\n" +
                                                    "  \"error\": null\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    @interface GetUserNotificationsApi {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "알림 읽음 처리",
            description = "지정한 알림 ID의 알림을 읽음 상태로 변경합니다. 본인의 알림이 아닌 경우 403 에러를 반환합니다.",
            parameters = {
                    @Parameter(name = "notificationId", description = "읽음 처리할 알림 ID", required = true, in = ParameterIn.PATH, schema = @Schema(type = "integer"))
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "알림 읽음 처리 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class, subTypes = {NotificationResponseDTO.class}),
                                    examples = @ExampleObject(
                                            name = "알림 읽음 처리 성공",
                                            summary = "읽음 처리된 알림 반환",
                                            value = "{\n" +
                                                    "  \"status\": \"SUCCESS\",\n" +
                                                    "  \"data\": {\n" +
                                                    "    \"notificationId\": 15,\n" +
                                                    "    \"type\": \"BID_FAILED\",\n" +
                                                    "    \"title\": \"입찰 실패\",\n" +
                                                    "    \"content\": \"다른 입찰자가 더 높은 금액을 입찰했습니다.\",\n" +
                                                    "    \"readFlag\": true,\n" +
                                                    "    \"relatedId\": 25,\n" +
                                                    "    \"relatedType\": \"AUCTION\",\n" +
                                                    "    \"createdAt\": \"2025-04-03T15:32:14+09:00\"\n" +
                                                    "  },\n" +
                                                    "  \"error\": null\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음"),
                    @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다")
            }
    )
    @interface ReadNotificationApi {}
}
