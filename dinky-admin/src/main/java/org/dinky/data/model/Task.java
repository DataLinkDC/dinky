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

import org.dinky.data.model.ext.TaskExtConfig;
import org.dinky.data.typehandler.JSONObjectHandler;
import org.dinky.data.typehandler.ListTypeHandler;
import org.dinky.mybatis.model.SuperEntity;

import org.apache.ibatis.type.JdbcType;

import java.util.List;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 任务
 *
 * @since 2021-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_task")
@NoArgsConstructor
@ApiModel(value = "Task", description = "Task Information")
public class Task extends SuperEntity<Task> {

    private static final long serialVersionUID = 5988972129893667154L;

    @ApiModelProperty(value = "Dialect", dataType = "String", notes = "Dialect for the task")
    private String dialect;

    @ApiModelProperty(
            value = "Tenant ID",
            dataType = "Integer",
            example = "1001",
            notes = "ID of the tenant associated with the task")
    private Integer tenantId;

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

    @ApiModelProperty(value = "Step", dataType = "Integer", example = "1", notes = "Step for the task")
    private Integer step;

    @ApiModelProperty(
            value = "Job Instance ID",
            dataType = "Integer",
            example = "8001",
            notes = "ID of the job instance associated with the task")
    private Integer jobInstanceId;

    @ApiModelProperty(
            value = "Version ID",
            dataType = "Integer",
            example = "9001",
            notes = "ID of the version associated with the task")
    private Integer versionId;

    @ApiModelProperty(value = "Enabled", dataType = "Boolean", example = "true", notes = "Whether the task is enabled")
    private Boolean enabled;

    @ApiModelProperty(value = "Statement", dataType = "String", notes = "SQL statement for the task")
    private String statement;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(
            value = "Operator",
            dataType = "Integer",
            example = "1001",
            notes = "ID of the user who created the task")
    private Integer operator;

    @ApiModelProperty(
            value = "First Level Owner",
            dataType = "Integer",
            example = "1001",
            notes = "primary responsible person id")
    private Integer firstLevelOwner;

    @ApiModelProperty(
            value = "Second Level Owners",
            dataType = "List",
            notes = "list of secondary responsible persons' ids")
    @TableField(typeHandler = ListTypeHandler.class)
    private List<Integer> secondLevelOwners;

    public Task(Integer id, Integer jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
        this.setId(id);
    }
}
