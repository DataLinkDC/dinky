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

/**
 * StudioCADTO
 *
 * @since 2021/6/23 14:00
 */
@Getter
@Setter
@ApiModel(value = "StudioCADTO", description = "DTO for Studio SQL query")
public class StudioLineageDTO extends AbstractStatementDTO {

    @ApiModelProperty(
            value = "Use Statement Set",
            dataType = "Boolean",
            example = "true",
            notes = "Flag indicating whether to use Statement Set")
    private Boolean statementSet;

    @ApiModelProperty(value = "Type", dataType = "Integer", example = "1", notes = "The type of the SQL query")
    private Integer type;

    @ApiModelProperty(value = "Dialect", dataType = "String", example = "MySQL", notes = "The SQL dialect")
    private String dialect;

    @ApiModelProperty(
            value = "Database ID",
            dataType = "Integer",
            example = "1",
            notes = "The identifier of the target database")
    private Integer databaseId;
}
