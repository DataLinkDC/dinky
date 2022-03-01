package com.dlink.model;

/**
 * JobInstanceStatus
 *
 * @author wenmo
 * @since 2022/2/28 22:25
 */
public class JobInstanceStatus {
    private Integer all = 0;
    private Integer initializing = 0;
    private Integer running = 0;
    private Integer finished = 0;
    private Integer failed = 0;
    private Integer canceled = 0;

    public JobInstanceStatus() {
    }

    public JobInstanceStatus(Integer all, Integer initializing, Integer running, Integer finished, Integer failed, Integer canceled) {
        this.all = all;
        this.initializing = initializing;
        this.running = running;
        this.finished = finished;
        this.failed = failed;
        this.canceled = canceled;
    }

    public Integer getAll() {
        return all;
    }

    public void setAll(Integer all) {
        this.all = all;
    }

    public Integer getInitializing() {
        return initializing;
    }

    public void setInitializing(Integer initializing) {
        this.initializing = initializing;
    }

    public Integer getRunning() {
        return running;
    }

    public void setRunning(Integer running) {
        this.running = running;
    }

    public Integer getFinished() {
        return finished;
    }

    public void setFinished(Integer finished) {
        this.finished = finished;
    }

    public Integer getFailed() {
        return failed;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }

    public Integer getCanceled() {
        return canceled;
    }

    public void setCanceled(Integer canceled) {
        this.canceled = canceled;
    }
}
