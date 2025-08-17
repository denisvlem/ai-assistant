package com.denisvlem.aiassistant.web;

import com.denisvlem.aiassistant.web.dto.UserPromptRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient client;

    @PostMapping("/ask")
    public String askChat(@RequestBody @Valid UserPromptRequest request) {
        String llmResponse = client.prompt()
                .user(request.prompt())
                .call().content(); //blocking request, wait till the whole response is generated
        log.info("LLM response: {}", llmResponse);
        return llmResponse;
    }

    @GetMapping(value = "/ask-with-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter askWithStreaming(@RequestParam String prompt) {
        SseEmitter sseEmitter = new SseEmitter(0L);

        client.prompt().user(prompt)
                .stream().chatResponse().subscribe(
                        result -> processAnotherResponseToken(result, sseEmitter),
                        sseEmitter::completeWithError,
                        () -> log.info("The response is processed"));

        return sseEmitter;
    }

    @SneakyThrows
    private static void processAnotherResponseToken(ChatResponse result, SseEmitter sseEmitter) {
        var textChunk = Optional.ofNullable(result)
                .map(ChatResponse::getResult)
                .map(Generation::getOutput)
                .map(AbstractMessage::getText)
                .orElseThrow();

        sseEmitter.send(textChunk);
    }

}
