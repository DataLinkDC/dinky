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

package com.dlink.dto;

import com.dlink.gateway.config.GatewayConfig;
import com.dlink.gateway.config.SavePointStrategy;
import com.dlink.job.JobConfig;

import lombok.Getter;
import lombok.Setter;

/**
 * APIExecuteJarDTO
 *
 * @author wenmo
 * @since 2021/12/12 19:46
 */
@Getter
@Setter
public class APIExecuteJarDTO {
    private String type;
    private String jobName;
    private String savePointPath;
    private GatewayConfig gatewayConfig;

    public JobConfig getJobConfig() {
        JobConfig config = new JobConfig();
        config.setType(type);
        config.setJobName(jobName);
        config.setSavePointStrategy(SavePointStrategy.CUSTOM);
        config.setSavePointPath(savePointPath);
        config.setGatewayConfig(gatewayConfig);
        return config;
    }
}
