package com.example.collaborationService.entity;

import com.example.collaborationService.enums.InvitationStatus;
import com.example.collaborationService.enums.TeamRole;
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
@Table(name = "team_invitations")
public class TeamInvitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invitationId;

    private Long startupId;

    private Long invitedUserId;

    private Long founderId;

    @Enumerated(EnumType.STRING)
    private TeamRole role;

    @Enumerated(EnumType.STRING)
    private InvitationStatus status;

    private LocalDateTime createdAt;
}
