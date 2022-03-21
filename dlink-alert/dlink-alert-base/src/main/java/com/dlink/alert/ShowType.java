package com.dlink.alert;

/**
 * ShowType
 *
 * @author wenmo
 * @since 2022/2/23 21:32
 **/
public enum ShowType {

    TABLE(0, "markdown"),
    TEXT(1, "text");

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
