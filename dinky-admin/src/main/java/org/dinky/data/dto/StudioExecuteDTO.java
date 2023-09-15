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

import org.dinky.data.model.TaskExtConfig;
import org.dinky.job.JobConfig;

import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * StudioExecuteDTO
 *
 * @since 2021/5/30 11:09
 */
@Getter
@Setter
@Slf4j
@ApiModel(value = "StudioExecuteDTO", description = "DTO for executing SQL queries")
public class StudioExecuteDTO extends AbstractStatementDTO {

    @ApiModelProperty(
            value = "Run Mode",
            dataType = "String",
            example = "BATCH",
            notes = "The execution mode for the SQL query")
    private String type;

    @ApiModelProperty(
            value = "Dialect",
            dataType = "String",
            example = "MySQL",
            notes = "The SQL dialect for the query")
    private String dialect;

    @ApiModelProperty(
            value = "Use Result",
            dataType = "boolean",
            example = "true",
            notes = "Flag indicating whether to use the query result")
    private boolean useResult;

    @ApiModelProperty(
            value = "Use Change Log",
            dataType = "boolean",
            example = "false",
            notes = "Flag indicating whether to use change logs")
    private boolean useChangeLog;

    @ApiModelProperty(
            value = "Use Auto Cancel",
            dataType = "boolean",
            example = "false",
            notes = "Flag indicating whether to use auto-canceling")
    private boolean useAutoCancel;

    @ApiModelProperty(
            value = "Use Statement Set",
            dataType = "boolean",
            example = "false",
            notes = "Flag indicating whether to use a statement set")
    private boolean statementSet;

    @ApiModelProperty(
            value = "Batch Model",
            dataType = "boolean",
            example = "true",
            notes = "Flag indicating whether to use batch processing")
    private boolean batchModel;

    @ApiModelProperty(
            value = "Use Session",
            dataType = "boolean",
            example = "false",
            notes = "Flag indicating whether to use a session")
    private boolean useSession;

    @ApiModelProperty(value = "Session", dataType = "String", example = "session_id", notes = "The session identifier")
    private String session;

    @ApiModelProperty(
            value = "Cluster ID",
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

    @ApiModelProperty(value = "JAR ID", dataType = "Integer", example = "4", notes = "The identifier of the JAR")
    private Integer jarId;

    @ApiModelProperty(value = "Job Name", dataType = "String", example = "MyJob", notes = "The name of the job")
    private String jobName;

    @ApiModelProperty(value = "Task ID", dataType = "Integer", example = "5", notes = "The identifier of the task")
    private Integer taskId;

    @ApiModelProperty(value = "ID", dataType = "Integer", example = "6", notes = "The identifier of the execution")
    private Integer id;

    @ApiModelProperty(
            value = "Max Row Number",
            dataType = "Integer",
            example = "100",
            notes = "The maximum number of rows to return")
    private Integer maxRowNum;

    @ApiModelProperty(value = "Check Point", dataType = "Integer", example = "0", notes = "The check point value")
    private Integer checkPoint;

    @ApiModelProperty(value = "Parallelism", dataType = "Integer", example = "4", notes = "The parallelism level")
    private Integer parallelism;

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

    @ApiModelProperty(
            value = "Configuration JSON",
            dataType = "Object",
            example = "{}",
            notes = "The JSON configuration for the query")
    private TaskExtConfig configJson;

    public JobConfig getJobConfig() {

        Map<String, String> parsedConfig =
                this.configJson == null ? new HashMap<>(0) : this.configJson.getCustomConfigMaps();

        return new JobConfig(
                type,
                useResult,
                useChangeLog,
                useAutoCancel,
                useSession,
                session,
                clusterId,
                clusterConfigurationId,
                jarId,
                taskId,
                jobName,
                isFragment(),
                statementSet,
                batchModel,
                maxRowNum,
                checkPoint,
                parallelism,
                savePointStrategy,
                savePointPath,
                getVariables(),
                parsedConfig);
    }

    public Integer getTaskId() {
        return taskId == null ? getId() : taskId;
    }
}
