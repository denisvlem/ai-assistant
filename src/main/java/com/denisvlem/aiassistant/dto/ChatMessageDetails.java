package com.denisvlem.aiassistant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDetails {

    @NotNull
    private Long id;
    @NotNull
    private String content;
    @NotNull
    private String role;
    @NotNull
    private LocalDateTime createdAt;
    @NotNull
    private Long chatId;
}
