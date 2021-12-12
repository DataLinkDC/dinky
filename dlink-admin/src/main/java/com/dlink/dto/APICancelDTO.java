package com.dlink.dto;

import com.dlink.gateway.config.GatewayConfig;
import com.dlink.job.JobConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * APICancelDTO
 *
 * @author wenmo
 * @since 2021/12/12 18:53
 */
@Getter
@Setter
public class APICancelDTO {
    private String jobId;
    private String address;
    private GatewayConfig gatewayConfig;

    public JobConfig getJobConfig() {
        JobConfig config = new JobConfig();
        config.setAddress(address);
        config.setGatewayConfig(gatewayConfig);
        return config;
    }
}
