package com.dlink.model;

/**
 * @author mydq
 * @version 1.0
 * @date 2022/7/16 21:13
 **/
public enum TaskOperatingStatus {

    INIT(1, "init", "初始化"),

    OPERATING_BEFORE(4, "operatingBefore", "操作前准备"),

    TASK_STATUS_NO_DONE(8, "taskStatusNoDone", "任务不是完成状态"),

    OPERATING(12, "operating", "正在操作"),

    EXCEPTION(13, "exception", "异常"),

    SUCCESS(16, "success", "成功"),
    FAIL(20, "fail", "失败");

    private Integer code;

    private String name;

    private String message;


    TaskOperatingStatus(Integer code, String name, String message) {
        this.code = code;
        this.name = name;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }


}
