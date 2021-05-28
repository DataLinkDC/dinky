package com.dlink.executor;

/**
 * ExecutorSetting
 *
 * @author wenmo
 * @since 2021/5/25 13:43
 **/
public class ExecutorSetting {
    private String type = Executor.LOCAL;
    private Long checkpoint;
    private boolean useSqlFragment = true;

    public ExecutorSetting(String type) {
        this.type = type;
    }

    public ExecutorSetting(String type, Long checkpoint) {
        this.type = type;
        this.checkpoint = checkpoint;
    }

    public ExecutorSetting(String type, Long checkpoint, boolean useSqlFragment) {
        this.type = type;
        this.checkpoint = checkpoint;
        this.useSqlFragment = useSqlFragment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(Long checkpoint) {
        this.checkpoint = checkpoint;
    }

    public boolean isUseSqlFragment() {
        return useSqlFragment;
    }

    public void setUseSqlFragment(boolean useSqlFragment) {
        this.useSqlFragment = useSqlFragment;
    }
}
