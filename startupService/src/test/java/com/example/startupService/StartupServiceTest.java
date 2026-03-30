package com.example.startupService;

import com.example.startupService.dto.*;
import com.example.startupService.entity.Startup;
import com.example.startupService.exception.StartupNotFoundException;
import com.example.startupService.exception.UnauthorizedException;
import com.example.startupService.feign.UserClient;
import com.example.startupService.producer.NotificationProducer;
import com.example.startupService.repository.StartupRepository;
import com.example.startupService.service.StartupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StartupServiceTest {
    @Mock
    private StartupRepository startupRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserClient userClient;

    @Mock
    private NotificationProducer notificationProducer;

    @InjectMocks
    private StartupService startupService;
    private void mockUser(Long userId) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userId, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    @Test
    void testCreateStartup_Success() {
        mockUser(1L);
        StartupCreateRequest request = new StartupCreateRequest();
        request.setStartupName("Test Startup");
        UserResponse userResponse = new UserResponse();
        userResponse.setRole(com.example.startupService.enums.Role.ROLE_FOUNDER);
        Startup startup = new Startup();
        startup.setStartupName("Test Startup");
        Startup saved = new Startup();
        saved.setStartupId(1L);
        saved.setStartupName("Test Startup");
        StartupResponse response = new StartupResponse();
        response.setStartupId(1L);
        response.setStartupName("Test Startup");
        when(userClient.getUserById(1L)).thenReturn(userResponse);
        when(modelMapper.map(request, Startup.class)).thenReturn(startup);
        when(startupRepository.save(any())).thenReturn(saved);
        doReturn(response).when(modelMapper).map(saved, StartupResponse.class);
        StartupResponse result = startupService.createStartup(request);
        assertNotNull(result);
        assertEquals("Test Startup", result.getStartupName());
        verify(notificationProducer, times(1)).sendNotification(any());
    }
    @Test
    void testCreateStartup_NotFounder() {
        mockUser(1L);
        StartupCreateRequest request = new StartupCreateRequest();
        UserResponse userResponse = new UserResponse();
        userResponse.setRole(com.example.startupService.enums.Role.ROLE_INVESTOR);
        when(userClient.getUserById(1L)).thenReturn(userResponse);
        assertThrows(UnauthorizedException.class, () -> {
            startupService.createStartup(request);
        });
    }
    @Test
    void testGetStartupById_Success() {
        Startup startup = new Startup();
        startup.setStartupId(1L);
        StartupResponse response = new StartupResponse();
        response.setStartupId(1L);
        when(startupRepository.findById(1L)).thenReturn(Optional.of(startup));
        doReturn(response).when(modelMapper).map(startup, StartupResponse.class);
        StartupResponse result = startupService.getStartupById(1L);
        assertEquals(1L, result.getStartupId());
    }
    @Test
    void testGetStartupById_NotFound() {
        when(startupRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(StartupNotFoundException.class, () -> {
            startupService.getStartupById(1L);
        });
    }
    @Test
    void testUpdateStartup_Success() {
        mockUser(1L);
        Startup startup = new Startup();
        startup.setStartupId(1L);
        startup.setFounderId(1L);
        StartupUpdateRequest request = new StartupUpdateRequest();
        request.setStartupName("Updated");
        StartupResponse response = new StartupResponse();
        response.setStartupId(1L);
        when(startupRepository.findById(1L)).thenReturn(Optional.of(startup));
        when(startupRepository.save(any())).thenReturn(startup);
        doReturn(response).when(modelMapper).map(startup, StartupResponse.class);
        StartupResponse result = startupService.updateStartup(1L, request);
        assertNotNull(result);
    }
    @Test
    void testUpdateStartup_Unauthorized() {
        mockUser(2L);
        Startup startup = new Startup();
        startup.setFounderId(1L);
        when(startupRepository.findById(1L)).thenReturn(Optional.of(startup));
        assertThrows(UnauthorizedException.class, () -> {
            startupService.updateStartup(1L, new StartupUpdateRequest());
        });
    }
    @Test
    void testDeleteStartup_Success() {

        mockUser(1L);

        Startup startup = new Startup();
        startup.setFounderId(1L);

        when(startupRepository.findById(1L)).thenReturn(Optional.of(startup));

        String result = startupService.deleteStartup(1L);

        assertEquals("Startup deleted successfully", result);
        verify(startupRepository).delete(startup);
    }
    @Test
    void testDeleteStartup_Unauthorized() {

        mockUser(2L);

        Startup startup = new Startup();
        startup.setFounderId(1L);

        when(startupRepository.findById(1L)).thenReturn(Optional.of(startup));

        assertThrows(UnauthorizedException.class, () -> {
            startupService.deleteStartup(1L);
        });
    }
    @Test
    void testApproveStartup() {

        Startup startup = new Startup();
        startup.setStartupId(1L);
        startup.setFounderId(1L);

        StartupResponse response = new StartupResponse();

        when(startupRepository.findById(1L)).thenReturn(Optional.of(startup));
        when(startupRepository.save(any())).thenReturn(startup);
        doReturn(response).when(modelMapper).map(startup, StartupResponse.class);

        StartupResponse result = startupService.approveStartup(1L);

        assertNotNull(result);
        verify(notificationProducer).sendNotification(any());
    }
    @Test
    void testRejectStartup() {

        Startup startup = new Startup();
        startup.setStartupId(1L);
        startup.setFounderId(1L);

        StartupResponse response = new StartupResponse();

        when(startupRepository.findById(1L)).thenReturn(Optional.of(startup));
        when(startupRepository.save(any())).thenReturn(startup);
        doReturn(response).when(modelMapper).map(startup, StartupResponse.class);

        StartupResponse result = startupService.rejectStartup(1L);

        assertNotNull(result);
        verify(notificationProducer).sendNotification(any());
    }
    @Test
    void testGetAllStartupsPage() {

        Startup startup = new Startup();
        startup.setStartupId(1L);

        Page<Startup> page = new PageImpl<>(List.of(startup));

        when(startupRepository.findAll(any(Pageable.class))).thenReturn(page);

        StartupResponse response = new StartupResponse();

        doReturn(response).when(modelMapper).map(any(Startup.class), eq(StartupResponse.class));

        PageResponse<StartupResponse> result =
                startupService.getAllStartupsPage(0, 10, "startupId", "asc");

        assertEquals(1, result.getListContent().size());
    }
}
