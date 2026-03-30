package com.example.investmentService.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvestmentCreateRequest {
    @NotNull(message = "Startup ID is required")
    private Long startupId;

    @NotNull(message = "Amount is required")
    private Double amount;
}
