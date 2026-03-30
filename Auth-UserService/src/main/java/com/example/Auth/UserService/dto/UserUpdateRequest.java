package com.example.Auth.UserService.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserUpdateRequest {
    @Size(max = 500, message = "Skills cannot exceed 500 characters")
    private String skills;

    @Size(max = 300, message = "Experience cannot exceed 300 characters")
    private String experience;

    @Size(max = 1000, message = "Bio cannot exceed 1000 characters")
    private String bio;

    @Size(max = 500, message = "Portfolio links cannot exceed 500 characters")
    private String portfolioLinks;

    @Size(max = 200, message = "Location cannot exceed 200 characters")
    private String location;

    @Size(max = 200, message = "Companyname cannot exceed 200 characters")
    private String companyName;
}
