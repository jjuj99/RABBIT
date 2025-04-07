package com.rabbit.notification.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.notification.domain.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {
    private Integer notificationId;
    private SysCommonCodes.NotificationType type;         // ex. "AUCTION_SUCCESS"
    private String title;        // 알림 제목
    private String content;      // 알림 내용
    private Boolean readFlag;    // 읽음 여부
    private Integer relatedId;      // 연관된 항목 ID
    private SysCommonCodes.NotificationRelatedType relatedType;  // 연관된 항목 유형
    private ZonedDateTime createdAt;


    public static NotificationResponseDTO from(Notification notification) {
        return NotificationResponseDTO.builder()
                .notificationId(notification.getNotificationId())
                .type(notification.getType()) // enum name 그대로
                .title(notification.getTitle())
                .content(notification.getContent())
                .readFlag(notification.getReadFlag())
                .relatedId(notification.getRelatedId())
                .relatedType(notification.getRelatedType())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
