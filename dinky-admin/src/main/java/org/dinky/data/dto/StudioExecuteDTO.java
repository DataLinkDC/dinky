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

import org.dinky.data.model.TaskExtConfig;
import org.dinky.job.JobConfig;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * StudioExecuteDTO
 *
 * @since 2021/5/30 11:09
 */
@Getter
@Setter
@Slf4j
public class StudioExecuteDTO extends AbstractStatementDTO {

    // RUN_MODE
    private String type;
    private String dialect;
    private boolean useResult;
    private boolean useChangeLog;
    private boolean useAutoCancel;
    private boolean statementSet;
    private boolean batchModel;
    private boolean useSession;
    private String session;
    private Integer clusterId;
    private Integer clusterConfigurationId;
    private Integer databaseId;
    private Integer jarId;
    private String jobName;
    private Integer taskId;
    private Integer id;
    private Integer maxRowNum;
    private Integer checkPoint;
    private Integer parallelism;
    private Integer savePointStrategy;
    private String savePointPath;
    private TaskExtConfig configJson;

    public JobConfig getJobConfig() {

        Map<String, String> parsedConfig = this.configJson.getCustomConfigMaps();

        return new JobConfig(
                type,
                useResult,
                useChangeLog,
                useAutoCancel,
                useSession,
                session,
                clusterId,
                clusterConfigurationId,
                jarId,
                taskId,
                jobName,
                isFragment(),
                statementSet,
                batchModel,
                maxRowNum,
                checkPoint,
                parallelism,
                savePointStrategy,
                savePointPath,
                getVariables(),
                parsedConfig);
    }

    public Integer getTaskId() {
        return taskId == null ? getId() : taskId;
    }
}
