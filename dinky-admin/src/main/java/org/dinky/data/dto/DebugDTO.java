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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 *  Param for debug flink sql and common sql
 */
@Getter
@Setter
@ApiModel(value = "DebugDTO", description = "Param for debug flink sql and common sql")
public class DebugDTO {

    @ApiModelProperty(
            value = "Task ID",
            dataType = "Integer",
            example = "1",
            notes = "The ID of Task which is debugged")
    @ProcessId
    private Integer id;

    @ApiModelProperty(
            value = "Use ChangeLog",
            dataType = "boolean",
            example = "false",
            notes = "Flag indicating whether to preview change log")
    private boolean useChangeLog = false;

    @ApiModelProperty(
            value = "Use Auto Cancel",
            dataType = "boolean",
            example = "true",
            notes = "Flag indicating whether to auto cancel after preview the maximum rows")
    private boolean useAutoCancel = true;

    @ApiModelProperty(
            value = "Max Row Number",
            dataType = "Integer",
            example = "1000",
            notes = "The maximum number of rows to preview")
    private Integer maxRowNum = 1000;
}
