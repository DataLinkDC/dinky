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

package com.dlink.result;

import com.dlink.common.result.Result;
import com.dlink.model.CodeEnum;
import com.dlink.model.Task;
import com.dlink.model.TaskOperatingSavepointSelect;
import com.dlink.model.TaskOperatingStatus;
import lombok.Data;

/**
 * @author mydq
 * @version 1.0
 * @date 2022/7/16 21:09
 **/
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

    public TaskOperatingResult(Task task, TaskOperatingSavepointSelect taskOperatingSavepointSelect) {
        this.task = task;
        this.status = TaskOperatingStatus.INIT;
        this.taskOperatingSavepointSelect = taskOperatingSavepointSelect;
    }

    public void parseResult(Result result) {
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
