package com.denisvlem.aiassistant.repository;

import com.denisvlem.aiassistant.entity.ChatMessage;

import java.util.List;

public interface ChatMessageRepository {
    List<ChatMessage> findByChatId(Long id);
    void save(ChatMessage chatMessage);
}
