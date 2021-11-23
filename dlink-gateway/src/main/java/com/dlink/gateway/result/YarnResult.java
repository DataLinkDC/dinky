package com.dlink.gateway.result;

import com.dlink.gateway.GatewayType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * YarnResult
 *
 * @author wenmo
 * @since 2021/10/29
 **/
@Getter
@Setter
public class YarnResult extends AbstractGatewayResult {

    private String appId;
    private String webURL;

    public YarnResult(GatewayType type, LocalDateTime startTime) {
        super(type, startTime);
    }

    public YarnResult(String appId, LocalDateTime startTime, LocalDateTime endTime, boolean isSuccess, String exceptionMsg) {
        super(startTime, endTime, isSuccess, exceptionMsg);
        this.appId = appId;
    }

    public String getAppId() {
        return appId;
    }

    public String getWebURL() {
        return webURL;
    }

    public static YarnResult build(GatewayType type){
        return new YarnResult(type,LocalDateTime.now());
    }

}
