package com.denisvlem.aiassistant.service;

import com.denisvlem.aiassistant.dto.ChatMessageDetails;

import java.util.List;

public interface ChatMessageService {

    List<ChatMessageDetails> findByChatId(Long chatId);
}
