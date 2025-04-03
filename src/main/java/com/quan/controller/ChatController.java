package com.quan.controller;

import com.quan.pojo.constant.ChatType;
import com.quan.pojo.dto.ChatDTO;
import com.quan.service.IChatHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient chatClient;

    private final IChatHistoryService chatHistoryService;

    @PostMapping(value = "/chat", produces = "text/html;charset=utf8")
    public Flux<String> chat(@RequestBody ChatDTO chatDTO) {

        chatHistoryService.save(chatDTO.getType(),chatDTO.getSessionId());

        return chatClient.prompt()
                .user(chatDTO.getPrompt())
                .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatDTO.getSessionId()))
                .stream()
                .content();
    }
}
