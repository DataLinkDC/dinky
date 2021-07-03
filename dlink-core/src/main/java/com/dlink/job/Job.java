package com.dlink.job;

import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import com.dlink.parser.SqlType;
import com.dlink.result.IResult;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Job
 *
 * @author wenmo
 * @since 2021/6/26 23:39
 */
@Getter
@Setter
public class Job {
    private Integer id;
    private JobConfig jobConfig;
    private String jobManagerAddress;
    private JobStatus status;
    private SqlType type;
    private String statement;
    private String jobId;
    private String error;
    private IResult result;
    private ExecutorSetting executorSetting;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Executor executor;

    enum JobStatus{
        INITIALIZE,
        RUNNING,
        SUCCESS,
        FAILED,
        CANCEL
    }

    public Job(JobConfig jobConfig, String jobManagerAddress, JobStatus status, String statement,ExecutorSetting executorSetting, LocalDateTime startTime, Executor executor) {
        this.jobConfig = jobConfig;
        this.jobManagerAddress = jobManagerAddress;
        this.status = status;
        this.statement = statement;
        this.executorSetting = executorSetting;
        this.startTime = startTime;
        this.executor = executor;
    }

    public JobResult getJobResult(){
        return new JobResult(id,jobConfig,jobManagerAddress,status,statement,jobId,error,result,startTime,endTime);
    }
}
