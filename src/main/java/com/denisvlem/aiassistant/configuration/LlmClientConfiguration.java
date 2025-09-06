package com.denisvlem.aiassistant.configuration;

import com.denisvlem.aiassistant.repository.ChatMessageRepository;
import com.denisvlem.aiassistant.repository.ChatRepository;
import com.denisvlem.aiassistant.service.PostgresMemory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LlmClientConfiguration {

    private final VectorStore vectorStore;

    @Bean
    public ChatClient client(ChatClient.Builder builder, ChatMemory chatMemory) {
        return builder.defaultAdvisors(getHistoryAdvisor(chatMemory), getRagAdvisor()).build();
    }

    public Advisor getHistoryAdvisor(ChatMemory chatMemory) {
        return MessageChatMemoryAdvisor.builder(chatMemory).build();
    }

    public Advisor getRagAdvisor() {
        return QuestionAnswerAdvisor.builder(vectorStore).build();
    }

    @Bean
    public ChatMemory getChatMemory(ChatRepository chatRepository,
                                    ChatHistoryProperties chatHistoryProperties,
                                    TransactionTemplate tx,
                                    ChatMessageRepository chatMessageRepository) {
        return PostgresMemory.builder()
                .chatRepository(chatRepository)
                .chatHistoryProperties(chatHistoryProperties)
                .tx(tx)
                .chatmessageRepository(chatMessageRepository)
                .build();
    }

}