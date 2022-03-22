package com.dlink.alert;

/**
 * AlertResult
 *
 * @author wenmo
 * @since 2022/2/23 20:20
 **/
public class AlertResult {
    private boolean success;
    private String message;

    public AlertResult() {
    }

    public AlertResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean getSuccess() {
        return success;
    }

    public Integer getSuccessCode() {
        return success ? 1 : 0;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
