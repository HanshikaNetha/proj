package com.example.notificationService.service;

import com.example.notificationService.dto.NotificationEvent;
import com.example.notificationService.dto.NotificationResponse;
import com.example.notificationService.entity.Notification;
import com.example.notificationService.enums.NotificationType;
import com.example.notificationService.exception.NotificationNotFoundException;
import com.example.notificationService.exception.UnauthorizedException;
import com.example.notificationService.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    public void createNotification(NotificationEvent notificationEvent){
        Notification notification=new Notification();
        notification.setUserId(notificationEvent.getUserId());
        notification.setTitle(notificationEvent.getTitle());
        notification.setMessage(notificationEvent.getMessage());
        notification.setType(notificationEvent.getType());
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getUserNotificationsById(Long userId){
        Long tokenUserId=(Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!tokenUserId.equals(userId)){
            throw new UnauthorizedException("You dont want see others notification, do you?");
        }
        List<Notification> notifications=notificationRepository.findByUserId(userId);
        List<NotificationResponse> notificationResponses=notifications.stream().map(i->modelMapper.map(i, NotificationResponse.class)).toList();
        return notificationResponses;
    }

    public NotificationResponse markAsRead(Long notificationId){
        Long tokenUserId=(Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Notification notification=notificationRepository.findById(notificationId).orElseThrow(()->new NotificationNotFoundException("Notification Not found"));
        if(!notification.getUserId().equals(tokenUserId)){
            throw new UnauthorizedException("dont modify others notifications");
        }
        notification.setRead(true);
        Notification savedNotification=notificationRepository.save(notification);
        NotificationResponse notificationResponse=modelMapper.map(savedNotification, NotificationResponse.class);
        return notificationResponse;
    }
}
