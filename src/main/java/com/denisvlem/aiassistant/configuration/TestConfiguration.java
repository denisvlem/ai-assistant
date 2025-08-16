package com.denisvlem.aiassistant.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TestConfiguration {

    @Bean
    public ChatClient client(ChatClient.Builder builder) {
        return builder.build();
    }

}