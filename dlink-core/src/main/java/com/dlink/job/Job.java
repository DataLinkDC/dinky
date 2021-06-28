package com.dlink.job;

import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Job
 *
 * @author wenmo
 * @since 2021/6/26 23:39
 */
@Getter
@Setter
public class Job {
    private JobConfig jobConfig;
    private String jobManagerAddress;
    private boolean isRemote;
    private boolean isSession;
    private JobStatus status;
    private String statement;
    private JobType type;
    private String error;
    private String result;
    private ExecutorSetting executorSetting;
    private LocalDate startTime;
    private LocalDate endTime;
    private String msg;
    private Executor executor;

    enum JobType{
        EXECUTE,
        SUBMIT
    }

    enum JobStatus{
        INITIALIZE,
        RUNNING,
        SUCCESS,
        FAILED,
        CANCEL
    }
}
