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
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/** @description: 任务版本记录 */
@Data
@ApiModel(value = "TaskVersionHistoryDTO", description = "DTO for task version history")
public class TaskVersionHistoryDTO implements Serializable {

    @ApiModelProperty(value = "ID", dataType = "Integer", example = "1", notes = "The ID of the task version history")
    private Integer id;

    @ApiModelProperty(
            value = "Task ID",
            dataType = "Integer",
            example = "2",
            notes = "The ID of the task associated with this version history")
    private Integer taskId;

    @ApiModelProperty(
            value = "Name",
            dataType = "String",
            example = "Version 1",
            notes = "The name of the task version")
    private String name;

    @ApiModelProperty(
            value = "Dialect",
            dataType = "String",
            example = "SQL",
            notes = "The SQL dialect used in the task version")
    private String dialect;

    @ApiModelProperty(
            value = "Type",
            dataType = "String",
            example = "BATCH",
            notes = "The type of the task version (e.g., BATCH)")
    private String type;

    @ApiModelProperty(
            value = "Statement",
            dataType = "String",
            example = "SELECT * FROM table",
            notes = "The SQL statement of the task version")
    private String statement;

    @ApiModelProperty(value = "Version ID", dataType = "Integer", example = "3", notes = "The ID of the task version")
    private Integer versionId;

    @ApiModelProperty(
            value = "Create Time",
            dataType = "Date",
            example = "2023-09-15T10:00:00Z",
            notes = "The timestamp when the task version was created")
    private Date createTime;
}
