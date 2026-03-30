package com.example.startupService.producer;

import com.example.startupService.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotificationProducer {
    private final RabbitTemplate rabbitTemplate;
    private static final String NOTIFICATION_EXCHANGE="notification.exchange";
    private static final String ROUTING_KEY="notification.routing";
    public void sendNotification(NotificationEvent notificationEvent){
        rabbitTemplate.convertAndSend(NOTIFICATION_EXCHANGE, ROUTING_KEY, notificationEvent);
    }
}
