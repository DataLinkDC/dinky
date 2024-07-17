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
@ApiModel(value = "QueryData", description = "Data for a SQL query")
public class QueryData {

    @ApiModelProperty(
            value = "Unique identifier for the query",
            dataType = "Integer",
            example = "1",
            notes = "Unique identifier for the query")
    private Integer id;

    @ApiModelProperty(
            value = "Name of the schema",
            dataType = "String",
            example = "public",
            notes = "Name of the schema for the SQL query")
    private String schemaName;

    @ApiModelProperty(
            value = "Name of the table",
            dataType = "String",
            example = "users",
            notes = "Name of the table for the SQL query")
    private String tableName;

    @ApiModelProperty(
            value = "SQL query string",
            dataType = "String",
            example = "SELECT * FROM users WHERE age > 30",
            notes = "SQL query string")
    private String sql;

    @ApiModelProperty(value = "Query options", dataType = "Option", notes = "Options for customizing the query")
    private Option option;

    @Data
    @ApiModel(value = "Option", description = "Options for customizing a SQL query")
    public class Option {

        @ApiModelProperty(
                value = "WHERE clause for the query",
                dataType = "String",
                example = "age > 30",
                notes = "WHERE clause for the SQL query")
        private String where;

        @ApiModelProperty(
                value = "ORDER BY clause for the query",
                dataType = "String",
                example = "name ASC",
                notes = "ORDER BY clause for the SQL query")
        private String order;

        @ApiModelProperty(
                value = "Starting row for LIMIT",
                dataType = "String",
                example = "0",
                notes = "Starting row number for the LIMIT clause")
        private int limitStart;

        @ApiModelProperty(
                value = "Ending row for LIMIT",
                dataType = "String",
                example = "10",
                notes = "Ending row number for the LIMIT clause")
        private int limitEnd;
    }
}
