package com.example.investmentService.entity;

import com.example.investmentService.enums.InvestmentStatus;
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
@Table(name = "investments")
public class Investment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long investmentId;

    @Column(nullable = false)
    private Long startupId;

    @Column(nullable = false)
    private Long investorId;

    @Column(nullable = false)
    private Long founderId;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    private InvestmentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
