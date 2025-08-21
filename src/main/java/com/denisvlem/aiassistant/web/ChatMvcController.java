package com.denisvlem.aiassistant.web;

import com.denisvlem.aiassistant.entity.Chat;
import com.denisvlem.aiassistant.repository.ChatMessageRepository;
import com.denisvlem.aiassistant.repository.ChatRepository;
import com.denisvlem.aiassistant.service.PostgresMemory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatMvcController {

    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatClient chatClient;
    private final PostgresMemory postgresMemory;

    @GetMapping("/")
    public String mainPage(ModelMap modelMap) {
        List<Chat> chats = chatRepository.findAll();
        modelMap.addAttribute("chats", chats);
        return "chat";
    }

    @GetMapping("/chat/{chatId}")
    public String getChat(@PathVariable Long chatId, ModelMap modelMap) {
        modelMap.addAttribute("chats", chatRepository.findAll());
        modelMap.addAttribute("chat", chatRepository.findById(chatId));
        modelMap.addAttribute("history", chatMessageRepository.findByChatId(chatId));
        return "chat";
    }

    @PostMapping("/chat")
    public String addChat(@RequestParam @Valid @NotEmpty String title) {
        var chat = chatRepository.save(Chat.builder().title(title).build());
        return "redirect:/chat/" + chat.getId();
    }

    @PostMapping("/chat/{chatId}/delete")
    public String deleteChat(@PathVariable @Valid @NotNull Long chatId) {
        chatRepository.delete(chatId);
        return "redirect:/";
    }

    @PostMapping("/chat/{chatId}/send")
    public String sendMessage(@PathVariable @Valid @NotNull Long chatId,
                              @RequestParam @Valid @NotEmpty String prompt) {

        chatClient.prompt()
                .advisors(MessageChatMemoryAdvisor.builder(postgresMemory).
                        conversationId(String.valueOf(chatId))
                        .build())
                .user(prompt)
                .call().content();

        return "redirect:/chat/" + chatId;
    }

}
