package com.quan.controller;

import com.quan.pojo.dto.ChatDTO;
import com.quan.service.IChatHistoryService;
import com.quan.service.IFileService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.model.Media;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient defaultModel;

    private final ChatClient gameModel;

    private final ChatClient questionModel;

    private final IChatHistoryService chatHistoryService;

    private final IFileService fileService;

    /**
     * 默认的聊天助手
     * @param chatDTO
     * @return
     */
    @PostMapping(value = "/chat", produces = "text/html;charset=utf8")
    public Flux<String> chat(ChatDTO chatDTO) {

        chatHistoryService.save(chatDTO.getType(),chatDTO.getSessionId());
        if(ObjectUtils.isEmpty(chatDTO.getFiles())){
            return defaultModel.prompt()
                    .user(chatDTO.getPrompt())
                    .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatDTO.getSessionId()))
                    .stream()
                    .content();
        }else {
            List<Media> mediaList = chatDTO.getFiles().stream()
                    .map(file -> new Media(
                            MimeType.valueOf(Objects.requireNonNull(file.getContentType())),
                            file.getResource()
                    ) ).toList();
            return defaultModel.prompt()
                    .user(p -> p.text(chatDTO.getPrompt()).media(mediaList.toArray(Media[]::new)))
                    .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatDTO.getSessionId()))
                    .stream()
                    .content();
        }

    }

    /**
     * 哄哄模拟器
     * @param prompt
     * @param chatId
     * @return
     */
    @GetMapping(value = "/game", produces = "text/html;charset=utf8")
    public Flux<String> gameChat(String prompt, String chatId) {
        return gameModel.prompt()
                .user(prompt)
                .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .stream()
                .content();
    }

    @GetMapping(value = "/pdf", produces = "text/html;charset=UTF-8")
    public Flux<String> chat(String prompt, String chatId) {
        chatHistoryService.save(3L,chatId);
        Resource file = fileService.getFile(chatId);
        return questionModel
                .prompt(prompt)
                .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .advisors(a -> a.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, "file_name == '"+file.getFilename()+"'"))
                .stream()
                .content();
    }
}
