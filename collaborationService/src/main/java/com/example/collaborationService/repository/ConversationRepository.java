package com.example.collaborationService.repository;

import com.example.collaborationService.entity.Converstion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Converstion, Long> {
    public Optional<Converstion> findByUser1IdAndUser2Id(Long user1Id, Long user2Id);
    public Optional<Converstion> findByUser2IdAndUser1Id(Long user1Id, Long user2Id);
}
