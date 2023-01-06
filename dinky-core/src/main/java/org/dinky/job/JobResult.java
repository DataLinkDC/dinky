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

package com.dlink.job;

import com.dlink.result.IResult;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

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

    public JobResult(Integer id, Integer jobInstanceId, JobConfig jobConfig, String jobManagerAddress, Job.JobStatus status,
                     String statement, String jobId, String error, IResult result, LocalDateTime startTime, LocalDateTime endTime) {
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

    public void setStartTimeNow() {
        this.setStartTime(LocalDateTime.now());
    }

    public void setEndTimeNow() {
        this.setEndTime(LocalDateTime.now());
    }
}
