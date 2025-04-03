package com.rabbit.notification.controller;

import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.notification.controller.swagger.NotificationControllerSwagger;
import com.rabbit.notification.domain.dto.response.NotificationResponseDTO;
import com.rabbit.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
