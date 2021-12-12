package com.dlink.dto;

import com.dlink.gateway.config.GatewayConfig;
import com.dlink.gateway.config.SavePointStrategy;
import com.dlink.job.JobConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * APIExecuteJarDTO
 *
 * @author wenmo
 * @since 2021/12/12 19:46
 */
@Getter
@Setter
public class APIExecuteJarDTO {
    private String type;
    private String jobName;
    private String savePointPath;
    private GatewayConfig gatewayConfig;

    public JobConfig getJobConfig() {
        JobConfig config = new JobConfig();
        config.setType(type);
        config.setJobName(jobName);
        config.setSavePointStrategy(SavePointStrategy.CUSTOM);
        config.setSavePointPath(savePointPath);
        config.setGatewayConfig(gatewayConfig);
        return config;
    }
}
