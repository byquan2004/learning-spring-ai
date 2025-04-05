package com.quan.controller;

import com.quan.pojo.vo.MessageVO;
import com.quan.service.IChatHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ai/history")
@RequiredArgsConstructor
public class ChatHistoryController {

    private final IChatHistoryService chatHistoryService;

    /**
     * 根据类型获取会话id集合
     * @param type 1-chat 2-service 3-pdf
     * @return
     */
    @GetMapping("/{type}")
    public List<String> getSessionIdsByType(@PathVariable Long type) {
        return chatHistoryService.getSessionIdsByType(type);
    }

    /**
     * 根据类型和会话id获取聊天记录
     * @return
     */
    @GetMapping("/{type}/{sessionId}")
    public List<MessageVO> historyMessage(@PathVariable(value = "type") Long type, @PathVariable(value = "sessionId") String sessionId) {
        return chatHistoryService.historyMessages(type, sessionId);
    }
}
