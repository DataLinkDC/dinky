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
@ApiModel(value = "MetricsLayoutDTO", description = "DTO for Metrics Layout information")
public class MetricsLayoutDTO {

    @ApiModelProperty(
            value = "Layout ID",
            dataType = "Integer",
            example = "1",
            notes = "The unique identifier for the layout")
    private Integer id;

    @ApiModelProperty(
            value = "Task ID",
            dataType = "Integer",
            example = "101",
            notes = "The identifier of the associated task")
    private Integer taskId;

    @ApiModelProperty(
            value = "Vertices",
            dataType = "String",
            example = "vertex1,vertex2,vertex3",
            notes = "The vertices associated with the layout")
    private String vertices;

    @ApiModelProperty(
            value = "Metrics",
            dataType = "String",
            example = "metric1,metric2,metric3",
            notes = "The metrics to display in the layout")
    private String metrics;

    @ApiModelProperty(value = "Position", dataType = "Integer", example = "2", notes = "The position of the layout")
    private Integer position;

    @ApiModelProperty(
            value = "Show Type",
            dataType = "String",
            example = "table",
            notes = "The type of visualization to show")
    private String showType;

    @ApiModelProperty(
            value = "Show Size",
            dataType = "String",
            example = "large",
            notes = "The size of the visualization")
    private String showSize;

    @ApiModelProperty(
            value = "Title",
            dataType = "String",
            example = "Metrics Dashboard",
            notes = "The title of the layout")
    private String title;

    @ApiModelProperty(
            value = "Layout Name",
            dataType = "String",
            example = "dashboard",
            notes = "The name of the layout")
    private String layoutName;
}
