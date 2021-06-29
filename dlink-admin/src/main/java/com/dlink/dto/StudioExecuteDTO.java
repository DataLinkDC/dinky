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
    private boolean isResult=true;
    private boolean isSession=false;
    private String session;
    private boolean isRemote=false;
    private Integer clusterId;
    private boolean fragment=false;
    private String statement;
    private String jobName;
    private Integer taskId;
    private Integer maxRowNum=100;
    private Integer checkPoint=0;
    private Integer parallelism=1;
    private String savePointPath;

    public JobConfig getJobConfig(){
        return new JobConfig(isResult,isSession,session,isRemote,clusterId,taskId,jobName,fragment,maxRowNum,checkPoint,parallelism,savePointPath);
    }
}
