package com.quan.service;

import com.quan.pojo.vo.MessageVO;

import java.util.List;

public interface IChatHistoryService {

    /**
     * 根据类型和会话id获取聊天记录
     */
    List<MessageVO> historyMessages(Long type, String sessionId);

    /**
     * 根据类型保存会话id
     */
    void save(Long type, String sessionId);

    /**
     * 根据类型获取会话列表
     */
    List<String> getSessionIdsByType(Long type);
}
