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

package org.dinky.data.model.job;

import org.dinky.data.flink.checkpoint.CheckPointOverView;
import org.dinky.data.flink.config.CheckpointConfigInfo;
import org.dinky.data.flink.config.FlinkJobConfigInfo;
import org.dinky.data.flink.exceptions.FlinkJobExceptionsDetail;
import org.dinky.data.flink.job.FlinkJobDetailInfo;
import org.dinky.data.model.mapping.ClusterConfigurationMapping;
import org.dinky.data.model.mapping.ClusterInstanceMapping;
import org.dinky.data.typehandler.JSONObjectHandler;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

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
    @TableField(typeHandler = JSONObjectHandler.class)
    private FlinkJobDetailInfo jobJson;

    @ApiModelProperty(
            value = "Exceptions JSON",
            dataType = "String",
            example = "{\"exceptionType\": \"RuntimeException\"}",
            notes = "JSON representation of exceptions")
    @TableField(typeHandler = JSONObjectHandler.class)
    private FlinkJobExceptionsDetail exceptionsJson;

    @ApiModelProperty(
            value = "Checkpoints JSON",
            dataType = "String",
            example = "{\"checkpointId\": 123}",
            notes = "JSON representation of checkpoints")
    @TableField(typeHandler = JSONObjectHandler.class)
    private CheckPointOverView checkpointsJson;

    @ApiModelProperty(
            value = "Checkpoints Config JSON",
            dataType = "String",
            example = "{\"configParam\": \"value\"}",
            notes = "JSON representation of checkpoints config")
    @TableField(typeHandler = JSONObjectHandler.class)
    private CheckpointConfigInfo checkpointsConfigJson;

    @ApiModelProperty(
            value = "Config JSON",
            dataType = "String",
            example = "{\"configParam\": \"value\"}",
            notes = "JSON representation of config")
    @TableField(typeHandler = JSONObjectHandler.class)
    private FlinkJobConfigInfo configJson;

    @ApiModelProperty(
            value = "ClusterInstance JSON",
            dataType = "String",
            example = "{\"clusterName\": \"exampleCluster\"}",
            notes = "JSON representation of the cluster")
    @TableField(typeHandler = JSONObjectHandler.class)
    private ClusterInstanceMapping clusterJson;

    @ApiModelProperty(
            value = "Cluster Configuration JSON",
            dataType = "String",
            example = "{\"configParam\": \"value\"}",
            notes = "JSON representation of cluster configuration")
    @TableField(typeHandler = JSONObjectHandler.class)
    private ClusterConfigurationMapping clusterConfigurationJson;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "Update Time", example = "2022-02-24 20:12:00", dataType = "LocalDateTime")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateTime;
}
