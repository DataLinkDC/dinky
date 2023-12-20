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

package org.dinky.job;

import org.dinky.data.result.IResult;
import org.dinky.metadata.result.JdbcSelectResult;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * JobResult
 *
 * @since 2021/6/29 23:56
 */
@Getter
@Setter
@ApiModel(value = "JobResult", description = "Result of a job execution")
public class JobResult {

    @ApiModelProperty(value = "Unique identifier for the job result", dataType = "Integer", example = "123")
    private Integer id;

    @ApiModelProperty(
            value = "Configuration details of the job",
            dataType = "JobConfig",
            notes = "Configuration details of the job")
    private JobConfig jobConfig;

    @ApiModelProperty(
            value = "Address of the job manager",
            dataType = "String",
            example = "localhost:8081",
            notes = "Address of the job manager")
    private String jobManagerAddress;

    @ApiModelProperty(value = "Status of the job", dataType = "Job.JobStatus", notes = "Status of the job")
    private Job.JobStatus status;

    @ApiModelProperty(
            value = "Flag indicating whether the job was successful",
            dataType = "boolean",
            example = "true",
            notes = "Flag indicating whether the job was successful")
    private boolean success;

    @ApiModelProperty(
            value = "SQL statement executed by the job",
            dataType = "String",
            example = "SELECT * FROM table_name",
            notes = "SQL statement executed by the job")
    private String statement;

    @ApiModelProperty(
            value = "Unique identifier for the job",
            dataType = "String",
            example = "job_123",
            notes = "Unique identifier for the job")
    private String jobId;

    @ApiModelProperty(
            value = "Unique identifier for the job instance",
            dataType = "Integer",
            example = "456",
            notes = "Unique identifier for the job instance")
    private Integer jobInstanceId;

    @ApiModelProperty(
            value = "Error message in case of job failure",
            dataType = "String",
            example = "Job failed due to a timeout",
            notes = "Error message in case of job failure")
    private String error;

    @ApiModelProperty(value = "Result data of the job", dataType = "IResult", notes = "Result data of the job")
    private IResult result;

    @ApiModelProperty(value = "Result data of the job", dataType = "IResult", notes = "Result data of the job")
    private List<JdbcSelectResult> results;

    @ApiModelProperty(
            value = "Start time of job execution",
            dataType = "LocalDateTime",
            example = "2023-09-15T10:30:00",
            notes = "Start time of job execution")
    private LocalDateTime startTime;

    @ApiModelProperty(
            value = "End time of job execution",
            dataType = "LocalDateTime",
            example = "2023-09-15T11:15:00",
            notes = "End time of job execution")
    private LocalDateTime endTime;

    public JobResult() {}

    public JobResult(
            Integer id,
            Integer jobInstanceId,
            JobConfig jobConfig,
            String jobManagerAddress,
            Job.JobStatus status,
            String statement,
            String jobId,
            String error,
            IResult result,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        this.id = id;
        this.jobInstanceId = jobInstanceId;
        this.jobConfig = jobConfig;
        this.jobManagerAddress = jobManagerAddress;
        this.status = status;
        this.success = status == (Job.JobStatus.SUCCESS);
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
