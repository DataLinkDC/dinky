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

import org.dinky.job.JobConfig;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * StudioDDLDTO
 *
 * @since 2021/6/3
 */
@Getter
@Setter
@ApiModel(value = "StudioDDLDTO", description = "DTO for Studio DDL operations")
public class StudioDDLDTO {

    @ApiModelProperty(value = "Type", dataType = "String", example = "FlinkSql", notes = "The type of DDL operation")
    private String type;

    @ApiModelProperty(
            value = "Use Result",
            dataType = "boolean",
            example = "true",
            notes = "Flag indicating whether to use result")
    private boolean useResult;

    @ApiModelProperty(
            value = "Use Session",
            dataType = "boolean",
            example = "true",
            notes = "Flag indicating whether to use a session")
    private boolean useSession;

    @ApiModelProperty(value = "Session", dataType = "String", example = "session_id", notes = "The session identifier")
    private String session;

    @ApiModelProperty(
            value = "Use Remote",
            dataType = "boolean",
            example = "true",
            notes = "Flag indicating whether to use a remote execution")
    private boolean useRemote;

    @ApiModelProperty(
            value = "ClusterInstance ID",
            dataType = "Integer",
            example = "1",
            notes = "The identifier of the cluster")
    private Integer clusterId;

    @ApiModelProperty(
            value = "Statement",
            dataType = "String",
            example = "CREATE TABLE users (id INT, name VARCHAR(255))",
            notes = "The DDL statement")
    private String statement;

    @ApiModelProperty(
            value = "Max Row Number",
            dataType = "Integer",
            example = "10000",
            notes = "The maximum number of rows to return")
    private Integer maxRowNum = 10000;

    public JobConfig getJobConfig() {
        JobConfig jobConfig = new JobConfig();
        BeanUtil.copyProperties(this, jobConfig);
        return jobConfig;
    }
}
