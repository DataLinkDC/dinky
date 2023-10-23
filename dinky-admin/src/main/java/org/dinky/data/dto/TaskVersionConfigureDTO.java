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

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/** @description: 版本信息配置 */
@Data
@ApiModel(value = "TaskVersionConfigureDTO", description = "DTO for task version configuration")
public class TaskVersionConfigureDTO implements Serializable {

    @ApiModelProperty(value = "CheckPoint", dataType = "Integer", example = "1", notes = "The CheckPoint configuration")
    private Integer checkPoint;

    @ApiModelProperty(
            value = "SavePoint strategy",
            dataType = "Integer",
            example = "2",
            notes = "The SavePoint strategy configuration")
    private Integer savePointStrategy;

    @ApiModelProperty(
            value = "SavePoint path",
            dataType = "String",
            example = "/path/to/savepoint",
            notes = "The SavePoint path configuration")
    private String savePointPath;

    @ApiModelProperty(
            value = "Parallelism",
            dataType = "Integer",
            example = "4",
            notes = "The parallelism configuration")
    private Integer parallelism;

    @ApiModelProperty(
            value = "Fragment",
            dataType = "Boolean",
            example = "false",
            notes = "Whether to enable fragment mode")
    private Boolean fragment;

    @ApiModelProperty(
            value = "Statement Set",
            dataType = "Boolean",
            example = "true",
            notes = "Whether to enable statement set")
    private Boolean statementSet;

    @ApiModelProperty(
            value = "Batch Model",
            dataType = "Boolean",
            example = "false",
            notes = "Whether to use batch mode")
    private Boolean batchModel;

    @ApiModelProperty(
            value = "Flink ClusterInstance ID",
            dataType = "Integer",
            example = "3",
            notes = "The ID of the Flink cluster")
    private Integer clusterId;

    @ApiModelProperty(
            value = "Cluster Configuration ID",
            dataType = "Integer",
            example = "4",
            notes = "The ID of the cluster configuration")
    private Integer clusterConfigurationId;

    @ApiModelProperty(
            value = "Database ID",
            dataType = "Integer",
            example = "5",
            notes = "The ID of the database source")
    private Integer databaseId;

    @ApiModelProperty(value = "Jar ID", dataType = "Integer", example = "6", notes = "The ID of the JAR file")
    private Integer jarId;

    @ApiModelProperty(
            value = "Environment ID",
            dataType = "Integer",
            example = "7",
            notes = "The ID of the environment")
    private Integer envId;

    @ApiModelProperty(
            value = "Alert Group ID",
            dataType = "Integer",
            example = "8",
            notes = "The ID of the alert group")
    private Integer alertGroupId;

    @ApiModelProperty(
            value = "Configuration JSON",
            dataType = "String",
            example = "{\"key\":\"value\"}",
            notes = "The configuration JSON")
    private String configJson;

    @ApiModelProperty(
            value = "Note",
            dataType = "String",
            example = "Task configuration notes",
            notes = "Additional notes")
    private String note;

    @ApiModelProperty(value = "Step", dataType = "Integer", example = "1", notes = "The task lifecycle step")
    private Integer step;

    @ApiModelProperty(
            value = "Job Instance ID",
            dataType = "Integer",
            example = "9",
            notes = "The ID of the job instance")
    private Integer jobInstanceId;

    @ApiModelProperty(
            value = "Enabled",
            dataType = "Boolean",
            example = "true",
            notes = "Whether the configuration is enabled")
    private Boolean enabled;
}
