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

package org.dinky.service.task;

import org.dinky.config.Dialect;
import org.dinky.data.annotations.SupportDialect;
import org.dinky.data.dto.TaskDTO;
import org.dinky.data.model.Task;
import org.dinky.function.FunctionFactory;
import org.dinky.function.data.model.UDF;
import org.dinky.job.Job;
import org.dinky.job.JobResult;
import org.dinky.utils.UDFUtils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.exceptions.ExceptionUtil;

@SupportDialect({Dialect.JAVA, Dialect.PYTHON, Dialect.SCALA})
public class UdfTask extends BaseTask {
    public UdfTask(TaskDTO task) {
        super(task);
    }

    @Override
    public JobResult execute() throws Exception {
        JobResult jobResult = new JobResult();
        jobResult.setSuccess(true);
        jobResult.setStatus(Job.JobStatus.SUCCESS);
        try {
            UDF udf = UDFUtils.taskToUDF(BeanUtil.toBean(task, Task.class));
            FunctionFactory.initUDF(udf, task.getId());
        } catch (Exception e) {
            jobResult.setSuccess(false);
            jobResult.setError(ExceptionUtil.getRootCauseMessage(e));
            jobResult.setStatus(Job.JobStatus.FAILED);
        }
        return jobResult;
    }

    @Override
    public boolean stop() {
        return true;
    }
}
