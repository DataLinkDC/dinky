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

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_approval")
@ApiModel(value = "Approval", description = "Approval instance")
public class Approval extends Model<Approval> {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "ID", dataType = "Integer", notes = "Unique identifier for the approval")
    private Integer id;

    @ApiModelProperty(value = "Task Id", dataType = "Integer", notes = "Task identifier for the task approval linked")
    private Integer taskId;

    @ApiModelProperty(value = "Tenant Id", dataType = "Integer", notes = "Tenant id of current approval")
    private Integer tenantId;

    @ApiModelProperty(
            value = "Previous Task Version",
            dataType = "Integer",
            notes = "Previous online version before this task is submitted")
    private Integer previousTaskVersion;

    @ApiModelProperty(value = "Current Task Version", dataType = "Integer", notes = "Task version required for publish")
    private Integer currentTaskVersion;

    @ApiModelProperty(value = "Approval status", dataType = "String", notes = "Approval status")
    private String status;

    @ApiModelProperty(value = "Submitter", dataType = "Integer", notes = "Submitter user id")
    private Integer submitter;

    @ApiModelProperty(value = "Submitter Comment", dataType = "String", notes = "Submitter comment")
    private String submitterComment;

    @ApiModelProperty(value = "Reviewer", dataType = "Integer", notes = "Reviewer user id")
    private Integer reviewer;

    @ApiModelProperty(value = "Reviewer Comment", dataType = "String", notes = "Reviewer comment")
    private String reviewerComment;

    @TableField(fill = FieldFill.INSERT)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Create Time", dataType = "Date", notes = "Timestamp when the approval was created")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Update Time", dataType = "Date", notes = "Timestamp when the approval was updated")
    private LocalDateTime updateTime;
}
