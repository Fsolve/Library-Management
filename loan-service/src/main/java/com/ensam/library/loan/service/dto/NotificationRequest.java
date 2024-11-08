package com.ensam.library.loan.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private Long userId;
    private String userEmail;
    private Long bookId;
    private String bookTitle;
    private String type;
    private String message;
}