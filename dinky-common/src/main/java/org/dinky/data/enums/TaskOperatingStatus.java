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

/** @version 1.0 */
public enum TaskOperatingStatus {
    INIT(1, "init", "初始化"),

    OPERATING_BEFORE(4, "operatingBefore", "操作前准备"),

    TASK_STATUS_NO_DONE(8, "taskStatusNoDone", "任务不是完成状态"),

    OPERATING(12, "operating", "正在操作"),

    EXCEPTION(13, "exception", "异常"),

    SUCCESS(16, "success", "成功"),
    FAIL(20, "fail", "失败");

    private Integer code;

    private String name;

    private String message;

    TaskOperatingStatus(Integer code, String name, String message) {
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
}
