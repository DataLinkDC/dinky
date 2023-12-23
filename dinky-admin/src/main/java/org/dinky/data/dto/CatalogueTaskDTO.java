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

import org.dinky.data.model.ext.TaskExtConfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * CatalogueTaskDTO
 *
 * @since 2021/6/1 20:16
 */
@Getter
@Setter
@ApiModel(value = "CatalogueTaskDTO", description = "DTO for cataloging tasks")
public class CatalogueTaskDTO {

    @ApiModelProperty(value = "Task ID", dataType = "Integer", example = "1", notes = "The ID of the task")
    private Integer id;

    @ApiModelProperty(value = "Tenant ID", dataType = "Integer", example = "1", notes = "The ID of the tenant")
    private Integer tenantId;

    @ApiModelProperty(value = "Parent ID", dataType = "Integer", example = "2", notes = "The ID of the parent task")
    private Integer parentId;

    @ApiModelProperty(value = "Task ID", dataType = "Integer", example = "3", notes = "The ID of the associated task")
    private Integer taskId;

    @ApiModelProperty(
            value = "Is Leaf",
            dataType = "boolean",
            example = "true",
            notes = "Specifies whether the task is a leaf node")
    private boolean isLeaf;

    @ApiModelProperty(value = "Name", dataType = "String", example = "Task 1", notes = "The name of the task")
    private String name;

    @ApiModelProperty(value = "Type", dataType = "String", example = "SQL", notes = "The type of the task")
    private String type;

    @ApiModelProperty(
            value = "Dialect",
            dataType = "String",
            example = "MySQL",
            notes = "The SQL dialect used by the task")
    private String dialect;

    @ApiModelProperty(
            value = "Note",
            dataType = "String",
            example = "This is a sample task",
            notes = "Additional notes about the task")
    private String note;

    @ApiModelProperty(
            value = "Task Configuration JSON",
            dataType = "TaskExtConfig",
            notes = "The task's extended configuration in JSON format")
    private TaskExtConfig configJson;

    @ApiModelProperty(value = "Task", dataType = "TaskDTO", notes = "The task information")
    private TaskDTO task;
}
