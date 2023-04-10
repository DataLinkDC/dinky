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

package org.dinky.service.impl;

import org.dinky.process.model.ProcessEntity;
import org.dinky.process.pool.ConsolePool;
import org.dinky.process.pool.ProcessPool;
import org.dinky.service.ProcessService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

/**
 * ProcessServiceImpl
 *
 * @since 2022/10/16 22:45
 */
@Service
public class ProcessServiceImpl implements ProcessService {

    @Override
    public List<ProcessEntity> listAllProcess(boolean active) {
        return ProcessPool.INSTANCE.values().stream()
                .filter(t -> active ? t.isActiveProcess() : true)
                .sorted(Comparator.comparing(ProcessEntity::getStartTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public String getConsoleByUserId(Integer userId) {
        String user = userId.toString();
        return ConsolePool.INSTANCE.getOrDefault(user, new StringBuilder("")).toString();
    }

    @Override
    public void clearConsoleByUserId(Integer userId) {
        ConsolePool.clear(userId);
    }
}
