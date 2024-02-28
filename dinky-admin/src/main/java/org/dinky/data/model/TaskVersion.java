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

import org.dinky.data.dto.TaskVersionConfigureDTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** 作业 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "dinky_task_version", autoResultMap = true)
@ApiModel(value = "TaskVersion", description = "Task Version Information")
public class TaskVersion implements Serializable {

    @ApiModelProperty(value = "ID", dataType = "Integer", notes = "Unique identifier for the task version")
    private Integer id;

    @ApiModelProperty(value = "Tenant ID", dataType = "Integer", notes = "ID of the tenant")
    private Integer tenantId;

    @ApiModelProperty(value = "Task ID", dataType = "Integer", notes = "ID of the task associated with this version")
    @TableField(value = "task_id")
    private Integer taskId;

    @ApiModelProperty(value = "Version ID", dataType = "Integer", notes = "ID of the version")
    @TableField(value = "version_id")
    private Integer versionId;

    @ApiModelProperty(value = "Flink SQL Content", dataType = "String", notes = "Flink SQL content")
    @TableField(value = "`statement`")
    private String statement;

    @ApiModelProperty(value = "Name", dataType = "String", notes = "Name of the version")
    @TableField(value = "`name`")
    private String name;

    @ApiModelProperty(value = "Dialect", dataType = "String", notes = "SQL dialect")
    @TableField(value = "dialect")
    private String dialect;

    @ApiModelProperty(value = "Type", dataType = "String", notes = "Type of the version")
    @TableField(value = "`type`")
    private String type;

    @ApiModelProperty(
            value = "Task Configure",
            dataType = "TaskVersionConfigureDTO",
            notes = "Task version configuration")
    @TableField(value = "task_configure", typeHandler = JacksonTypeHandler.class)
    private TaskVersionConfigureDTO taskConfigure;

    @ApiModelProperty(value = "Create Time", dataType = "Date", notes = "Timestamp when the version was created")
    @TableField(value = "create_time")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "Creator", required = true, dataType = "Integer", example = "Creator")
    private Integer creator;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskVersion that = (TaskVersion) o;
        return Objects.equals(taskId, that.taskId)
                && Objects.equals(versionId, that.versionId)
                && Objects.equals(statement, that.statement)
                && Objects.equals(dialect, that.dialect)
                && Objects.equals(type, that.type)
                && Objects.equals(taskConfigure, that.taskConfigure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, versionId, statement, dialect, type, taskConfigure);
    }
}
