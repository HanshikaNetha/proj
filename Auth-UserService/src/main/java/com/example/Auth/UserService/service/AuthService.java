package com.example.Auth.UserService.service;

import com.example.Auth.UserService.dto.*;
import com.example.Auth.UserService.entity.RefreshToken;
import com.example.Auth.UserService.entity.UserDetails;
import com.example.Auth.UserService.entity.UserRegistration;
import com.example.Auth.UserService.enums.Role;
import com.example.Auth.UserService.exception.*;
import com.example.Auth.UserService.repository.RefreshTokenRepository;
import com.example.Auth.UserService.repository.UserDetailsRepository;
import com.example.Auth.UserService.repository.UserRegistrationRepository;
import com.example.Auth.UserService.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRegistrationRepository userRegistrationRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtUtil jwtUtil;

    public RegisterResponse register(RegisterRequest request) {
        if (userRegistrationRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistException("Email already exists");
        }
        Role role;
        try {
            role = request.getRole();
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException("you are supposed to give correct role");
        }

        if (request.getRole() == Role.ROLE_ADMIN) {
            throw new UnauthorizedRoleException("You are not allowed to register as ADMIN");
        }
        UserRegistration userRegistration=modelMapper.map(request, UserRegistration.class);
        userRegistration.setPassword(passwordEncoder.encode(request.getPassword()));
        userRegistration.setCreatedAt(LocalDateTime.now());
        userRegistration.setEnabled(true);
        UserRegistration savedUser = userRegistrationRepository.save(userRegistration);

        UserDetails userDetails=modelMapper.map(savedUser, UserDetails.class);
        userDetails.setUserId(savedUser.getUserId());
        userDetails.setUpdatedAt(LocalDateTime.now());
        userDetailsRepository.save(userDetails);

        RegisterResponse registerResponse=modelMapper.map(userDetails, RegisterResponse.class);
        registerResponse.setUserId(userDetails.getUserId());
        registerResponse.setMessage("User registered successfully");

        return registerResponse;
    }

    public LoginResponse login(LoginRequest loginRequest){
        UserRegistration user=userRegistrationRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()->new InvalidCredintialsException("Invalid email or password"));
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new InvalidCredintialsException("Invalid email or password");
        }
        String accessToken=jwtUtil.generateToken(user);
        String refreshToken=java.util.UUID.randomUUID().toString();
        refreshTokenRepository.deleteByUserId(user.getUserId());
        RefreshToken tokenSaveinDb = new RefreshToken();
        tokenSaveinDb.setToken(refreshToken);
        tokenSaveinDb.setUserId(user.getUserId());
        tokenSaveinDb.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(tokenSaveinDb);
        LoginResponse loginResponse=modelMapper.map(user, LoginResponse.class);
        loginResponse.setUserId(user.getUserId());
        loginResponse.setMessage("Login successful");
        loginResponse.setToken(accessToken);
        loginResponse.setRefreshToken(refreshToken);
        return loginResponse;
    }

    public void createADminIfNotThere(){
        boolean adminExists = userRegistrationRepository.existsByRole(Role.ROLE_ADMIN);
        if (adminExists) {
            System.out.println("Admin already exists");
            return;
        }
        UserRegistration admin = new UserRegistration();

        admin.setName("Admin");
        admin.setEmail("admin@gmail.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ROLE_ADMIN);

        userRegistrationRepository.save(admin);

        System.out.println("Admin created successfully");
    }

    public LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest){
        String refreshToken=refreshTokenRequest.getRefreshToken();
        RefreshToken tokenEntity=refreshTokenRepository.findByToken(refreshToken).orElseThrow(()->new RefreshTokenNotFoundException("invalid refresh token"));
        if(tokenEntity.getExpiryDate().isBefore(LocalDateTime.now())){
            throw new RefreshTokenExpiredException("Refresh token expired");
        }
        UserRegistration user=userRegistrationRepository.findById(tokenEntity.getUserId()).orElseThrow(()->new UserNotFoundException("User not found"));
        String newAccessToken= jwtUtil.generateToken(user);
        LoginResponse loginResponse=modelMapper.map(user, LoginResponse.class);
        loginResponse.setUserId(user.getUserId());
        loginResponse.setToken(newAccessToken);
        loginResponse.setRefreshToken(refreshToken);
        loginResponse.setMessage("Access token refreshed");
        return loginResponse;
    }

    public void logout(RefreshTokenRequest request){
        String refrehToken= request.getRefreshToken();
        RefreshToken findToken=refreshTokenRepository.findByToken(refrehToken).orElseThrow(()->new RefreshTokenNotFoundException("invalid refresh token"));
        refreshTokenRepository.delete(findToken);
    }
}
