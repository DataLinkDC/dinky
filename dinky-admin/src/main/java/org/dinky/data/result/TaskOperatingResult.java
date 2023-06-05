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

package org.dinky.data.result;

import org.dinky.data.enums.CodeEnum;
import org.dinky.data.enums.TaskOperatingSavepointSelect;
import org.dinky.data.enums.TaskOperatingStatus;
import org.dinky.data.model.Task;

import lombok.Data;

/** @version 1.0 */
@Data
public class TaskOperatingResult {

    private Task task;

    private TaskOperatingStatus status;

    private Integer code;

    private String message;

    private TaskOperatingSavepointSelect taskOperatingSavepointSelect;

    public TaskOperatingResult(Task task) {
        this.task = task;
        this.status = TaskOperatingStatus.INIT;
        this.taskOperatingSavepointSelect = TaskOperatingSavepointSelect.DEFAULT_CONFIG;
    }

    public TaskOperatingResult(
            Task task, TaskOperatingSavepointSelect taskOperatingSavepointSelect) {
        this.task = task;
        this.status = TaskOperatingStatus.INIT;
        this.taskOperatingSavepointSelect = taskOperatingSavepointSelect;
    }

    public void parseResult(Result<Void> result) {
        if (result == null) {
            return;
        }
        if (CodeEnum.SUCCESS.getCode().equals(result.getCode())) {
            this.status = TaskOperatingStatus.SUCCESS;
        } else if (CodeEnum.ERROR.getCode().equals(result.getCode())) {
            this.status = TaskOperatingStatus.FAIL;
        }
        this.code = result.getCode();
        this.message = result.getMsg();
    }
}
