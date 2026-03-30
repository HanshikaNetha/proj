package com.example.collaborationService.dto;

import com.example.collaborationService.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponse {
    private Long userId;
    private String name;
    private String email;
    private Role role;
}
