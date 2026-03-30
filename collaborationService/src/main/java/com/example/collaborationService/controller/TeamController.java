package com.example.collaborationService.controller;

import com.example.collaborationService.dto.InvitationResponse;
import com.example.collaborationService.dto.TeamInviteRequest;
import com.example.collaborationService.dto.TeamMemberResponse;
import com.example.collaborationService.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @PostMapping("/inviteTeamMember")
    @PreAuthorize("hasAuthority('ROLE_FOUNDER')")
    public ResponseEntity<InvitationResponse> invite(@RequestBody TeamInviteRequest request) {
        InvitationResponse response = teamService.inviteTeamMember(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/invitations/accept/{id}")
    @PreAuthorize("hasAuthority('ROLE_COFUNDER')")
    public ResponseEntity<InvitationResponse> accept(@PathVariable Long id) {
        InvitationResponse response = teamService.accept(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/invitations/reject/{id}")
    @PreAuthorize("hasAuthority('ROLE_COFUNDER')")
    public ResponseEntity<InvitationResponse> reject(@PathVariable Long id) {
        InvitationResponse response = teamService.reject(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/startup/{startupId}")
    public ResponseEntity<List<TeamMemberResponse>> getTeamMembers(@PathVariable Long startupId) {
        List<TeamMemberResponse> members = teamService.getTeamMembers(startupId);
        return ResponseEntity.ok(members);
    }

}
