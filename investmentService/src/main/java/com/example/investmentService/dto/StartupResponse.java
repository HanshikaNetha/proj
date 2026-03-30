package com.example.investmentService.dto;

import com.example.investmentService.enums.ApprovalStatus;
import com.example.investmentService.enums.StartupStage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartupResponse {
    private Long startupId;
    private String startupName;
    private Double fundingGoal;
    private Long founderId;
    private StartupStage stage;
    private ApprovalStatus approvalStatus;
}
