package com.example.collaborationService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageRequest {
    @NotNull
    private Long conversationId;
    @NotNull
    private String content;
}
