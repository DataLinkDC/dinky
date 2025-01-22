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

package org.dinky.data.enums;

import org.dinky.assertion.Asserts;

import java.util.Arrays;

public enum ApprovalStatus {
    UNKNOWN("UNKNOWN"),
    CREATED("CREATED"),
    SUBMITTED("SUBMITTED"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    CANCELED("CANCELED");
    private final String value;

    ApprovalStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ApprovalStatus fromValue(String value) {
        return Arrays.stream(ApprovalStatus.values())
                .filter(type -> Asserts.isEqualsIgnoreCase(type.getValue(), value))
                .findFirst()
                .orElse(ApprovalStatus.UNKNOWN);
    }

    public static boolean isInProcess(ApprovalStatus status) {
        return status.equals(ApprovalStatus.SUBMITTED);
    }

    public boolean equalVal(String value) {
        return this.value.equals(value);
    }
}
