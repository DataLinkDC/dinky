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

package org.dinky.data.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * JobHistory
 *
 * @since 2022/3/2 19:48
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_job_history")
@ApiModel(value = "JobHistory", description = "Job History Information")
public class JobHistory implements Serializable {

    private static final long serialVersionUID = 4984787372340047250L;

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

    @ApiModelProperty(
            value = "Job JSON",
            dataType = "String",
            example = "{\"jobName\": \"Example Job\"}",
            notes = "JSON representation of the job")
    private String jobJson;

    @ApiModelProperty(
            value = "Exceptions JSON",
            dataType = "String",
            example = "{\"exceptionType\": \"RuntimeException\"}",
            notes = "JSON representation of exceptions")
    private String exceptionsJson;

    @ApiModelProperty(
            value = "Checkpoints JSON",
            dataType = "String",
            example = "{\"checkpointId\": 123}",
            notes = "JSON representation of checkpoints")
    private String checkpointsJson;

    @ApiModelProperty(
            value = "Checkpoints Config JSON",
            dataType = "String",
            example = "{\"configParam\": \"value\"}",
            notes = "JSON representation of checkpoints config")
    private String checkpointsConfigJson;

    @ApiModelProperty(
            value = "Config JSON",
            dataType = "String",
            example = "{\"configParam\": \"value\"}",
            notes = "JSON representation of config")
    private String configJson;

    @ApiModelProperty(
            value = "Jar JSON",
            dataType = "String",
            example = "{\"jarName\": \"example.jar\"}",
            notes = "JSON representation of the JAR")
    private String jarJson;

    @ApiModelProperty(
            value = "Cluster JSON",
            dataType = "String",
            example = "{\"clusterName\": \"exampleCluster\"}",
            notes = "JSON representation of the cluster")
    private String clusterJson;

    @ApiModelProperty(
            value = "Cluster Configuration JSON",
            dataType = "String",
            example = "{\"configParam\": \"value\"}",
            notes = "JSON representation of cluster configuration")
    private String clusterConfigurationJson;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(
            value = "Update Time",
            dataType = "LocalDateTime",
            notes = "Timestamp indicating the last update time")
    private LocalDateTime updateTime;
}
