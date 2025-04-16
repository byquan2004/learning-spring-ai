package com.quan.utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quan.pojo.Msg;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Component
public class RedisChatMemory implements ChatMemory {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final static String PREFIX = "chat:";

    @Override
    public void add(String conversationId, List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }

        String key = PREFIX + conversationId;
        // 使用AtomicDouble来存储可变的score
        AtomicLong score = new AtomicLong(
                redisTemplate.opsForZSet().zCard(key) != null ?
                        redisTemplate.opsForZSet().zCard(key) : 0
        );

        // 批量添加消息到Sorted Set
        messages.forEach(message -> {
            try {
                String messageJson = objectMapper.writeValueAsString(new Msg(message));
                // 使用AtomicDouble的getAndIncrement()方法来递增score
                redisTemplate.opsForZSet().add(key, messageJson, score.getAndIncrement());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        String key = PREFIX + conversationId;
        Long size = redisTemplate.opsForZSet().zCard(key);
        if (size == null || size == 0) {
            return List.of();
        }

        long startIndex = Math.max(0, size - lastN);
        Set<String> messages = redisTemplate.opsForZSet().range(
                key,
                startIndex,
                size - 1
        );

        if (messages == null || messages.isEmpty()) {
            return List.of();
        }

        return messages.stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, Msg.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(Msg::toMessage)
                .collect(Collectors.toList());
    }

    @Override
    public void clear(String conversationId) {
        redisTemplate.delete(PREFIX + conversationId);
    }
}