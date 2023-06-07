/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.data.result;

import org.dinky.job.Job;
import org.dinky.job.JobResult;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * APIJobResult
 *
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

    public APIJobResult(
            String jobManagerAddress,
            Job.JobStatus status,
            boolean success,
            String jobId,
            String error,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        this.jobManagerAddress = jobManagerAddress;
        this.status = status;
        this.success = success;
        this.jobId = jobId;
        this.error = error;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static APIJobResult build(JobResult jobResult) {
        return new APIJobResult(
                jobResult.getJobManagerAddress(),
                jobResult.getStatus(),
                jobResult.isSuccess(),
                jobResult.getJobId(),
                jobResult.getError(),
                jobResult.getStartTime(),
                jobResult.getEndTime());
    }
}
