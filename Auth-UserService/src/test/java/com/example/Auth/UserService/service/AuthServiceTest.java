package com.example.Auth.UserService.service;

import com.example.Auth.UserService.dto.LoginRequest;
import com.example.Auth.UserService.dto.LoginResponse;
import com.example.Auth.UserService.dto.RegisterRequest;
import com.example.Auth.UserService.dto.RegisterResponse;
import com.example.Auth.UserService.entity.UserDetails;
import com.example.Auth.UserService.entity.UserRegistration;
import com.example.Auth.UserService.repository.RefreshTokenRepository;
import com.example.Auth.UserService.repository.UserDetailsRepository;
import com.example.Auth.UserService.repository.UserRegistrationRepository;
import com.example.Auth.UserService.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.Auth.UserService.enums.Role;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRegistrationRepository userRegistrationRepository;
    @Mock
    private UserDetailsRepository userDetailsRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthService authService;


    @Test
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setRole(Role.ROLE_INVESTOR);
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setEmail(request.getEmail());
        userRegistration.setPassword(request.getPassword());
        UserRegistration savedUser = new UserRegistration();
        savedUser.setUserId(1L);
        savedUser.setEmail(request.getEmail());
        UserDetails userDetails = new UserDetails();
        userDetails.setUserId(1L);
        userDetails.setEmail(request.getEmail());
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setUserId(1L);
        registerResponse.setMessage("User registered successfully");
        when(userRegistrationRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);
        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encodedPassword");
        when(userRegistrationRepository.save(any(UserRegistration.class)))
                .thenReturn(savedUser);
        when(userDetailsRepository.save(any(UserDetails.class)))
                .thenReturn(userDetails);
        doReturn(userRegistration)
                .when(modelMapper)
                .map(any(RegisterRequest.class), eq(UserRegistration.class));
        doReturn(userDetails)
                .when(modelMapper)
                .map(any(UserRegistration.class), eq(UserDetails.class));
        doReturn(registerResponse)
                .when(modelMapper)
                .map(any(UserDetails.class), eq(RegisterResponse.class));
        RegisterResponse response = authService.register(request);
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("User registered successfully", response.getMessage());
    }
    @Test
    void testRegister_EmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@gmail.com");
        when(userRegistrationRepository.existsByEmail("test@gmail.com"))
                .thenReturn(true);
        assertThrows(RuntimeException.class, () -> {
            authService.register(request);
        });
        verify(userRegistrationRepository, never()).save(any());
    }
    @Test
    void testLogin_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("123456");
        UserRegistration user = new UserRegistration();
        user.setEmail("test@gmail.com");
        user.setPassword("encodedPass");
        user.setUserId(1L);
        when(userRegistrationRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", "encodedPass"))
                .thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn("token");
        when(refreshTokenRepository.save(any())).thenReturn(null);
        when(modelMapper.map(any(), eq(LoginResponse.class)))
                .thenReturn(new LoginResponse());
        var response = authService.login(request);
        assertNotNull(response);
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void testLogin_WrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("wrong");
        UserRegistration user = new UserRegistration();
        user.setPassword("encodedPass");
        when(userRegistrationRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encodedPass"))
                .thenReturn(false);
        assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });
        verify(refreshTokenRepository, never()).save(any());
    }
}
