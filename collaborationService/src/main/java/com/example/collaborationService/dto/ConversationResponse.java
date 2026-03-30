package com.example.collaborationService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationResponse {
    private Long conversationId;
    private Long user1Id;
    private Long user2Id;
    private LocalDateTime createdAt;
}
