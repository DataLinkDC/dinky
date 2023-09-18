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

import org.dinky.data.model.JobHistory;
import org.dinky.utils.JSONUtil;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.JsonNode;

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

    @TableField(exist = false)
    @ApiModelProperty(value = "Job Object", notes = "Object representing job details")
    private JsonNode job;

    @TableField(exist = false)
    @ApiModelProperty(value = "Exceptions Object", notes = "Object representing job exceptions")
    private JsonNode exceptions;

    @TableField(exist = false)
    @ApiModelProperty(value = "Checkpoints Object", notes = "Object representing job checkpoints")
    private JsonNode checkpoints;

    @TableField(exist = false)
    @ApiModelProperty(value = "Checkpoints Config Object", notes = "Object representing checkpoints configuration")
    private JsonNode checkpointsConfig;

    @TableField(exist = false)
    @ApiModelProperty(value = "Config Object", notes = "Object representing job configuration")
    private JsonNode config;

    @TableField(exist = false)
    @ApiModelProperty(value = "Jar Object", notes = "Object representing the JAR used in the job")
    private JsonNode jar;

    @TableField(exist = false)
    @ApiModelProperty(value = "Cluster Object", notes = "Object representing the cluster")
    private JsonNode cluster;

    @TableField(exist = false)
    @ApiModelProperty(value = "Cluster Configuration Object", notes = "Object representing cluster configuration")
    private JsonNode clusterConfiguration;

    @TableField(exist = false)
    @ApiModelProperty(
            value = "Error Flag",
            dataType = "boolean",
            example = "true",
            notes = "Flag indicating if there was an error")
    private boolean error;

    @TableField(exist = false)
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
                .jobJson(JSONUtil.toJsonString(getJob()))
                .exceptionsJson(JSONUtil.toJsonString(getExceptions()))
                .checkpointsJson(JSONUtil.toJsonString(getCheckpoints()))
                .checkpointsConfigJson(JSONUtil.toJsonString(getCheckpointsConfig()))
                .configJson(JSONUtil.toJsonString(getConfig()))
                .jarJson(JSONUtil.toJsonString(getJar()))
                .clusterJson(JSONUtil.toJsonString(getCluster()))
                .clusterConfigurationJson(JSONUtil.toJsonString(getClusterConfiguration()))
                .updateTime(LocalDateTime.now())
                .build();
    }

    public static JobDataDto fromJobHistory(JobHistory jobHistory) {
        return JobDataDto.builder()
                .id(jobHistory.getId())
                .tenantId(jobHistory.getTenantId())
                .job(JSONUtil.toJsonNode(jobHistory.getJobJson()))
                .exceptions(JSONUtil.toJsonNode(jobHistory.getExceptionsJson()))
                .checkpoints(JSONUtil.toJsonNode(jobHistory.getCheckpointsJson()))
                .checkpointsConfig(JSONUtil.toJsonNode(jobHistory.getCheckpointsConfigJson()))
                .config(JSONUtil.toJsonNode(jobHistory.getConfigJson()))
                .jar(JSONUtil.toJsonNode(jobHistory.getJarJson()))
                .cluster(JSONUtil.toJsonNode(jobHistory.getClusterJson()))
                .clusterConfiguration(JSONUtil.toJsonNode(jobHistory.getClusterConfigurationJson()))
                .build();
    }
}
