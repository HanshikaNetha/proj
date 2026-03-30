package com.example.investmentService.dto;

import com.example.investmentService.enums.NotificationType;
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
