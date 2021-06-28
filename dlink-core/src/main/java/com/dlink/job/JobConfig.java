package com.dlink.job;

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
    private String host;
    private String session;
    private String type;
    private Integer taskId;
    private Integer clusterId;
    private Integer checkpoint;
    private Integer parallelism;
    private boolean useSqlFragment;
    private String savePointPath;
    private String jobName;

    public ExecutorSetting getExecutorSetting(){
        return new ExecutorSetting(host,type,checkpoint,parallelism,useSqlFragment,savePointPath,jobName);
    }
}
