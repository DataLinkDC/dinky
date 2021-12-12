package com.dlink.dto;

import com.dlink.gateway.config.GatewayConfig;
import com.dlink.job.JobConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * APISavePointDTO
 *
 * @author wenmo
 * @since 2021/12/12 19:09
 */
@Getter
@Setter
public class APISavePointDTO {
    private String jobId;
    private String savePointType;
    private String savePoint;
    private String address;
    private GatewayConfig gatewayConfig;

    public JobConfig getJobConfig() {
        JobConfig config = new JobConfig();
        config.setAddress(address);
        config.setGatewayConfig(gatewayConfig);
        return config;
    }
}
