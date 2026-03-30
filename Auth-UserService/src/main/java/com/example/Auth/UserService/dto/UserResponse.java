package com.example.Auth.UserService.dto;



import com.example.Auth.UserService.enums.Role;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserResponse {
    private Long userId;
    private String name;
    private String email;
    private Role role;
    private String skills;
    private String experience;
    private String bio;
    private String portfolioLinks;
    private String location;
    private String companyName;
}
