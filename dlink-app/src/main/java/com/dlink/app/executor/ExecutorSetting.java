package com.dlink.app.executor;

import org.apache.flink.api.java.utils.ParameterTool;

import java.util.Map;

/**
 * ExecutorSetting
 *
 * @author wenmo
 * @since 2021/5/25 13:43
 **/

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

    public Integer getCheckpoint() {
        return checkpoint;
    }

    public Integer getParallelism() {
        return parallelism;
    }

    public boolean isUseSqlFragment() {
        return useSqlFragment;
    }

    public String getSavePointPath() {
        return savePointPath;
    }

    public String getJobName() {
        return jobName;
    }

    public Map<String, String> getConfig() {
        return config;
    }
}
