package com.quan.pojo.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.Message;

@Data
@NoArgsConstructor
public class MessageVO {

    /**
     * 角色
     */
    private String role;

    /**
     * 内容
     */
    private String content;

    public MessageVO(Message message) {
        switch (message.getMessageType()) {
            case USER -> role = "USER";
            case ASSISTANT -> role = "ASSISTANT";
            default -> role = "";
        }
        this.content = message.getText();
    }
}
