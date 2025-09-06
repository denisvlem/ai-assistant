package com.denisvlem.aiassistant.web;

import com.denisvlem.aiassistant.dto.ChatDetails;
import com.denisvlem.aiassistant.entity.Chat;
import com.denisvlem.aiassistant.service.ChatMessageService;
import com.denisvlem.aiassistant.service.ChatService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatMessageService chatMessageService;

    private final ChatClient chatClient;
    @SneakyThrows
    private static void processAnotherResponseToken(ChatResponse result, SseEmitter sseEmitter) {
        var messagePart = Optional.ofNullable(result)
                .map(ChatResponse::getResult)
                .map(Generation::getOutput)
                .orElseThrow();
        log.info("Msg: {}", messagePart);
        sseEmitter.send(messagePart);
    }

    @GetMapping("/")
    public String mainPage(ModelMap modelMap) {
        List<ChatDetails> chats = chatService.findAll();
        modelMap.addAttribute("chats", chats);
        return "chat";
    }

    @GetMapping("/chat/{chatId}")
    public String getChat(@PathVariable Long chatId, ModelMap modelMap) {
        modelMap.addAttribute("chats", chatService.findAll());
        modelMap.addAttribute("chat", chatService.get(chatId));
        modelMap.addAttribute("history", chatMessageService.findByChatId(chatId));
        return "chat";
    }

    @PostMapping("/chat/add")
    public String addChat(@RequestParam @Valid @NotEmpty String title) {
        var chat = chatService.save(Chat.builder().title(title).build());
        return "redirect:/chat/" + chat.getId();
    }

    @PostMapping("/chat/{chatId}/delete")
    public String deleteChat(@PathVariable @Valid @NotNull Long chatId) {
        chatService.delete(chatId);
        return "redirect:/";
    }

    @PostMapping("/chat/{chatId}/ask")
    public String ask(@PathVariable @Valid @NotNull Long chatId,
                      @RequestParam @Valid @NotEmpty String prompt) {

        chatClient.prompt()
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .user(prompt)
                .call().content();

        return "redirect:/chat/" + chatId;
    }

    @GetMapping(
            value = "/chat/{chatId}/ask-with-stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter askWithStreaming(@PathVariable @Valid @NotNull Long chatId,
                                       @RequestParam @Valid @NotEmpty String prompt) {

        SseEmitter sseEmitter = new SseEmitter(0L);
        chatClient.prompt()
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .user(prompt)
                .stream().chatResponse().subscribe(
                        result -> processAnotherResponseToken(result, sseEmitter),
                        sseEmitter::completeWithError,
                        () -> log.info("Streaming for the chat {} response is finished", chatId));
        return sseEmitter;
    }

}
