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
import org.dinky.data.result.IResult;
import org.dinky.data.result.InsertResult;
import org.dinky.data.result.ResultBuilder;
import org.dinky.executor.Executor;
import org.dinky.gateway.Gateway;
import org.dinky.gateway.enums.GatewayType;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.interceptor.FlinkInterceptor;
import org.dinky.interceptor.FlinkInterceptorResult;
import org.dinky.job.Job;
import org.dinky.job.JobBuilder;
import org.dinky.job.JobConfig;
import org.dinky.job.JobManager;
import org.dinky.job.StatementParam;
import org.dinky.parser.SqlType;
import org.dinky.utils.URLUtils;

import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.table.api.TableResult;

import java.util.ArrayList;
import java.util.List;

/**
 * JobTransBuilder
 *
 */
public class JobTransBuilder extends JobBuilder {

    public JobTransBuilder(JobManager jobManager) {
        super(jobManager);
    }

    public static JobTransBuilder build(JobManager jobManager) {
        return new JobTransBuilder(jobManager);
    }

    @Override
    public void run() throws Exception {
        if (!jobParam.getTrans().isEmpty()) {
            // Use statement set or gateway only submit inserts.
            if (useStatementSet && useGateway) {
                List<String> inserts = new ArrayList<>();
                for (StatementParam item : jobParam.getTrans()) {
                    inserts.add(item.getValue());
                }

                // Use statement set need to merge all insert sql into a sql.
                jobManager.setCurrentSql(String.join(sqlSeparator, inserts));
                GatewayResult gatewayResult = submitByGateway(inserts);
                // Use statement set only has one jid.
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
            } else if (useStatementSet && !useGateway) {
                List<String> inserts = new ArrayList<>();
                for (StatementParam item : jobParam.getTrans()) {
                    if (item.getType().equals(SqlType.INSERT)) {
                        inserts.add(item.getValue());
                    } else if (item.getType().equals(SqlType.CTAS)) {
                        executor.getCustomTableEnvironment()
                                .getParser()
                                .parse(item.getValue())
                                .forEach(x -> {
                                    executor.getCustomTableEnvironment().executeCTAS(x);
                                });
                    }
                }
                if (!inserts.isEmpty()) {
                    jobManager.setCurrentSql(String.join(sqlSeparator, inserts));
                    // Remote mode can get the table result.
                    TableResult tableResult = executor.executeStatementSet(inserts);
                    if (tableResult.getJobClient().isPresent()) {
                        job.setJobId(tableResult.getJobClient().get().getJobID().toHexString());
                        job.setJids(new ArrayList<String>() {

                            {
                                add(job.getJobId());
                            }
                        });
                    }
                    if (config.isUseResult()) {
                        // Build insert result.
                        IResult result = ResultBuilder.build(
                                        SqlType.INSERT,
                                        job.getId().toString(),
                                        config.getMaxRowNum(),
                                        config.isUseChangeLog(),
                                        config.isUseAutoCancel(),
                                        executor.getTimeZone())
                                .getResult(tableResult);
                        job.setResult(result);
                    }
                }
            } else if (!useStatementSet && useGateway) {
                List<String> inserts = new ArrayList<>();
                for (StatementParam item : jobParam.getTrans()) {
                    inserts.add(item.getValue());
                    // Only can submit the first of insert sql, when not use statement set.
                    break;
                }
                jobManager.setCurrentSql(String.join(sqlSeparator, inserts));
                GatewayResult gatewayResult = submitByGateway(inserts);
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
                for (StatementParam item : jobParam.getTrans()) {
                    jobManager.setCurrentSql(item.getValue());
                    FlinkInterceptorResult flinkInterceptorResult = FlinkInterceptor.build(executor, item.getValue());
                    if (Asserts.isNotNull(flinkInterceptorResult.getTableResult())) {
                        if (config.isUseResult()) {
                            IResult result = ResultBuilder.build(
                                            item.getType(),
                                            job.getId().toString(),
                                            config.getMaxRowNum(),
                                            config.isUseChangeLog(),
                                            config.isUseAutoCancel(),
                                            executor.getTimeZone())
                                    .getResult(flinkInterceptorResult.getTableResult());
                            job.setResult(result);
                        }
                    } else {
                        if (!flinkInterceptorResult.isNoExecute()) {
                            TableResult tableResult = executor.executeSql(item.getValue());
                            if (tableResult.getJobClient().isPresent()) {
                                job.setJobId(tableResult
                                        .getJobClient()
                                        .get()
                                        .getJobID()
                                        .toHexString());
                                job.setJids(new ArrayList<String>() {

                                    {
                                        add(job.getJobId());
                                    }
                                });
                            }
                            if (config.isUseResult()) {
                                IResult result = ResultBuilder.build(
                                                item.getType(),
                                                job.getId().toString(),
                                                config.getMaxRowNum(),
                                                config.isUseChangeLog(),
                                                config.isUseAutoCancel(),
                                                executor.getTimeZone())
                                        .getResult(tableResult);
                                job.setResult(result);
                            }
                        }
                    }
                    // Only can submit the first of insert sql, when not use statement set.
                    break;
                }
            }
        }
    }

    private GatewayResult submitByGateway(List<String> inserts) {
        JobConfig config = jobManager.getConfig();
        GatewayType runMode = jobManager.getRunMode();
        Executor executor = jobManager.getExecutor();

        GatewayResult gatewayResult = null;

        // Use gateway need to build gateway config, include flink configeration.
        config.addGatewayConfig(executor.getSetConfig());

        if (runMode.isApplicationMode()) {
            // Application mode need to submit dinky-app.jar that in the hdfs or image.
            gatewayResult = Gateway.build(config.getGatewayConfig()).submitJar();
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
