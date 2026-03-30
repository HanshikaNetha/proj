package com.example.collaborationService.dto;

import com.example.collaborationService.enums.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeamInviteRequest {
    @NotNull
    private Long startupId;

    @NotNull
    private Long invitedUserId;

    @NotNull
    private TeamRole role;
}
