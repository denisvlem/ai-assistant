package com.denisvlem.aiassistant.repository;

import com.denisvlem.aiassistant.entity.ChatMessage;

import java.util.List;

public interface ChatMessageRepository {
    List<ChatMessage> findByChatId(Long id);

    List<ChatMessage> findByChatIdOrderDescLimit(Long id, int limit);

    ChatMessage save(ChatMessage chatMessage);

    List<ChatMessage> saveAll(List<ChatMessage> messages);
}
