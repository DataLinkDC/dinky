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
@ApiModel(value = "JobTypeOverView", description = "Job Type Overview Information")
public class JobTypeOverView {

    @ApiModelProperty(value = "Job Type", dataType = "String", example = "FlinkJar", notes = "Type of the job")
    private String jobType;

    @ApiModelProperty(
            value = "Job Type Count",
            dataType = "Integer",
            example = "5",
            notes = "Count of jobs with the specified type")
    private Integer jobTypeCount;

    @ApiModelProperty(
            value = "Job Type Rate",
            dataType = "Double",
            example = "0.75",
            notes = "Rate or percentage of jobs with the specified type")
    private Double rate;
}
