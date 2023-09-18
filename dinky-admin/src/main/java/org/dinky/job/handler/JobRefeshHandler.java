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

package org.dinky.job.handler;

import org.dinky.api.FlinkAPI;
import org.dinky.assertion.Asserts;
import org.dinky.context.SpringContextUtils;
import org.dinky.data.constant.FlinkRestResultConstant;
import org.dinky.data.dto.JobDataDto;
import org.dinky.data.enums.JobStatus;
import org.dinky.data.model.ClusterConfiguration;
import org.dinky.data.model.JobInfoDetail;
import org.dinky.data.model.JobInstance;
import org.dinky.gateway.Gateway;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.enums.GatewayType;
import org.dinky.gateway.exception.NotSupportGetStatusException;
import org.dinky.gateway.model.FlinkClusterConfig;
import org.dinky.job.JobConfig;
import org.dinky.service.JobHistoryService;
import org.dinky.service.JobInstanceService;
import org.dinky.utils.TimeUtil;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import cn.hutool.json.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@DependsOn("springContextUtils")
public class JobRefeshHandler {

    private static final JobInstanceService jobInstanceService;
    private static final JobHistoryService jobHistoryService;

    static {
        jobInstanceService = SpringContextUtils.getBean("jobInstanceServiceImpl", JobInstanceService.class);
        jobHistoryService = SpringContextUtils.getBean("jobHistoryServiceImpl", JobHistoryService.class);
    }

    /**
     * Refresh the job
     * It receives two parameters: {@link org.dinky.data.model.JobInfoDetail} and needSave and returns a Boolean value.
     * When the return value is true, the job has completed and needs to be removed from the thread pool,
     * otherwise it means that the next round of flushing continues
     *
     * @param jobInfoDetail job info detail.
     * @param needSave      Indicates if the job needs to be saved.
     * @return True if the job is done, false otherwise.
     */
    public static boolean refeshJob(JobInfoDetail jobInfoDetail, boolean needSave) {
        log.debug("Start to refresh job: {}->{}", jobInfoDetail.getInstance().getId(), jobInfoDetail.getInstance().getName());

        JobInstance jobInstance = jobInfoDetail.getInstance();
        String oldStatus = jobInstance.getStatus();

        JobDataDto jobDataDto = getJobHistory(
                jobInstance.getId(),
                jobInfoDetail.getCluster().getJobManagerHost(),
                jobInfoDetail.getInstance().getJid());

        if (Asserts.isNull(jobDataDto.getJob()) || jobDataDto.isError()) {
            //If the job fails to get it, the default Finish Time is the current time
            jobInstance.setStatus(JobStatus.UNKNOWN.getValue());
            jobInstance.setFinishTime(LocalDateTime.now());
            jobInstance.setError(jobDataDto.getErrorMsg());
            jobInfoDetail.getJobDataDto().setError(true);
            jobInfoDetail.getJobDataDto().setErrorMsg(jobDataDto.getErrorMsg());
        } else {
            jobInfoDetail.setJobDataDto(jobDataDto);
            JsonNode job = jobDataDto.getJob();
            Long startTime = job.get(FlinkRestResultConstant.JOB_CREATE_TIME).asLong();
            Long endTime = job.get(FlinkRestResultConstant.JOB_FINISH_TIME).asLong();

            jobInstance.setStatus(getJobStatus(jobInfoDetail).getValue());
            jobInstance.setDuration(
                    job.get(FlinkRestResultConstant.JOB_DURATION).asLong());
            jobInstance.setCreateTime(TimeUtil.longToLocalDateTime(startTime));
            //if the job is still running the end-time is -1
            jobInstance.setFinishTime(TimeUtil.longToLocalDateTime(endTime));
        }
        jobInstance.setUpdateTime(LocalDateTime.now());

        //Set to true if the job status has completed
        //If the job status is Unknown and the status fails to be updated for 1 minute, set to true and discard the update
        boolean isDone = (JobStatus.isDone(jobInstance.getStatus()))
                || (TimeUtil.localDateTimeToLong(jobInstance.getFinishTime()) > 0
                && Duration.between(jobInstance.getFinishTime(), LocalDateTime.now())
                .toMinutes()
                >= 1);

        if (!oldStatus.equals(jobInstance.getStatus()) || isDone || needSave) {
            log.debug("Dump JobInfo to database: {}->{}", jobInstance.getId(), jobInstance.getName());
            jobInstanceService.updateById(jobInstance);
            jobHistoryService.updateById(jobInfoDetail.getJobDataDto().toJobHistory());
        }

        if (isDone) {
            log.debug("Job is done: {}->{}", jobInstance.getId(), jobInstance.getName());
            handleJobDone(jobInfoDetail);
        }
        return isDone;
    }

