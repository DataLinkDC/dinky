package com.dlink.job;

import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import lombok.Getter;
import lombok.Setter;

/**
 * JobConfig
 *
 * @author wenmo
 * @since 2021/6/27 18:45
 */
@Getter
@Setter
public class JobConfig {

    private boolean isResult;
    private boolean isSession;
    private String session;
    private boolean isRemote;
    private Integer clusterId;
    private String host;
    private Integer taskId;
    private String jobName;
    private boolean useSqlFragment;
    private Integer maxRowNum;
    private Integer checkpoint;
    private Integer parallelism;
    private String savePointPath;

    public JobConfig(boolean isResult, boolean isSession, String session, boolean isRemote, Integer clusterId,
                     Integer taskId, String jobName, boolean useSqlFragment, Integer maxRowNum, Integer checkpoint,
                     Integer parallelism, String savePointPath) {
        this.isResult = isResult;
        this.isSession = isSession;
        this.session = session;
        this.isRemote = isRemote;
        this.clusterId = clusterId;
        this.taskId = taskId;
        this.jobName = jobName;
        this.useSqlFragment = useSqlFragment;
        this.maxRowNum = maxRowNum;
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.savePointPath = savePointPath;
    }

    public ExecutorSetting getExecutorSetting(){
        String type = Executor.LOCAL;
        if(isRemote){
            type = Executor.REMOTE;
        }
        return new ExecutorSetting(host,type,checkpoint,parallelism,useSqlFragment,savePointPath,jobName);
    }
}
