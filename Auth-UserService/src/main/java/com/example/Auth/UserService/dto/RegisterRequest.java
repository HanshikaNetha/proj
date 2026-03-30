package com.example.Auth.UserService.dto;

import com.example.Auth.UserService.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Please provide your name")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "email is not valid")
    private String email;

    @NotBlank(message = "password must not be null")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Role is required")
    private Role role;
}
