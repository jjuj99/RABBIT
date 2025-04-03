package com.rabbit.notification.domain.entity;

import com.rabbit.global.code.domain.enums.SysCommonCodes;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Integer notificationId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false)
    private SysCommonCodes.NotificationType type; // 알림 유형 Enum

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "read_flag", nullable = false)
    private Boolean readFlag = false;

    @Column(name = "related_id")
    private Integer relatedId;

    @Enumerated(EnumType.STRING)
    @Column(name = "related_type", length = 50)
    private SysCommonCodes.NotificationRelatedType relatedType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    // 도메인 메서드 예시
    public void markAsRead() {
        this.readFlag = true;
    }
}
