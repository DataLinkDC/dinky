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

import org.dinky.data.typehandler.JSONObjectHandler;
import org.dinky.job.JobConfig;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * History
 *
 * @since 2021/6/26 22:48
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_history")
@ApiModel(value = "History", description = "History Information")
public class History implements Serializable {

    private static final long serialVersionUID = 4058280957630503072L;

    @ApiModelProperty(value = "ID", dataType = "Integer", example = "1")
    private Integer id;

    @ApiModelProperty(value = "Tenant ID", dataType = "Integer", example = "1", required = true)
    private Integer tenantId;

    @ApiModelProperty(value = "ClusterInstance ID", dataType = "Integer")
    private Integer clusterId;

    @ApiModelProperty(value = "Cluster Configuration ID", dataType = "Integer")
    private Integer clusterConfigurationId;

    @ApiModelProperty(value = "Session", dataType = "String")
    private String session;

    @ApiModelProperty(value = "Job ID", dataType = "String")
    private String jobId;

    @ApiModelProperty(value = "Job Name", dataType = "String")
    private String jobName;

    @ApiModelProperty(value = "Job Manager Address", dataType = "String")
    private String jobManagerAddress;

    @ApiModelProperty(value = "batchModel", dataType = "Boolean")
    private Boolean batchModel;

    @ApiModelProperty(value = "Status", dataType = "Integer")
    private Integer status;

    @ApiModelProperty(value = "Statement", dataType = "String")
    private String statement;

    @ApiModelProperty(value = "Type", dataType = "String")
    private String type;

    @ApiModelProperty(value = "Error", dataType = "String")
    private String error;

    @ApiModelProperty(value = "Result", dataType = "String")
    private String result;

    @ApiModelProperty(value = "JSON Configuration", dataType = "String")
    @TableField(typeHandler = JSONObjectHandler.class)
    private JobConfig configJson;

    @ApiModelProperty(value = "Start Time", dataType = "LocalDateTime")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "End Time", dataType = "LocalDateTime")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "Task ID", dataType = "Integer")
    private Integer taskId;

    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String statusText;

    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String clusterName;

    @TableField(
            value = "count(*)",
            select = false,
            insertStrategy = FieldStrategy.NEVER,
            updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "Group by count", dataType = "Integer")
    private Long count;

    @ApiModelProperty(hidden = true)
    public JobInstance buildJobInstance() {
        JobInstance jobInstance = new JobInstance();
        jobInstance.setHistoryId(id);
        jobInstance.setClusterId(clusterId);
        jobInstance.setTaskId(taskId);
        jobInstance.setName(jobName);
        return jobInstance;
    }
}
