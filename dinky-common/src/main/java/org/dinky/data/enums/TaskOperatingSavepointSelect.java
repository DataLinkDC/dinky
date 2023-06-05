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

/** @version 1.0 */
public enum TaskOperatingSavepointSelect {
    DEFAULT_CONFIG(0, "defaultConfig", "默认保存点"),

    LATEST(1, "latest", "最新保存点");

    private Integer code;

    private String name;

    private String message;

    TaskOperatingSavepointSelect(Integer code, String name, String message) {
        this.code = code;
        this.name = name;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public static TaskOperatingSavepointSelect valueByCode(Integer code) {
        return Arrays.stream(values())
                .filter(savepointSelect -> savepointSelect.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}
