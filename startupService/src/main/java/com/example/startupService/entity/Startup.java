package com.example.startupService.entity;

import com.example.startupService.enums.ApprovalStatus;
import com.example.startupService.enums.StartupStage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "startups")
public class Startup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long startupId;

    @Column(nullable = false)
    private String startupName;

    @Column(columnDefinition = "TEXT")
    private String description;
    private String industry;

    @Column(columnDefinition = "TEXT")
    private String problemStatement;

    @Column(columnDefinition = "TEXT")
    private String solution;
    private Double fundingGoal;

    @Enumerated(EnumType.STRING)
    private StartupStage stage;
    private Long founderId;
    private String location;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
