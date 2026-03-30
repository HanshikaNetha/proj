package com.example.startupService.dto;

import com.example.startupService.enums.ApprovalStatus;
import com.example.startupService.enums.StartupStage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartupResponse {
    private Long startupId;
    private String startupName;
    private String description;
    private String industry;
    private String problemStatement;
    private String solution;
    private Double fundingGoal;
    private StartupStage stage;
    private Long founderId;
    private String location;
    private ApprovalStatus approvalStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String message;
}
