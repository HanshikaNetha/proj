package com.example.collaborationService.dto;

import com.example.collaborationService.enums.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeamMemberResponse {
    private Long memberId;
    private Long startupId;
    private Long userId;
    private TeamRole role;
    private LocalDateTime joinedAt;
}
