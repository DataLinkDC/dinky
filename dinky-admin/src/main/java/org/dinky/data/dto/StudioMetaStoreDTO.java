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

import lombok.Getter;
import lombok.Setter;

/**
 * StudioMetaStoreDTO
 *
 * @since 2022/7/16 23:18
 */
@Getter
@Setter
public class StudioMetaStoreDTO extends AbstractStatementDTO {

    private String catalog;
    private String database;
    private String table;
    private String dialect;
    private Integer databaseId;

    public JobConfig getJobConfig() {
        return new JobConfig(
                GatewayType.LOCAL.getLongValue(),
                true,
                false,
                false,
                false,
                null,
                null,
                null,
                null,
                null,
                null,
                isFragment(),
                false,
                false,
                0,
                null,
                null,
                null,
                null,
                null,
                null);
    }
}
