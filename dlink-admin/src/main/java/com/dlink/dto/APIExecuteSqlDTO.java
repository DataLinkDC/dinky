package com.dlink.dto;

import com.dlink.assertion.Asserts;
import com.dlink.gateway.config.GatewayConfig;
import com.dlink.job.JobConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * APIExecuteSqlDTO
 *
 * @author wenmo
 * @since 2021/12/11 21:50
 */
@Getter
@Setter
public class APIExecuteSqlDTO extends AbstractStatementDTO {
    // RUN_MODE
    private String type;
    private boolean useResult = false;
    private boolean useChangeLog = false;
    private boolean useAutoCancel = false;
    private boolean useStatementSet = false;
    private String address;
    private String jobName;
    private Integer maxRowNum = 100;
    private Integer checkPoint = 0;
    private Integer parallelism;
    private String savePointPath;
    private Map<String, String> configuration;
    private GatewayConfig gatewayConfig;

    public JobConfig getJobConfig() {
        Integer savePointStrategy = 0;
        if (Asserts.isNotNullString(savePointPath)) {
            savePointStrategy = 3;
        }
        return new JobConfig(
                type, useResult, useChangeLog, useChangeLog, false, null, true, address, jobName,
                isFragment(), useStatementSet, maxRowNum, checkPoint, parallelism, savePointStrategy,
                savePointPath, configuration, gatewayConfig);
    }
}
