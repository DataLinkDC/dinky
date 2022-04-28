package com.dlink.alert.wechat;

/**
 * WeChatType
 *
 * @author wenmo
 * @since 2022/2/23 21:36
 **/
public enum WeChatType {

    APP(1, "应用"),
    CHAT(2, "群聊");

    private final int code;
    private final String value;

    WeChatType(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
