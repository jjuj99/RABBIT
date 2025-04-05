package com.rabbit.notification.controller;

import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.global.response.MessageResponse;
import com.rabbit.notification.controller.swagger.NotificationControllerSwagger;
import com.rabbit.notification.domain.dto.response.NotificationResponseDTO;
import com.rabbit.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @NotificationControllerSwagger.GetUserNotificationsApi
    @GetMapping
    public ResponseEntity<CustomApiResponse<?>> getUserNotifications(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        List<NotificationResponseDTO> result = notificationService.getUserNotifications(Integer.parseInt(userId));

        return ResponseEntity.ok(CustomApiResponse.success(result));
    }

    @NotificationControllerSwagger.ReadNotificationApi
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<CustomApiResponse<?>> readNotification(@PathVariable Integer notificationId) {
        String userId = "3";

        notificationService.readNotification(notificationId, Integer.parseInt(userId));

        return ResponseEntity.ok(CustomApiResponse.success(MessageResponse.of("알림 읽음 상태 변경 성공했습니다.")));
    }
}
