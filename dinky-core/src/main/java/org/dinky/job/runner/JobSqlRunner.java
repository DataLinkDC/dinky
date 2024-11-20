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
import org.dinky.constant.FlinkSQLConstant;
import org.dinky.data.enums.GatewayType;
import org.dinky.data.exception.DinkyException;
import org.dinky.data.job.JobStatement;
import org.dinky.data.job.SqlType;
import org.dinky.data.result.IResult;
import org.dinky.data.result.InsertResult;
import org.dinky.data.result.ResultBuilder;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.executor.Executor;
import org.dinky.gateway.Gateway;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.interceptor.FlinkInterceptor;
import org.dinky.interceptor.FlinkInterceptorResult;
import org.dinky.job.Job;
import org.dinky.job.JobConfig;
import org.dinky.job.JobManager;
import org.dinky.utils.SqlUtil;
import org.dinky.utils.URLUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.TableResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import cn.hutool.core.text.StrFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobSqlRunner extends AbstractJobRunner {

    private List<JobStatement> statements;

    public JobSqlRunner(JobManager jobManager) {
        this.jobManager = jobManager;
        this.statements = new ArrayList<>();
    }

    @Override
    public void run(JobStatement jobStatement) throws Exception {
        statements.add(jobStatement);
        if (jobStatement.isFinalExecutableStatement()) {
            if (inferStatementSet()) {
                handleStatementSet();
            } else {
                handleNonStatementSet();
            }
        }
    }

    @Override
    public SqlExplainResult explain(JobStatement jobStatement) {
        SqlExplainResult.Builder resultBuilder = SqlExplainResult.Builder.newBuilder();
        // show and desc
        if (!jobStatement.getSqlType().isPipeline()) {
            try {
                resultBuilder = SqlExplainResult.newBuilder(
                        jobManager.getExecutor().explainSqlRecord(jobStatement.getStatement()));
                resultBuilder.parseTrue(true).explainTrue(true);
            } catch (Exception e) {
                String error = StrFormatter.format(
                        "Exception in explaining FlinkSQL:\n{}\n{}",
                        SqlUtil.addLineNumber(jobStatement.getStatement()),
                        e.getMessage());
                resultBuilder
                        .type(jobStatement.getSqlType().getType())
                        .index(jobStatement.getIndex())
                        .error(error)
                        .parseTrue(false)
                        .explainTrue(false)
                        .explainTime(LocalDateTime.now());
                log.error(error);
                return resultBuilder.build();
            }
            resultBuilder
                    .index(jobStatement.getIndex())
                    .type(jobStatement.getSqlType().getType())
                    .explainTime(LocalDateTime.now())
                    .sql(jobStatement.getStatement());
            return resultBuilder.build();
        }
        statements.add(jobStatement);
        if (!jobStatement.isFinalExecutableStatement()) {
            return resultBuilder.invalid().build();
        }
        if (inferStatementSet()) {
            List<String> inserts =
                    statements.stream().map(JobStatement::getStatement).collect(Collectors.toList());
            if (!inserts.isEmpty()) {
                String sqlSet = StringUtils.join(inserts, FlinkSQLConstant.SEPARATOR);
                try {
                    resultBuilder =
                            SqlExplainResult.newBuilder(jobManager.getExecutor().explainStatementSet(statements));
                } catch (Exception e) {
                    String error = StrFormatter.format(
                            "Exception in explaining FlinkSQL:\n{}\n{}",
                            SqlUtil.addLineNumber(jobStatement.getStatement()),
                            e.getMessage());
                    resultBuilder
                            .sql(sqlSet)
                            .index(jobStatement.getIndex())
                            .type(SqlType.INSERT.getType())
                            .error(error)
                            .parseTrue(false)
                            .explainTrue(false)
                            .explainTime(LocalDateTime.now());
                    log.error(error);
                    return resultBuilder.build();
                }
                resultBuilder
                        .type(SqlType.INSERT.getType())
                        .index(jobStatement.getIndex())
                        .explainTime(LocalDateTime.now())
                        .sql(sqlSet);
                return resultBuilder.build();
            }
            return resultBuilder.invalid().build();
        } else {
            try {
                resultBuilder = SqlExplainResult.newBuilder(
                        jobManager.getExecutor().explainSqlRecord(jobStatement.getStatement()));
                resultBuilder.parseTrue(true).explainTrue(true);
            } catch (Exception e) {
                String error = StrFormatter.format(
                        "Exception in explaining FlinkSQL:\n{}\n{}",
                        SqlUtil.addLineNumber(jobStatement.getStatement()),
                        e.getMessage());
                resultBuilder
                        .type(jobStatement.getSqlType().getType())
                        .index(jobStatement.getIndex())
                        .error(error)
                        .parseTrue(false)
                        .explainTrue(false)
                        .explainTime(LocalDateTime.now());
                log.error(error);
                return resultBuilder.build();
            }
            resultBuilder
                    .type(jobStatement.getSqlType().getType())
                    .index(jobStatement.getIndex())
                    .explainTime(LocalDateTime.now())
                    .sql(jobStatement.getStatement());
            return resultBuilder.build();
        }
    }

    @Override
    public StreamGraph getStreamGraph(JobStatement jobStatement) {
        statements.add(jobStatement);
        if (!jobStatement.isFinalExecutableStatement()) {
            return null;
        }
        if (!statements.isEmpty()) {
            return jobManager.getExecutor().getStreamGraphFromStatement(statements);
        }
        throw new DinkyException("None jobs in statement.");
    }

    @Override
    public JobPlanInfo getJobPlanInfo(JobStatement jobStatement) {
        statements.add(jobStatement);
        if (!jobStatement.isFinalExecutableStatement()) {
            return null;
        }
        if (!statements.isEmpty()) {
            return jobManager.getExecutor().getJobPlanInfoFromStatements(statements);
        }
        throw new DinkyException("None jobs in statement.");
    }

    private boolean inferStatementSet() {
        boolean hasInsert = false;
        for (JobStatement item : statements) {
            if (item.getSqlType().equals(SqlType.INSERT)) {
                hasInsert = true;
                break;
            }
        }
        return hasInsert;
    }

    private void handleStatementSet() throws Exception {
        if (jobManager.isUseGateway()) {
            processWithGateway();
            return;
        }
        processWithoutGateway();
    }

    private void handleNonStatementSet() throws Exception {
        if (jobManager.isUseGateway()) {
            processSingleInsertWithGateway();
            return;
        }
        processFirstStatement();
    }

    private void processWithGateway() {
        List<String> inserts =
                statements.stream().map(JobStatement::getStatement).collect(Collectors.toList());
        jobManager.setCurrentSql(StringUtils.join(inserts, FlinkSQLConstant.SEPARATOR));
        GatewayResult gatewayResult = submitByGateway(statements);
        setJobResultFromGatewayResult(gatewayResult);
    }

    private void processWithoutGateway() {
        if (!statements.isEmpty()) {
            List<String> inserts =
                    statements.stream().map(JobStatement::getStatement).collect(Collectors.toList());
            jobManager.setCurrentSql(StringUtils.join(inserts, FlinkSQLConstant.SEPARATOR));
            TableResult tableResult = jobManager.getExecutor().executeStatements(statements);
            updateJobWithTableResult(tableResult);
        }
    }

    private void processSingleInsertWithGateway() {
        List<JobStatement> singleInsert = Collections.singletonList(statements.get(0));
        jobManager.getJob().setPipeline(statements.get(0).getSqlType().isPipeline());
        jobManager.setCurrentSql(statements.get(0).getStatement());
        GatewayResult gatewayResult = submitByGateway(singleInsert);
        setJobResultFromGatewayResult(gatewayResult);
    }

    private void processFirstStatement() throws Exception {
        if (statements.isEmpty()) {
            return;
        }
        // Only process the first statement when not using statement set
        JobStatement item = statements.get(0);
        jobManager.getJob().setPipeline(item.getSqlType().isPipeline());
        jobManager.setCurrentSql(item.getStatement());
        processSingleStatement(item);
    }

    private void processSingleStatement(JobStatement item) {
        FlinkInterceptorResult flinkInterceptorResult =
                FlinkInterceptor.build(jobManager.getExecutor(), item.getStatement());
        if (Asserts.isNotNull(flinkInterceptorResult.getTableResult())) {
            updateJobWithTableResult(flinkInterceptorResult.getTableResult(), item.getSqlType());
        } else if (!flinkInterceptorResult.isNoExecute()) {
            TableResult tableResult = jobManager.getExecutor().executeSql(item.getStatement());
            updateJobWithTableResult(tableResult, item.getSqlType());
        }
    }

    private void setJobResultFromGatewayResult(GatewayResult gatewayResult) {
        jobManager.getJob().setResult(InsertResult.success(gatewayResult.getId()));
        jobManager.getJob().setJobId(gatewayResult.getId());
        jobManager.getJob().setJids(gatewayResult.getJids());
        jobManager.getJob().setJobManagerAddress(URLUtils.formatAddress(gatewayResult.getWebURL()));
        jobManager.getJob().setStatus(gatewayResult.isSuccess() ? Job.JobStatus.SUCCESS : Job.JobStatus.FAILED);
        if (!gatewayResult.isSuccess()) {
            jobManager.getJob().setError(gatewayResult.getError());
        }
    }

    private void updateJobWithTableResult(TableResult tableResult) {
        updateJobWithTableResult(tableResult, SqlType.INSERT);
    }

    private void updateJobWithTableResult(TableResult tableResult, SqlType sqlType) {
        if (tableResult.getJobClient().isPresent()) {
            jobManager
                    .getJob()
                    .setJobId(tableResult.getJobClient().get().getJobID().toHexString());
            jobManager
                    .getJob()
                    .setJids(Collections.singletonList(jobManager.getJob().getJobId()));
        } else if (!sqlType.getCategory().getHasJobClient()) {
            jobManager.getJob().setJobId(UUID.randomUUID().toString().replace("-", ""));
            jobManager
                    .getJob()
                    .setJids(Collections.singletonList(jobManager.getJob().getJobId()));
        }

        if (jobManager.getConfig().isUseResult()) {
            IResult result = ResultBuilder.build(
                            sqlType,
                            jobManager.getJob().getId().toString(),
                            jobManager.getConfig().getMaxRowNum(),
                            jobManager.getConfig().isUseChangeLog(),
                            jobManager.getConfig().isUseAutoCancel(),
                            jobManager.getExecutor().getTimeZone(),
                            jobManager.getConfig().isMockSinkFunction())
                    .getResultWithPersistence(tableResult, jobManager.getHandler());
            jobManager.getJob().setResult(result);
        }
    }

    private GatewayResult submitByGateway(List<JobStatement> inserts) {
        JobConfig config = jobManager.getConfig();
        GatewayType runMode = jobManager.getRunMode();
        Executor executor = jobManager.getExecutor();

        GatewayResult gatewayResult = null;

        // Use gateway need to build gateway config, include flink configuration.
        config.addGatewayConfig(executor.getCustomTableEnvironment().getConfig().getConfiguration());
        if (runMode.isApplicationMode()) {
            // Application mode need to submit dinky-app.jar that in the hdfs or image.
            config.getGatewayConfig().setSql(jobManager.getJobStatementPlan().getStatements());
            gatewayResult = Gateway.build(config.getGatewayConfig())
                    .submitJar(executor.getDinkyClassLoader().getUdfPathContextHolder());
        } else {
            JobGraph jobGraph = executor.getJobGraphFromInserts(inserts);
            // Perjob mode need to set savepoint restore path, when recovery from savepoint.
            if (Asserts.isNotNullString(config.getSavePointPath())) {
                jobGraph.setSavepointRestoreSettings(SavepointRestoreSettings.forPath(config.getSavePointPath(), true));
            }
            // Perjob mode need to submit job graph.
            gatewayResult = Gateway.build(config.getGatewayConfig()).submitJobGraph(jobGraph);
        }
        return gatewayResult;
    }
}
