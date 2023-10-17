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

package org.dinky.process.enums;

import org.dinky.assertion.Asserts;
import org.dinky.data.enums.Status;

import lombok.Getter;

@Getter
public enum ProcessStepType {
    SUBMIT_TASK("SUBMIT_TASK", null, Status.PROCESS_SUBMIT_SUBMITTASK),
    SUBMIT_PRECHECK("SUBMIT_PRECHECK", SUBMIT_TASK, Status.PROCESS_SUBMIT_CHECKSQL),
    SUBMIT_EXECUTE("SUBMIT_EXECUTE", SUBMIT_TASK, Status.PROCESS_SUBMIT_EXECUTE),
    SUBMIT_BUILD_CONFIG("SUBMIT_BUILD_CONFIG", SUBMIT_EXECUTE, Status.PROCESS_SUBMIT_BUILDCONFIG),
    SUBMIT_EXECUTE_COMMON_SQL("SUBMIT_EXECUTE_COMMON_SQL", SUBMIT_BUILD_CONFIG, Status.PROCESS_SUBMIT_EXECUTECOMMSQL),
    SUBMIT_EXECUTE_FLINK_SQL("SUBMIT_EXECUTE_FLINK_SQL", SUBMIT_BUILD_CONFIG, Status.PROCESS_SUBMIT_EXECUTEFLINKSQL),
    UNKNOWN("UNKNOWN", null, Status.UNKNOWN_ERROR),
    ;

    private final String value;
    private final ProcessStepType parentStep;
    private final Status desc;

    ProcessStepType(String type, ProcessStepType parentStep, Status desc) {
        this.value = type;
        this.parentStep = parentStep;
        this.desc = desc;
    }

    public static ProcessStepType get(String value) {
        for (ProcessStepType type : ProcessStepType.values()) {
            if (Asserts.isEquals(type.getValue(), value)) {
                return type;
            }
        }
        return ProcessStepType.UNKNOWN;
    }
}
