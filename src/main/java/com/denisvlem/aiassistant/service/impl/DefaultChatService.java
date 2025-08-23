package com.denisvlem.aiassistant.service.impl;

import com.denisvlem.aiassistant.dto.ChatDetails;
import com.denisvlem.aiassistant.entity.Chat;
import com.denisvlem.aiassistant.repository.ChatRepository;
import com.denisvlem.aiassistant.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DefaultChatService implements ChatService {

    private final ChatRepository chatRepository;
    private final TransactionTemplate tx;

    @Override
    public ChatDetails get(Long id) {
        Chat chat = tx.execute(status -> chatRepository.getById(id));
        Objects.requireNonNull(chat);
        return this.toChatDetails(chat);
    }

    @Override
    public ChatDetails save(Chat newChat) {
        Chat savedChat = tx.execute(status -> chatRepository.save(newChat));
        Objects.requireNonNull(savedChat);
        return this.toChatDetails(savedChat);
    }

    @Override
    public void delete(Long id) {
        tx.executeWithoutResult(status -> chatRepository.delete(id));
    }

    @Override
    public List<ChatDetails> findAll() {
        List<Chat> chats = tx.execute(status -> chatRepository.findAll());
        return Objects.requireNonNull(chats)
                .stream().map(this::toChatDetails).toList();
    }

    private ChatDetails toChatDetails(Chat chat) {
        return ChatDetails.builder()
                .id(chat.getId())
                .title(chat.getTitle())
                .createdAt(chat.getCreatedAt())
                .build();
    }
}
