package com.dlink.scheduler.enums;

/**
 * define process and task priority
 */
public enum Priority {
    /**
     * 0 highest priority
     * 1 higher priority
     * 2 medium priority
     * 3 lower priority
     * 4 lowest priority
     */
    HIGHEST(0, "highest"),
    HIGH(1, "high"),
    MEDIUM(2, "medium"),
    LOW(3, "low"),
    LOWEST(4, "lowest");

    Priority(int code, String descp) {
        this.code = code;
        this.descp = descp;
    }

    private final int code;
    private final String descp;

    public int getCode() {
        return code;
    }

    public String getDescp() {
        return descp;
    }
}
