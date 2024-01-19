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

package org.dinky.data.model.home;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "JobStatusOverView", description = "Job Status Overview Information")
public class JobStatusOverView {

    @ApiModelProperty(value = "Job Running Count", dataType = "Integer", example = "5", notes = "Count of running jobs")
    private Integer jobRunningCount;

    @ApiModelProperty(
            value = "Job Finished Count",
            dataType = "Integer",
            example = "3",
            notes = "Count of finished jobs")
    private Integer jobFinishedCount;

    @ApiModelProperty(
            value = "Job Recovered Count",
            dataType = "Integer",
            example = "2",
            notes = "Count of recovered jobs")
    private Integer jobRecoveredCount;

    @ApiModelProperty(value = "Job Online Count", dataType = "Integer", example = "8", notes = "Count of online jobs")
    private Integer jobOnlineCount;

    @ApiModelProperty(value = "Job Offline Count", dataType = "Integer", example = "1", notes = "Count of offline jobs")
    private Integer jobOfflineCount;

    @ApiModelProperty(
            value = "Job Error Count",
            dataType = "Integer",
            example = "0",
            notes = "Count of jobs with errors")
    private Integer jobErrorCount;
}
