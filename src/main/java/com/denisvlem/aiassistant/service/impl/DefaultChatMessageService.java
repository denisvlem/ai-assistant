package com.denisvlem.aiassistant.service.impl;

import com.denisvlem.aiassistant.dto.ChatMessageDetails;
import com.denisvlem.aiassistant.entity.ChatMessage;
import com.denisvlem.aiassistant.repository.ChatMessageRepository;
import com.denisvlem.aiassistant.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DefaultChatMessageService implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final TransactionTemplate tx;

    @Override
    public List<ChatMessageDetails> findByChatId(Long chatId) {
        List<ChatMessage> chatMessages = tx.execute(status -> chatMessageRepository.findByChatId(chatId));
        return Objects.requireNonNull(chatMessages)
                .stream().map(this::toMessageDetails).toList();
    }

    private ChatMessageDetails toMessageDetails(ChatMessage chatMessage) {
        return ChatMessageDetails.builder()
                .id(chatMessage.getId())
                .content(chatMessage.getContent())
                .createdAt(chatMessage.getCreatedAt())
                .role(chatMessage.getRole().getName())
                .chatId(chatMessage.getChatId())
                .build();
    }
}
