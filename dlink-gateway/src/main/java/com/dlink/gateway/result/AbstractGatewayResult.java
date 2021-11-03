package com.dlink.gateway.result;

import com.dlink.gateway.GatewayType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * AbstractGatewayResult
 *
 * @author wenmo
 * @since 2021/10/29 15:44
 **/
@Setter
@Getter
public abstract class AbstractGatewayResult implements GatewayResult {

    protected GatewayType type;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;
    protected boolean isSuccess;
    protected String exceptionMsg;

    public AbstractGatewayResult(GatewayType type, LocalDateTime startTime) {
        this.type = type;
        this.startTime = startTime;
    }

    public AbstractGatewayResult(LocalDateTime startTime, LocalDateTime endTime, boolean isSuccess, String exceptionMsg) {
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
