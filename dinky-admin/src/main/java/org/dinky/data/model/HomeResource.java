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

package org.dinky.data.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "HomeResource", description = "Home Resource Information")
public class HomeResource {

    @ApiModelProperty(value = "Flink ClusterInstance Count", dataType = "Integer")
    private Integer flinkClusterCount;

    @ApiModelProperty(value = "Flink Config Count", dataType = "Integer")
    private Integer flinkConfigCount;

    @ApiModelProperty(value = "Database Source Count", dataType = "Integer")
    private Integer dbSourceCount;

    @ApiModelProperty(value = "Global Variable Count", dataType = "Integer")
    private Integer globalVarCount;

    @ApiModelProperty(value = "Alert Instance Count", dataType = "Integer")
    private Integer alertInstanceCount;

    @ApiModelProperty(value = "Alert Group Count", dataType = "Integer")
    private Integer alertGroupCount;

    @ApiModelProperty(value = "Git Project Count", dataType = "Integer")
    private Integer gitProjectCount;
}
