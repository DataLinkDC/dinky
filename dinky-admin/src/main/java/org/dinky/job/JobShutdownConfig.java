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

package org.dinky.job;

import org.dinky.context.TenantContextHolder;
import org.dinky.sandbox.Sandbox;
import org.dinky.sandbox.SandboxFactory;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * JobShutdownConfig.
 *
 * @since 2024/6/3 18:09
 */
@Slf4j
@Component
public class JobShutdownConfig {

    @PreDestroy
    public void destroy() {
        log.info("Job shutdown.");
        // TODO: Change it to be configuration - based
        if ("MemorySandbox".equals("MemorySandbox")) {
            Sandbox sandbox = SandboxFactory.getDefaultSandbox();
            List<String> jobIds = sandbox.getAllTables().stream()
                    .filter(tableInfo -> tableInfo.getTableId().isPrivate())
                    .map(tableInfo -> tableInfo.getTableId().getPrivateName())
                    .collect(Collectors.toList());
            if (CollectionUtil.isEmpty(jobIds)) {
                log.info("Memory Sandbox is empty.");
                return;
            }
            // set job result status to destroyed
            JobHandler jobHandler = JobHandler.build();
            TenantContextHolder.ignoreTenant();
            jobHandler.persistResultData(jobIds);
        }
    }
}
