package com.dlink.dto;

import com.dlink.job.JobConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * StudioExecuteDTO
 *
 * @author wenmo
 * @since 2021/5/30 11:09
 */
@Getter
@Setter
public class StudioExecuteDTO {
    private boolean useResult;
    private boolean useSession;
    private String session;
    private boolean useRemote;
    private Integer clusterId;
    private boolean fragment;
    private String statement;
    private String jobName;
    private Integer taskId;
    private Integer maxRowNum;
    private Integer checkPoint;
    private Integer parallelism;
    private String savePointPath;

    public JobConfig getJobConfig() {
        return new JobConfig(useResult, useSession, session, useRemote, clusterId, taskId, jobName, fragment, maxRowNum, checkPoint, parallelism, savePointPath);
    }

    /*public String getSession() {
        if(useRemote) {
            return clusterId + "_" + session;
        }else{
            return "0_" + session;
        }
    }*/
}
