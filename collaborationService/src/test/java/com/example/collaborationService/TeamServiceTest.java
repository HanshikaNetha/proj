package com.example.collaborationService;

import com.example.collaborationService.dto.*;
import com.example.collaborationService.entity.TeamInvitation;
import com.example.collaborationService.entity.TeamMember;
import com.example.collaborationService.exception.UnauthorizedException;
import com.example.collaborationService.feign.StartupClient;
import com.example.collaborationService.feign.UserClient;
import com.example.collaborationService.producer.NotificationProducer;
import com.example.collaborationService.repository.TeamInvitationRepository;
import com.example.collaborationService.repository.TeamMemberRepository;
import com.example.collaborationService.service.TeamService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {
    @Mock
    private TeamInvitationRepository teamInvitationRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private StartupClient startupClient;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private NotificationProducer notificationProducer;

    @InjectMocks
    private TeamService teamService;

    private void mockUser(Long id) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(id, null, List.of())
        );
    }


    @Test
    void testInvite_Success() {
        mockUser(1L);

        TeamInviteRequest request = new TeamInviteRequest(10L, 2L, null);

        StartupResponse startup = new StartupResponse();
        startup.setFounderId(1L);

        when(startupClient.getStartupById(10L)).thenReturn(startup);
        when(userClient.getUserById(2L)).thenReturn(new UserResponse());

        TeamInvitation saved = new TeamInvitation();
        saved.setInvitationId(1L);

        when(teamInvitationRepository.save(any())).thenReturn(saved);

        InvitationResponse response = new InvitationResponse();

        doReturn(response).when(modelMapper).map(saved, InvitationResponse.class);

        InvitationResponse result = teamService.inviteTeamMember(request);

        assertNotNull(result);
        verify(notificationProducer).sendNotification(any());
    }

    @Test
    void testInvite_Unauthorized() {
        mockUser(2L);

        StartupResponse startup = new StartupResponse();
        startup.setFounderId(1L);

        when(startupClient.getStartupById(any())).thenReturn(startup);

        assertThrows(UnauthorizedException.class,
                () -> teamService.inviteTeamMember(new TeamInviteRequest(10L, 2L, null)));
    }


    @Test
    void testAccept_Success() {
        mockUser(2L);

        TeamInvitation invitation = new TeamInvitation();
        invitation.setInvitedUserId(2L);
        invitation.setStartupId(10L);

        when(teamInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));

        when(teamMemberRepository.save(any())).thenReturn(new TeamMember());
        when(teamInvitationRepository.save(any())).thenReturn(invitation);

        InvitationResponse response = new InvitationResponse();

        doReturn(response).when(modelMapper).map(invitation, InvitationResponse.class);

        InvitationResponse result = teamService.accept(1L);

        assertNotNull(result);
    }

    @Test
    void testAccept_Unauthorized() {
        mockUser(3L);

        TeamInvitation invitation = new TeamInvitation();
        invitation.setInvitedUserId(2L);

        when(teamInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));

        assertThrows(UnauthorizedException.class,
                () -> teamService.accept(1L));
    }


    @Test
    void testReject_Success() {
        mockUser(2L);

        TeamInvitation invitation = new TeamInvitation();
        invitation.setInvitedUserId(2L);

        when(teamInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(teamInvitationRepository.save(any())).thenReturn(invitation);

        InvitationResponse response = new InvitationResponse();

        doReturn(response).when(modelMapper).map(invitation, InvitationResponse.class);

        InvitationResponse result = teamService.reject(1L);

        assertNotNull(result);
    }


    @Test
    void testGetTeamMembers() {
        TeamMember member = new TeamMember();

        when(teamMemberRepository.findByStartupId(10L))
                .thenReturn(List.of(member));

        TeamMemberResponse response = new TeamMemberResponse();

        doReturn(response)
                .when(modelMapper)
                .map(any(TeamMember.class), eq(TeamMemberResponse.class));

        List<TeamMemberResponse> result = teamService.getTeamMembers(10L);

        assertEquals(1, result.size());
    }

}
