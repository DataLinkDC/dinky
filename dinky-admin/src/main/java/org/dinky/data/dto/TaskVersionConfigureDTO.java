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

import java.io.Serializable;

import lombok.Data;

/** @description: 版本信息配置 */
@Data
public class TaskVersionConfigureDTO implements Serializable {

    /** CheckPoint */
    private Integer checkPoint;

    /** SavePoint策略 */
    private Integer savePointStrategy;

    /** SavePointPath */
    private String savePointPath;

    /** parallelism */
    private Integer parallelism;

    /** fragment */
    private Boolean fragment;

    /** 启用语句集 */
    private Boolean statementSet;

    /** 使用批模式 */
    private Boolean batchModel;

    /** Flink集群ID */
    private Integer clusterId;

    /** 集群配置ID */
    private Integer clusterConfigurationId;

    /** 数据源ID */
    private Integer databaseId;

    /** jarID */
    private Integer jarId;

    /** 环境ID */
    private Integer envId;

    /** 报警组ID */
    private Integer alertGroupId;

    /** 配置JSON */
    private String configJson;

    /** 注释 */
    private String note;

    /** 作业生命周期 */
    private Integer step;

    /** 作业实例ID */
    private Integer jobInstanceId;

    /** 是否启用 */
    private Boolean enabled;
}
