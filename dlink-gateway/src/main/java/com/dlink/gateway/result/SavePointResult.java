package com.dlink.gateway.result;

import com.dlink.gateway.GatewayType;
import com.dlink.gateway.model.JobInfo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO
 *
 * @author wenmo
 * @since 2021/11/3 22:20
 */
@Getter
@Setter
public class SavePointResult extends AbstractGatewayResult {
    private String appId;
    private List<JobInfo> jobInfos;

    public SavePointResult(GatewayType type, LocalDateTime startTime) {
        super(type, startTime);
    }

    public SavePointResult(LocalDateTime startTime, LocalDateTime endTime, boolean isSuccess, String exceptionMsg) {
        super(startTime, endTime, isSuccess, exceptionMsg);
    }

    @Override
    public String getAppId() {
        return appId;
    }

    @Override
    public String getWebURL() {
        return null;
    }

    public static SavePointResult build(GatewayType type){
        return new SavePointResult(type,LocalDateTime.now());
    }

}
