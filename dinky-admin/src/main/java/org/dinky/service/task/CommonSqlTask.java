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
import org.dinky.data.dto.SqlDTO;
import org.dinky.data.dto.TaskDTO;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.job.JobResult;
import org.dinky.service.DataBaseService;

import java.util.List;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SupportDialect({
    Dialect.SQL,
    Dialect.MYSQL,
    Dialect.ORACLE,
    Dialect.POSTGRESQL,
    Dialect.HIVE,
    Dialect.SQLSERVER,
    Dialect.CLICKHOUSE,
    Dialect.DORIS,
    Dialect.PHOENIX,
    Dialect.STAR_ROCKS,
    Dialect.PRESTO
})
public class CommonSqlTask extends BaseTask {

    public CommonSqlTask(TaskDTO task) {
        super(task);
    }

    @Override
    public List<SqlExplainResult> explain() {
        DataBaseService dataBaseService = SpringUtil.getBean(DataBaseService.class);
        return dataBaseService.explainCommonSql(task);
    }

    @Override
    public JobResult execute() {
        log.info("Preparing to execute common sql...");
        SqlDTO sqlDTO = SqlDTO.build(task.getStatement(), task.getDatabaseId(), null);
        DataBaseService dataBaseService = SpringUtil.getBean(DataBaseService.class);
        JobResult jobResult = dataBaseService.executeCommonSql(sqlDTO);
        return jobResult;
    }

    @Override
    public JobResult StreamExecute() {
        log.info("Preparing to execute common sql...");
        SqlDTO sqlDTO = SqlDTO.build(task.getStatement(), task.getDatabaseId(), null);
        DataBaseService dataBaseService = SpringUtil.getBean(DataBaseService.class);
        JobResult jobResult = dataBaseService.StreamExecuteCommonSql(sqlDTO);
        return jobResult;
    }

    @Override
    public boolean stop() {
        return false;
    }
}
