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

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * JobInstance
 *
 * @since 2022/2/1 16:46
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_job_instance")
@ApiModel(value = "JobInstance", description = "Job Instance Information")
public class JobInstance implements Serializable {

    private static final long serialVersionUID = -3410230507904303730L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(
            value = "ID",
            dataType = "Integer",
            example = "1",
            notes = "Unique identifier for the job instance")
    private Integer id;

    @ApiModelProperty(
            value = "Tenant ID",
            dataType = "Integer",
            example = "1",
            notes = "Tenant ID associated with the job instance")
    private Integer tenantId;

    @ApiModelProperty(value = "Name", dataType = "String", notes = "Name of the job instance")
    private String name;

    @ApiModelProperty(
            value = "Task ID",
            dataType = "Integer",
            example = "1",
            notes = "Task ID associated with the job instance")
    private Integer taskId;

    @ApiModelProperty(value = "Step", dataType = "Integer", example = "1", notes = "Step number of the job instance")
    private Integer step;

    @ApiModelProperty(
            value = "ClusterInstance ID",
            dataType = "Integer",
            example = "1",
            notes = "ClusterInstance ID associated with the job instance")
    private Integer clusterId;

    @ApiModelProperty(value = "JID", dataType = "String", notes = "JID of the job instance")
    private String jid;

    @ApiModelProperty(value = "Status", dataType = "String", notes = "Status of the job instance")
    private String status;

    @ApiModelProperty(
            value = "History ID",
            dataType = "Integer",
            example = "1",
            notes = "History ID associated with the job instance")
    private Integer historyId;

    @ApiModelProperty(value = "Error", dataType = "String", notes = "Error message associated with the job instance")
    private String error;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @ApiModelProperty(
            value = "Create Time",
            dataType = "String",
            notes = "Timestamp indicating the creation time of the job instance")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "Update Time", example = "2022-02-24 20:12:00", dataType = "LocalDateTime")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(
            value = "Finish Time",
            dataType = "String",
            notes = "Timestamp indicating the finish time of the job instance")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime finishTime;

    @ApiModelProperty(
            value = "Duration",
            dataType = "Long",
            example = "3600",
            notes = "Duration of the job instance in seconds")
    private Long duration;

    @ApiModelProperty(
            value = "Failed Restart Count",
            dataType = "Integer",
            example = "2",
            notes = "Count of failed restarts")
    private Integer failedRestartCount;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "Creator", required = true, dataType = "Integer", example = "Creator")
    private Integer creator;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "updater", required = true, dataType = "Integer", example = "updater")
    private Integer updater;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "Operator", required = true, dataType = "Integer", example = "Operator")
    private Integer operator;

    @TableField(
            value = "count(*)",
            select = false,
            insertStrategy = FieldStrategy.NEVER,
            updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "Group by count", dataType = "Integer")
    private Long count;
}
