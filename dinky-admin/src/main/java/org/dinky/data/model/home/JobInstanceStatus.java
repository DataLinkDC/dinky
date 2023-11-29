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

/**
 * JobInstanceStatus
 *
 * @since 2022/2/28 22:25
 */
@ApiModel(value = "JobInstanceStatus", description = "Job Instance Status Information")
@Data
public class JobInstanceStatus {

    @ApiModelProperty(value = "All", dataType = "Integer", example = "10", notes = "Total count of job instances")
    private Integer all = 0;

    @ApiModelProperty(
            value = "Initializing",
            dataType = "Integer",
            example = "2",
            notes = "Count of job instances in the Initializing state")
    private Integer initializing = 0;

    @ApiModelProperty(
            value = "Running",
            dataType = "Integer",
            example = "3",
            notes = "Count of job instances in the Running state")
    private Integer running = 0;

    @ApiModelProperty(
            value = "Finished",
            dataType = "Integer",
            example = "1",
            notes = "Count of job instances in the Finished state")
    private Integer finished = 0;

    @ApiModelProperty(
            value = "Failed",
            dataType = "Integer",
            example = "0",
            notes = "Count of job instances in the Failed state")
    private Integer failed = 0;

    @ApiModelProperty(
            value = "Canceled",
            dataType = "Integer",
            example = "0",
            notes = "Count of job instances in the Canceled state")
    private Integer canceled = 0;

    @ApiModelProperty(
            value = "Restarting",
            dataType = "Integer",
            example = "0",
            notes = "Count of job instances in the Restarting state")
    private Integer restarting = 0;

    @ApiModelProperty(
            value = "Created",
            dataType = "Integer",
            example = "0",
            notes = "Count of job instances in the Created state")
    private Integer created = 0;

    @ApiModelProperty(
            value = "Failing",
            dataType = "Integer",
            example = "0",
            notes = "Count of job instances in the Failing state")
    private Integer failing = 0;

    @ApiModelProperty(
            value = "Cancelling",
            dataType = "Integer",
            example = "0",
            notes = "Count of job instances in the Cancelling state")
    private Integer cancelling = 0;

    @ApiModelProperty(
            value = "Suspended",
            dataType = "Integer",
            example = "0",
            notes = "Count of job instances in the Suspended state")
    private Integer suspended = 0;

    @ApiModelProperty(
            value = "Reconciling",
            dataType = "Integer",
            example = "0",
            notes = "Count of job instances in the Reconciling state")
    private Integer reconciling = 0;

    @ApiModelProperty(
            value = "Unknown",
            dataType = "Integer",
            example = "0",
            notes = "Count of job instances in the Unknown state")
    private Integer unknown = 0;

    @ApiModelProperty(value = "modelOverview", dataType = "JobModelOverview", notes = "batch and steamng count")
    private JobModelOverview modelOverview;
}
