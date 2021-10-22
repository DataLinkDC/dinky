package com.dlink.executor;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * ExecutorSetting
 *
 * @author wenmo
 * @since 2021/5/25 13:43
 **/
@Setter
@Getter
public class ExecutorSetting {
    private Integer checkpoint;
    private Integer parallelism;
    private boolean useSqlFragment;
    private String savePointPath;
    private String jobName;
    private Map<String,String> config;
    public static final ExecutorSetting DEFAULT = new ExecutorSetting(0,1,true);

    public ExecutorSetting(boolean useSqlFragment) {
        this.useSqlFragment = useSqlFragment;
    }

    public ExecutorSetting(Integer checkpoint) {
        this.checkpoint = checkpoint;
    }

    public ExecutorSetting(Integer checkpoint, boolean useSqlFragment) {
        this.checkpoint = checkpoint;
        this.useSqlFragment = useSqlFragment;
    }

    public ExecutorSetting(Integer checkpoint, Integer parallelism, boolean useSqlFragment) {
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.useSqlFragment = useSqlFragment;
    }

    public ExecutorSetting(Integer checkpoint, Integer parallelism, boolean useSqlFragment, String savePointPath, String jobName) {
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.useSqlFragment = useSqlFragment;
        this.savePointPath = savePointPath;
        this.jobName = jobName;
    }

    public ExecutorSetting(Integer checkpoint, Integer parallelism, boolean useSqlFragment, String savePointPath) {
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.useSqlFragment = useSqlFragment;
        this.savePointPath = savePointPath;
    }

    public ExecutorSetting(Integer checkpoint, Integer parallelism, boolean useSqlFragment, String savePointPath, String jobName, Map<String, String> config) {
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.useSqlFragment = useSqlFragment;
        this.savePointPath = savePointPath;
        this.jobName = jobName;
        this.config = config;
    }
}
