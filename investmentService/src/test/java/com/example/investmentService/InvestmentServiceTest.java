package com.example.investmentService;

import com.example.investmentService.dto.InvestmentCreateRequest;
import com.example.investmentService.dto.InvestmentResponse;
import com.example.investmentService.dto.StartupResponse;
import com.example.investmentService.dto.UserResponse;
import com.example.investmentService.entity.Investment;
import com.example.investmentService.enums.InvestmentStatus;
import com.example.investmentService.enums.Role;
import com.example.investmentService.exception.InvestmentNotFoundException;
import com.example.investmentService.exception.UnauthorizedException;
import com.example.investmentService.feign.StartupClient;
import com.example.investmentService.feign.UserClient;
import com.example.investmentService.producer.NotificationProducer;
import com.example.investmentService.repository.InvestmentRepository;
import com.example.investmentService.service.InvestmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvestmentServiceTest {
    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private StartupClient startupClient;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private NotificationProducer notificationProducer;

    @InjectMocks
    private InvestmentService investmentService;

    // Helper for SecurityContext
    private void mockUser(Long userId) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userId, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


    @Test
    void testCreateInvestment_Success() {
        mockUser(1L);

        InvestmentCreateRequest request = new InvestmentCreateRequest(10L, 5000.0);

        UserResponse user = new UserResponse();
        user.setRole(Role.ROLE_INVESTOR);

        StartupResponse startup = new StartupResponse();
        startup.setStartupId(10L);
        startup.setFounderId(2L);
        startup.setStartupName("Test Startup");

        Investment saved = new Investment();
        saved.setInvestmentId(1L);
        saved.setAmount(5000.0);

        InvestmentResponse response = new InvestmentResponse();
        response.setInvestmentId(1L);

        when(userClient.getUserById(1L)).thenReturn(user);
        when(startupClient.getStartupById(10L)).thenReturn(startup);
        when(investmentRepository.save(any())).thenReturn(saved);

        doReturn(response)
                .when(modelMapper)
                .map(saved, InvestmentResponse.class);

        InvestmentResponse result = investmentService.createInvestment(request);

        assertNotNull(result);
        verify(notificationProducer).sendNotification(any());
    }

    @Test
    void testCreateInvestment_NotInvestor() {
        mockUser(1L);

        UserResponse user = new UserResponse();
        user.setRole(Role.ROLE_FOUNDER);

        when(userClient.getUserById(1L)).thenReturn(user);

        assertThrows(UnauthorizedException.class, () ->
                investmentService.createInvestment(new InvestmentCreateRequest()));
    }


    @Test
    void testApproveInvestment_Success() {
        mockUser(2L);

        Investment investment = new Investment();
        investment.setInvestmentId(1L);
        investment.setFounderId(2L);

        InvestmentResponse response = new InvestmentResponse();

        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));
        when(investmentRepository.save(any())).thenReturn(investment);

        doReturn(response)
                .when(modelMapper)
                .map(investment, InvestmentResponse.class);

        InvestmentResponse result = investmentService.approveInvestment(1L);

        assertNotNull(result);
        verify(notificationProducer).sendNotification(any());
    }

    @Test
    void testApproveInvestment_Unauthorized() {
        mockUser(1L);

        Investment investment = new Investment();
        investment.setFounderId(2L);

        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));

        assertThrows(UnauthorizedException.class,
                () -> investmentService.approveInvestment(1L));
    }


    @Test
    void testCompleteInvestment_Success() {
        mockUser(2L);

        Investment investment = new Investment();
        investment.setFounderId(2L);
        investment.setStatus(InvestmentStatus.APPROVED);

        InvestmentResponse response = new InvestmentResponse();

        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));
        when(investmentRepository.save(any())).thenReturn(investment);

        doReturn(response)
                .when(modelMapper)
                .map(investment, InvestmentResponse.class);

        InvestmentResponse result = investmentService.completeInvestment(1L);

        assertNotNull(result);
    }

    @Test
    void testCompleteInvestment_NotApproved() {
        mockUser(2L);

        Investment investment = new Investment();
        investment.setFounderId(2L);
        investment.setStatus(InvestmentStatus.PENDING);

        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));

        assertThrows(RuntimeException.class,
                () -> investmentService.completeInvestment(1L));
    }


    @Test
    void testGetInvestmentsByStartup_Success() {
        mockUser(2L);

        StartupResponse startup = new StartupResponse();
        startup.setFounderId(2L);

        Investment inv = new Investment();

        InvestmentResponse response = new InvestmentResponse();

        when(startupClient.getStartupById(10L)).thenReturn(startup);
        when(investmentRepository.findByStartupId(10L))
                .thenReturn(List.of(inv));

        doReturn(response)
                .when(modelMapper)
                .map(any(Investment.class), eq(InvestmentResponse.class));

        List<InvestmentResponse> result =
                investmentService.getInvestmentsByStartup(10L);

        assertEquals(1, result.size());
    }

    @Test
    void testGetInvestmentsByStartup_Unauthorized() {
        mockUser(1L);

        StartupResponse startup = new StartupResponse();
        startup.setFounderId(2L);

        when(startupClient.getStartupById(10L)).thenReturn(startup);

        assertThrows(UnauthorizedException.class,
                () -> investmentService.getInvestmentsByStartup(10L));
    }


    @Test
    void testGetMyInvestments() {
        mockUser(1L);

        Investment inv = new Investment();

        InvestmentResponse response = new InvestmentResponse();

        when(investmentRepository.findByInvestorId(1L))
                .thenReturn(List.of(inv));

        doReturn(response)
                .when(modelMapper)
                .map(any(Investment.class), eq(InvestmentResponse.class));

        List<InvestmentResponse> result = investmentService.getMyInvestments();

        assertEquals(1, result.size());
    }


    @Test
    void testApproveInvestment_NotFound() {
        when(investmentRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(InvestmentNotFoundException.class,
                () -> investmentService.approveInvestment(1L));
    }

    @Test
    void testRejectInvestment_Success() {
        mockUser(2L);

        Investment investment = new Investment();
        investment.setInvestmentId(1L);
        investment.setFounderId(2L);

        InvestmentResponse response = new InvestmentResponse();

        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));
        when(investmentRepository.save(any())).thenReturn(investment);

        doReturn(response)
                .when(modelMapper)
                .map(investment, InvestmentResponse.class);

        InvestmentResponse result = investmentService.rejectInvestment(1L);

        assertNotNull(result);
        verify(notificationProducer).sendNotification(any());
    }
    @Test
    void testRejectInvestment_Unauthorized() {
        mockUser(1L);

        Investment investment = new Investment();
        investment.setFounderId(2L);

        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));

        assertThrows(UnauthorizedException.class,
                () -> investmentService.rejectInvestment(1L));
    }
    @Test
    void testCompleteInvestment_Unauthorized() {
        mockUser(1L);

        Investment investment = new Investment();
        investment.setFounderId(2L);

        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));

        assertThrows(UnauthorizedException.class,
                () -> investmentService.completeInvestment(1L));
    }
    @Test
    void testGetMyInvestments_Empty() {
        mockUser(1L);

        when(investmentRepository.findByInvestorId(1L))
                .thenReturn(List.of());

        List<InvestmentResponse> result = investmentService.getMyInvestments();

        assertTrue(result.isEmpty());
    }


}
