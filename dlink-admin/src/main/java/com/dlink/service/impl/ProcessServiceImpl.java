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

package com.dlink.service.impl;

import com.dlink.process.model.ProcessEntity;
import com.dlink.process.pool.ConsolePool;
import com.dlink.process.pool.ProcessPool;
import com.dlink.service.ProcessService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

/**
 * ProcessServiceImpl
 *
 * @author wenmo
 * @since 2022/10/16 22:45
 */
@Service
public class ProcessServiceImpl implements ProcessService {

    @Override
    public List<ProcessEntity> listAllProcess(boolean active) {
        Map<String, ProcessEntity> processEntityMap = ProcessPool.getInstance().getMap();
        if (active) {
            return processEntityMap.values().stream().filter(x -> x.isActiveProcess())
                    .sorted(Comparator.comparing(ProcessEntity::getStartTime).reversed()).collect(Collectors.toList());
        }
        return processEntityMap.values().stream().sorted(Comparator.comparing(ProcessEntity::getStartTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public String getConsoleByUserId(Integer userId) {
        String user = userId.toString();
        if (ConsolePool.getInstance().exist(user)) {
            return ConsolePool.getInstance().get(user).toString();
        } else {
            return "";
        }
    }
}
