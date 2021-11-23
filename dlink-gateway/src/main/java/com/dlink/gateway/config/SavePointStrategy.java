package com.dlink.gateway.config;

/**
 * SavePointStrategy
 *
 * @author wenmo
 * @since 2021/11/23 10:28
 **/
public enum SavePointStrategy {
    NONE(0), LATEST(1), EARLIEST(2), CUSTOM(3);

    private Integer value;

    SavePointStrategy(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static SavePointStrategy get(Integer value) {
        for (SavePointStrategy type : SavePointStrategy.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return SavePointStrategy.NONE;
    }
}
