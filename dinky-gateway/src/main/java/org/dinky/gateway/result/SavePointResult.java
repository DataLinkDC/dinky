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

package org.dinky.gateway.result;

import org.dinky.data.enums.GatewayType;
import org.dinky.gateway.model.JobInfo;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO
 *
 * @since 2021/11/3 22:20
 */
@Getter
@Setter
@ApiModel(value = "SavePointResult", description = "Result of SavePoint operation")
public class SavePointResult extends AbstractGatewayResult {

    @ApiModelProperty(
            value = "Application ID",
            dataType = "String",
            example = "app123",
            notes = "Unique identifier for the application")
    private String appId;

    @ApiModelProperty(
            value = "List of Job Information",
            dataType = "List<JobInfo>",
            example =
                    "[{\"jobId\":\"job1\",\"savePoint\":\"savepoint1\",\"status\":\"RUNNING\"},{\"jobId\":\"job2\",\"savePoint\":\"savepoint2\",\"status\":\"COMPLETED\"}]",
            notes = "List of job information associated with the SavePoint operation")
    private List<JobInfo> jobInfos;

    public SavePointResult(GatewayType type, LocalDateTime startTime) {
        super(type, startTime);
    }

    public SavePointResult(LocalDateTime startTime, LocalDateTime endTime, boolean isSuccess, String exceptionMsg) {
        super(startTime, endTime, isSuccess, exceptionMsg);
    }

    @Override
    public String getId() {
        return appId;
    }

    @Override
    public GatewayResult setId(String id) {
        this.appId = id;
        return this;
    }

    @Override
    public String getWebURL() {
        return null;
    }

    @Override
    public List<String> getJids() {
        return null;
    }

    public static SavePointResult build(GatewayType type) {
        return new SavePointResult(type, LocalDateTime.now());
    }
}
