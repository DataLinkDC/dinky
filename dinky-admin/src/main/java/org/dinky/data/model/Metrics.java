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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** @TableName dinky_metrics */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "dinky_metrics")
@Data
@ApiModel(value = "Metrics", description = "Metrics Information")
public class Metrics extends Model<Metrics> {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "ID", dataType = "Integer", example = "1", notes = "Unique identifier for the metrics")
    private Integer id;

    @ApiModelProperty(value = "Task ID", dataType = "Integer", example = "1001", notes = "ID of the associated task")
    private Integer taskId;

    @ApiModelProperty(value = "Vertices", dataType = "String", notes = "Vertices information")
    private String vertices;

    @ApiModelProperty(value = "Metrics Data", dataType = "String", notes = "Metrics data")
    private String metrics;

    @ApiModelProperty(value = "Position", dataType = "Integer", example = "1", notes = "Position of the metrics")
    private Integer position;

    @ApiModelProperty(value = "Show Type", dataType = "String", notes = "Type of display for the metrics")
    private String showType;

    @ApiModelProperty(value = "Show Size", dataType = "String", notes = "Size of display for the metrics")
    private String showSize;

    @ApiModelProperty(value = "Title", dataType = "String", notes = "Title for the metrics")
    private String title;

    @ApiModelProperty(value = "Layout Name", dataType = "String", notes = "Name of the layout")
    private String layoutName;

    @TableField(fill = FieldFill.INSERT)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @ApiModelProperty(
            value = "Create Time",
            dataType = "String",
            notes = "Timestamp indicating the creation time of the metrics")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @ApiModelProperty(
            value = "Update Time",
            dataType = "String",
            notes = "Timestamp indicating the last update time of the metrics")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Job ID", dataType = "String", notes = "ID of the associated job")
    @TableField(exist = false)
    private String jobId;
}
