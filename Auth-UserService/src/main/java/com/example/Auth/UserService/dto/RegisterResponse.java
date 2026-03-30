package com.example.Auth.UserService.dto;

import com.example.Auth.UserService.enums.Role;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RegisterResponse {
    private Long userId;
    private String email;
    private Role role;
    private String message;
}
