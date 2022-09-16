package com.dlink.scheduler.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * condition type
 */
public enum ConditionType {
    /**
     * 0 none
     * 1 judge
     * 2 delay
     */
    NONE(0, "none"),
    JUDGE(1, "judge"),
    DELAY(2, "delay");

    ConditionType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final int code;
    private final String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    private static final Map<String, ConditionType> CONDITION_TYPE_MAP = new HashMap<>();

    static {
        for (ConditionType conditionType : ConditionType.values()) {
            CONDITION_TYPE_MAP.put(conditionType.desc, conditionType);
        }
    }

    public static ConditionType of(String desc) {
        if (CONDITION_TYPE_MAP.containsKey(desc)) {
            return CONDITION_TYPE_MAP.get(desc);
        }
        throw new IllegalArgumentException("invalid type : " + desc);
    }
}
