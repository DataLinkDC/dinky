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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SqlGeneration
 *
 * @since 2022/1/29 16:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "SqlGeneration", description = "SQL Generation Information")
public class SqlGeneration {

    @ApiModelProperty(
            value = "Flink SQL Create Statement",
            dataType = "String",
            notes = "Flink SQL statement for creating a table or view")
    private String flinkSqlCreate = "";

    @ApiModelProperty(value = "SQL Select Statement", dataType = "String", notes = "SQL SELECT statement")
    private String sqlSelect = "";

    @ApiModelProperty(value = "SQL Create Statement", dataType = "String", notes = "SQL statement for creating a table")
    private String sqlCreate = "";
}
