package com.example.Auth.UserService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Enter email")
    @Email(message = "Enter a valid email")
    private String email;

    @NotBlank(message = "Fill the password")
    private String password;
}
