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

import org.dinky.assertion.Asserts;
import org.dinky.config.Dialect;
import org.dinky.data.annotations.SupportDialect;
import org.dinky.data.dto.TaskDTO;
import org.dinky.data.enums.GatewayType;
import org.dinky.data.exception.NotSupportExecuteExcepition;
import org.dinky.data.exception.NotSupportExplainExcepition;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.job.JobManager;
import org.dinky.job.JobResult;
import org.dinky.service.TaskService;
import org.dinky.service.impl.TaskServiceImpl;

import java.util.List;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SupportDialect(Dialect.FLINK_SQL_ENV)
public class FlinkSqlEnvTask extends BaseTask {

    protected final JobManager jobManager;

    public FlinkSqlEnvTask(TaskDTO task) {
        super(task);
        // Default run mode is local.
        if (Asserts.isNull(task.getType())) {
            task.setType(GatewayType.LOCAL.getLongValue());
        }
        this.jobManager = getJobManager();
    }

    @Override
    public List<SqlExplainResult> explain() throws NotSupportExplainExcepition {
        return jobManager.explainSql(task.getStatement()).getSqlExplainResults();
    }

    @Override
    public JobResult execute() throws Exception {
        throw new NotSupportExecuteExcepition(StrFormatter.format(
                "Task [{}] dialect [{}] don't support execute.", task.getName(), task.getDialect()));
    }

    @Override
    public boolean stop() {
        return false;
    }

    protected JobManager getJobManager() {
        TaskService taskService = SpringUtil.getBean(TaskServiceImpl.class);
        return JobManager.build(taskService.buildJobSubmitConfig(task));
    }
}
