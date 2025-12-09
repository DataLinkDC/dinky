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

import static org.dinky.utils.JsonUtils.objectMapper;

import org.dinky.api.FlinkAPI;
import org.dinky.assertion.Asserts;
import org.dinky.cluster.FlinkClusterInfo;
import org.dinky.context.SpringContextUtils;
import org.dinky.context.TenantContextHolder;
import org.dinky.data.constant.FlinkRestResultConstant;
import org.dinky.data.dto.ClusterConfigurationDTO;
import org.dinky.data.dto.JobDataDto;
import org.dinky.data.enums.GatewayType;
import org.dinky.data.enums.JobStatus;
import org.dinky.data.flink.backpressure.FlinkJobNodeBackPressure;
import org.dinky.data.flink.checkpoint.CheckPointOverView;
import org.dinky.data.flink.config.CheckpointConfigInfo;
import org.dinky.data.flink.config.FlinkJobConfigInfo;
import org.dinky.data.flink.exceptions.FlinkJobExceptionsDetail;
import org.dinky.data.flink.job.FlinkJobDetailInfo;
import org.dinky.data.flink.watermark.FlinkJobNodeWaterMark;
import org.dinky.data.model.ClusterInstance;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.model.ext.JobInfoDetail;
import org.dinky.data.model.job.History;
import org.dinky.data.model.job.JobInstance;
import org.dinky.gateway.Gateway;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.exception.NotSupportGetStatusException;
import org.dinky.gateway.model.FlinkClusterConfig;
import org.dinky.init.FlinkHistoryServer;
import org.dinky.job.JobConfig;
import org.dinky.service.ClusterInstanceService;
import org.dinky.service.HistoryService;
import org.dinky.service.JobHistoryService;
import org.dinky.service.JobInstanceService;
import org.dinky.service.TaskService;
import org.dinky.utils.JsonUtils;
import org.dinky.utils.TimeUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.type.CollectionType;

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
    private static final ClusterInstanceService clusterInstanceService;
    private static final HistoryService historyService;
    private static final TaskService taskService;

    static {
        jobInstanceService = SpringContextUtils.getBean("jobInstanceServiceImpl", JobInstanceService.class);
        jobHistoryService = SpringContextUtils.getBean("jobHistoryServiceImpl", JobHistoryService.class);
        clusterInstanceService = SpringContextUtils.getBean("clusterInstanceServiceImpl", ClusterInstanceService.class);
        historyService = SpringContextUtils.getBean("historyServiceImpl", HistoryService.class);
        taskService = SpringContextUtils.getBean("taskServiceImpl", TaskService.class);
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
        if (Asserts.isNull(TenantContextHolder.get())) {
            jobInstanceService.initTenantByJobInstanceId(
                    jobInfoDetail.getInstance().getId());
        }
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

        checkAndRefreshCluster(jobInfoDetail);

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
            Optional<JobStatus> jobStatus = getJobStatus(jobInfoDetail);
            if (jobStatus.isPresent() && JobStatus.isDone(jobStatus.get().getValue())) {
                jobInstance.setStatus(jobStatus.get().getValue());
            } else {
                // If the job fails to get it, the default Finish Time is the current time
                jobInstance.setStatus(JobStatus.RECONNECTING.getValue());
                jobInstance.setError(jobDataDto.getErrorMsg());
                jobInfoDetail.getJobDataDto().setError(true);
                jobInfoDetail.getJobDataDto().setErrorMsg(jobDataDto.getErrorMsg());
            }
            if (jobInstance.getFinishTime() == null || TimeUtil.localDateTimeToLong(jobInstance.getFinishTime()) < 1) {
                jobInstance.setFinishTime(LocalDateTime.now());
            }
        } else {
            jobInfoDetail.setJobDataDto(jobDataDto);
            FlinkJobDetailInfo flinkJobDetailInfo = jobDataDto.getJob();
            jobInstance.setStatus(flinkJobDetailInfo.getState());
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

        if (JobStatus.isTransition(
                jobInstance.getStatus(),
                Asserts.isNull(jobDataDto.getJob()) ? null : jobDataDto.getJob().getEndTime())) {
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
            if (jobInstance.getStatus().equals(JobStatus.UNKNOWN.getValue())
                    || jobInstance.getStatus().equals(JobStatus.RECONNECTING.getValue())) {
                JobInstance fromDb = jobInstanceService.getById(jobInstance.getId());
                // If the job status is unknown and the job status in the database is not done, update the job status
                // just prevent the task from being mistakenly updated to UNKNOWN
                if (JobStatus.valueOf(fromDb.getStatus()).isDone()) {
                    // if status is RECONNECTING, ignore it
                    isDone = true;
                } else {
                    jobInstanceService.updateById(jobInstance);
                    jobHistoryService.updateById(jobInfoDetail.getJobDataDto().toJobHistory());
                }
            } else {
                jobInstanceService.updateById(jobInstance);
                jobHistoryService.updateById(jobInfoDetail.getJobDataDto().toJobHistory());
            }
        }

        if (isDone) {
            try {
                log.debug("Job is done: {}->{}", jobInstance.getId(), jobInstance.getName());
                // 检查是否需要自动重启
                if (shouldAutoRestart(jobInstance, jobInfoDetail)) {
                    tryAutoRestart(jobInstance, jobInfoDetail);
                }
                handleJobDone(jobInfoDetail);
            } catch (Exception e) {
                log.error("failed handel job done：", e);
            }
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
        if (FlinkHistoryServer.HISTORY_JOBID_SET.contains(jobId)
                && SystemConfiguration.getInstances().getUseFlinkHistoryServer().getValue()) {
            jobManagerHost = "127.0.0.1:"
                    + SystemConfiguration.getInstances()
                            .getFlinkHistoryServerPort()
                            .getValue();
        }
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
                        try {
                            CollectionType listType = objectMapper
                                    .getTypeFactory()
                                    .constructCollectionType(ArrayList.class, FlinkJobNodeWaterMark.class);
                            List<FlinkJobNodeWaterMark> watermark =
                                    objectMapper.readValue(api.getWatermark(jobId, vertex), listType);
                            planNode.setWatermark(watermark);
                        } catch (Exception ignored) {
                        }
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
    private static Optional<JobStatus> getJobStatus(JobInfoDetail jobInfoDetail) {

        ClusterConfigurationDTO clusterCfg = jobInfoDetail.getClusterConfiguration();
        ClusterInstance clusterInstance = jobInfoDetail.getClusterInstance();
        if (!Asserts.isNull(clusterCfg)
                && (GatewayType.YARN_PER_JOB.getLongValue().equals(clusterInstance.getType())
                        || GatewayType.YARN_APPLICATION.getLongValue().equals(clusterInstance.getType()))) {
            try {
                String appId = jobInfoDetail.getClusterInstance().getName();

                GatewayConfig gatewayConfig = GatewayConfig.build(clusterCfg.getConfig());
                gatewayConfig.getClusterConfig().setAppId(appId);
                gatewayConfig
                        .getFlinkConfig()
                        .setJobName(jobInfoDetail.getInstance().getName());

                Gateway gateway = Gateway.build(gatewayConfig);
                return Optional.of(gateway.getJobStatusById(appId));
            } catch (NotSupportGetStatusException ignored) {
                // if the gateway does not support get status, then use the api to get job status
                // ignore to do something here
            }
        }
        return Optional.empty();
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

    /**
     * Check if the job should be auto-restarted.
     *
     * @param jobInstance The job instance.
     * @param jobInfoDetail The job info detail.
     * @return True if the job should be auto-restarted, false otherwise.
     */
    private static boolean shouldAutoRestart(JobInstance jobInstance, JobInfoDetail jobInfoDetail) {
        String status = jobInstance.getStatus();
        // 只对FAILED和UNKNOWN状态进行自动重启
        if (!JobStatus.FAILED.getValue().equals(status)
                && !JobStatus.UNKNOWN.getValue().equals(status)) {
            return false;
        }

        // 检查任务配置中是否启用了自动重启
        try {
            History history = jobInfoDetail.getHistory();
            if (Asserts.isNull(history) || Asserts.isNull(history.getConfigJson())) {
                return false;
            }

            JobConfig jobConfig = history.getConfigJson();
            Boolean autoRestart = jobConfig.getAutoRestart();
            return Boolean.TRUE.equals(autoRestart);
        } catch (Exception e) {
            log.warn("Failed to check auto restart config for job {}: {}", jobInstance.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * Try to auto-restart the job from the latest checkpoint.
     *
     * @param jobInstance The job instance.
     * @param jobInfoDetail The job info detail.
     */
    private static void tryAutoRestart(JobInstance jobInstance, JobInfoDetail jobInfoDetail) {
        if (Asserts.isNull(jobInstance.getTaskId())) {
            log.warn("Cannot auto restart job {}: taskId is null", jobInstance.getId());
            return;
        }

        try {
            // 获取最新的checkpoint路径
            String checkpointPath = getLatestCheckpointPath(jobInfoDetail.getJobDataDto());
            log.info("Auto restarting job {} from checkpoint: {}", jobInstance.getId(), checkpointPath);
            taskService.restartTask(jobInstance.getTaskId(), checkpointPath);
            log.info("Auto restart job {} triggered successfully", jobInstance.getId());
        } catch (Exception e) {
            log.error("Failed to auto restart job {}: {}", jobInstance.getId(), e.getMessage(), e);
        }
    }

    /**
     * Get the latest checkpoint path from JobDataDto.
     *
     * @param jobDataDto The job data DTO.
     * @return The latest checkpoint path, or null if not found.
     */
    private static String getLatestCheckpointPath(JobDataDto jobDataDto) {
        if (Asserts.isNull(jobDataDto) || Asserts.isNull(jobDataDto.getCheckpoints())) {
            return null;
        }

        CheckPointOverView checkpoints = jobDataDto.getCheckpoints();
        CheckPointOverView.LatestCheckpoints latestCheckpoints = checkpoints.getLatestCheckpoints();
        if (Asserts.isNull(latestCheckpoints)) {
            return null;
        }

        // 优先使用completed checkpoint
        CheckPointOverView.CompletedCheckpointStatistics completedCheckpoint =
                latestCheckpoints.getCompletedCheckpointStatistics();
        if (Asserts.isNotNull(completedCheckpoint) && Asserts.isNotNullString(completedCheckpoint.getExternalPath())) {
            return completedCheckpoint.getExternalPath();
        }

        // 如果没有completed checkpoint，尝试使用savepoint
        CheckPointOverView.CompletedCheckpointStatistics savepointStatistics =
                latestCheckpoints.getSavepointStatistics();
        if (Asserts.isNotNull(savepointStatistics) && Asserts.isNotNullString(savepointStatistics.getExternalPath())) {
            return savepointStatistics.getExternalPath();
        }

        return null;
    }

    /**
     * In a YARN cluster with HA mode enabled,
     * if the jobManagerHost cannot be connected,
     * attempt to retrieve the latest address of the jobManagerHost from ZK
     *
     * @param jobInfoDetail The job info detail.
     * @return The job status.
     */
    private static void checkAndRefreshCluster(JobInfoDetail jobInfoDetail) {
        if (!GatewayType.isDeployYarnCluster(jobInfoDetail.getClusterInstance().getType())) {
            return;
        }

        FlinkClusterInfo flinkClusterInfo = clusterInstanceService.checkHeartBeat(
                jobInfoDetail.getClusterInstance().getHosts(),
                jobInfoDetail.getClusterInstance().getJobManagerHost());
        if (!flinkClusterInfo.isEffective()) {
            ClusterConfigurationDTO clusterCfg = jobInfoDetail.getClusterConfiguration();
            ClusterInstance clusterInstance = jobInfoDetail.getClusterInstance();
            if (!Asserts.isNull(clusterCfg)) {
                String appId = jobInfoDetail.getClusterInstance().getName();

                GatewayConfig gatewayConfig = GatewayConfig.build(clusterCfg.getConfig());
                gatewayConfig.getClusterConfig().setAppId(appId);
                gatewayConfig
                        .getFlinkConfig()
                        .setJobName(jobInfoDetail.getInstance().getName());

                Gateway gateway = Gateway.build(gatewayConfig);
                String latestJobManageHost = gateway.getLatestJobManageHost(appId, clusterInstance.getJobManagerHost());

                if (Asserts.isNotNull(latestJobManageHost)) {
                    clusterInstance.setHosts(latestJobManageHost);
                    clusterInstance.setJobManagerHost(latestJobManageHost);
                    clusterInstanceService.updateById(clusterInstance);
                    if (Asserts.isNotNull(jobInfoDetail.getHistory())) {
                        jobInfoDetail.getHistory().setJobManagerAddress(latestJobManageHost);
                        historyService.updateById(jobInfoDetail.getHistory());
                    }
                }
            }
        }
    }
}
