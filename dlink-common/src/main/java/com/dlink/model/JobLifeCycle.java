package com.dlink.model;

/**
 * JobLifeCycle
 *
 * @author wenmo
 * @since 2022/2/1 16:37
 */
public enum JobLifeCycle {
    UNKNOWN(0, "未知"),
    CREATE(1, "创建"),
    DEVELOP(2, "开发"),
    DEBUG(3, "调试"),
    RELEASE(4, "发布"),
    ONLINE(5, "上线"),
    CANCEL(6, "注销");

    private Integer value;
    private String label;

    JobLifeCycle(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public Integer getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }


    public static JobLifeCycle get(Integer value) {
        for (JobLifeCycle item : JobLifeCycle.values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return JobLifeCycle.UNKNOWN;
    }

    public boolean equalsValue(Integer step) {
        if (value == step) {
            return true;
        }
        return false;
    }
}
