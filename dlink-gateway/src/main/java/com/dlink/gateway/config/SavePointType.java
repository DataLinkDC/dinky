package com.dlink.gateway.config;

import com.dlink.assertion.Asserts;

/**
 * SavePointType
 *
 * @author wenmo
 * @since 2021/11/3 21:58
 */
public enum SavePointType{
    TRIGGER("trigger"),DISPOSE("dispose"),STOP("stop"),CANCEL("cancel");

    private String value;

    SavePointType(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SavePointType get(String value){
        for (SavePointType type : SavePointType.values()) {
            if(Asserts.isEqualsIgnoreCase(type.getValue(),value)){
                return type;
            }
        }
        return SavePointType.TRIGGER;
    }
}
