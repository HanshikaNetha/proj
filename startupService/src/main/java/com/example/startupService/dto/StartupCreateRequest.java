package com.example.startupService.dto;

import com.example.startupService.enums.StartupStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartupCreateRequest {
    @NotBlank(message = "Startup name is mandatory")
    private String startupName;

    @NotBlank(message = "Description is mandatory")
    private String description;
    private String industry;
    private String problemStatement;
    private String solution;

    @NotNull(message = "Funding goal is mandatory")
    private Double fundingGoal;

    @NotNull(message = "Stage is mandatory")
    private StartupStage stage;
    private String location;
}
