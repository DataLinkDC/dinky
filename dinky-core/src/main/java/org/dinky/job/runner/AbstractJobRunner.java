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

package org.dinky.job.runner;

import org.dinky.data.job.JobStatement;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.job.JobManager;
import org.dinky.job.JobRunner;
import org.dinky.utils.LogUtil;
import org.dinky.utils.SqlUtil;

import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.streaming.api.graph.StreamGraph;

import java.time.LocalDateTime;

import cn.hutool.core.text.StrFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractJobRunner implements JobRunner {

    protected JobManager jobManager;

    public SqlExplainResult explain(JobStatement jobStatement) {
        SqlExplainResult.Builder resultBuilder = SqlExplainResult.Builder.newBuilder();
        try {
            run(jobStatement);
            resultBuilder
                    .parseTrue(true)
                    .explainTrue(true)
                    .type(jobStatement.getSqlType().getType())
                    .sql(jobStatement.getStatement())
                    .explainTime(LocalDateTime.now())
                    .index(jobStatement.getIndex());
        } catch (Exception e) {
            String error = StrFormatter.format(
                    "Exception in explaining FlinkSQL:\n{}\n{}",
                    SqlUtil.addLineNumber(jobStatement.getStatement()),
                    LogUtil.getError(e));
            resultBuilder
                    .parseTrue(false)
                    .error(error)
                    .explainTrue(false)
                    .type(jobStatement.getSqlType().getType())
                    .sql(jobStatement.getStatement())
                    .explainTime(LocalDateTime.now())
                    .index(jobStatement.getIndex());
            log.error(error);
            return resultBuilder.build();
        }
        return resultBuilder.build();
    }

    public StreamGraph getStreamGraph(JobStatement jobStatement) {
        explain(jobStatement);
        return null;
    }

    public JobPlanInfo getJobPlanInfo(JobStatement jobStatement) {
        explain(jobStatement);
        return null;
    }
}
