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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "ApprovalDTO", description = "Approval Data Transfer Object")
public class ApprovalDTO {
    @ApiModelProperty(value = "Approval ID", dataType = "Integer", example = "123", notes = "The ID of the approval")
    private Integer id;

    @ApiModelProperty(value = "Task ID", dataType = "Integer", example = "123", notes = "The ID of the task")
    private Integer taskId;

    @ApiModelProperty(
            value = "Reviewer id required for current approval",
            dataType = "Integer",
            example = "123",
            notes = "The ID of the reviewer")
    private Integer reviewer;

    @ApiModelProperty(
            value = "Comment",
            dataType = "String",
            example = "Looks good to me/Please take a look",
            notes = "Comment from reviewer or submitter")
    private String comment;
}
