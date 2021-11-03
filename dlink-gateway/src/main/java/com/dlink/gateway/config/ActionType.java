package com.dlink.gateway.config;

import com.dlink.assertion.Asserts;

/**
 * ActionType
 *
 * @author wenmo
 * @since 2021/11/3 21:58
 */
public enum ActionType{
    SAVEPOINT("savepoint"),CANCEL("cancel");

    private String value;

    ActionType(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ActionType get(String value){
        for (ActionType type : ActionType.values()) {
            if(Asserts.isEquals(type.getValue(),value)){
                return type;
            }
        }
        return ActionType.SAVEPOINT;
    }
}
