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

import org.dinky.data.annotations.ProcessId;
import org.dinky.data.model.Task;
import org.dinky.data.model.alert.AlertGroup;
import org.dinky.data.model.ext.TaskExtConfig;
import org.dinky.job.JobConfig;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * StudioExecuteDTO
 *
 */
@Getter
@Setter
@Slf4j
@ApiModel(value = "StudioExecuteDTO", description = "DTO for executing SQL queries")
public class TaskDTO extends AbstractStatementDTO {

    @ApiModelProperty(value = "ID", dataType = "Integer", example = "6", notes = "The identifier of the execution")
    @ProcessId
    private Integer id;

    @ApiModelProperty(value = "Name", required = true, dataType = "String", example = "Name")
    private String name;

    @ApiModelProperty(value = "Dialect", dataType = "String", notes = "Dialect for the task")
    private String dialect;

    @ApiModelProperty(
            value = "Run Mode",
            dataType = "String",
            example = "Local",
            notes = "The execution mode for the SQL query")
    private String type;

    @ApiModelProperty(
            value = "Save Point Strategy",
            dataType = "Integer",
            example = "1",
            notes = "The save point strategy")
    private Integer savePointStrategy;

    @ApiModelProperty(
            value = "Save Point Path",
            dataType = "String",
            example = "/savepoints",
            notes = "The path for save points")
    private String savePointPath;

    @ApiModelProperty(value = "Parallelism", dataType = "Integer", example = "4", notes = "The parallelism level")
    private Integer parallelism;

    @ApiModelProperty(
            value = "Fragment",
            dataType = "Boolean",
            example = "true",
            notes = "Fragment option for the task")
    private Boolean fragment;

    @ApiModelProperty(
            value = "Use Statement Set",
            dataType = "boolean",
            example = "false",
            notes = "Flag indicating whether to use a statement set")
    private boolean statementSet = true;

    @ApiModelProperty(
            value = "Batch Model",
            dataType = "boolean",
            example = "true",
            notes = "Flag indicating whether to use batch processing")
    private boolean batchModel;

    @ApiModelProperty(
            value = "ClusterInstance ID",
            dataType = "Integer",
            example = "1",
            notes = "The identifier of the cluster")
    private Integer clusterId;

    @ApiModelProperty(
            value = "Cluster Configuration ID",
            dataType = "Integer",
            example = "2",
            notes = "The identifier of the cluster configuration")
    private Integer clusterConfigurationId;

    @ApiModelProperty(
            value = "Database ID",
            dataType = "Integer",
            example = "3",
            notes = "The identifier of the database")
    private Integer databaseId;

    @ApiModelProperty(
            value = "Alert Group ID",
            dataType = "Integer",
            example = "7001",
            notes = "ID of the alert group associated with the task")
    private Integer alertGroupId;

    @ApiModelProperty(value = "Alert Group", dataType = "AlertGroup", notes = "Alert group associated with the task")
    private AlertGroup alertGroup;

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
            value = "Job status",
            dataType = "String",
            example = "RUNNING",
            notes = "THE_RUNNING_STATUS_OF_THE_CURRENT_TASK")
    private String status;

    @ApiModelProperty(
            value = "Version ID",
            dataType = "Integer",
            example = "9001",
            notes = "ID of the version associated with the task")
    private Integer versionId;

    @ApiModelProperty(value = "Enabled", required = true, dataType = "Boolean", example = "true")
    private Boolean enabled;

    @ApiModelProperty(value = "Statement", dataType = "String", notes = "SQL statement for the task")
    private String statement;

    @ApiModelProperty(value = "ClusterInstance Name", dataType = "String", notes = "Name of the associated cluster")
    private String clusterName;

    @ApiModelProperty(
            value = "Configuration JSON",
            dataType = "TaskExtConfig",
            notes = "Extended configuration in JSON format for the task")
    private TaskExtConfig configJson;

    @ApiModelProperty(value = "Path", dataType = "String", notes = "Path associated with the task")
    private String path;

    @ApiModelProperty(
            value = "Cluster Configuration Name",
            dataType = "String",
            notes = "Name of the associated cluster configuration")
    private String clusterConfigurationName;

    @ApiModelProperty(value = "Database Name", dataType = "String", notes = "Name of the associated database")
    private String databaseName;

    @ApiModelProperty(value = "Environment Name", dataType = "String", notes = "Name of the associated environment")
    private String envName;

    @ApiModelProperty(value = "Alert Group Name", dataType = "String", notes = "Name of the associated alert group")
    private String alertGroupName;

    @ApiModelProperty(
            value = "UseResult",
            dataType = "boolean",
            example = "true",
            notes = "Flagindicatingwhethertousethequeryresult")
    private boolean useResult;

    @ApiModelProperty(
            value = "UseChangeLog",
            dataType = "boolean",
            example = "false",
            notes = "Flagindicatingwhethertousechangelogs")
    private boolean useChangeLog = false;

    @ApiModelProperty(
            value = "Use Auto Cancel",
            dataType = "boolean",
            example = "false",
            notes = "Flag indicating whether to use auto-canceling")
    private boolean useAutoCancel = true;

    @ApiModelProperty(value = "Session", dataType = "String", example = "session_id", notes = "The session identifier")
    private String session;

    @ApiModelProperty(value = "Job Name", dataType = "String", example = "MyJob", notes = "The name of the job")
    private String jobName;

    @ApiModelProperty(
            value = "Max Row Number",
            dataType = "Integer",
            example = "100",
            notes = "The maximum number of rows to return")
    private Integer maxRowNum = 100;

    public JobConfig getJobConfig() {

        Map<String, String> parsedConfig =
                this.configJson == null ? new HashMap<>(0) : this.configJson.getCustomConfigMaps();

        JobConfig jobConfig = new JobConfig();
        BeanUtil.copyProperties(this, jobConfig);
        jobConfig.setConfigJson(parsedConfig);
        jobConfig.setTaskId(id);
        jobConfig.setJobName(name);

        return jobConfig;
    }

    public Task buildTask() {
        Task task = new Task();
        BeanUtil.copyProperties(this, task);
        return task;
    }
}
