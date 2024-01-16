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

import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * AbstractStatementDTO
 *
 * @since 2021/12/29
 */
@ApiModel(value = "AbstractStatementDTO", description = "Abstract Statement Data Transfer Object")
@Data
public class AbstractStatementDTO {

    @ApiModelProperty(value = "Statement", dataType = "String", example = "SELECT * FROM table", notes = "SQL语句")
    private String statement;

    @ApiModelProperty(value = "Environment ID", dataType = "Integer", example = "1", notes = "环境ID")
    private Integer envId;

    @ApiModelProperty(value = "Fragment Flag", dataType = "boolean", example = "false", notes = "是否为片段")
    private Boolean fragment = false;

    @ApiModelProperty(
            value = "Variables",
            dataType = "Map<String, String>",
            example = "{\"key\": \"value\"}",
            notes = "变量集合")
    private Map<String, String> variables = new HashMap<>();
}
