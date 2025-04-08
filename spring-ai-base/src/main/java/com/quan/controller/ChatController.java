package com.quan.controller;

import com.quan.pojo.dto.ChatDTO;
import com.quan.service.IChatHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient defaultModel;

    private final ChatClient gameModel;

    private final IChatHistoryService chatHistoryService;

    /**
     * 默认的聊天助手
     * @param chatDTO
     * @return
     */
    @PostMapping(value = "/chat", produces = "text/html;charset=utf8")
    public Flux<String> chat(ChatDTO chatDTO) {

        chatHistoryService.save(chatDTO.getType(),chatDTO.getSessionId());

        return defaultModel.prompt()
                .user(chatDTO.getPrompt())
                .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatDTO.getSessionId()))
                .stream()
                .content();
    }

    /**
     * 哄哄模拟器
     * @param prompt
     * @param sessionId
     * @return
     */
    @GetMapping(value = "/game", produces = "text/html;charset=utf8")
    public Flux<String> chat(String prompt, String chatId) {
        return gameModel.prompt()
                .user(prompt)
                .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .stream()
                .content();
    }
}
