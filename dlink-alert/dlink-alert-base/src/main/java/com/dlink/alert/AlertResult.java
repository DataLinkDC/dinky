package com.dlink.alert;

/**
 * AlertResult
 *
 * @author wenmo
 * @since 2022/2/23 20:20
 **/
public class AlertResult {
    private String status;
    private String message;

    public AlertResult() {
    }

    public AlertResult(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
