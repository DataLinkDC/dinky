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

package org.dinky.gateway.enums;

import org.dinky.assertion.Asserts;

/**
 * ActionType
 *
 * @since 2021/11/3 21:58
 */
public enum ActionType {
    SAVEPOINT("savepoint"),
    CANCEL("cancel");

    private final String value;

    ActionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ActionType get(String value) {
        for (ActionType type : ActionType.values()) {
            if (Asserts.isEquals(type.getValue(), value)) {
                return type;
            }
        }
        return ActionType.SAVEPOINT;
    }
}
