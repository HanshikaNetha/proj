package com.example.investmentService.service;

import com.example.investmentService.dto.*;
import com.example.investmentService.entity.Investment;
import com.example.investmentService.enums.InvestmentStatus;
import com.example.investmentService.enums.NotificationType;
import com.example.investmentService.enums.Role;
import com.example.investmentService.exception.InvestmentNotFoundException;
import com.example.investmentService.exception.UnauthorizedException;
import com.example.investmentService.feign.StartupClient;
import com.example.investmentService.feign.UserClient;
import com.example.investmentService.producer.NotificationProducer;
import com.example.investmentService.repository.InvestmentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestmentService {
    private final InvestmentRepository investmentRepository;
    private final UserClient userClient;
    private final StartupClient startupClient;
    private final ModelMapper modelMapper;
    private final NotificationProducer notificationProducer;

    public InvestmentResponse createInvestment(InvestmentCreateRequest request) {
        Long investorId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserResponse user = userClient.getUserById(investorId);
        String role=user.getRole().toString();
        if (!role.equals("ROLE_INVESTOR")) {
            throw new UnauthorizedException("Only investors can invest");
        }
        StartupResponse startup = startupClient.getStartupById(request.getStartupId());
        Investment investment = new Investment();
        investment.setStartupId(startup.getStartupId());
        investment.setInvestorId(investorId);
        investment.setFounderId(startup.getFounderId());
        investment.setAmount(request.getAmount());
        investment.setStatus(InvestmentStatus.PENDING);
        investment.setCreatedAt(LocalDateTime.now());
        investment.setUpdatedAt(LocalDateTime.now());
        Investment saved = investmentRepository.save(investment);

        NotificationEvent notificationEvent=new NotificationEvent();
        notificationEvent.setUserId(startup.getFounderId());
        notificationEvent.setTitle("new investment");
        notificationEvent.setMessage("new investment is received for your startup "+startup.getStartupName()+" of amount "+saved.getAmount());
        notificationEvent.setType(NotificationType.INVESTMENT_CREATED);
        notificationProducer.sendNotification(notificationEvent);

        InvestmentResponse response = modelMapper.map(saved, InvestmentResponse.class);
        response.setMessage("Investment created successfully");
        return response;
    }

    public InvestmentResponse approveInvestment(Long id) {
        Investment investment = investmentRepository.findById(id).orElseThrow(() -> new InvestmentNotFoundException("Investment not found"));
        Long currentUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!investment.getFounderId().equals(currentUserId)) {
            throw new UnauthorizedException("You are not allowed to approve this investment");
        }
        investment.setStatus(InvestmentStatus.APPROVED);
        investment.setUpdatedAt(LocalDateTime.now());
        Investment saved = investmentRepository.save(investment);

        NotificationEvent notificationEvent=new NotificationEvent();
        notificationEvent.setUserId(investment.getInvestorId());
        notificationEvent.setTitle("investment approved");
        notificationEvent.setMessage("you investment has been approved for startup id--"+investment.getStartupId() );
        notificationEvent.setType(NotificationType.INVESTMENT_APPROVED);
        notificationProducer.sendNotification(notificationEvent);

        InvestmentResponse response = modelMapper.map(saved, InvestmentResponse.class);
        response.setMessage("Investment approved");
        return response;
    }

    public InvestmentResponse rejectInvestment(Long id) {
        Investment investment = investmentRepository.findById(id).orElseThrow(() -> new InvestmentNotFoundException("Investment not found"));
        Long currentUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!investment.getFounderId().equals(currentUserId)) {
            throw new UnauthorizedException("You are not allowed to reject this investment");
        }
        investment.setStatus(InvestmentStatus.REJECTED);
        investment.setUpdatedAt(LocalDateTime.now());
        Investment saved = investmentRepository.save(investment);

        NotificationEvent notificationEvent=new NotificationEvent();
        notificationEvent.setUserId(investment.getInvestorId());
        notificationEvent.setTitle("investment rejected");
        notificationEvent.setMessage("your investment has been rejected for startup id-"+investment.getStartupId());
        notificationEvent.setType(NotificationType.INVESTMENT_REJECTED);
        notificationProducer.sendNotification(notificationEvent);

        InvestmentResponse response = modelMapper.map(saved, InvestmentResponse.class);
        response.setMessage("Investment rejected");
        return response;
    }

    public InvestmentResponse completeInvestment(Long id) {
        Investment investment = investmentRepository.findById(id).orElseThrow(() -> new InvestmentNotFoundException("Investment not found"));
        Long currentUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!investment.getFounderId().equals(currentUserId)) {
            throw new UnauthorizedException("You are not allowed to complete this investment");
        }
        if (!investment.getStatus().equals(InvestmentStatus.APPROVED)) {
            throw new RuntimeException("Only approved investments can be completed");
        }
        investment.setStatus(InvestmentStatus.COMPLETED);
        investment.setUpdatedAt(LocalDateTime.now());
        Investment saved = investmentRepository.save(investment);
        InvestmentResponse response = modelMapper.map(saved, InvestmentResponse.class);
        response.setMessage("Investment completed");
        return response;
    }

    public List<InvestmentResponse> getInvestmentsByStartup(Long startupId) {
        Long currentUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        StartupResponse startup = startupClient.getStartupById(startupId);
        if (!startup.getFounderId().equals(currentUserId)) {
            throw new UnauthorizedException("You are not allowed to view this startup's investments");
        }
        List<Investment> investments = investmentRepository.findByStartupId(startupId);
        List<InvestmentResponse> investmentResponses=investments.stream().map(i -> modelMapper.map(i, InvestmentResponse.class)).toList();
        return investmentResponses;
    }

    public List<InvestmentResponse>  getMyInvestments() {
        Long currentUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Investment> investments = investmentRepository.findByInvestorId(currentUserId);
        List<InvestmentResponse> investmentResponses=investments.stream().map(i -> modelMapper.map(i, InvestmentResponse.class)).toList();
        return investmentResponses;
    }
}
