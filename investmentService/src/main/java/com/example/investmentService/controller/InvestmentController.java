package com.example.investmentService.controller;

import com.example.investmentService.dto.InvestmentCreateRequest;
import com.example.investmentService.dto.InvestmentResponse;
import com.example.investmentService.service.InvestmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/investments")
@RequiredArgsConstructor
public class InvestmentController {
    private final InvestmentService investmentService;

    @PostMapping("/createInvestment")
    @PreAuthorize("hasAuthority('ROLE_INVESTOR')")
    public ResponseEntity<InvestmentResponse> createInvestment(@Valid @RequestBody InvestmentCreateRequest request) {
        InvestmentResponse response = investmentService.createInvestment(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/approve/{id}")
    @PreAuthorize("hasAuthority('ROLE_FOUNDER')")
    public ResponseEntity<InvestmentResponse> approve(@PathVariable Long id) {
        InvestmentResponse investmentResponse=investmentService.approveInvestment(id);
        return ResponseEntity.ok(investmentResponse);
    }

    @PutMapping("/reject/{id}")
    @PreAuthorize("hasAuthority('ROLE_FOUNDER')")
    public ResponseEntity<InvestmentResponse> reject(@PathVariable Long id) {
        InvestmentResponse investmentResponse=investmentService.rejectInvestment(id);
        return ResponseEntity.ok(investmentResponse);
    }

    @PutMapping("/complete/{id}")
    @PreAuthorize("hasAuthority('ROLE_FOUNDER')")
    public ResponseEntity<InvestmentResponse> complete(@PathVariable Long id) {
        InvestmentResponse investmentResponse=investmentService.completeInvestment(id);
        return ResponseEntity.ok(investmentResponse);    }

    @GetMapping("/getByStartup/{startupId}")
    @PreAuthorize("hasAuthority('ROLE_FOUNDER')")
    public ResponseEntity<List<InvestmentResponse>> getByStartup(@PathVariable Long startupId) {
        List<InvestmentResponse> investmentResponses=investmentService.getInvestmentsByStartup(startupId);
        return ResponseEntity.ok(investmentResponses);
    }

    @GetMapping("/getMyInvestments")
    @PreAuthorize("hasAuthority('ROLE_INVESTOR')")
    public ResponseEntity<List<InvestmentResponse>> getMyInvestments() {
        List<InvestmentResponse> investmentResponses=investmentService.getMyInvestments();
        return ResponseEntity.ok(investmentResponses);    }
}
