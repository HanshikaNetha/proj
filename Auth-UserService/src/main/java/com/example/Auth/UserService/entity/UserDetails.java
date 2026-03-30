package com.example.Auth.UserService.entity;

import com.example.Auth.UserService.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailsId;

    @Column(nullable = false)
    private Long userId;
    private String name;
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;
    private String skills;
    private String experience;
    private String bio;
    private String portfolioLinks;
    private String location;
    private String companyName;
    private LocalDateTime updatedAt;
}
