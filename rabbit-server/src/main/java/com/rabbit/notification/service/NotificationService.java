package com.rabbit.notification.service;

import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.notification.domain.dto.request.NotificationRequestDTO;
import com.rabbit.notification.domain.dto.response.NotificationResponseDTO;
import com.rabbit.notification.domain.entity.Notification;
import com.rabbit.notification.repository.NotificationRepository;
import com.rabbit.sse.domain.dto.response.SseEventResponseDTO;
import com.rabbit.sse.service.SseEventPublisher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.Int;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SseEventPublisher sseEventPublisher;

    @Transactional
    public Notification createNotification(NotificationRequestDTO req) {
        // Enum에서 title / content 자동 설정
        String title = req.getType().getCodeName();
        String content = req.getType().getDescription();

        Notification notification = Notification.builder()
                .userId(req.getUserId())
                .type(req.getType())
                .title(title)
                .content(content)
                .readFlag(false)
                .relatedId(req.getRelatedId())
                .relatedType(req.getRelatedType())
                .build();

        Notification saved = notificationRepository.save(notification);

        NotificationResponseDTO notificationResponseDTO = NotificationResponseDTO.from(saved);

        // SSE 알림 전송
        sseEventPublisher.publish(
                req.getType().getCode(), // 이벤트 타입
                "user-" + req.getUserId(), // 유저별 SSE 채널 키
                notificationResponseDTO
        );

        return saved;
    }

    public List<NotificationResponseDTO> getUserNotifications(Integer userId) {
        List<Notification> notifications = notificationRepository
                .findAllByUserIdOrderByCreatedAtDesc(userId);

        return notifications.stream()
                .map(NotificationResponseDTO::from)
                .toList();
    }

    public void readNotification(Integer notificationId, Integer userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 알림을 찾을 수 없습니다."));

        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        if (!notification.getReadFlag()) {
            notification.markAsRead();
            notificationRepository.save(notification);
        }
    }
}
