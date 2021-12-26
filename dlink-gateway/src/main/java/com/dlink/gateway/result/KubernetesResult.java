package com.dlink.gateway.result;

import com.dlink.gateway.GatewayType;

import java.time.LocalDateTime;

/**
 * KubernetesResult
 *
 * @author wenmo
 * @since 2021/12/26 15:06
 */
public class KubernetesResult extends AbstractGatewayResult {
    private String clusterId;
    private String webURL;

    public KubernetesResult(GatewayType type, LocalDateTime startTime) {
        super(type, startTime);
    }

    public KubernetesResult(String clusterId, LocalDateTime startTime, LocalDateTime endTime, boolean isSuccess, String exceptionMsg) {
        super(startTime, endTime, isSuccess, exceptionMsg);
        this.clusterId = clusterId;
    }

    public String getClusterId() {
        return clusterId;
    }

    @Override
    public String getAppId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public void setWebURL(String webURL) {
        this.webURL = webURL;
    }

    public String getWebURL() {
        return webURL;
    }

    public static KubernetesResult build(GatewayType type){
        return new KubernetesResult(type,LocalDateTime.now());
    }
}
