package com.dlink.dto;

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
public class APIExplainSqlDTO extends AbstractStatementDTO {
    private boolean useStatementSet = false;
    private Integer parallelism;
    private Map<String, String> configuration;

    public JobConfig getJobConfig() {
        return new JobConfig("local", false, false, isFragment(), useStatementSet, parallelism, configuration);
    }
}
