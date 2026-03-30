package com.example.notificationService.consumer;

import com.example.notificationService.dto.NotificationEvent;
import com.example.notificationService.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotificationConsumer {
    private final NotificationService notificationService;

    @RabbitListener(queues = "notification.queue")
    public void consumeNotification(NotificationEvent notificationEvent){
        System.out.println("Notification recieved");
        System.out.println("user id: "+notificationEvent.getUserId());
        System.out.println("title: "+notificationEvent.getTitle());
        System.out.println("message: "+notificationEvent.getMessage());
        System.out.println("Type: "+notificationEvent.getType());

        notificationService.createNotification(notificationEvent);
    }
}
