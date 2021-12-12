package com.dlink.dto;

import com.dlink.assertion.Asserts;
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.config.GatewayConfig;
import com.dlink.job.JobConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * APIExplainSqlDTO
 *
 * @author wenmo
 * @since 2021/12/12 13:01
 */
@Getter
@Setter
public class APIExplainSqlDTO {
    private boolean useStatementSet = false;
    private boolean fragment = false;
    private String statement;
    private Integer parallelism;
    private Map<String, String> configuration;

    public JobConfig getJobConfig() {
        return new JobConfig("local", false, false, fragment, useStatementSet, parallelism, configuration);
    }
}
