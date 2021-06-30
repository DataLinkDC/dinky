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

    private boolean useResult;
    private boolean useSession;
    private String sessionKey;
    private boolean useRemote;
    private Integer clusterId;
    private String host;
    private Integer taskId;
    private String jobName;
    private boolean useSqlFragment;
    private Integer maxRowNum;
    private Integer checkpoint;
    private Integer parallelism;
    private String savePointPath;

    public JobConfig(boolean useResult, boolean useSession, String sessionKey, boolean useRemote, Integer clusterId,
                     Integer taskId, String jobName, boolean useSqlFragment, Integer maxRowNum, Integer checkpoint,
                     Integer parallelism, String savePointPath) {
        this.useResult = useResult;
        this.useSession = useSession;
        this.sessionKey = sessionKey;
        this.useRemote = useRemote;
        this.clusterId = clusterId;
        this.taskId = taskId;
        this.jobName = jobName;
        this.useSqlFragment = useSqlFragment;
        this.maxRowNum = maxRowNum;
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.savePointPath = savePointPath;
    }

    public JobConfig(boolean useResult, boolean useSession, String sessionKey, boolean useRemote, Integer clusterId) {
        this.useResult = useResult;
        this.useSession = useSession;
        this.sessionKey = sessionKey;
        this.useRemote = useRemote;
        this.clusterId = clusterId;
    }

    public ExecutorSetting getExecutorSetting(){
        String type = Executor.LOCAL;
        if(useRemote){
            type = Executor.REMOTE;
        }
        return new ExecutorSetting(host,type,checkpoint,parallelism,useSqlFragment,savePointPath,jobName);
    }
}
