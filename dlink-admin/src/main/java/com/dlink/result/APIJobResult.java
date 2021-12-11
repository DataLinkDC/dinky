package com.dlink.result;

import com.dlink.job.Job;
import com.dlink.job.JobResult;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * APIJobResult
 *
 * @author wenmo
 * @since 2021/12/11 22:49
 */
@Getter
@Setter
public class APIJobResult {
    private String jobManagerAddress;
    private Job.JobStatus status;
    private boolean success;
    private String jobId;
    private String error;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public APIJobResult(String jobManagerAddress, Job.JobStatus status, boolean success, String jobId, String error, LocalDateTime startTime, LocalDateTime endTime) {
        this.jobManagerAddress = jobManagerAddress;
        this.status = status;
        this.success = success;
        this.jobId = jobId;
        this.error = error;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static APIJobResult build(JobResult jobResult){
        return new APIJobResult(jobResult.getJobManagerAddress(),jobResult.getStatus(),jobResult.isSuccess(),
                jobResult.getJobId(),jobResult.getError(),jobResult.getStartTime(),jobResult.getEndTime());
    }
}
