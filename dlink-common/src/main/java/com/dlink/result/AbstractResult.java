package com.dlink.result;

import java.time.Duration;
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
    protected long time;
    protected String error;

    public void success() {
        this.setEndTime(LocalDateTime.now());
        this.setSuccess(true);
    }

    public void error(String error) {
        this.setEndTime(LocalDateTime.now());
        this.setSuccess(false);
        this.setError(error);
    }

    public void setStartTime(LocalDateTime startTime) {
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
        if (startTime != null && endTime != null) {
            Duration duration = java.time.Duration.between(startTime, endTime);
            time = duration.toMillis();
        }
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
