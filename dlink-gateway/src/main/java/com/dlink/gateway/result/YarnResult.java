package com.dlink.gateway.result;

import com.dlink.gateway.GatewayType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * YarnResult
 *
 * @author wenmo
 * @since 2021/10/29
 **/
public class YarnResult extends AbstractGatewayResult {

    private String appId;
    private String webURL;
    private List<String> jids;

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

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setWebURL(String webURL) {
        this.webURL = webURL;
    }

    public List<String> getJids() {
        return jids;
    }

    public void setJids(List<String> jids) {
        this.jids = jids;
    }

    public static YarnResult build(GatewayType type) {
        return new YarnResult(type, LocalDateTime.now());
    }

}
