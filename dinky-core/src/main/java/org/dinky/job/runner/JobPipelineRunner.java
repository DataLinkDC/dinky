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

import org.dinky.assertion.Asserts;
import org.dinky.data.exception.DinkyException;
import org.dinky.data.job.JobStatement;
import org.dinky.data.job.SqlType;
import org.dinky.data.result.IResult;
import org.dinky.data.result.InsertResult;
import org.dinky.data.result.ResultBuilder;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.executor.CustomTableResultImpl;
import org.dinky.executor.Executor;
import org.dinky.gateway.Gateway;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.job.Job;
import org.dinky.job.JobConfig;
import org.dinky.job.JobManager;
import org.dinky.utils.FlinkStreamEnvironmentUtil;
import org.dinky.utils.LogUtil;
import org.dinky.utils.SqlUtil;
import org.dinky.utils.URLUtils;

import org.apache.flink.core.execution.JobClient;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.types.Row;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobPipelineRunner extends AbstractJobRunner {

    private List<JobStatement> statements;
    private TableResult tableResult;

    public JobPipelineRunner(JobManager jobManager) {
        this.jobManager = jobManager;
        this.statements = new ArrayList<>();
    }

    @Override
    public void run(JobStatement jobStatement) throws Exception {
        statements.add(jobStatement);
        tableResult = jobManager.getExecutor().executeSql(jobStatement.getStatement());
        if (statements.size() == 1) {
            if (jobManager.isUseGateway()) {
                processWithGateway();
            } else {
                processWithoutGateway();
            }
        } else {
            log.error(
                    "Only one pipeline job is executed. The statement has be skipped: " + jobStatement.getStatement());
            return;
        }
    }

    @Override
    public SqlExplainResult explain(JobStatement jobStatement) {
        SqlExplainResult.Builder resultBuilder = SqlExplainResult.Builder.newBuilder();
        statements.add(jobStatement);
        // pipeline job execute to generate stream graph.
        jobManager.getExecutor().executeSql(jobStatement.getStatement());
        if (statements.size() == 1) {
            try {
                resultBuilder
                        .explain(FlinkStreamEnvironmentUtil.getStreamingPlanAsJSON(
                                jobManager.getExecutor().getStreamGraph()))
                        .type(jobStatement.getSqlType().getType())
                        .parseTrue(true)
                        .explainTrue(true)
                        .sql(jobStatement.getStatement())
                        .explainTime(LocalDateTime.now())
                        .index(jobStatement.getIndex());
            } catch (Exception e) {
                String error = LogUtil.getError(
                        "Exception in explaining FlinkSQL:\n" + SqlUtil.addLineNumber(jobStatement.getStatement()), e);
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
        } else {
            String error =
                    "Only one pipeline job is explained. The statement has be skipped: " + jobStatement.getStatement();
            log.error(error);
            resultBuilder
                    .parseTrue(false)
                    .error(error)
                    .explainTrue(false)
                    .type(jobStatement.getSqlType().getType())
                    .sql(jobStatement.getStatement())
                    .explainTime(LocalDateTime.now())
                    .index(jobStatement.getIndex());
            return resultBuilder.build();
        }
    }

    @Override
    public StreamGraph getStreamGraph(JobStatement jobStatement) {
        statements.add(jobStatement);
        // pipeline job execute to generate stream graph.
        jobManager.getExecutor().executeSql(jobStatement.getStatement());
        if (statements.size() == 1) {
            return jobManager.getExecutor().getStreamGraph();
        } else {
            throw new DinkyException(
                    "Only one pipeline job is explained. The statement has be skipped: " + jobStatement.getStatement());
        }
    }

    @Override
    public JobPlanInfo getJobPlanInfo(JobStatement jobStatement) {
        statements.add(jobStatement);
        // pipeline job execute to generate stream graph.
        jobManager.getExecutor().executeSql(jobStatement.getStatement());
        if (statements.size() == 1) {
            return jobManager.getExecutor().getJobPlanInfo();
        } else {
            throw new DinkyException(
                    "Only one pipeline job is explained. The statement has be skipped: " + jobStatement.getStatement());
        }
    }

    private void processWithGateway() throws Exception {
        Executor executor = jobManager.getExecutor();
        JobConfig config = jobManager.getConfig();
        Job job = jobManager.getJob();
        config.addGatewayConfig(executor.getSetConfig());
        config.addGatewayConfig(executor.getCustomTableEnvironment().getConfig().getConfiguration());
        GatewayResult gatewayResult = null;
        if (jobManager.getRunMode().isApplicationMode()) {
            config.getGatewayConfig().setSql(jobManager.getJobStatementPlan().getStatements());
            gatewayResult = Gateway.build(config.getGatewayConfig())
                    .submitJar(executor.getDinkyClassLoader().getUdfPathContextHolder());
        } else {
            StreamGraph streamGraph = executor.getStreamGraph();
            streamGraph.setJobName(config.getJobName());
            JobGraph jobGraph = streamGraph.getJobGraph();
            if (Asserts.isNotNullString(config.getSavePointPath())) {
                jobGraph.setSavepointRestoreSettings(SavepointRestoreSettings.forPath(config.getSavePointPath(), true));
            }
            gatewayResult = Gateway.build(config.getGatewayConfig()).submitJobGraph(jobGraph);
        }
        job.setResult(InsertResult.success(gatewayResult.getId()));
        job.setJobId(gatewayResult.getId());
        job.setJids(gatewayResult.getJids());
        job.setJobManagerAddress(URLUtils.formatAddress(gatewayResult.getWebURL()));
        if (gatewayResult.isSuccess()) {
            job.setStatus(Job.JobStatus.SUCCESS);
        } else {
            job.setStatus(Job.JobStatus.FAILED);
            job.setError(gatewayResult.getError());
        }
    }

    private void processWithoutGateway() throws Exception {
        Executor executor = jobManager.getExecutor();
        JobConfig config = jobManager.getConfig();
        Job job = jobManager.getJob();
        JobClient jobClient = executor.executeAsync(config.getJobName());
        if (Asserts.isNotNull(jobClient)) {
            job.setJobId(jobClient.getJobID().toHexString());
            job.setJids(new ArrayList<String>() {
                {
                    add(job.getJobId());
                }
            });
            final List<Row> rowList = new ArrayList<>();
            tableResult.getResolvedSchema().getColumns().forEach(column -> rowList.add(Row.of(-1)));
            tableResult = CustomTableResultImpl.builder()
                    .resultKind(tableResult.getResultKind())
                    .schema(tableResult.getResolvedSchema())
                    .data(rowList)
                    .jobClient(jobClient)
                    .build();
        }
        if (config.isUseResult()) {
            IResult result = ResultBuilder.build(
                            SqlType.EXECUTE,
                            job.getId().toString(),
                            config.getMaxRowNum(),
                            config.isUseChangeLog(),
                            config.isUseAutoCancel(),
                            executor.getTimeZone(),
                            jobManager.getConfig().isMockSinkFunction())
                    .getResultWithPersistence(tableResult, jobManager.getHandler());
            job.setResult(result);
        }
    }
}
