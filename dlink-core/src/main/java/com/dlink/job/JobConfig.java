package com.dlink.job;

import com.dlink.executor.EnvironmentSetting;
import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import com.dlink.session.SessionConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

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
    private String session;
    private boolean useRemote;
    private Integer clusterId;
    private String address;
    private Integer taskId;
    private String jobName;
    private boolean useSqlFragment;
    private Integer maxRowNum;
    private Integer checkpoint;
    private Integer parallelism;
    private String savePointPath;
    //private Map<String,String> config;

    public JobConfig(boolean useResult, boolean useSession, String session, boolean useRemote, Integer clusterId,
                     Integer taskId, String jobName, boolean useSqlFragment, Integer maxRowNum, Integer checkpoint,
                     Integer parallelism, String savePointPath) {
        this.useResult = useResult;
        this.useSession = useSession;
        this.session = session;
        this.useRemote = useRemote;
        this.clusterId = clusterId;
        this.taskId = taskId;
        this.jobName = jobName;
        this.useSqlFragment = useSqlFragment;
        this.maxRowNum = maxRowNum;
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.savePointPath = savePointPath;
//        this.config = config;
    }

    public JobConfig(boolean useResult, boolean useSession, String session, boolean useRemote, Integer clusterId) {
        this.useResult = useResult;
        this.useSession = useSession;
        this.session = session;
        this.useRemote = useRemote;
        this.clusterId = clusterId;
    }

    public JobConfig(boolean useResult, boolean useSession, boolean useRemote, Integer clusterId,
                     Integer taskId, String jobName, boolean useSqlFragment, Integer checkpoint,
                     Integer parallelism, String savePointPath) {
        this.useResult = useResult;
        this.useSession = useSession;
        this.useRemote = useRemote;
        this.clusterId = clusterId;
        this.taskId = taskId;
        this.jobName = jobName;
        this.useSqlFragment = useSqlFragment;
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.savePointPath = savePointPath;
    }

    public ExecutorSetting getExecutorSetting(){
        return new ExecutorSetting(checkpoint,parallelism,useSqlFragment,savePointPath,jobName);
    }

    public EnvironmentSetting getEnvironmentSetting(){
        return EnvironmentSetting.build(address);
    }

    public JobConfig buildSubmitConfig(Integer clusterId, Integer taskId, String jobName,boolean useSqlFragment, Integer checkpoint,
                                       Integer parallelism, String savePointPath){
        return new JobConfig(false,false,false,clusterId,taskId,jobName,useSqlFragment,checkpoint,parallelism,savePointPath);
    }

    public void setSessionConfig(SessionConfig sessionConfig){
        if(sessionConfig!=null) {
            address = sessionConfig.getAddress();
            clusterId = sessionConfig.getClusterId();
            useRemote = sessionConfig.isUseRemote();
        }
    }
}
