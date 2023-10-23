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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * APIJobResult
 *
 * @since 2021/12/11 22:49
 */
@Getter
@Setter
@ApiModel(value = "APIJobResult", description = "Result of a Job from the API")
public class APIJobResult {

    @ApiModelProperty(
            value = "Job Manager Address",
            dataType = "String",
            notes = "Address of the Job Manager",
            example = "localhost:8081")
    private String jobManagerAddress;

    @ApiModelProperty(
            value = "Job Status",
            dataType = "Job.JobStatus",
            notes = "Status of the Job",
            example = "RUNNING")
    private Job.JobStatus status;

    @ApiModelProperty(
            value = "Success Flag",
            dataType = "boolean",
            notes = "Whether the Job execution was successful",
            example = "true")
    private boolean success;

    @ApiModelProperty(value = "Job ID", dataType = "String", notes = "ID of the Job", example = "job-12345")
    private String jobId;

    @ApiModelProperty(
            value = "Error Message",
            dataType = "String",
            notes = "Error message if the Job execution failed",
            example = "An error occurred while processing the Job")
    private String error;

    @ApiModelProperty(
            value = "Start Time",
            dataType = "LocalDateTime",
            notes = "Start time of the Job execution",
            example = "2023-09-15 10:00:00")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty(
            value = "End Time",
            dataType = "LocalDateTime",
            notes = "End time of the Job execution",
            example = "2023-09-15 10:30:00")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
