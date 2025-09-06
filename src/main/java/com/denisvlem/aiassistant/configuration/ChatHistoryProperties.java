package com.denisvlem.aiassistant.configuration;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "chat.history")
public class ChatHistoryProperties {

    @NotNull
    private Integer contextLimit = 10;
}
