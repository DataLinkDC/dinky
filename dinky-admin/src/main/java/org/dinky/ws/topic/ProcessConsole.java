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

package org.dinky.ws.topic;

import org.dinky.context.ConsoleContextHolder;
import org.dinky.data.model.ProcessEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessConsole extends BaseTopic {
    private final Map<String, ProcessEntity> logPross = new ConcurrentHashMap<>();
    public static final ProcessConsole INSTANCE = new ProcessConsole();

    private ProcessConsole() {}

    @Override
    public Map<String, Object> autoDataSend(Set<String> allParams) {
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> firstDataSend(Set<String> allParams) {
        Map<String, Object> result = new HashMap<>();
        allParams.forEach(processName -> {
            ProcessEntity process = ConsoleContextHolder.getInstances().getProcess(processName);
            if (process != null) {
                result.put(processName, process);
            }
        });
        return result;
    }
}
