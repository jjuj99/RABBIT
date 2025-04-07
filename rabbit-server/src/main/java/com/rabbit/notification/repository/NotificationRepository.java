package com.rabbit.notification.repository;

import com.rabbit.notification.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findAllByUserIdOrderByCreatedAtDesc(Integer userId);
}
