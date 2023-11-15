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
@ApiModel(value = "JobModelOverview", description = "Job Model Overview Information")
public class JobModelOverview {

    @ApiModelProperty(value = "Batch Job Count", dataType = "Integer", example = "5", notes = "Count of batch jobs")
    private Integer batchJobCount;

    @ApiModelProperty(
            value = "Streaming Job Count",
            dataType = "Integer",
            example = "3",
            notes = "Count of streaming jobs")
    private Integer streamingJobCount;
}
