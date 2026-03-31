package com.example.notificationService;

import com.example.notificationService.dto.NotificationEvent;
import com.example.notificationService.dto.NotificationResponse;
import com.example.notificationService.entity.Notification;
import com.example.notificationService.enums.NotificationType;
import com.example.notificationService.exception.NotificationNotFoundException;
import com.example.notificationService.exception.UnauthorizedException;
import com.example.notificationService.repository.NotificationRepository;
import com.example.notificationService.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NotificationService notificationService;

    // Helper
    private void mockUser(Long userId) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId, null, List.of())
        );
    }


    @Test
    void testCreateNotification() {
        NotificationEvent event = new NotificationEvent(
                1L,
                "Title",
                "Message",
                NotificationType.STARTUP_CREATED
        );

        notificationService.createNotification(event);

        verify(notificationRepository).save(any(Notification.class));
    }


    @Test
    void testGetUserNotifications_Success() {
        mockUser(1L);

        Notification notification = new Notification();
        notification.setUserId(1L);

        NotificationResponse response = new NotificationResponse();

        when(notificationRepository.findByUserId(1L))
                .thenReturn(List.of(notification));

        doReturn(response)
                .when(modelMapper)
                .map(notification, NotificationResponse.class);

        List<NotificationResponse> result =
                notificationService.getUserNotificationsById(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testGetUserNotifications_Unauthorized() {
        mockUser(2L);

        assertThrows(UnauthorizedException.class,
                () -> notificationService.getUserNotificationsById(1L));
    }


    @Test
    void testMarkAsRead_Success() {
        mockUser(1L);

        Notification notification = new Notification();
        notification.setNotificationId(1L);
        notification.setUserId(1L);

        NotificationResponse response = new NotificationResponse();

        when(notificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        when(notificationRepository.save(any()))
                .thenReturn(notification);

        doReturn(response)
                .when(modelMapper)
                .map(notification, NotificationResponse.class);

        NotificationResponse result =
                notificationService.markAsRead(1L);

        assertNotNull(result);
        verify(notificationRepository).save(notification);
    }

    @Test
    void testMarkAsRead_NotFound() {
        mockUser(1L);

        when(notificationRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class,
                () -> notificationService.markAsRead(1L));
    }

    @Test
    void testMarkAsRead_Unauthorized() {
        mockUser(2L);

        Notification notification = new Notification();
        notification.setUserId(1L);

        when(notificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        assertThrows(UnauthorizedException.class,
                () -> notificationService.markAsRead(1L));
    }

}
