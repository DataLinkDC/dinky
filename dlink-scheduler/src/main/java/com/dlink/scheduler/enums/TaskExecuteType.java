package com.dlink.scheduler.enums;

/**
 * task execute type
 */
public enum TaskExecuteType {
    /**
     * 0 batch
     * 1 stream
     */
    BATCH(0, "batch"),
    STREAM(1, "stream");

    TaskExecuteType(int code, String desc) {
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
}
