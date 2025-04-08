package com.quan.pojo.constants;

public enum ChatType {

    CHAT(1L), // chat
    SERVICE(2L), // 客服
    PDF(3L); // pdf 文档

    private final Long type;

    ChatType( Long type) {
        this.type = type;
    }

    public Long getType() {
        return type;
    }
}
