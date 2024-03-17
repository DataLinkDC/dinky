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
import org.dinky.data.enums.GatewayType;
import org.dinky.data.result.IResult;
import org.dinky.data.result.InsertResult;
import org.dinky.data.result.ResultBuilder;
import org.dinky.executor.Executor;
import org.dinky.gateway.Gateway;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.job.Job;
import org.dinky.job.JobBuilder;
import org.dinky.job.JobConfig;
import org.dinky.job.JobManagerHandler;
import org.dinky.job.JobParam;
import org.dinky.job.StatementParam;
import org.dinky.parser.SqlType;
import org.dinky.utils.URLUtils;

import org.apache.flink.core.execution.JobClient;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.streaming.api.graph.StreamGraph;

import java.util.Collections;

/**
 * JobExecuteBuilder
 */
public class JobExecuteBuilder implements JobBuilder {

    private final JobParam jobParam;
    private final boolean useGateway;
    private final Executor executor;
    private final boolean useStatementSet;
    private final JobConfig config;
    private final GatewayType runMode;
    private final Job job;

    public JobExecuteBuilder(
            JobParam jobParam,
            boolean useGateway,
            Executor executor,
            boolean useStatementSet,
            JobConfig config,
            GatewayType runMode,
            Job job) {
        this.jobParam = jobParam;
        this.useGateway = useGateway;
        this.executor = executor;
        this.useStatementSet = useStatementSet;
        this.config = config;
        this.runMode = runMode;
        this.job = job;
    }

    public static JobExecuteBuilder build(JobManagerHandler jobManager) {
        return new JobExecuteBuilder(
                jobManager.getJobParam(),
                jobManager.isUseGateway(),
                jobManager.getExecutor(),
                jobManager.isUseStatementSet(),
                jobManager.getConfig(),
                jobManager.getRunMode(),
                jobManager.getJob());
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
                    job.setJids(Collections.singletonList(job.getJobId()));
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
}
