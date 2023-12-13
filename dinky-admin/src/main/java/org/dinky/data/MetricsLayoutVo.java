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

package org.dinky.data;

import org.dinky.data.model.Metrics;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetricsLayoutVo {
    @ApiModelProperty(value = "Layout Name", dataType = "String", notes = "Name of the layout")
    private String layoutName;

    @ApiModelProperty(value = "Job ID", dataType = "String", notes = "ID of the associated job")
    private String flinkJobId;

    @ApiModelProperty(value = "Task ID", dataType = "Integer", example = "1001", notes = "ID of the associated task")
    private int taskId;

    /**
     * The feature is not complete and may be implemented in the future
     */
    @ApiModelProperty(value = "Show In Dashboard", dataType = "Boolean", notes = "Whether to show in dashboard")
    private boolean showInDashboard;

    @ApiModelProperty(value = "Metrics", dataType = "List<Metrics>", notes = "Metrics information")
    private List<Metrics> metrics;
}
