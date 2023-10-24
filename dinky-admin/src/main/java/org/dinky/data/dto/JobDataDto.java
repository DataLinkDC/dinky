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

package org.dinky.data.dto;

import org.dinky.data.flink.checkpoint.CheckPointOverView;
import org.dinky.data.flink.config.CheckpointConfigInfo;
import org.dinky.data.flink.config.FlinkJobConfigInfo;
import org.dinky.data.flink.exceptions.FlinkJobExceptionsDetail;
import org.dinky.data.flink.job.FlinkJobDetailInfo;
import org.dinky.data.model.JobHistory;
import org.dinky.data.model.mapping.ClusterConfigurationMapping;
import org.dinky.data.model.mapping.ClusterInstanceMapping;
import org.dinky.utils.JsonUtils;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;

import cn.hutool.core.lang.Opt;
import cn.hutool.json.JSONUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDataDto {

    @ApiModelProperty(
            value = "ID",
            dataType = "Integer",
            example = "1",
            notes = "Unique identifier for the job history")
    private Integer id;

    @ApiModelProperty(
            value = "Tenant ID",
            dataType = "Integer",
            example = "1",
            notes = "Tenant ID associated with the job history")
    private Integer tenantId;

    @ApiModelProperty(value = "FlinkJobDetailInfo", notes = "FlinkJobDetailInfo representing job details")
    private FlinkJobDetailInfo job;

    @ApiModelProperty(value = "Exceptions Detail Object", notes = "Object representing job exceptions details")
    private FlinkJobExceptionsDetail exceptions;

    @ApiModelProperty(value = "Checkpoints Object", notes = "Object representing job checkpoints")
    private CheckPointOverView checkpoints;

    @ApiModelProperty(value = "Checkpoints Config Object", notes = "Object representing checkpoints configuration")
    private CheckpointConfigInfo checkpointsConfig;

    @ApiModelProperty(value = "JobConfigInfo", notes = "JobConfigInfo representing job configuration")
    private FlinkJobConfigInfo config;

    @ApiModelProperty(value = "Jar Object", notes = "Object representing the JAR used in the job")
    private JsonNode jar;

    @ApiModelProperty(value = "ClusterInstance Object", notes = "Object representing the cluster")
    private ClusterInstanceMapping cluster;

    @ApiModelProperty(value = "Cluster Configuration Object", notes = "Object representing cluster configuration")
    private ClusterConfigurationMapping clusterConfiguration;

    @ApiModelProperty(
            value = "Error Flag",
            dataType = "boolean",
            example = "true",
            notes = "Flag indicating if there was an error")
    private boolean error;

    @ApiModelProperty(
            value = "Error Message",
            dataType = "boolean",
            example = "true",
            notes = "Flag indicating if there was an error")
    private String errorMsg;

    public JobHistory toJobHistory() {
        return JobHistory.builder()
                .id(this.id)
                .tenantId(this.tenantId)
                .jobJson(this.job)
                .exceptionsJson(this.exceptions)
                .checkpointsJson(this.checkpoints)
                .checkpointsConfigJson(this.checkpointsConfig)
                .configJson(this.config)
                .jarJson(JSONUtil.toJsonStr(getJar()))
                .clusterJson(this.cluster)
                .clusterConfigurationJson(this.clusterConfiguration)
                .updateTime(LocalDateTime.now())
                .build();
    }

    public static JobDataDto fromJobHistory(JobHistory jobHistory) {
        return Opt.ofNullable(jobHistory)
                .map(x -> JobDataDto.builder()
                        .id(jobHistory.getId())
                        .tenantId(jobHistory.getTenantId())
                        .job(jobHistory.getJobJson())
                        .exceptions(jobHistory.getExceptionsJson())
                        .checkpoints(jobHistory.getCheckpointsJson())
                        .checkpointsConfig(jobHistory.getCheckpointsConfigJson())
                        .config(jobHistory.getConfigJson())
                        .jar(JsonUtils.parseToJsonNode(jobHistory.getJarJson()))
                        .cluster(jobHistory.getClusterJson())
                        .clusterConfiguration(jobHistory.getClusterConfigurationJson())
                        .build())
                .orElse(null);
    }
}
