package com.example.Auth.UserService.service;

import com.example.Auth.UserService.dto.*;
import com.example.Auth.UserService.entity.RefreshToken;
import com.example.Auth.UserService.entity.UserDetails;
import com.example.Auth.UserService.entity.UserRegistration;
import com.example.Auth.UserService.exception.*;
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

import java.time.LocalDateTime;
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
        request.setEmail("test@gmail.com");
        request.setPassword("123456");
        request.setRole(Role.ROLE_INVESTOR);

        UserRegistration userReg = new UserRegistration();
        UserRegistration savedUser = new UserRegistration();
        savedUser.setUserId(1L);

        UserDetails userDetails = new UserDetails();
        userDetails.setUserId(1L);

        RegisterResponse response = new RegisterResponse();
        response.setUserId(1L);
        response.setMessage("User registered successfully");

        when(userRegistrationRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRegistrationRepository.save(any())).thenReturn(savedUser);
        when(userDetailsRepository.save(any())).thenReturn(userDetails);

        doReturn(userReg).when(modelMapper).map(any(RegisterRequest.class), eq(UserRegistration.class));
        doReturn(userDetails).when(modelMapper).map(any(UserRegistration.class), eq(UserDetails.class));
        doReturn(response).when(modelMapper).map(any(UserDetails.class), eq(RegisterResponse.class));

        RegisterResponse result = authService.register(request);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        verify(userRegistrationRepository).save(any());
        verify(userDetailsRepository).save(any());
    }

    @Test
    void testRegister_EmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@gmail.com");

        when(userRegistrationRepository.existsByEmail("test@gmail.com")).thenReturn(true);

        assertThrows(UserAlreadyExistException.class, () -> authService.register(request));
    }

    @Test
    void testRegister_AdminNotAllowed() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@gmail.com");
        request.setRole(Role.ROLE_ADMIN);

        when(userRegistrationRepository.existsByEmail(any())).thenReturn(false);

        assertThrows(UnauthorizedRoleException.class, () -> authService.register(request));
    }


    @Test
    void testLogin_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("123");

        UserRegistration user = new UserRegistration();
        user.setUserId(1L);
        user.setPassword("encoded");

        when(userRegistrationRepository.findByEmail(any()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtUtil.generateToken(any())).thenReturn("token");

        when(modelMapper.map(any(), eq(LoginResponse.class)))
                .thenReturn(new LoginResponse());

        LoginResponse result = authService.login(request);

        assertNotNull(result);
        verify(refreshTokenRepository).deleteByUserId(1L);
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void testLogin_UserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");

        when(userRegistrationRepository.findByEmail(any()))
                .thenReturn(Optional.empty());

        assertThrows(InvalidCredintialsException.class, () -> authService.login(request));
    }

    @Test
    void testLogin_WrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("wrong");

        UserRegistration user = new UserRegistration();
        user.setPassword("encoded");

        when(userRegistrationRepository.findByEmail(any()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(InvalidCredintialsException.class, () -> authService.login(request));
    }


    @Test
    void testRefreshToken_Success() {
        RefreshTokenRequest request = new RefreshTokenRequest("abc");

        RefreshToken token = new RefreshToken();
        token.setUserId(1L);
        token.setExpiryDate(LocalDateTime.now().plusDays(1));

        UserRegistration user = new UserRegistration();
        user.setUserId(1L);

        when(refreshTokenRepository.findByToken("abc")).thenReturn(Optional.of(token));
        when(userRegistrationRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(any())).thenReturn("newToken");

        when(modelMapper.map(any(), eq(LoginResponse.class)))
                .thenReturn(new LoginResponse());

        LoginResponse result = authService.refreshToken(request);

        assertNotNull(result);
    }

    @Test
    void testRefreshToken_NotFound() {
        when(refreshTokenRepository.findByToken(any()))
                .thenReturn(Optional.empty());

        assertThrows(RefreshTokenNotFoundException.class,
                () -> authService.refreshToken(new RefreshTokenRequest("abc")));
    }

    @Test
    void testRefreshToken_Expired() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(LocalDateTime.now().minusDays(1));

        when(refreshTokenRepository.findByToken(any()))
                .thenReturn(Optional.of(token));

        assertThrows(RefreshTokenExpiredException.class,
                () -> authService.refreshToken(new RefreshTokenRequest("abc")));
    }


    @Test
    void testLogout_Success() {
        RefreshToken token = new RefreshToken();

        when(refreshTokenRepository.findByToken(any()))
                .thenReturn(Optional.of(token));

        authService.logout(new RefreshTokenRequest("abc"));

        verify(refreshTokenRepository).delete(token);
    }

    @Test
    void testLogout_TokenNotFound() {
        when(refreshTokenRepository.findByToken(any()))
                .thenReturn(Optional.empty());

        assertThrows(RefreshTokenNotFoundException.class,
                () -> authService.logout(new RefreshTokenRequest("abc")));
    }

    // ================= CREATE ADMIN =================

    @Test
    void testCreateAdmin_AlreadyExists() {
        when(userRegistrationRepository.existsByRole(Role.ROLE_ADMIN))
                .thenReturn(true);

        authService.createADminIfNotThere();

        verify(userRegistrationRepository, never()).save(any());
    }

    @Test
    void testCreateAdmin_NewAdminCreated() {
        when(userRegistrationRepository.existsByRole(Role.ROLE_ADMIN))
                .thenReturn(false);

        when(passwordEncoder.encode(any())).thenReturn("encoded");

        authService.createADminIfNotThere();

        verify(userRegistrationRepository).save(any());
    }

}
