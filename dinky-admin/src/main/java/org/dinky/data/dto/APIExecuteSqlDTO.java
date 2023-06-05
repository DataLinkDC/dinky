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

import org.dinky.assertion.Asserts;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.job.JobConfig;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * APIExecuteSqlDTO
 *
 * @since 2021/12/11 21:50
 */
@Getter
@Setter
public class APIExecuteSqlDTO extends AbstractStatementDTO {

    // RUN_MODE
    private String type;
    private boolean useResult = false;
    private boolean useChangeLog = false;
    private boolean useAutoCancel = false;
    private boolean useStatementSet = false;
    private String address;
    private String jobName;
    private Integer maxRowNum = 100;
    private Integer checkPoint = 0;
    private Integer parallelism;
    private String savePointPath;
    private Map<String, String> configuration;
    private GatewayConfig gatewayConfig;

    public JobConfig getJobConfig() {
        int savePointStrategy = Asserts.isNotNullString(savePointPath) ? 3 : 0;

        return new JobConfig(
                type,
                useResult,
                useChangeLog,
                useAutoCancel,
                false,
                null,
                true,
                address,
                jobName,
                isFragment(),
                useStatementSet,
                maxRowNum,
                checkPoint,
                parallelism,
                savePointStrategy,
                savePointPath,
                configuration,
                gatewayConfig);
    }
}
