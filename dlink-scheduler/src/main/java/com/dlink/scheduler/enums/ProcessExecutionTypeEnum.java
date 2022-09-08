package com.dlink.scheduler.enums;

import java.util.HashMap;

public enum ProcessExecutionTypeEnum {

    PARALLEL(0, "parallel"),
    SERIAL_WAIT(1, "serial wait"),
    SERIAL_DISCARD(2, "serial discard"),
    SERIAL_PRIORITY(3, "serial priority");

    ProcessExecutionTypeEnum(int code, String descp) {
        this.code = code;
        this.descp = descp;
    }

    private final int code;
    private final String descp;

    private static HashMap<Integer, ProcessExecutionTypeEnum> EXECUTION_STATUS_MAP = new HashMap<>();

    static {
        for (ProcessExecutionTypeEnum executionType : ProcessExecutionTypeEnum.values()) {
            EXECUTION_STATUS_MAP.put(executionType.code, executionType);
        }
    }

    public boolean typeIsSerial() {
        return this != PARALLEL;
    }

    public boolean typeIsSerialWait() {
        return this == SERIAL_WAIT;
    }

    public boolean typeIsSerialDiscard() {
        return this == SERIAL_DISCARD;
    }

    public boolean typeIsSerialPriority() {
        return this == SERIAL_PRIORITY;
    }

    public int getCode() {
        return code;
    }

    public String getDescp() {
        return descp;
    }

    public static ProcessExecutionTypeEnum of(int executionType) {
        if (EXECUTION_STATUS_MAP.containsKey(executionType)) {
            return EXECUTION_STATUS_MAP.get(executionType);
        }
        throw new IllegalArgumentException("invalid status : " + executionType);
    }

}
