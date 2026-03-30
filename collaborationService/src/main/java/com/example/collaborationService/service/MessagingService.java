package com.example.collaborationService.service;

import com.example.collaborationService.dto.ConversationResponse;
import com.example.collaborationService.dto.CreateConversationRequest;
import com.example.collaborationService.dto.MessageResponse;
import com.example.collaborationService.dto.SendMessageRequest;
import com.example.collaborationService.entity.Converstion;
import com.example.collaborationService.entity.Message;
import com.example.collaborationService.exception.ConversationNotFoundException;
import com.example.collaborationService.exception.UnauthorizedException;
import com.example.collaborationService.exception.UserNotFoundException;
import com.example.collaborationService.feign.UserClient;
import com.example.collaborationService.repository.ConversationRepository;
import com.example.collaborationService.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MessagingService {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserClient userClient;
    private final ModelMapper modelMapper;

    public ConversationResponse createConversation(CreateConversationRequest request) {
        Long currentUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long otherUserId = request.getUserId();
        Converstion existing = conversationRepository.findByUser1IdAndUser2Id(currentUserId, otherUserId)
                .orElseGet(() -> conversationRepository.findByUser2IdAndUser1Id(currentUserId, otherUserId).orElse(null));
        if (existing != null) {
            return modelMapper.map(existing, ConversationResponse.class);
        }
        try {
            userClient.getUserById(otherUserId);
        } catch (Exception e) {
            throw new UserNotFoundException("User not found");
        }
        Converstion conversation = new Converstion();
        conversation.setUser1Id(currentUserId);
        conversation.setUser2Id(otherUserId);
        conversation.setCreatedAt(LocalDateTime.now());
        Converstion saved = conversationRepository.save(conversation);
        ConversationResponse conversationResponse=modelMapper.map(saved, ConversationResponse.class);
        return conversationResponse;
    }

    public MessageResponse sendMessage(SendMessageRequest request) {
        Long currentUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Converstion conversation = conversationRepository.findById(request.getConversationId()).orElseThrow(() -> new ConversationNotFoundException("Conversation not found"));
        if (!conversation.getUser1Id().equals(currentUserId) && !conversation.getUser2Id().equals(currentUserId)) {
            throw new UnauthorizedException("send mssg in your own conversation, You are not part of this conversation");
        }
        Message message = new Message();
        message.setConversationId(request.getConversationId());
        message.setSenderId(currentUserId);
        message.setContent(request.getContent());
        message.setSentAt(LocalDateTime.now());
        Message saved = messageRepository.save(message);
        MessageResponse messageResponse=modelMapper.map(saved, MessageResponse.class);
        return messageResponse;
    }

    public List<MessageResponse> getMessagesByConversationId(Long conversationId) {
        Long currentUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Converstion conversation = conversationRepository.findById(conversationId).orElseThrow(() -> new ConversationNotFoundException("Conversation not found"));
        if (!conversation.getUser1Id().equals(currentUserId) && !conversation.getUser2Id().equals(currentUserId)) {
            throw new UnauthorizedException("You are not allowed to see these messages, you are not part of it");
        }
        List<Message> messages = messageRepository.findByConversationId(conversationId);
        List<MessageResponse> messageResponseList=messages.stream().map(msg -> modelMapper.map(msg, MessageResponse.class)).toList();
        return messageResponseList;
    }

}
