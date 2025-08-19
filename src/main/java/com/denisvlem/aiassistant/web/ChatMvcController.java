package com.denisvlem.aiassistant.web;

import com.denisvlem.aiassistant.entity.Chat;
import com.denisvlem.aiassistant.repository.ChatRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        return "chat";
    }

    @PostMapping("/chat")
    public String addChat(@RequestParam @Valid @NotEmpty String title) {
        var chat = chatRepository.save(Chat.builder().title(title).build());
        return "redirect:/chat/" + chat.getId();
    }

    @PostMapping("/chat/{chatId}/delete")
    public String deleteChat(@Valid @NotNull @PathVariable Long chatId) {
        chatRepository.delete(chatId);
        return "redirect:/";
    }

}
