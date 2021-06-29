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

    public void setStartTime(LocalDateTime startTime){
        this.startTime = startTime;
    }
}
