package com.quan.service.impl;

import com.quan.pojo.vo.MessageVO;
import com.quan.service.IChatHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatHistoryServiceImpl implements IChatHistoryService {

    private final Map<Long, List<String>> sessionIds = new HashMap<>();

    private final ChatMemory chatMemory;

    /**
     * 根据类型和会话id获取聊天记录
     * @param type
     * @param sessionId
     * @return
     */
    @Override
    public List<MessageVO> historyMessages(Long type, String sessionId) {
        List<Message> messages = chatMemory.get(sessionId, Integer.MAX_VALUE);
        return messages.stream().map(MessageVO::new).toList();
    }

    /**
     * 保存会话id
     * @param type 1-chat 2-service 3-pdf
     * @param sessionId 来自前端
     */
    @Override
    public void save(Long type, String sessionId) {
        // 先根据类型查看集合是否存在 不存在创建该类型集合
        List<String> ids = sessionIds.computeIfAbsent(type, k -> new ArrayList<>());
        // 判断集合中是否存在该id
        if(ids.contains(sessionId)) {return;}
        ids.add(sessionId);
    }

    /**
     * 根据会话类型返回会话id集合
     * @param type 1-chat 2-service 3-pdf
     * @return
     */
    @Override
    public List<String> getSessionIdsByType(Long type) {
        /*List<String> sessionIds = this.sessionIds.get(type);
        return sessionIds == null? List.of(): sessionIds;*/
        return sessionIds.getOrDefault(type, List.of());
    }
}
