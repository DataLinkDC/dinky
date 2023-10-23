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

import org.dinky.gateway.enums.GatewayType;
import org.dinky.job.JobConfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * StudioMetaStoreDTO
 *
 * @since 2022/7/16 23:18
 */
@Getter
@Setter
@ApiModel(value = "StudioMetaStoreDTO", description = "DTO for storing metadata in Studio")
public class StudioMetaStoreDTO extends AbstractStatementDTO {

    @ApiModelProperty(value = "Catalog", dataType = "String", example = "my_catalog", notes = "The name of the catalog")
    private String catalog;

    @ApiModelProperty(
            value = "Database",
            dataType = "String",
            example = "my_database",
            notes = "The name of the database")
    private String database;

    @ApiModelProperty(value = "Table", dataType = "String", example = "my_table", notes = "The name of the table")
    private String table;

    @ApiModelProperty(
            value = "Dialect",
            dataType = "String",
            example = "MySQL",
            notes = "The SQL dialect for the table")
    private String dialect;

    @ApiModelProperty(
            value = "Database ID",
            dataType = "Integer",
            example = "1",
            notes = "The identifier of the database")
    private Integer databaseId;

    public JobConfig getJobConfig() {
        return JobConfig.builder()
                .type(GatewayType.LOCAL.getLongValue())
                .useResult(true)
                .useChangeLog(false)
                .useAutoCancel(false)
                .fragment(isFragment())
                .statementSet(false)
                .batchModel(false)
                .maxRowNum(0)
                .build();
    }
}
