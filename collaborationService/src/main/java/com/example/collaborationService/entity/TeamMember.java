package com.example.collaborationService.entity;

import com.example.collaborationService.enums.TeamRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
@Entity
@Table(name = "team_members")
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private Long startupId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private TeamRole role;

    private LocalDateTime joinedAt;
}
