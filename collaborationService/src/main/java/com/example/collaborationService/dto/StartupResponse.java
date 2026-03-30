package com.example.collaborationService.dto;

import com.example.collaborationService.enums.ApprovalStatus;
import com.example.collaborationService.enums.StartupStage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StartupResponse {
    private Long startupId;
    private String startupName;
    private Long founderId;
    private StartupStage stage;
    private ApprovalStatus approvalStatus;
}
