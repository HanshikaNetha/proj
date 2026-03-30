package com.example.collaborationService.repository;

import com.example.collaborationService.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    public List<TeamMember> findByStartupId(Long startupId);
}
