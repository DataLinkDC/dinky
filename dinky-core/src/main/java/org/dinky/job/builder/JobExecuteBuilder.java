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
import org.dinky.data.job.SqlType;
import org.dinky.data.result.IResult;
import org.dinky.data.result.InsertResult;
import org.dinky.data.result.ResultBuilder;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.gateway.Gateway;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.job.Job;
import org.dinky.job.JobBuilder;
import org.dinky.job.JobManager;
import org.dinky.job.StatementParam;
import org.dinky.trans.dml.ExecuteJarOperation;
import org.dinky.trans.parse.ExecuteJarParseStrategy;
import org.dinky.utils.FlinkStreamEnvironmentUtil;
import org.dinky.utils.SqlUtil;
import org.dinky.utils.URLUtils;

import org.apache.flink.api.dag.Pipeline;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.streaming.api.graph.StreamGraph;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.text.StrFormatter;
import lombok.extern.slf4j.Slf4j;

/**
 * JobExecuteBuilder
 *
 */
@Slf4j
public class JobExecuteBuilder extends JobBuilder {

    public JobExecuteBuilder(JobManager jobManager) {
        super(jobManager);
    }

    public static JobExecuteBuilder build(JobManager jobManager) {
        return new JobExecuteBuilder(jobManager);
    }

    @Override
    public void run() throws Exception {
        if (!jobParam.getExecute().isEmpty()) {
            if (useGateway) {
                for (StatementParam item : jobParam.getExecute()) {
                    executor.executeSql(item.getValue());
                    if (!useStatementSet) {
                        break;
                    }
                }
                GatewayResult gatewayResult = null;
                config.addGatewayConfig(executor.getSetConfig());
                config.addGatewayConfig(
                        executor.getCustomTableEnvironment().getConfig().getConfiguration());
                config.getGatewayConfig().setSql(jobParam.getParsedSql());

                if (runMode.isApplicationMode()) {
                    gatewayResult = Gateway.build(config.getGatewayConfig())
                            .submitJar(executor.getDinkyClassLoader().getUdfPathContextHolder());
                } else {
                    StreamGraph streamGraph = executor.getStreamGraph();
                    streamGraph.setJobName(config.getJobName());
                    JobGraph jobGraph = streamGraph.getJobGraph();
                    if (Asserts.isNotNullString(config.getSavePointPath())) {
                        jobGraph.setSavepointRestoreSettings(
                                SavepointRestoreSettings.forPath(config.getSavePointPath(), true));
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
            } else {
                for (StatementParam item : jobParam.getExecute()) {
                    executor.executeSql(item.getValue());
                    if (!useStatementSet) {
                        break;
                    }
                }
                JobClient jobClient = executor.executeAsync(config.getJobName());
                if (Asserts.isNotNull(jobClient)) {
                    job.setJobId(jobClient.getJobID().toHexString());
                    job.setJids(new ArrayList<String>() {

                        {
                            add(job.getJobId());
                        }
                    });
                }
                if (config.isUseResult()) {
                    IResult result = ResultBuilder.build(
                                    SqlType.EXECUTE,
                                    job.getId().toString(),
                                    config.getMaxRowNum(),
                                    config.isUseChangeLog(),
                                    config.isUseAutoCancel(),
                                    executor.getTimeZone())
                            .getResult(null);
                    job.setResult(result);
                }
            }
        }
    }

    @Override
    public List<SqlExplainResult> explain() {
        List<SqlExplainResult> sqlExplainResults = new ArrayList<>();
        if (Asserts.isNullCollection(jobParam.getExecute())) {
            return sqlExplainResults;
        }
        for (StatementParam item : jobParam.getExecute()) {
            SqlExplainResult.Builder resultBuilder = SqlExplainResult.Builder.newBuilder();
            try {
                SqlExplainResult sqlExplainResult = executor.explainSqlRecord(item.getValue());
                if (!sqlExplainResult.isInvalid()) {
                    sqlExplainResult = new SqlExplainResult();
                } else if (ExecuteJarParseStrategy.INSTANCE.match(item.getValue())) {
                    List<URL> allFileByAdd = jobManager.getAllFileSet();
                    Pipeline pipeline = new ExecuteJarOperation(item.getValue())
                            .explain(executor.getCustomTableEnvironment(), allFileByAdd);
                    sqlExplainResult.setExplain(FlinkStreamEnvironmentUtil.getStreamingPlanAsJSON(pipeline));
                } else {
                    executor.executeSql(item.getValue());
                }
                resultBuilder = SqlExplainResult.newBuilder(sqlExplainResult);
                resultBuilder.type(item.getType().getType()).parseTrue(true);
            } catch (Exception e) {
                String error = StrFormatter.format(
                        "Exception in executing FlinkSQL:\n{}\n{}",
                        SqlUtil.addLineNumber(item.getValue()),
                        e.getMessage());
                resultBuilder
                        .type(item.getType().getType())
                        .error(error)
                        .explainTrue(false)
                        .explainTime(LocalDateTime.now())
                        .sql(item.getValue());
                sqlExplainResults.add(resultBuilder.build());
                log.error(error);
                break;
            }
            resultBuilder
                    .type(item.getType().getType())
                    .explainTrue(true)
                    .explainTime(LocalDateTime.now())
                    .sql(item.getValue());
            sqlExplainResults.add(resultBuilder.build());
        }
        return sqlExplainResults;
    }

    @Override
    public StreamGraph getStreamGraph() {
        return executor.getStreamGraphFromCustomStatements(jobParam.getExecuteStatement());
    }

    @Override
    public JobPlanInfo getJobPlanInfo() {
        return executor.getJobPlanInfo();
    }
}
