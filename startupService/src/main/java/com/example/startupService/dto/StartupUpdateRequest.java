package com.example.startupService.dto;

import com.example.startupService.enums.StartupStage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartupUpdateRequest {
    private String startupName;
    private String description;
    private String industry;
    private String problemStatement;
    private String solution;
    private Double fundingGoal;
    private StartupStage stage;
    private String location;
}
