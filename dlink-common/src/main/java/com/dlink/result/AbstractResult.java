package com.dlink.result;

import java.time.LocalDateTime;

/**
 * AbstractResult
 *
 * @author wenmo
 * @since 2021/6/29 22:49
 */
public class AbstractResult {

    protected boolean success;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;
    protected String error;

    public void setStartTime(LocalDateTime startTime){
        this.startTime = startTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
