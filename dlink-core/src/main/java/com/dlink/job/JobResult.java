package com.dlink.job;

import com.dlink.result.IResult;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JobResult
 *
 * @author wenmo
 * @since 2021/6/29 23:56
 */
@Getter
@Setter
public class JobResult {
    private Integer id;
    private JobConfig jobConfig;
    private String jobManagerAddress;
    private Job.JobStatus status;
    private boolean success;
    private String statement;
    private String jobId;
    private Integer jobInstanceId;
    private String error;
    private IResult result;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public JobResult() {
    }

    public JobResult(Integer id, Integer jobInstanceId, JobConfig jobConfig, String jobManagerAddress, Job.JobStatus status, String statement, String jobId, String error, IResult result, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.jobInstanceId = jobInstanceId;
        this.jobConfig = jobConfig;
        this.jobManagerAddress = jobManagerAddress;
        this.status = status;
        this.success = (status == (Job.JobStatus.SUCCESS)) ? true : false;
        this.statement = statement;
        this.jobId = jobId;
        this.error = error;
        this.result = result;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
