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

import java.util.Arrays;

/**
 * JobLifeCycle
 *
 * @since 2022/2/1 16:37
 */
public enum JobLifeCycle {
    UNKNOWN(0, "未知"),
    CREATE(1, "创建"),
    DEVELOP(2, "开发"),
    DEBUG(3, "调试"),
    RELEASE(4, "发布"),
    ONLINE(5, "上线"),
    CANCEL(6, "注销");

    private Integer value;
    private String label;

    JobLifeCycle(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public Integer getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public static JobLifeCycle get(Integer value) {
        return Arrays.stream(values())
                .filter(item -> item.getValue().equals(value))
                .findFirst()
                .orElse(JobLifeCycle.UNKNOWN);
    }

    public boolean equalsValue(Integer step) {
        return value.equals(step);
    }
}
