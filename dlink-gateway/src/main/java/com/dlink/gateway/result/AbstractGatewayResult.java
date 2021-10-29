package com.dlink.gateway.result;

import com.dlink.gateway.GatewayType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * AbstractGatewayResult
 *
 * @author qiwenkai
 * @since 2021/10/29 15:44
 **/
@Setter
@Getter
public abstract class AbstractGatewayResult implements GatewayResult {

    protected String jobId;
    protected GatewayType type;
    protected String savePointPath;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;
    protected boolean isSuccess;
    protected String exceptionMsg;

    public AbstractGatewayResult(GatewayType type, LocalDateTime startTime) {
        this.type = type;
        this.startTime = startTime;
    }

    public AbstractGatewayResult(String jobId, String savePointPath, LocalDateTime startTime, LocalDateTime endTime, boolean isSuccess, String exceptionMsg) {
        this.jobId = jobId;
        this.savePointPath = savePointPath;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isSuccess = isSuccess;
        this.exceptionMsg = exceptionMsg;
    }

    public void success(){
        this.isSuccess = true;
        this.endTime = LocalDateTime.now();
    }

    public void fail(String error){
        this.isSuccess = false;
        this.endTime = LocalDateTime.now();
        this.exceptionMsg = error;
    }
}
