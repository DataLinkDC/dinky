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

package org.dinky.data.model.ext;

import org.dinky.alert.rules.CheckpointsRule;
import org.dinky.alert.rules.ExceptionRule;
import org.dinky.data.dto.JobDataDto;
import org.dinky.data.flink.checkpoint.CheckPointOverView;
import org.dinky.data.flink.exceptions.FlinkJobExceptionsDetail;
import org.dinky.data.model.ClusterInstance;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.model.job.JobInstance;
import org.dinky.data.options.JobAlertRuleOptions;
import org.dinky.job.JobConfig;
import org.dinky.utils.JsonUtils;
import org.dinky.utils.TimeUtil;

import com.fasterxml.jackson.annotation.JsonProperty;

import cn.hutool.core.text.StrFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobAlertData {

    /**
     * Time about
     */
    @JsonProperty(value = JobAlertRuleOptions.FIELD_NAME_TIME)
    private String alertTime = "";

    @JsonProperty(value = JobAlertRuleOptions.FIELD_NAME_START_TIME)
    private String jobStartTime = "";

    @JsonProperty(value = JobAlertRuleOptions.FIELD_NAME_END_TIME)
    private String jobEndTime = "";

    @JsonProperty(value = JobAlertRuleOptions.FIELD_NAME_DURATION)
    private Long duration = 0L;

    /**
     * Job About
     */
    @JsonProperty(value = JobAlertRuleOptions.FIELD_NAME_JOB_NAME)
    private String jobName = "";

    @JsonProperty(value = JobAlertRuleOptions.FIELD_NAME_JOB_ID)
    private String jobId = "";

    @JsonProperty(value = JobAlertRuleOptions.FIELD_NAME_JOB_STATUS)
    private String jobStatus = "";

    @JsonProperty(value = JobAlertRuleOptions.FIELD_TASK_ID)
    private Integer taskId = 0;

    @JsonProperty(value = JobAlertRuleOptions.FIELD_JOB_INSTANCE_ID)
    private Integer jobInstanceId = 0;

    @JsonProperty(value = JobAlertRuleOptions.FIELD_JOB_TASK_URL)
    private String taskUrl = "";

    @JsonProperty(value = JobAlertRuleOptions.FIELD_JOB_BATCH_MODEL)
    private boolean batchModel = false;

    /**
     * Cluster About
     */
    @JsonProperty(value = JobAlertRuleOptions.FIELD_NAME_CLUSTER_NAME)
    private String clusterName = "";

    @JsonProperty(value = JobAlertRuleOptions.FIELD_NAME_CLUSTER_TYPE)
    private String clusterType = "";

    @JsonProperty(value = JobAlertRuleOptions.FIELD_NAME_CLUSTER_HOSTS)
    private String clusterHosts = "";

    /**
     * Flink About
     */
    @JsonProperty(value = JobAlertRuleOptions.FIELD_NAME_EXCEPTIONS_MSG)
    private String errorMsg = "";

    @JsonProperty(value = JobAlertRuleOptions.FIELD_NAME_CHECKPOINT_COST_TIME)
    private Long checkpointCostTime = 0L;

    @JsonProperty(value = JobAlertRuleOptions.FIELD_NAME_CHECKPOINT_FAILED_COUNT)
    private Long checkpointFailedCount = 0L;

    @JsonProperty(value = JobAlertRuleOptions.FIELD_NAME_CHECKPOINT_COMPLETE_COUNT)
    private Long checkpointCompleteCount = 0L;

    @JsonProperty(value = JobAlertRuleOptions.FIELD_NAME_CHECKPOINT_FAILED)
    private boolean isCheckpointFailed = false;

    @JsonProperty(value = JobAlertRuleOptions.FIELD_NAME_IS_EXCEPTION)
    private boolean isException = false;

    private static String buildTaskUrl(JobInstance jobInstance) {
        return StrFormatter.format(
                "{}/#/devops/job-detail?id={}",
                SystemConfiguration.getInstances().getDinkyAddr(),
                jobInstance.getTaskId());
    }

    public static JobAlertData buildData(JobInfoDetail jobInfoDetail) {
        JobAlertDataBuilder builder = JobAlertData.builder();
        builder.alertTime(TimeUtil.nowStr());

        JobDataDto jobDataDto = jobInfoDetail.getJobDataDto();
        JobConfig job = JsonUtils.parseObject(jobInfoDetail.getHistory().getConfigJson(), JobConfig.class);
        ClusterInstance clusterInstance = jobInfoDetail.getClusterInstance();
        CheckPointOverView checkpoints = jobDataDto.getCheckpoints();
        FlinkJobExceptionsDetail exceptions = jobDataDto.getExceptions();

        JobInstance jobInstance = jobInfoDetail.getInstance();
        String id = jobInstance.getId().toString();

        builder.jobStatus(jobInstance.getStatus())
                .jobInstanceId(jobInstance.getId())
                .taskId(jobInstance.getTaskId())
                .taskUrl(buildTaskUrl(jobInstance))
                .jobName(jobInstance.getName())
                .jobId(jobInstance.getJid())
                .duration(jobInstance.getDuration())
                .jobStartTime(TimeUtil.convertTimeToString(jobInstance.getCreateTime()))
                .jobEndTime(TimeUtil.convertTimeToString(jobInstance.getFinishTime()));
        if (job != null) {
            builder.batchModel(job.isBatchModel());
        }
        if (clusterInstance != null) {
            builder.clusterName(clusterInstance.getName())
                    .clusterType(clusterInstance.getType())
                    .clusterHosts(clusterInstance.getHosts());
        }

        if (jobDataDto.isError()) {
            builder.errorMsg(jobDataDto.getErrorMsg());
        } else if (exceptions != null && ExceptionRule.isException(id, exceptions)) {
            // The error message is too long to send an alarm,
            // and only the first line of abnormal information is used
            builder.isException(true).errorMsg(exceptions.getRootException().split("\n")[0]);
        }

        if (checkpoints != null) {
            builder.checkpointCostTime(CheckpointsRule.checkpointTime(id, checkpoints))
                    .checkpointFailedCount(checkpoints.getCounts().getNumberFailedCheckpoints())
                    .checkpointCompleteCount(checkpoints.getCounts().getNumberCompletedCheckpoints())
                    .isCheckpointFailed(CheckpointsRule.checkFailed(id, checkpoints));
        }
        return builder.build();
    }
}
