package com.denisvlem.aiassistant.service;

import com.denisvlem.aiassistant.dto.ChatDetails;
import com.denisvlem.aiassistant.entity.Chat;

import java.util.List;

public interface ChatService {

    ChatDetails get(Long id);

    ChatDetails save(Chat newChat);

    void delete(Long id);

    List<ChatDetails> findAll();
}