    /**
     * Retrieves job history.
     * getJobStatusInformationFromFlinkRestAPI
     *
     * @param id             The job ID.
     * @param jobManagerHost The job manager host.
     * @param jobId          The job ID.
     * @return {@link org.dinky.data.dto.JobDataDto}.
     */
    public static JobDataDto getJobHistory(Integer id, String jobManagerHost, String jobId) {
        JobDataDto.JobDataDtoBuilder builder = JobDataDto.builder();
        FlinkAPI api = FlinkAPI.build(jobManagerHost);
        try {
            JsonNode jobInfo = FlinkAPI.build(jobManagerHost).getJobInfo(jobId);
            if (jobInfo.has(FlinkRestResultConstant.ERRORS)) {
                throw new Exception(String.valueOf(jobInfo.get(FlinkRestResultConstant.ERRORS)));
            }
            return builder.id(id)
                    .checkpoints(api.getCheckPoints(jobId))
                    .checkpointsConfig(api.getCheckPointsConfig(jobId))
                    .exceptions(api.getException(jobId))
                    .job(jobInfo)
                    .config(api.getJobsConfig(jobId))
                    .build();
        } catch (Exception e) {
            log.error("Connect {} failed,{}", jobManagerHost, e.getMessage());
            return builder.id(id).error(true).errorMsg(e.getMessage()).build();
        }
    }


    /**
     * Gets the job status.
     *
     * @param jobInfoDetail The job info detail.
     * @return The job status.
     */
    private static JobStatus getJobStatus(JobInfoDetail jobInfoDetail) {

        ClusterConfiguration clusterCfg = jobInfoDetail.getClusterConfiguration();

        if (!Asserts.isNull(clusterCfg)) {
            try {
                String appId = jobInfoDetail.getCluster().getName();

                GatewayConfig gatewayConfig = GatewayConfig.build(clusterCfg.getFlinkClusterCfg());
                gatewayConfig.getClusterConfig().setAppId(appId);
                gatewayConfig
                        .getFlinkConfig()
                        .setJobName(jobInfoDetail.getInstance().getName());

                Gateway gateway = Gateway.build(gatewayConfig);
                return gateway.getJobStatusById(appId);
            } catch (NotSupportGetStatusException ignored) {
            }
        }

        JobDataDto jobDataDto = jobInfoDetail.getJobDataDto();
        String status =
                jobDataDto.getJob().get(FlinkRestResultConstant.JOB_STATE).asText();
        return JobStatus.get(status);
    }

    /**
     * Handles job completion.
     *
     * @param jobInfoDetail The job info detail.
     */
    private static void handleJobDone(JobInfoDetail jobInfoDetail) {
        JobInstance jobInstance = jobInfoDetail.getInstance();
        JobDataDto jobDataDto = jobInfoDetail.getJobDataDto();

        if (GatewayType.isDeployCluster(jobInstance.getType())) {
            JobConfig jobConfig = new JobConfig();
            String configJson =
                    jobDataDto.getClusterConfiguration().get("configJson").asText();
            jobConfig.buildGatewayConfig(new JSONObject(configJson).toBean(FlinkClusterConfig.class));
            jobConfig.getGatewayConfig().setType(GatewayType.get(jobInstance.getType()));
            jobConfig.getGatewayConfig().getFlinkConfig().setJobName(jobInstance.getName());
            Gateway.build(jobConfig.getGatewayConfig()).onJobFinishCallback(jobInstance.getStatus());
        }
    }
}
