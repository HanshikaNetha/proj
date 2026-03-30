package com.example.notificationService.dto;

import com.example.notificationService.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationEvent {
    private Long userId;
    private String title;
    private String message;
    private NotificationType type;
}
