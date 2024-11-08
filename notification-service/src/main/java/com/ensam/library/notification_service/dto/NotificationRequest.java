package com.ensam.library.notification_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.ensam.library.notification_service.model.NotificationType;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class NotificationRequest {
    private Long userId;
    private Long bookId;
    private String userEmail;
    private String bookTitle;
    private NotificationType type;
    private String message;
}