package com.dlink.gateway.model;

import lombok.Getter;
import lombok.Setter;

/**
 * JobInfo
 *
 * @author wenmo
 * @since 2021/11/3 21:45
 */
@Getter
@Setter
public class JobInfo {
    private String jobId;
    private String savePoint;
    private JobStatus status;

    public JobInfo(String jobId) {
        this.jobId = jobId;
    }

    public JobInfo(String jobId, JobStatus status) {
        this.jobId = jobId;
        this.status = status;
    }

    public enum JobStatus{
        RUN("run"),STOP("stop"),CANCEL("cancel"),FAIL("fail");

        private String value;

        JobStatus(String value){
            this.value = value;
        }
    }
}
