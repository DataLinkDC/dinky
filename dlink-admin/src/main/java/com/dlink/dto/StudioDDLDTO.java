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
    private boolean isResult;
    private boolean isSession;
    private String session;
    private boolean isRemote;
    private Integer clusterId;
    private String statement;

    public JobConfig getJobConfig() {
        return new JobConfig(isResult, isSession, getSession(), isRemote, clusterId);
    }

    public String getSession() {
        if(isRemote) {
            return clusterId + "_" + session;
        }else{
            return "0_" + session;
        }
    }
}
