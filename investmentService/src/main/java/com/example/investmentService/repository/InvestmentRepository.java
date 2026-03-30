package com.example.investmentService.repository;

import com.example.investmentService.entity.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    public List<Investment> findByStartupId(Long startupId);

    public List<Investment> findByInvestorId(Long investorId);

}
