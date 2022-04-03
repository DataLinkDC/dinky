package com.dlink.alert;

/**
 * ShowType
 *
 * @author wenmo
 * @since 2022/2/23 21:32
 **/
public enum ShowType {

    MARKDOWN(0, "markdown"), // 通用markdown格式
    TEXT(1, "text"), //通用文本格式
    POST(2, "post"), // 飞书的富文本msgType
    TABLE(0, "table"), // table格式
    ATTACHMENT(3, "attachment"), // 邮件相关  只发送附件
    TABLE_ATTACHMENT(4, "table attachment"); // 邮件相关 邮件表格+附件

    private int code;
    private String value;

    ShowType(int code, String value) {
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
