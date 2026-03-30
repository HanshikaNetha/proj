package com.example.collaborationService.service;

import com.example.collaborationService.dto.*;
import com.example.collaborationService.entity.TeamInvitation;
import com.example.collaborationService.entity.TeamMember;
import com.example.collaborationService.enums.InvitationStatus;
import com.example.collaborationService.enums.NotificationType;
import com.example.collaborationService.exception.InvitationNotFoundException;
import com.example.collaborationService.exception.StartUpFoundNotException;
import com.example.collaborationService.exception.UnauthorizedException;
import com.example.collaborationService.exception.UserNotFoundException;
import com.example.collaborationService.feign.StartupClient;
import com.example.collaborationService.feign.UserClient;
import com.example.collaborationService.producer.NotificationProducer;
import com.example.collaborationService.repository.TeamInvitationRepository;
import com.example.collaborationService.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamInvitationRepository teamInvitationRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserClient userClient;
    private final StartupClient startupClient;
    private final ModelMapper modelMapper;
    private final NotificationProducer notificationProducer;

    public InvitationResponse inviteTeamMember(TeamInviteRequest request) {
        Long founderId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        StartupResponse startup;
        try {
            startup = startupClient.getStartupById(request.getStartupId());
        } catch (Exception e) {
            throw new StartUpFoundNotException("Startup not found");
        }
        if (!startup.getFounderId().equals(founderId)) {
            throw new UnauthorizedException("You are not owner of this startup");
        }
        UserResponse invitedUser ;
        try {
            invitedUser = userClient.getUserById(request.getInvitedUserId());
        } catch (Exception e) {
            throw new UserNotFoundException("Invited user not found");
        }

        TeamInvitation invitation = new TeamInvitation();
        invitation.setStartupId(request.getStartupId());
        invitation.setInvitedUserId(invitedUser.getUserId());
        invitation.setFounderId(founderId);
        invitation.setRole(request.getRole());
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setCreatedAt(LocalDateTime.now());
        TeamInvitation saved = teamInvitationRepository.save(invitation);

        NotificationEvent notificationEvent=new NotificationEvent();
        notificationEvent.setUserId(request.getInvitedUserId());
        notificationEvent.setTitle("Team invitation");
        notificationEvent.setMessage("you have been invited to join a startup team");
        notificationEvent.setType(NotificationType.TEAM_INVITE_SENT);
        notificationProducer.sendNotification(notificationEvent);

        InvitationResponse response = modelMapper.map(saved, InvitationResponse.class);
        response.setMessage("Invitation sent successfully");
        return response;
    }

    public InvitationResponse accept(Long invitationId) {
        Long currentUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TeamInvitation invitation = teamInvitationRepository.findById(invitationId).orElseThrow(() -> new InvitationNotFoundException("Invitation not found"));

        if (!invitation.getInvitedUserId().equals(currentUserId)) {
            throw new UnauthorizedException("You are not allowed to accept this invitation");
        }
        invitation.setStatus(InvitationStatus.ACCEPTED);

        TeamMember member = new TeamMember();
        member.setStartupId(invitation.getStartupId());
        member.setUserId(currentUserId);
        member.setRole(invitation.getRole());
        member.setJoinedAt(LocalDateTime.now());
        teamMemberRepository.save(member);
        teamInvitationRepository.save(invitation);

        NotificationEvent notificationEvent=new NotificationEvent();
        notificationEvent.setUserId(invitation.getFounderId());
        notificationEvent.setTitle("invitation accepted");
        notificationEvent.setMessage("user id-"+currentUserId+" accepted your team invitation");
        notificationEvent.setType(NotificationType.TEAM_INVITE_ACCEPTED);
        notificationProducer.sendNotification(notificationEvent);

        InvitationResponse response = modelMapper.map(invitation, InvitationResponse.class);
        response.setMessage("Invitation accepted");
        return response;
    }

    public InvitationResponse reject(Long invitationId) {
        Long currentUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TeamInvitation invitation = teamInvitationRepository.findById(invitationId).orElseThrow(() -> new InvitationNotFoundException("Invitation not found"));
        if (!invitation.getInvitedUserId().equals(currentUserId)) {
            throw new UnauthorizedException("You are not allowed to reject this invitation");
        }
        invitation.setStatus(InvitationStatus.REJECTED);
        teamInvitationRepository.save(invitation);

        NotificationEvent notificationEvent=new NotificationEvent();
        notificationEvent.setUserId(invitation.getFounderId());
        notificationEvent.setTitle("invitation rejected");
        notificationEvent.setMessage("user id-"+currentUserId+" rejected your team invitation");
        notificationEvent.setType(NotificationType.TEAM_INVITE_REJECTED);
        notificationProducer.sendNotification(notificationEvent);

        InvitationResponse response = modelMapper.map(invitation, InvitationResponse.class);
        response.setMessage("Invitation rejected");
        return response;
    }

    public List<TeamMemberResponse> getTeamMembers(Long startupId) {
        List<TeamMember> members = teamMemberRepository.findByStartupId(startupId);
        List<TeamMemberResponse> teamMemberResponseList=members.stream().map(i -> modelMapper.map(i, TeamMemberResponse.class)).toList();
        return teamMemberResponseList;
    }

}
