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

import org.dinky.data.model.Task;
import org.dinky.data.model.ext.TaskExtConfig;
import org.dinky.data.typehandler.JSONObjectHandler;
import org.dinky.mybatis.annotation.Save;

import org.apache.ibatis.type.JdbcType;

import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TaskSaveDTO {

    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "ID", required = true, dataType = "Integer", example = "1", notes = "Primary Key")
    private Integer id;

    @NotNull(
            message = "Name cannot be null",
            groups = {Save.class})
    @ApiModelProperty(value = "Name", required = true, dataType = "String", example = "Name")
    private String name;

    @NotNull(
            message = "Enabled cannot be null",
            groups = {Save.class})
    @ApiModelProperty(value = "Enabled", required = true, dataType = "Boolean", example = "true")
    private Boolean enabled;

    @ApiModelProperty(value = "Dialect", dataType = "String", notes = "Dialect for the task")
    private String dialect;

    @ApiModelProperty(value = "Type", dataType = "String", notes = "Type of the task")
    private String type;

    @ApiModelProperty(value = "Check Point", dataType = "Integer", example = "1", notes = "Check point for the task")
    private Integer checkPoint;

    @ApiModelProperty(value = "Save point strategy", dataType = "SavePointStrategy", notes = "Save point strategy")
    private Integer savePointStrategy;

    @ApiModelProperty(value = "Save Point Path", dataType = "String", notes = "Save point path for the task")
    private String savePointPath;

    @ApiModelProperty(value = "Parallelism", dataType = "Integer", example = "4", notes = "Parallelism for the task")
    private Integer parallelism;

    @ApiModelProperty(
            value = "Fragment",
            dataType = "Boolean",
            example = "true",
            notes = "Fragment option for the task")
    private Boolean fragment;

    @ApiModelProperty(
            value = "Statement Set",
            dataType = "Boolean",
            example = "false",
            notes = "Statement set option for the task")
    private Boolean statementSet;

    @ApiModelProperty(
            value = "Batch Model",
            dataType = "Boolean",
            example = "true",
            notes = "Batch model option for the task")
    private Boolean batchModel;

    @ApiModelProperty(
            value = "ClusterInstance ID",
            dataType = "Integer",
            example = "2001",
            notes = "ID of the cluster associated with the task")
    private Integer clusterId;

    @ApiModelProperty(
            value = "Cluster Configuration ID",
            dataType = "Integer",
            example = "3001",
            notes = "ID of the cluster configuration associated with the task")
    private Integer clusterConfigurationId;

    @ApiModelProperty(
            value = "Database ID",
            dataType = "Integer",
            example = "4001",
            notes = "ID of the database associated with the task")
    private Integer databaseId;

    @ApiModelProperty(
            value = "Environment ID",
            dataType = "Integer",
            example = "6001",
            notes = "ID of the environment associated with the task")
    private Integer envId;

    @ApiModelProperty(
            value = "Alert Group ID",
            dataType = "Integer",
            example = "7001",
            notes = "ID of the alert group associated with the task")
    private Integer alertGroupId;

    @ApiModelProperty(
            value = "Configuration JSON",
            dataType = "TaskExtConfig",
            notes = "Extended configuration in JSON format for the task")
    @TableField(typeHandler = JSONObjectHandler.class, jdbcType = JdbcType.VARCHAR)
    private TaskExtConfig configJson;

    @ApiModelProperty(value = "Note", dataType = "String", notes = "Additional notes for the task")
    private String note;

    @ApiModelProperty(value = "Statement", dataType = "String", notes = "SQL statement for the task")
    private String statement;

    @ApiModelProperty(value = "Step", dataType = "Integer", example = "1", notes = "Step for the task")
    private Integer step;

    public Task toTaskEntity() {
        Task task = new Task();
        BeanUtil.copyProperties(this, task);
        return task;
    }
}
