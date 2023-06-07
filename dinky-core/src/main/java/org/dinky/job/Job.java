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
import org.dinky.executor.Executor;
import org.dinky.executor.ExecutorSetting;
import org.dinky.gateway.enums.GatewayType;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Job
 *
 * @since 2021/6/26 23:39
 */
@Getter
@Setter
public class Job {
    private Integer id;
    private Integer jobInstanceId;
    private JobConfig jobConfig;
    private String jobManagerAddress;
    private JobStatus status;
    private GatewayType type;
    private String statement;
    private String jobId;
    private String error;
    private IResult result;
    private ExecutorSetting executorSetting;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Executor executor;
    private boolean useGateway;
    private List<String> jids;

    public enum JobStatus {
        INITIALIZE,
        RUNNING,
        SUCCESS,
        FAILED,
        CANCEL
    }

    public Job(
            JobConfig jobConfig,
            GatewayType type,
            JobStatus status,
            String statement,
            ExecutorSetting executorSetting,
            Executor executor,
            boolean useGateway) {
        this.jobConfig = jobConfig;
        this.type = type;
        this.status = status;
        this.statement = statement;
        this.executorSetting = executorSetting;
        this.startTime = LocalDateTime.now();
        this.executor = executor;
        this.useGateway = useGateway;
    }

    public static Job init(
            GatewayType type,
            JobConfig jobConfig,
            ExecutorSetting executorSetting,
            Executor executor,
            String statement,
            boolean useGateway) {
        return new Job(
                jobConfig,
                type,
                JobStatus.INITIALIZE,
                statement,
                executorSetting,
                executor,
                useGateway);
    }

    public JobResult getJobResult() {
        return new JobResult(
                id,
                jobInstanceId,
                jobConfig,
                jobManagerAddress,
                status,
                statement,
                jobId,
                error,
                result,
                startTime,
                endTime);
    }

    public boolean isFailed() {
        return status.equals(JobStatus.FAILED);
    }
}
