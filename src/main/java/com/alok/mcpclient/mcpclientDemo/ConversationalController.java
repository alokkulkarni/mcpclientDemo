package com.alok.mcpclient.mcpclientDemo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/conversational/employees")
@ResponseBody
class ConversationalController {

    private final ChatClient chatClient;
    private final Map<String, PromptChatMemoryAdvisor> chatMemory = new ConcurrentHashMap<>();

    ConversationalController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/{id}/inquire")
    String inquire(@PathVariable String id, @RequestParam String question) {
        var promptChatMemoryAdvisor = chatMemory
                .computeIfAbsent(id, s -> PromptChatMemoryAdvisor.builder(new InMemoryChatMemory()).build());
        return chatClient
                .prompt()
                .user(question)
                .advisors(promptChatMemoryAdvisor)
                .call()
                .content();
    }
}