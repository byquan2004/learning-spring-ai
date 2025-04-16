package com.quan.service.impl;

import com.quan.pojo.vo.MessageVO;
import com.quan.service.IChatHistoryService;
import com.quan.utils.RedisChatMemory;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class RedisChatHistoryServiceImpl implements IChatHistoryService {

    private final RedisChatMemory chatMemory;

    private final StringRedisTemplate redisTemplate;

    private final static String CHAT_HISTORY_KEY_PREFIX = "chat:history:";

    @Override
    public List<MessageVO> historyMessages(Long type, String sessionId) {
        List<Message> messages = chatMemory.get(sessionId, Integer.MAX_VALUE);
        return messages.stream().map(MessageVO::new).toList();
    }

    @Override
    public void save(Long type, String chatId) {
        redisTemplate.opsForSet().add(CHAT_HISTORY_KEY_PREFIX + type, chatId);
    }

    @Override
    public List<String> getSessionIdsByType(Long type) {
        Set<String> chatIds = redisTemplate.opsForSet().members(CHAT_HISTORY_KEY_PREFIX + type);
        if(chatIds == null || chatIds.isEmpty()) {
            return Collections.emptyList();
        }
        return chatIds.stream().sorted(String::compareTo).toList();
    }
}