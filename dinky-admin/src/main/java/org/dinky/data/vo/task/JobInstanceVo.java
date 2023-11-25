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

package org.dinky.data.vo.task;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class JobInstanceVo {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(
            value = "ID",
            dataType = "Integer",
            example = "1",
            notes = "Unique identifier for the job instance")
    private Integer id;

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

    @ApiModelProperty(value = "JID", dataType = "String", notes = "JID of the job instance")
    private String jid;

    @ApiModelProperty(value = "Status", dataType = "String", notes = "Status of the job instance")
    private String status;

    @ApiModelProperty(value = "type", dataType = "String", notes = "run mode type", example = "kubernets-application")
    private String type;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(
            value = "Create Time",
            dataType = "String",
            notes = "Timestamp indicating the creation time of the job instance")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(
            value = "Update Time",
            dataType = "String",
            notes = "Timestamp indicating the last update time of the job instance")
    private LocalDateTime updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(
            value = "Finish Time",
            dataType = "String",
            notes = "Timestamp indicating the finish time of the job instance")
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
}
