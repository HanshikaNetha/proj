package com.example.collaborationService.dto;


import com.example.collaborationService.enums.InvitationStatus;
import com.example.collaborationService.enums.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvitationResponse {
    private Long invitationId;
    private Long startupId;
    private Long invitedUserId;
    private Long founderId;
    private TeamRole role;
    private InvitationStatus status;
    private LocalDateTime createdAt;
    private String message;
}
