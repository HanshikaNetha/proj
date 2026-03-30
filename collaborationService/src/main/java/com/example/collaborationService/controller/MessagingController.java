package com.example.collaborationService.controller;

import com.example.collaborationService.dto.ConversationResponse;
import com.example.collaborationService.dto.CreateConversationRequest;
import com.example.collaborationService.dto.MessageResponse;
import com.example.collaborationService.dto.SendMessageRequest;
import com.example.collaborationService.service.MessagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/messages")
public class MessagingController {
    private final MessagingService messagingService;

    @PostMapping("/CreateConversation")
    public ResponseEntity<ConversationResponse> createConversation(@RequestBody CreateConversationRequest request) {
        ConversationResponse response = messagingService.createConversation(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/SendMessage")
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody SendMessageRequest request) {
        MessageResponse response = messagingService.sendMessage(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<MessageResponse>> getMessagesByConversationId(@PathVariable Long conversationId) {
        List<MessageResponse> messages = messagingService.getMessagesByConversationId(conversationId);
        return ResponseEntity.ok(messages);
    }

}
