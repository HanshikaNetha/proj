package com.example.Auth.UserService.service;

import com.example.Auth.UserService.dto.PageResponse;
import com.example.Auth.UserService.dto.UserResponse;
import com.example.Auth.UserService.dto.UserUpdateRequest;
import com.example.Auth.UserService.entity.UserDetails;
import com.example.Auth.UserService.exception.UserNotFoundException;
import com.example.Auth.UserService.repository.UserDetailsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceTest {
    @Mock
    private UserDetailsRepository userDetailsRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserDetailsService userDetailsService;
    @Test
    void testUpdateUserProfile_Success() {
        Long userId = 1L;
        UserUpdateRequest request = new UserUpdateRequest();
        request.setSkills("Java");
        request.setExperience("2 years");
        UserDetails user = new UserDetails();
        user.setUserId(userId);
        UserResponse response = new UserResponse();
        response.setUserId(userId);
        when(userDetailsRepository.findByUserId(userId))
                .thenReturn(Optional.of(user));
        when(userDetailsRepository.save(any(UserDetails.class)))
                .thenReturn(user);
        doReturn(response)
                .when(modelMapper)
                .map(any(UserDetails.class), eq(UserResponse.class));
        UserResponse result = userDetailsService.updateUserProfile(userId, request);
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
    }
    @Test
    void testUpdateUserProfile_UserNotFound() {
        when(userDetailsRepository.findByUserId(1L))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> {
            userDetailsService.updateUserProfile(1L, new UserUpdateRequest());
        });
    }
    @Test
    void testGetUserById_Success() {
        Long userId = 1L;
        UserDetails user = new UserDetails();
        user.setUserId(userId);
        UserResponse response = new UserResponse();
        response.setUserId(userId);
        when(userDetailsRepository.findByUserId(userId))
                .thenReturn(Optional.of(user));
        doReturn(response)
                .when(modelMapper)
                .map(any(UserDetails.class), eq(UserResponse.class));
        UserResponse result = userDetailsService.getUserById(userId);
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
    }
    @Test
    void testGetUserById_NotFound() {
        when(userDetailsRepository.findByUserId(1L))
                .thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> {
            userDetailsService.getUserById(1L);
        });
    }
    @Test
    void testGetAllUsersPage_Success() {
        UserDetails user = new UserDetails();
        user.setUserId(1L);
        List<UserDetails> userList = List.of(user);
        Page<UserDetails> page = new PageImpl<>(userList);
        when(userDetailsRepository.findAll(any(Pageable.class)))
                .thenReturn(page);
        UserResponse response = new UserResponse();
        response.setUserId(1L);
        doReturn(response)
                .when(modelMapper)
                .map(any(UserDetails.class), eq(UserResponse.class));
        PageResponse<UserResponse> result =
                userDetailsService.getAllUsersPage(0, 10, "userId", "asc");
        assertNotNull(result);
        assertEquals(1, result.getListContent().size());
    }
}
