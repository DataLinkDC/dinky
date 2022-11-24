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

package com.dlink.process.model;

import com.dlink.assertion.Asserts;

/**
 * ProcessType
 *
 * @author wenmo
 * @since 2022/10/16 16:33
 */
public enum ProcessType {

    FLINKEXPLAIN("FlinkExplain"),
    FLINKEXECUTE("FlinkExecute"),
    FLINKSUBMIT("FlinkSubmit"),
    SQLEXPLAIN("SQLExplain"),
    SQLEXECUTE("SQLExecute"),
    SQKSUBMIT("SQLSubmit"),
    UNKNOWN("Unknown");

    private String value;

    ProcessType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ProcessType get(String value) {
        for (ProcessType type : ProcessType.values()) {
            if (Asserts.isEquals(type.getValue(), value)) {
                return type;
            }
        }
        return ProcessType.UNKNOWN;
    }

    public boolean equalsValue(String type) {
        if (Asserts.isEquals(value, type)) {
            return true;
        }
        return false;
    }
}
