package com.dlink.executor;

import lombok.Getter;
import lombok.Setter;

/**
 * ExecutorSetting
 *
 * @author wenmo
 * @since 2021/5/25 13:43
 **/
@Setter
@Getter
public class ExecutorSetting {
    private String host;
    private String type;
    private Integer checkpoint;
    private Integer parallelism;
    private boolean useSqlFragment;
    private String savePointPath;
    private String jobName;

    public ExecutorSetting(String type) {
        this.type = type;
    }

    public ExecutorSetting(String type, boolean useSqlFragment) {
        this.type = type;
        this.useSqlFragment = useSqlFragment;
    }

    public ExecutorSetting(String type, Integer checkpoint) {
        this.type = type;
        this.checkpoint = checkpoint;
    }

    public ExecutorSetting(String type, Integer checkpoint, boolean useSqlFragment) {
        this.type = type;
        this.checkpoint = checkpoint;
        this.useSqlFragment = useSqlFragment;
    }

    public ExecutorSetting(String type, Integer checkpoint, Integer parallelism, boolean useSqlFragment, String savePointPath,String jobName) {
        this.type = type;
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.useSqlFragment = useSqlFragment;
        this.savePointPath = savePointPath;
        this.jobName = jobName;
    }

    public ExecutorSetting(String type, Integer checkpoint, Integer parallelism, boolean useSqlFragment, String savePointPath) {
        this.type = type;
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.useSqlFragment = useSqlFragment;
        this.savePointPath = savePointPath;
    }

    public ExecutorSetting(String host, String type, Integer checkpoint, Integer parallelism, boolean useSqlFragment, String savePointPath, String jobName) {
        this.host = host;
        this.type = type;
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.useSqlFragment = useSqlFragment;
        this.savePointPath = savePointPath;
        this.jobName = jobName;
    }

    public boolean isRemote(){
        return type.equals(Executor.REMOTE);
    }
}
