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
 * SqlDTO
 *
 * @since 2021/12/29 19:42
 */
@Getter
@Setter
@ApiModel(value = "SqlDTO", description = "DTO for SQL query")
public class SqlDTO {

    @ApiModelProperty(
            value = "SQL Statement",
            dataType = "String",
            example = "SELECT * FROM users",
            notes = "The SQL query statement")
    private String statement;

    @ApiModelProperty(
            value = "Database ID",
            dataType = "Integer",
            example = "1",
            notes = "The identifier of the target database")
    private Integer databaseId;

    @ApiModelProperty(
            value = "Maximum Number of Rows",
            dataType = "Integer",
            example = "100",
            notes = "The maximum number of rows to fetch in the query result")
    private Integer maxRowNum;

    public SqlDTO(String statement, Integer databaseId, Integer maxRowNum) {
        this.statement = statement;
        this.databaseId = databaseId;
        this.maxRowNum = maxRowNum;
    }

    public static SqlDTO build(String statement, Integer databaseId, Integer maxRowNum) {
        return new SqlDTO(statement, databaseId, maxRowNum);
    }
}
