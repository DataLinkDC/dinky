package com.dlink.model;

/**
 * @author mydq
 * @version 1.0
 * @date 2022/7/16 21:13
 **/
public enum TaskOperatingStatus {

    INIT(1, "init"),

    OPERATING_BEFORE(4, "operatingBefore"),

    TASK_STATUS_NO_DONE(8, "taskStatusNoDone"),

    OPERATING(12, "operating"),

    EXCEPTION(13, "exception"),

    SUCCESS(16, "success"),
    FAIL(20, "fail");

    private Integer code;

    private String name;


    TaskOperatingStatus(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
