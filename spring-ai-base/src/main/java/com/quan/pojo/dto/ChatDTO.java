package com.quan.pojo.dto;

import com.quan.pojo.constant.ChatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatDTO {

    /**
     * 提示词
     */
    private String prompt;

    /**
     * 会话id
     */
    private String sessionId;

    /**
     * 类型 1-chat 2-service 3-pdf
     */
    private Long type;
}
