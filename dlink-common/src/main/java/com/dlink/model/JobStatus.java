package com.dlink.model;

/**
 * JobState
 *
 * @author wenmo
 * @since 2022/2/22 14:29
 **/
public enum JobStatus {
    /**
     * The job has been received by the Dispatcher, and is waiting for the job manager to receive
     * leadership and to be created.
     */
    INITIALIZING("INITIALIZING"),

    /**
     * Job is newly created, no task has started to run.
     */
    CREATED("CREATED"),

    /**
     * Some tasks are scheduled or running, some may be pending, some may be finished.
     */
    RUNNING("RUNNING"),

    /**
     * The job has failed and is currently waiting for the cleanup to complete.
     */
    FAILING("FAILING"),

    /**
     * The job has failed with a non-recoverable task failure.
     */
    FAILED("FAILED"),

    /**
     * Job is being cancelled.
     */
    CANCELLING("CANCELLING"),

    /**
     * Job has been cancelled.
     */
    CANCELED("CANCELED"),

    /**
     * All of the job's tasks have successfully finished.
     */
    FINISHED("FINISHED"),

    /**
     * The job is currently undergoing a reset and total restart.
     */
    RESTARTING("RESTARTING"),

    /**
     * The job has been suspended which means that it has been stopped but not been removed from a
     * potential HA job store.
     */
    SUSPENDED("SUSPENDED"),

    /**
     * The job is currently reconciling and waits for task execution report to recover state.
     */
    RECONCILING("RECONCILING"),

    /**
     * The job can't get any info.
     */
    UNKNOWN("UNKNOWN");

    private String value;

    JobStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
