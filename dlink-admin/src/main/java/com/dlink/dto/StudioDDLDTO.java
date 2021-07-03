package com.dlink.dto;

import com.dlink.job.JobConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * StudioDDLDTO
 *
 * @author wenmo
 * @since 2021/6/3
 */
@Getter
@Setter
public class StudioDDLDTO {
    private boolean useResult;
    private boolean useSession;
    private String session;
    private boolean useRemote;
    private Integer clusterId;
    private String statement;

    public JobConfig getJobConfig() {
        return new JobConfig(useResult, useSession, session, useRemote, clusterId);
    }

}
