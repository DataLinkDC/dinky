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

package org.dinky.data.app;

import org.dinky.config.Dialect;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AppTask {
    @ApiModelProperty(value = "ID", required = true, dataType = "Integer", example = "1", notes = "Primary Key")
    private Integer id;

    @ApiModelProperty(value = "Name", required = true, dataType = "String", example = "Name")
    private String name;

    @ApiModelProperty(value = "Type", dataType = "String", notes = "Type of the task")
    private String type;

    @ApiModelProperty(value = "Dialect", dataType = "Dialect", notes = "Dialect")
    private Dialect dialect;

    @ApiModelProperty(value = "Check Point", dataType = "Integer", example = "1", notes = "Check point for the task")
    private Integer checkPoint;

    @ApiModelProperty(value = "Save Point Path", dataType = "String", notes = "Save point path for the task")
    private String savePointPath;

    @ApiModelProperty(value = "Parallelism", dataType = "Integer", example = "4", notes = "Parallelism for the task")
    private Integer parallelism;

    @ApiModelProperty(value = "Fragment", dataType = "Boolean", example = "true", notes = "task Fragment")
    private Boolean fragment;

    @ApiModelProperty(value = "Statement Set", dataType = "Boolean", example = "false", notes = "use Statement")
    private Boolean statementSet;

    @ApiModelProperty(value = "Environment ID", dataType = "Integer", example = "6001", notes = "environment id")
    private Integer envId;

    @ApiModelProperty(value = "Configuration JSON", dataType = "TaskExtConfig", notes = "task config")
    private String configJson;

    @ApiModelProperty(value = "Batch Model", dataType = "Boolean", example = "true", notes = "Batch task")
    private Boolean batchModel;

    @ApiModelProperty(value = "Statement", dataType = "String", notes = "SQL statement for the task")
    private String statement;
}
