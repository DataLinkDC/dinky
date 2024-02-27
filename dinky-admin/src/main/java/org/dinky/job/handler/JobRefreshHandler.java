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
import org.dinky.data.dto.ClusterConfigurationDTO;
import org.dinky.data.dto.JobDataDto;
import org.dinky.data.enums.JobStatus;
import org.dinky.data.flink.backpressure.FlinkJobNodeBackPressure;
import org.dinky.data.flink.checkpoint.CheckPointOverView;
import org.dinky.data.flink.config.CheckpointConfigInfo;
import org.dinky.data.flink.config.FlinkJobConfigInfo;
import org.dinky.data.flink.exceptions.FlinkJobExceptionsDetail;
import org.dinky.data.flink.job.FlinkJobDetailInfo;
import org.dinky.data.flink.watermark.FlinkJobNodeWaterMark;
import org.dinky.data.model.ext.JobInfoDetail;
import org.dinky.data.model.job.JobInstance;
import org.dinky.gateway.Gateway;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.enums.GatewayType;
import org.dinky.gateway.exception.NotSupportGetStatusException;
import org.dinky.gateway.model.FlinkClusterConfig;
import org.dinky.job.JobConfig;
import org.dinky.service.JobHistoryService;
import org.dinky.service.JobInstanceService;
import org.dinky.utils.JsonUtils;
import org.dinky.utils.TimeUtil;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.JsonNode;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@DependsOn("springContextUtils")
public class JobRefreshHandler {

    private static final JobInstanceService jobInstanceService;
    private static final JobHistoryService jobHistoryService;

    static {
        jobInstanceService = SpringContextUtils.getBean("jobInstanceServiceImpl", JobInstanceService.class);
        jobHistoryService = SpringContextUtils.getBean("jobHistoryServiceImpl", JobHistoryService.class);
    }

