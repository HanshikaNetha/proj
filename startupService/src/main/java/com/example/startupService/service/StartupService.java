package com.example.startupService.service;

import com.example.startupService.dto.*;
import com.example.startupService.entity.Startup;
import com.example.startupService.enums.ApprovalStatus;
import com.example.startupService.enums.NotificationType;
import com.example.startupService.exception.StartupNotFoundException;
import com.example.startupService.exception.UnauthorizedException;
import com.example.startupService.feign.UserClient;
import com.example.startupService.producer.NotificationProducer;
import com.example.startupService.repository.StartupRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StartupService {
    private final StartupRepository startupRepository;
    private final ModelMapper modelMapper;
    private final UserClient userClient;
    private final NotificationProducer notificationProducer;

    public StartupResponse createStartup(StartupCreateRequest request) {
        Long founderId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserResponse userResponse=userClient.getUserById(founderId);
        String role = userResponse.getRole().toString();
        if (!role.equalsIgnoreCase("ROLE_FOUNDER")) {
            throw new UnauthorizedException("Only founders can create startups");
        }
        Startup startup = modelMapper.map(request, Startup.class);
        startup.setFounderId(founderId);
        startup.setApprovalStatus(ApprovalStatus.PENDING);
        startup.setCreatedAt(LocalDateTime.now());
        startup.setUpdatedAt(LocalDateTime.now());
        Startup savedStartup = startupRepository.save(startup);

        NotificationEvent notificationEvent=new NotificationEvent();
        notificationEvent.setUserId(founderId);
        notificationEvent.setTitle("startup created");
        notificationEvent.setMessage("your startup "+ savedStartup.getStartupName()+" has been created");
        notificationEvent.setType(NotificationType.STARTUP_CREATED);
        notificationProducer.sendNotification(notificationEvent);

        StartupResponse response = modelMapper.map(savedStartup, StartupResponse.class);
        response.setMessage("startup "+response.getStartupName()+" is created");
        return response;
    }

    public PageResponse<StartupResponse> getAllStartupsPage(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Startup> startupPage = startupRepository.findAll(pageable);
        List<StartupResponse> content = startupPage.getContent().stream().map(startup -> modelMapper.map(startup, StartupResponse.class)).toList();
        return new PageResponse<>(content, startupPage.getNumber(), startupPage.getSize(), startupPage.getTotalElements(), startupPage.getTotalPages());
    }

    public StartupResponse getStartupById(Long id) {
        Startup startup = startupRepository.findById(id).orElseThrow(() -> new StartupNotFoundException("Startup not found"));
        StartupResponse response= modelMapper.map(startup, StartupResponse.class);
        return response;
    }

    public StartupResponse updateStartup(Long id, StartupUpdateRequest request) {
        Startup startup = startupRepository.findById(id).orElseThrow(() -> new StartupNotFoundException("Startup not found"));
        Long currentUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!startup.getFounderId().equals(currentUserId)) {
            throw new UnauthorizedException("You are not allowed to update this startup");
        }
        if (request.getStartupName() != null) startup.setStartupName(request.getStartupName());
        if (request.getDescription() != null) startup.setDescription(request.getDescription());
        if (request.getIndustry() != null) startup.setIndustry(request.getIndustry());
        if (request.getProblemStatement() != null) startup.setProblemStatement(request.getProblemStatement());
        if (request.getSolution() != null) startup.setSolution(request.getSolution());
        if (request.getFundingGoal() != null) startup.setFundingGoal(request.getFundingGoal());
        if (request.getStage() != null) startup.setStage(request.getStage());
        if (request.getLocation() != null) startup.setLocation(request.getLocation());
        startup.setUpdatedAt(LocalDateTime.now());
        Startup updated = startupRepository.save(startup);
        StartupResponse response= modelMapper.map(updated, StartupResponse.class);
        response.setMessage("startup is updaated");
        return response;
    }

    public String deleteStartup(Long id) {
        Startup startup = startupRepository.findById(id).orElseThrow(() -> new StartupNotFoundException("Startup not found"));
        Long currentUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!startup.getFounderId().equals(currentUserId)) {
            throw new UnauthorizedException("You are not allowed to delete this startup");
        }
        startupRepository.delete(startup);
        return "Startup deleted successfully";
    }

    public StartupResponse approveStartup(Long id) {
        Startup startup = startupRepository.findById(id).orElseThrow(() -> new StartupNotFoundException("Startup not found"));
        startup.setApprovalStatus(ApprovalStatus.APPROVED);
        startup.setUpdatedAt(LocalDateTime.now());
        startupRepository.save(startup);

        NotificationEvent notificationEvent=new NotificationEvent();
        notificationEvent.setUserId(startup.getFounderId());
        notificationEvent.setTitle("startup approved");
        notificationEvent.setMessage("your startup "+startup.getStartupName()+" has been approved");
        notificationEvent.setType(NotificationType.STARTUP_APPROVED);
        notificationProducer.sendNotification(notificationEvent);

        StartupResponse response=modelMapper.map(startup, StartupResponse.class);
        response.setMessage("startup is approved");
        return response;
    }

    public StartupResponse rejectStartup(Long id) {
        Startup startup = startupRepository.findById(id).orElseThrow(() -> new StartupNotFoundException("Startup not found"));
        startup.setApprovalStatus(ApprovalStatus.REJECTED);
        startup.setUpdatedAt(LocalDateTime.now());
        startupRepository.save(startup);

        NotificationEvent notificationEvent=new NotificationEvent();
        notificationEvent.setUserId(startup.getFounderId());
        notificationEvent.setTitle("startup rejected");
        notificationEvent.setMessage("your startup "+startup.getStartupName()+" has been rejected");
        notificationEvent.setType(NotificationType.STARTUP_REJECTED);
        notificationProducer.sendNotification(notificationEvent);

        StartupResponse response=modelMapper.map(startup, StartupResponse.class);
        response.setMessage("startup is rejected");
        return response;
    }
}
