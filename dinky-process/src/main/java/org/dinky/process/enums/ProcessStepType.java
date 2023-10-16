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

import lombok.Getter;

@Getter
public enum ProcessStepType {
    SUBMIT_TASK("SUBMIT_TASK", null),
    SUBMIT_PRECHECK("SUBMIT_PRECHECK", SUBMIT_TASK),
    SUBMIT_EXECUTE("SUBMIT_EXECUTE", SUBMIT_TASK),
    SUBMIT_BUILD_CONFIG("SUBMIT_BUILD_CONFIG", SUBMIT_EXECUTE),
    UNKNOWN("Unknown", null);

    private final String value;
    private final ProcessStepType parentStep;

    ProcessStepType(String type, ProcessStepType parentStep) {
        this.value = type;
        this.parentStep = parentStep;
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
