package com.denisvlem.aiassistant.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final ChatClient client;

    public ChatController(ChatClient client) {
        this.client = client;
    }

    @PostMapping("/ask")
    public String askChat(@RequestBody @Valid AskRequest request) {
        String llmResponse = client.prompt()
                .user(request.prompt())
                .call().content(); //blocking request, wait till the whole response is generated
        log.info("LLM response: {}", llmResponse);
        return llmResponse;
    }

    public record AskRequest(@NotNull String prompt){}
}
