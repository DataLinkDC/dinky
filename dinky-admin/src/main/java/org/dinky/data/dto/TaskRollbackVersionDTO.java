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

/** @description: 任务回滚DTO */
@Data
@ApiModel(value = "TaskRollbackVersionDTO", description = "DTO for rolling back a task to a specific version")
public class TaskRollbackVersionDTO implements Serializable {

    @ApiModelProperty(value = "ID", dataType = "Integer", example = "1", notes = "The identifier of the task")
    private Integer taskId;

    @ApiModelProperty(
            value = "Version ID",
            dataType = "Integer",
            example = "2",
            notes = "The identifier of the version to rollback to")
    private Integer versionId;
}