    /**
     * Refresh the job
     * It receives two parameters: {@link JobInfoDetail} and needSave and returns a Boolean value.
     * When the return value is true, the job has completed and needs to be removed from the thread pool,
     * otherwise it means that the next round of flushing continues
     *
     * @param jobInfoDetail job info detail.
     * @param needSave      Indicates if the job needs to be saved.
     * @return True if the job is done, false otherwise.
     */
    public static boolean refreshJob(JobInfoDetail jobInfoDetail, boolean needSave) {
        log.debug(
                "Start to refresh job: {}->{}",
                jobInfoDetail.getInstance().getId(),
                jobInfoDetail.getInstance().getName());

        JobInstance jobInstance = jobInfoDetail.getInstance();
        JobDataDto jobDataDto = jobInfoDetail.getJobDataDto();
        String oldStatus = jobInstance.getStatus();

        // Cluster information is missing and cannot be monitored
        if (Asserts.isNull(jobInfoDetail.getClusterInstance())) {
            jobInstance.setStatus(JobStatus.UNKNOWN.getValue());
            jobInstanceService.updateById(jobInstance);
            return true;
        }
        // Update the value of JobData from the flink api while ignoring the null value to prevent
        // some other configuration from being overwritten
        BeanUtil.copyProperties(
                getJobData(
                        jobInstance.getId(),
                        jobInfoDetail.getClusterInstance().getJobManagerHost(),
                        jobInfoDetail.getInstance().getJid()),
                jobDataDto,
                CopyOptions.create().ignoreNullValue());

        if (Asserts.isNull(jobDataDto.getJob()) || jobDataDto.isError()) {
            // If the job fails to get it, the default Finish Time is the current time
            jobInstance.setStatus(JobStatus.RECONNECTING.getValue());
            jobInstance.setError(jobDataDto.getErrorMsg());
            jobInfoDetail.getJobDataDto().setError(true);
            jobInfoDetail.getJobDataDto().setErrorMsg(jobDataDto.getErrorMsg());
            if (jobInstance.getFinishTime() == null || TimeUtil.localDateTimeToLong(jobInstance.getFinishTime()) < 1) {
                jobInstance.setFinishTime(LocalDateTime.now());
            }
        } else {
            jobInfoDetail.setJobDataDto(jobDataDto);
            FlinkJobDetailInfo flinkJobDetailInfo = jobDataDto.getJob();
            jobInstance.setStatus(getJobStatus(jobInfoDetail).getValue());
            jobInstance.setDuration(flinkJobDetailInfo.getDuration());
            jobInstance.setCreateTime(TimeUtil.toLocalDateTime(flinkJobDetailInfo.getStartTime()));
            // if the job is still running the end-time is -1
            jobInstance.setFinishTime(TimeUtil.toLocalDateTime(flinkJobDetailInfo.getEndTime()));
        }
        jobInstance.setUpdateTime(LocalDateTime.now());

        // The transition status include failed and reconnecting ( Dinky custom )
        // The done status include failed and canceled and finished and unknown ( Dinky custom )
        // The task status of batch job which network unstable: run -> transition -> run -> transition -> done
        // The task status of stream job which automatically restart after failure: run -> transition -> run ->
        // transition -> run
        // Set to true if the job status which is done has completed
        // If the job status is transition and the status fails to be updated for 1 minute, set to true and discard the
        // update

        boolean isTransition = false;

        if (JobStatus.isTransition(jobInstance.getStatus())) {
            Long finishTime = TimeUtil.localDateTimeToLong(jobInstance.getFinishTime());
            long duration = Duration.between(jobInstance.getFinishTime(), LocalDateTime.now())
                    .toMinutes();
            if (finishTime > 0 && duration < 1) {
                log.debug("Job is transition: {}->{}", jobInstance.getId(), jobInstance.getName());
                isTransition = true;
            } else if (JobStatus.RECONNECTING.getValue().equals(jobInstance.getStatus())) {
                log.debug(
                        "Job is not reconnected success at the specified time,set as UNKNOWN: {}->{}",
                        jobInstance.getId(),
                        jobInstance.getName());
                jobInstance.setStatus(JobStatus.UNKNOWN.getValue());
            }
        }

        boolean isDone = (JobStatus.isDone(jobInstance.getStatus()))
                || (TimeUtil.localDateTimeToLong(jobInstance.getFinishTime()) > 0
                        && Duration.between(jobInstance.getFinishTime(), LocalDateTime.now())
                                        .toMinutes()
                                >= 1);

        isDone = !isTransition && isDone;

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
    public static JobDataDto getJobData(Integer id, String jobManagerHost, String jobId) {
        JobDataDto.JobDataDtoBuilder builder = JobDataDto.builder();
        FlinkAPI api = FlinkAPI.build(jobManagerHost);
        try {
            JsonNode jobInfo = FlinkAPI.build(jobManagerHost).getJobInfo(jobId);
            if (jobInfo.has(FlinkRestResultConstant.ERRORS)) {
                throw new Exception(String.valueOf(jobInfo.get(FlinkRestResultConstant.ERRORS)));
            }

            FlinkJobConfigInfo jobConfigInfo =
                    JSON.parseObject(api.getJobsConfig(jobId).toString()).toJavaObject(FlinkJobConfigInfo.class);

            FlinkJobDetailInfo flinkJobDetailInfo =
                    JSON.parseObject(jobInfo.toString()).toJavaObject(FlinkJobDetailInfo.class);
            // 获取 WATERMARK  & BACKPRESSURE 信息
            api.getVertices(jobId).forEach(vertex -> {
                flinkJobDetailInfo.getPlan().getNodes().forEach(planNode -> {
                    if (planNode.getId().equals(vertex)) {
                        planNode.setWatermark(
                                JsonUtils.toList(api.getWatermark(jobId, vertex), FlinkJobNodeWaterMark.class));
                        planNode.setBackpressure(JsonUtils.toJavaBean(
                                api.getBackPressure(jobId, vertex), FlinkJobNodeBackPressure.class));
                    }
                });
            });
            JsonNode checkPoints = api.getCheckPoints(jobId);
            if (checkPoints.findParent("errors") == null) {
                builder.checkpoints(JsonUtils.parseObject(checkPoints.toString(), CheckPointOverView.class));
            }
            JsonNode checkpointConfigInfo = api.getCheckPointsConfig(jobId);
            if (checkpointConfigInfo.findParent("errors") == null) {
                builder.checkpointsConfig(
                        JsonUtils.parseObject(checkpointConfigInfo.toString(), CheckpointConfigInfo.class));
            }
            return builder.id(id)
                    .exceptions(
                            JsonUtils.parseObject(api.getException(jobId).toString(), FlinkJobExceptionsDetail.class))
                    .job(flinkJobDetailInfo)
                    .config(jobConfigInfo)
                    .build();
        } catch (Exception e) {
            log.warn("Connect {} failed,{}", jobManagerHost, e.getMessage());
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

        ClusterConfigurationDTO clusterCfg = jobInfoDetail.getClusterConfiguration();

        if (!Asserts.isNull(clusterCfg)
                && GatewayType.YARN_PER_JOB.getLongValue().equals(clusterCfg.getType())) {
            try {
                String appId = jobInfoDetail.getClusterInstance().getName();

                GatewayConfig gatewayConfig = GatewayConfig.build(clusterCfg.getConfig());
                gatewayConfig.getClusterConfig().setAppId(appId);
                gatewayConfig
                        .getFlinkConfig()
                        .setJobName(jobInfoDetail.getInstance().getName());

                Gateway gateway = Gateway.build(gatewayConfig);
                return gateway.getJobStatusById(appId);
            } catch (NotSupportGetStatusException ignored) {
                // if the gateway does not support get status, then use the api to get job status
                // ignore to do something here
            }
        }
        JobDataDto jobDataDto = jobInfoDetail.getJobDataDto();
        String status = jobDataDto.getJob().getState();
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
        String clusterType = jobInfoDetail.getClusterInstance().getType();

        if (GatewayType.isDeployCluster(clusterType)) {
            JobConfig jobConfig = new JobConfig();
            FlinkClusterConfig configJson = jobDataDto.getClusterConfiguration().getConfigJson();
            jobConfig.buildGatewayConfig(configJson);
            jobConfig.getGatewayConfig().setType(GatewayType.get(clusterType));
            jobConfig.getGatewayConfig().getFlinkConfig().setJobName(jobInstance.getName());
            Gateway.build(jobConfig.getGatewayConfig()).onJobFinishCallback(jobInstance.getStatus());
        }
    }
}
