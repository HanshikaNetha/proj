package com.example.investmentService.dto;

import com.example.investmentService.enums.InvestmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvestmentResponse {
    private Long investmentId;
    private Long startupId;
    private Long investorId;
    private Long founderId;
    private Double amount;
    private InvestmentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String message;
}
