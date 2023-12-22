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
import org.dinky.data.exception.NotSupportExplainExcepition;
import org.dinky.data.result.SelectResult;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.job.JobResult;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class BaseTask {

    private static final Cache<String, SelectResult> results = new TimedCache<>(TimeUnit.MINUTES.toMillis(10));
    final TaskDTO task;

    public abstract JobResult execute() throws Exception;

    public abstract boolean stop();

    public List<SqlExplainResult> explain() throws NotSupportExplainExcepition {
        throw new NotSupportExplainExcepition(StrFormatter.format(
                "task [{}] dialect [{}] is can not explain, skip sqlExplain verify",
                task.getName(),
                task.getDialect()));
    }

    public ObjectNode getJobPlan() throws NotSupportExplainExcepition {
        throw new NotSupportExplainExcepition(
                StrFormatter.format("task [{}] dialect [{}] is can not getJobPlan", task.getName(), task.getDialect()));
    }

    public static BaseTask getTask(TaskDTO taskDTO) {
        Set<Class<?>> classes =
                ClassUtil.scanPackageBySuper(BaseTask.class.getPackage().getName(), BaseTask.class);
        for (Class<?> clazz : classes) {
            SupportDialect annotation = clazz.getAnnotation(SupportDialect.class);
            if (annotation != null) {
                for (Dialect dialect : annotation.value()) {
                    if (dialect.getValue().equalsIgnoreCase(taskDTO.getDialect())) {
                        return (BaseTask) ReflectUtil.newInstance(clazz, taskDTO);
                    }
                }
            }
        }
        throw new RuntimeException("Not support dialect: " + taskDTO.getDialect());
    }

    public JobResult StreamExecute() {
        return null;
    }
}
