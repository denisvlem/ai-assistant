package com.denisvlem.aiassistant.service;

import com.denisvlem.aiassistant.entity.Chat;
import com.denisvlem.aiassistant.entity.ChatMessage;
import com.denisvlem.aiassistant.entity.Role;
import com.denisvlem.aiassistant.repository.ChatMessageRepository;
import com.denisvlem.aiassistant.repository.ChatRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PostgresMemory implements ChatMemory {

    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatmessageRepository;
    private final TransactionTemplate tx;

    @Override
    @NonNull
    public void add(@NonNull String conversationId, @NonNull List<Message> messages) {
        tx.executeWithoutResult(status -> {
            Chat chat = chatRepository.getById(Long.valueOf(conversationId));
            chatmessageRepository.saveAll(
                    messages.stream().map(msg -> this.fromMessage(chat.getId(), msg)).toList());
        });
    }

    @Override
    @NonNull
    public List<Message> get(@NonNull String conversationId) {
        List<ChatMessage> messages = tx.execute(status ->
                chatmessageRepository.findByChatId(Long.valueOf(conversationId)));
        return Objects.requireNonNull(messages).stream()
                .map(this::toMessage).toList();
    }

    @Override
    public void clear(@NonNull String conversationId) {
        tx.executeWithoutResult(status -> chatRepository.delete(Long.valueOf(conversationId)));
    }

    @NotNull
    private ChatMessage fromMessage(Long chatId, Message message) {
        return ChatMessage.builder()
                .chatId(chatId)
                .content(message.getText())
                .role(Role.getRole(message.getMessageType().getValue()))
                .build();
    }

    @NotNull
    private Message toMessage(ChatMessage message) {
        if (message == null || message.getRole() == null) {
            throw new IllegalArgumentException("Message or message role is null");
        }
        return switch (message.getRole()) {
            case Role.USER: {
                yield new UserMessage(message.getContent());
            }
            case Role.SYSTEM: {
                yield new SystemMessage(message.getContent());
            }
            case Role.ASSISTANT: {
                yield new AssistantMessage(message.getContent());
            }
        };

    }
}
