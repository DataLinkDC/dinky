package com.dlink.executor;

/**
 * ExecutorSetting
 *
 * @author wenmo
 * @since 2021/5/25 13:43
 **/
public class ExecutorSetting {
    private String type;
    private Integer checkpoint;
    private Integer parallelism;
    private boolean useSqlFragment;
    private String savePointPath;

    public ExecutorSetting(String type) {
        this.type = type;
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

    public ExecutorSetting(String type, Integer checkpoint, Integer parallelism, boolean useSqlFragment, String savePointPath) {
        this.type = type;
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.useSqlFragment = useSqlFragment;
        this.savePointPath = savePointPath;
    }

    public boolean isRemote(){
        return type.equals(Executor.REMOTE);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(Integer checkpoint) {
        this.checkpoint = checkpoint;
    }

    public boolean isUseSqlFragment() {
        return useSqlFragment;
    }

    public void setUseSqlFragment(boolean useSqlFragment) {
        this.useSqlFragment = useSqlFragment;
    }

    public Integer getParallelism() {
        return parallelism;
    }

    public void setParallelism(Integer parallelism) {
        this.parallelism = parallelism;
    }

    public String getSavePointPath() {
        return savePointPath;
    }

    public void setSavePointPath(String savePointPath) {
        this.savePointPath = savePointPath;
    }
}
