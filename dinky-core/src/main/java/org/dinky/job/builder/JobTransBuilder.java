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

package org.dinky.job.builder;

import org.dinky.assertion.Asserts;
import org.dinky.constant.FlinkSQLConstant;
import org.dinky.data.enums.GatewayType;
import org.dinky.data.result.IResult;
import org.dinky.data.result.InsertResult;
import org.dinky.data.result.ResultBuilder;
import org.dinky.executor.Executor;
import org.dinky.gateway.Gateway;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.interceptor.FlinkInterceptor;
import org.dinky.interceptor.FlinkInterceptorResult;
import org.dinky.job.ExecuteSqlException;
import org.dinky.job.Job;
import org.dinky.job.JobBuilder;
import org.dinky.job.JobConfig;
import org.dinky.job.JobManagerHandler;
import org.dinky.job.JobParam;
import org.dinky.job.StatementParam;
import org.dinky.parser.SqlType;
import org.dinky.utils.URLUtils;

import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.table.api.TableResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JobTransBuilder
 */
public class JobTransBuilder implements JobBuilder {

    private String currentSql;
    private final JobParam jobParam;
    private final boolean useStatementSet;
    private final boolean useGateway;
    private final JobConfig config;
    private final Executor executor;
    private final GatewayType runMode;
    private final Job job;
    private JobManagerHandler jobManagerHandler;

    public JobTransBuilder(JobManagerHandler jobManagerHandler) {
        this(
                jobManagerHandler.getJobParam(),
                jobManagerHandler.isUseStatementSet(),
                jobManagerHandler.isUseGateway(),
                jobManagerHandler.getConfig(),
                jobManagerHandler.getExecutor(),
                jobManagerHandler.getRunMode(),
                jobManagerHandler.getJob());
        this.jobManagerHandler = jobManagerHandler;
    }

    public JobTransBuilder(
            JobParam jobParam,
            boolean useStatementSet,
            boolean useGateway,
            JobConfig config,
            Executor executor,
            GatewayType runMode,
            Job job) {
        this.jobParam = jobParam;
        this.useStatementSet = useStatementSet;
        this.useGateway = useGateway;
        this.config = config;
        this.executor = executor;
        this.runMode = runMode;
        this.job = job;
    }

    public static JobTransBuilder build(JobManagerHandler jobManager) {
        return new JobTransBuilder(jobManager);
    }

    @Override
    public void run() throws Exception {
        try {
            if (jobParam.getTrans().isEmpty()) {
                return;
            }

            if (useStatementSet) {
                handleStatementSet();
                return;
            }

            handleNonStatementSet();
        } catch (Exception ex) {
            throw new ExecuteSqlException(currentSql, ex);
        }
    }

    private void handleStatementSet() {
        List<String> inserts = collectInserts();

        if (useGateway) {
            processWithGateway(inserts);
            return;
        }
        processWithoutGateway(inserts);
    }

    private void handleNonStatementSet() {
        if (useGateway) {
            processSingleInsertWithGateway();
            return;
        }
        processFirstStatement();
    }

    private List<String> collectInserts() {
        List<String> inserts = new ArrayList<>();
        List<StatementParam> statementParams = useStatementSet
                ? jobParam.getTrans()
                : Collections.singletonList(jobParam.getTrans().get(0));
        for (StatementParam item : statementParams) {

            inserts.add(item.getValue());
        }
        return inserts;
    }

    private void processWithGateway(List<String> inserts) {
        currentSql = String.join(FlinkSQLConstant.SEPARATOR, inserts);
        GatewayResult gatewayResult = submitByGateway(inserts);
        setJobResultFromGatewayResult(gatewayResult);
    }

    private void processWithoutGateway(List<String> inserts) {
        if (!inserts.isEmpty()) {
            currentSql = String.join(FlinkSQLConstant.SEPARATOR, inserts);
            TableResult tableResult = executor.executeStatementSet(inserts);
            updateJobWithTableResult(tableResult);
        }
    }

    private void processSingleInsertWithGateway() {
        List<String> singleInsert = collectInserts();
        processWithGateway(singleInsert);
    }

    private void processFirstStatement() {
        if (jobParam.getTrans().isEmpty()) {
            return;
        }
        // Only process the first statement when not using statement set
        StatementParam item = jobParam.getTrans().get(0);
        currentSql = item.getValue();
        processSingleStatement(item);
    }

    private void processSingleStatement(StatementParam item) {
        FlinkInterceptorResult flinkInterceptorResult = FlinkInterceptor.build(executor, item.getValue());
        if (Asserts.isNotNull(flinkInterceptorResult.getTableResult())) {
            updateJobWithTableResult(flinkInterceptorResult.getTableResult(), item.getType());
        } else if (!flinkInterceptorResult.isNoExecute()) {
            TableResult tableResult = executor.executeSql(item.getValue());
            updateJobWithTableResult(tableResult, item.getType());
        }
    }

    private void setJobResultFromGatewayResult(GatewayResult gatewayResult) {
        job.setResult(InsertResult.success(gatewayResult.getId()));
        job.setJobId(gatewayResult.getId());
        job.setJids(gatewayResult.getJids());
        job.setJobManagerAddress(URLUtils.formatAddress(gatewayResult.getWebURL()));
        job.setStatus(gatewayResult.isSuccess() ? Job.JobStatus.SUCCESS : Job.JobStatus.FAILED);
        if (!gatewayResult.isSuccess()) {
            job.setError(gatewayResult.getError());
        }
    }

    private void updateJobWithTableResult(TableResult tableResult) {
        updateJobWithTableResult(tableResult, SqlType.INSERT);
    }

    private void updateJobWithTableResult(TableResult tableResult, SqlType sqlType) {
        if (tableResult.getJobClient().isPresent()) {
            job.setJobId(tableResult.getJobClient().get().getJobID().toHexString());
            job.setJids(Collections.singletonList(job.getJobId()));
        }

        if (config.isUseResult()) {
            IResult result = ResultBuilder.build(
                            sqlType,
                            job.getId().toString(),
                            config.getMaxRowNum(),
                            config.isUseChangeLog(),
                            config.isUseAutoCancel(),
                            executor.getTimeZone())
                    .getResult(tableResult);
            // TODO: 2024/7/15 persist result should execute at dinky server by network.
            //                    .getResultWithPersistence(tableResult, jobManagerHandler.getHandler());
            job.setResult(result);
        }
    }

    private GatewayResult submitByGateway(List<String> inserts) {
        GatewayResult gatewayResult = null;

        // Use gateway need to build gateway config, include flink configuration.
        config.addGatewayConfig(executor.getCustomTableEnvironment()
                .getConfig()
                .getConfiguration()
                .toMap());
        config.getGatewayConfig().setSql(jobParam.getParsedSql());
        if (runMode.isApplicationMode()) {
            // Application mode need to submit dinky-app.jar that in the hdfs or image.
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
