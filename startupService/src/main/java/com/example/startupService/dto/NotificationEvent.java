package com.example.startupService.dto;

import com.example.startupService.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotificationEvent {
    private Long userId;
    private String title;
    private String message;
    private NotificationType type;
}
