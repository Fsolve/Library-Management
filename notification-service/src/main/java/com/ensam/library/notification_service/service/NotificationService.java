package com.ensam.library.notification_service.service;

import com.ensam.library.notification_service.dto.EmailDetails;
import com.ensam.library.notification_service.dto.NotificationRequest;
import com.ensam.library.notification_service.model.Notification;
import com.ensam.library.notification_service.model.NotificationType;
import com.ensam.library.notification_service.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
            EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    public Notification createNotification(NotificationRequest request) {
        log.info("Creating notification for user: {}", request.getUserId());

        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setEmailTo(request.getUserEmail());
        notification.setType(request.getType());
        notification.setMessage(request.getMessage());
        notification.setCreatedAt(LocalDateTime.now());

        EmailDetails emailDetails = new EmailDetails(
                request.getUserEmail(),
                getSubjectForNotificationType(request.getType()),
                request.getMessage());

        boolean emailSent = emailService.sendEmail(emailDetails);
        notification.setSent(emailSent);
        if (emailSent) {
            notification.setSentAt(LocalDateTime.now());
        }

        return notificationRepository.save(notification);
    }

    private String getSubjectForNotificationType(NotificationType type) {
        return switch (type) {
            case LOAN_CREATED -> "New Book Loan Confirmation";
            case LOAN_RETURNED -> "Book Return Confirmation";
            case LOAN_OVERDUE -> "Book Loan Overdue Notice";
            case BOOK_AVAILABLE -> "Book Available Notification";
        };
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getPendingNotifications() {
        return notificationRepository.findBySentOrderByCreatedAtDesc(false);
    }
}