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

package org.dinky.process.model;

import org.dinky.data.constant.CommonConstant;
import org.dinky.process.enums.ProcessStatus;
import org.dinky.process.enums.ProcessType;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Process
 *
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessEntity {
    private String pid;
    private String name;
    private StringBuilder log;
    private StringBuilder errLog;
    private ProcessType type;
    private ProcessStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long time;
    private LinkedHashMap<String, ProcessStep> stepsMap;

    public void appendLog(String str) {
        log.append(str).append(CommonConstant.LineSep);
    }

    public void appendErrLog(String str) {
        errLog.append(str).append(CommonConstant.LineSep);
    }
}
