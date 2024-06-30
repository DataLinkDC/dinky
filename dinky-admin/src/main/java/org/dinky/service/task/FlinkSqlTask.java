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
import org.dinky.data.result.SqlExplainResult;
import org.dinky.job.JobManager;
import org.dinky.job.JobResult;
import org.dinky.service.TaskService;
import org.dinky.service.impl.TaskServiceImpl;
import org.dinky.utils.JsonUtils;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SupportDialect(Dialect.FLINK_SQL)
public class FlinkSqlTask extends BaseTask {
    protected final JobManager jobManager;

    public FlinkSqlTask(TaskDTO task) {
        super(task);
        // Default run mode is local.
        if (Asserts.isNull(task.getType())) {
            task.setType(GatewayType.LOCAL.getLongValue());
        }
        this.jobManager = getJobManager();
    }

    @Override
    public List<SqlExplainResult> explain() {
        return jobManager.explainSql(task.getStatement()).getSqlExplainResults();
    }

    public ObjectNode getJobPlan() {
        String planJson = jobManager.getJobPlanJson(task.getStatement());
        return JsonUtils.parseObject(planJson);
    }

    @Override
    public JobResult execute() throws Exception {
        log.info("Initializing Flink job config...");
        return jobManager.executeSql(task.getStatement());
    }

    protected JobManager getJobManager() {
        TaskService taskService = SpringUtil.getBean(TaskServiceImpl.class);
        return JobManager.build(taskService.buildJobSubmitConfig(task));
    }

    @Override
    public boolean stop() {
        return false;
    }
}
