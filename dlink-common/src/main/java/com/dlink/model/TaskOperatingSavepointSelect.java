package com.dlink.model;

/**
 * @author csz
 * @version 1.0
 * @date 2022/7/19 15:21
 **/
public enum TaskOperatingSavepointSelect {


    DEFAULT_CONFIG(0, "defaultConfig", "默认保存点"),

    LATEST(1, "latest", "最新保存点");


    private Integer code;

    private String name;

    private String message;

    TaskOperatingSavepointSelect(Integer code, String name, String message) {
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


    public static TaskOperatingSavepointSelect valueByCode(Integer code) {
        for (TaskOperatingSavepointSelect savepointSelect : TaskOperatingSavepointSelect.values()) {
            if (savepointSelect.getCode().equals(code)) {
                return savepointSelect;
            }
        }
        return null;
    }


}
