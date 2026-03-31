package com.example.collaborationService;

import com.example.collaborationService.dto.*;
import com.example.collaborationService.entity.Converstion;
import com.example.collaborationService.entity.Message;
import com.example.collaborationService.exception.ConversationNotFoundException;
import com.example.collaborationService.exception.UnauthorizedException;
import com.example.collaborationService.exception.UserNotFoundException;
import com.example.collaborationService.feign.UserClient;
import com.example.collaborationService.repository.ConversationRepository;
import com.example.collaborationService.repository.MessageRepository;
import com.example.collaborationService.service.MessagingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class MessageServiceTest {
    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MessagingService messagingService;

    private void mockUser(Long userId) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId, null, List.of())
        );
    }


    @Test
    void testCreateConversation_New() {
        mockUser(1L);

        CreateConversationRequest request = new CreateConversationRequest(2L);

        when(conversationRepository.findByUser1IdAndUser2Id(1L, 2L))
                .thenReturn(Optional.empty());
        when(conversationRepository.findByUser2IdAndUser1Id(1L, 2L))
                .thenReturn(Optional.empty());

        when(userClient.getUserById(2L)).thenReturn(new UserResponse());

        Converstion saved = new Converstion();
        saved.setConversationId(1L);

        when(conversationRepository.save(any())).thenReturn(saved);

        ConversationResponse response = new ConversationResponse();
        response.setConversationId(1L);

        doReturn(response).when(modelMapper).map(saved, ConversationResponse.class);

        ConversationResponse result = messagingService.createConversation(request);

        assertNotNull(result);
    }

    @Test
    void testCreateConversation_Existing() {
        mockUser(1L);

        Converstion existing = new Converstion();
        existing.setConversationId(1L);

        when(conversationRepository.findByUser1IdAndUser2Id(1L, 2L))
                .thenReturn(Optional.of(existing));

        ConversationResponse response = new ConversationResponse();
        response.setConversationId(1L);

        doReturn(response)
                .when(modelMapper)
                .map(existing, ConversationResponse.class);

        ConversationResponse result =
                messagingService.createConversation(new CreateConversationRequest(2L));

        assertNotNull(result);
    }

    @Test
    void testCreateConversation_UserNotFound() {
        mockUser(1L);

        when(conversationRepository.findByUser1IdAndUser2Id(any(), any()))
                .thenReturn(Optional.empty());
        when(conversationRepository.findByUser2IdAndUser1Id(any(), any()))
                .thenReturn(Optional.empty());

        when(userClient.getUserById(any())).thenThrow(RuntimeException.class);

        assertThrows(UserNotFoundException.class,
                () -> messagingService.createConversation(new CreateConversationRequest(2L)));
    }


    @Test
    void testSendMessage_Success() {
        mockUser(1L);

        Converstion conv = new Converstion();
        conv.setUser1Id(1L);
        conv.setUser2Id(2L);

        Message saved = new Message();

        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conv));
        when(messageRepository.save(any())).thenReturn(saved);

        MessageResponse response = new MessageResponse();

        doReturn(response).when(modelMapper).map(saved, MessageResponse.class);

        MessageResponse result =
                messagingService.sendMessage(new SendMessageRequest(1L, "Hello"));

        assertNotNull(result);
    }

    @Test
    void testSendMessage_Unauthorized() {
        mockUser(3L);

        Converstion conv = new Converstion();
        conv.setUser1Id(1L);
        conv.setUser2Id(2L);

        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conv));

        assertThrows(UnauthorizedException.class,
                () -> messagingService.sendMessage(new SendMessageRequest(1L, "Hi")));
    }


    @Test
    void testGetMessages_Success() {
        mockUser(1L);

        Converstion conv = new Converstion();
        conv.setUser1Id(1L);
        conv.setUser2Id(2L);

        Message msg = new Message();

        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conv));
        when(messageRepository.findByConversationId(1L))
                .thenReturn(List.of(msg));

        MessageResponse response = new MessageResponse();

        doReturn(response)
                .when(modelMapper)
                .map(any(Message.class), eq(MessageResponse.class));

        List<MessageResponse> result =
                messagingService.getMessagesByConversationId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testGetMessages_NotFound() {
        when(conversationRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(ConversationNotFoundException.class,
                () -> messagingService.getMessagesByConversationId(1L));
    }

    @Test
    void testGetMessages_Unauthorized() {
        mockUser(3L);

        Converstion conv = new Converstion();
        conv.setUser1Id(1L);
        conv.setUser2Id(2L);

        when(conversationRepository.findById(1L))
                .thenReturn(Optional.of(conv));

        assertThrows(UnauthorizedException.class,
                () -> messagingService.getMessagesByConversationId(1L));
    }

}
