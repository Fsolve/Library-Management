package com.ensam.library.notification_service.repository;

import com.ensam.library.notification_service.model.Notification;
import com.ensam.library.notification_service.model.NotificationType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findBySentOrderByCreatedAtDesc(boolean sent);

    List<Notification> findByTypeAndSentOrderByCreatedAtDesc(NotificationType type, boolean sent);
}