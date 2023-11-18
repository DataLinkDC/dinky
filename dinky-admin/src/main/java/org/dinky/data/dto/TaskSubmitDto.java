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

import java.util.Map;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskSubmitDto {

    @ApiModelProperty(value = "ID", dataType = "Integer", example = "6", notes = "The identifier of the execution")
    private Integer id;

    @ApiModelProperty(
            value = "Save Point Path",
            dataType = "String",
            example = "/savepoints",
            notes = "The path for save points")
    private String savePointPath;

    @ApiModelProperty(
            value = "Variables",
            dataType = "Map<String, String>",
            example = "{\"key\": \"value\"}",
            notes = "Variables")
    private Map<String, String> variables;
}
