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

package org.dinky.job;

import org.dinky.api.FlinkAPI;
import org.dinky.assertion.Asserts;
import org.dinky.context.CustomTableEnvironmentContext;
import org.dinky.context.RowLevelPermissionsContext;
import org.dinky.data.annotations.ProcessStep;
import org.dinky.data.enums.GatewayType;
import org.dinky.data.enums.ProcessStepType;
import org.dinky.data.exception.BusException;
import org.dinky.data.result.ErrorResult;
import org.dinky.data.result.ExplainResult;
import org.dinky.data.result.IResult;
import org.dinky.data.result.InsertResult;
import org.dinky.data.result.ResultBuilder;
import org.dinky.executor.Executor;
import org.dinky.executor.ExecutorConfig;
import org.dinky.executor.ExecutorFactory;
import org.dinky.explainer.Explainer;
import org.dinky.gateway.Gateway;
import org.dinky.gateway.config.FlinkConfig;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.enums.ActionType;
import org.dinky.gateway.enums.SavePointType;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.job.builder.JobDDLBuilder;
import org.dinky.job.builder.JobExecuteBuilder;
import org.dinky.job.builder.JobJarStreamGraphBuilder;
import org.dinky.job.builder.JobTransBuilder;
import org.dinky.job.builder.JobUDFBuilder;
import org.dinky.parser.SqlType;
import org.dinky.trans.Operations;
import org.dinky.trans.parse.AddFileSqlParseStrategy;
import org.dinky.trans.parse.AddJarSqlParseStrategy;
import org.dinky.utils.DinkyClassLoaderUtil;
import org.dinky.utils.JsonUtils;
import org.dinky.utils.LogUtil;
import org.dinky.utils.SqlUtil;
import org.dinky.utils.URLUtils;

import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.runtime.jobgraph.jsonplan.JsonPlanGenerator;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.yarn.configuration.YarnConfigOptions;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.text.StrFormatter;

public class JobManagerHandler implements IJobManager {
    Logger log = LoggerFactory.getLogger(JobManagerHandler.class);
    private ExecutorConfig executorConfig;
    private JobConfig config;

    private Executor executor;
    private boolean useGateway = false;
    private boolean useStatementSet = false;
    private GatewayType runMode = GatewayType.LOCAL;

    private JobParam jobParam = null;

    private Job job;

    private JobManagerHandler(JobConfig config, boolean isPlanMode) {
        this.config = config;

        if (!isPlanMode) {
            runMode = GatewayType.get(config.getType());
            useGateway = GatewayType.isDeployCluster(config.getType());
        }

        useStatementSet = config.isStatementSet();
        executorConfig = config.createExecutorSetting();
        executorConfig.setPlan(isPlanMode);
        executor = ExecutorFactory.buildExecutor(executorConfig);
    }

    @Override
    public void init(JobConfig config, boolean isPlanMode) {}

    public static JobManagerHandler build(JobConfig config, boolean isPlanMode) {
        return new JobManagerHandler(config, isPlanMode);
    }

    @Override
    public void prepare(String statement) {
        job = Job.build(runMode, config, executorConfig, statement, useGateway);
    }

    @Override
    public boolean close() {
        CustomTableEnvironmentContext.clear();
        RowLevelPermissionsContext.clear();
        try {
            executor.getDinkyClassLoader().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public ObjectNode getJarStreamGraphJson(String statement) {
        StreamGraph streamGraph = JobJarStreamGraphBuilder.build(this).getJarStreamGraph(statement);
        return JsonUtils.parseObject(JsonPlanGenerator.generatePlan(streamGraph.getJobGraph()));
    }

    @Override
    @ProcessStep(type = ProcessStepType.SUBMIT_EXECUTE)
    public JobResult executeJarSql(String statement) throws Exception {
        Objects.requireNonNull(job, "job is null, prepare() first");
        JobJarStreamGraphBuilder jobJarStreamGraphBuilder = JobJarStreamGraphBuilder.build(this);
        StreamGraph streamGraph = jobJarStreamGraphBuilder.getJarStreamGraph(statement);

        try {
            if (!useGateway) {
                JobClient jobClient = executor.getStreamExecutionEnvironment().executeAsync(streamGraph);
                if (Asserts.isNotNull(jobClient)) {
                    job.setJobId(jobClient.getJobID().toHexString());
                    job.setJids(Collections.singletonList(job.getJobId()));
                    job.setStatus(Job.JobStatus.SUCCESS);
                } else {
                    job.setStatus(Job.JobStatus.FAILED);
                }
            } else {
                GatewayResult gatewayResult;
                config.addGatewayConfig(executor.getCustomTableEnvironment()
                        .getConfig()
                        .getConfiguration()
                        .toMap());
                if (runMode.isApplicationMode()) {
                    gatewayResult =
                            Gateway.build(config.getGatewayConfig()).submitJar(executor.getUdfPathContextHolder());
                } else {
                    streamGraph.setJobName(config.getJobName());
                    JobGraph jobGraph = streamGraph.getJobGraph();
                    GatewayConfig gatewayConfig = config.getGatewayConfig();
                    List<String> uriList = jobJarStreamGraphBuilder.getUris(statement);
                    String[] jarPaths = uriList.stream()
                            .map(URLUtils::toFile)
                            .map(File::getAbsolutePath)
                            .toArray(String[]::new);
                    gatewayConfig.setJarPaths(jarPaths);
                    gatewayResult = Gateway.build(gatewayConfig).submitJobGraph(jobGraph);
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
                    log.error(gatewayResult.getError());
                }
            }
        } catch (Exception e) {
            String error =
                    LogUtil.getError("Exception in executing FlinkJarSQL:\n" + SqlUtil.addLineNumber(statement), e);
            job.setEndTime(LocalDateTime.now());
            job.setStatus(Job.JobStatus.FAILED);
            job.setError(error);
            throw new Exception(error, e);
        } finally {
            close();
        }
        return job.getJobResult();
    }

    @Override
    @ProcessStep(type = ProcessStepType.SUBMIT_EXECUTE)
    public JobResult executeSql(String statement) throws Exception {
        Objects.requireNonNull(job, "job is null, prepare() first");
        DinkyClassLoaderUtil.initClassLoader(config, executor.getDinkyClassLoader());
        jobParam =
                Explainer.build(executor, useStatementSet, this).pretreatStatements(SqlUtil.getStatements(statement));
        try {
            // step 1: init udf
            JobUDFBuilder.build(this).run();
            // step 2: execute ddl
            JobDDLBuilder.build(this).run();
            // step 3: execute insert/select/show/desc/CTAS...
            JobTransBuilder.build(this).run();
            // step 4: execute custom data stream task
            JobExecuteBuilder.build(this).run();
            // finished
            job.setEndTime(LocalDateTime.now());
            if (!job.isFailed()) {
                job.setStatus(Job.JobStatus.SUCCESS);
            }
        } catch (ExecuteSqlException e) {
            String error = StrFormatter.format(
                    "Exception in executing FlinkSQL:\n{}\n{}", SqlUtil.addLineNumber(e.getMessage()), e.getMessage());
            job.setEndTime(LocalDateTime.now());
            job.setStatus(Job.JobStatus.FAILED);
            job.setError(error);
            throw new Exception(error, e);
        } finally {
            close();
        }
        return job.getJobResult();
    }

    @Override
    public IResult executeDDL(String statement) {
        String[] statements = SqlUtil.getStatements(statement);
        try {
            IResult result = null;
            for (String item : statements) {
                String newStatement = executor.pretreatStatement(item);
                if (newStatement.trim().isEmpty()) {
                    continue;
                }
                SqlType operationType = Operations.getOperationType(newStatement);
                if (SqlType.INSERT == operationType || SqlType.SELECT == operationType) {
                    continue;
                }

                if (operationType.equals(SqlType.ADD) || operationType.equals(SqlType.ADD_JAR)) {
                    Set<File> allFilePath = AddJarSqlParseStrategy.getAllFilePath(item);
                    executor.getDinkyClassLoader().addURLs(allFilePath);
                } else if (operationType.equals(SqlType.ADD_FILE)) {
                    Set<File> allFilePath = AddFileSqlParseStrategy.getAllFilePath(item);
                    executor.getDinkyClassLoader().addURLs(allFilePath);
                }

                LocalDateTime startTime = LocalDateTime.now();
                TableResult tableResult = executor.executeSql(newStatement);
                result = ResultBuilder.build(
                                operationType, null, config.getMaxRowNum(), false, false, executor.getTimeZone())
                        .getResult(tableResult);
                result.setStartTime(startTime);
            }
            return result;
        } catch (Exception e) {
            log.error("executeDDL failed:", e);
        }
        return new ErrorResult();
    }

    @Override
    public ExplainResult explainSql(String statement) {
        return Explainer.build(executor, useStatementSet, this)
                .initialize(config, statement)
                .explainSql(statement);
    }

    @Override
    public ObjectNode getStreamGraph(String statement) {
        return Explainer.build(executor, useStatementSet, this)
                .initialize(config, statement)
                .getStreamGraph(statement);
    }

    @Override
    public String getJobPlanJson(String statement) {
        Explainer explainer = Explainer.build(executor, useStatementSet, this).initialize(config, statement);
        JobParam jobParam = explainer.pretreatStatements(SqlUtil.getStatements(statement));
        return executor.getJobPlanJson(jobParam);
    }

    @Override
    public boolean cancelNormal(String jobId) {
        try {
            return FlinkAPI.build(config.getAddress()).stop(jobId);
        } catch (Exception e) {
            log.error("stop flink job failed:", e);
            throw new BusException(e.getMessage());
        }
    }

    @Override
    public SavePointResult savepoint(String jobId, SavePointType savePointType, String savePoint, boolean isUseRestAPI) {
        if (useGateway && !isUseRestAPI) {
            config.getGatewayConfig()
                    .setFlinkConfig(
                            FlinkConfig.build(jobId, ActionType.SAVEPOINT.getValue(), savePointType.getValue(), null));
            return Gateway.build(config.getGatewayConfig()).savepointJob(savePoint);
        } else {
            return FlinkAPI.build(config.getAddress()).savepoints(jobId, savePointType, config.getConfigJson());
        }
    }

    @Override
    public String exportSql(String sql) {
        String statement = executor.pretreatStatement(sql);
        StringBuilder sb = new StringBuilder();
        if (Asserts.isNotNullString(config.getJobName())) {
            sb.append("set " + PipelineOptions.NAME.key() + " = " + config.getJobName() + ";\r\n");
        }
        if (Asserts.isNotNull(config.getParallelism())) {
            sb.append("set " + CoreOptions.DEFAULT_PARALLELISM.key() + " = " + config.getParallelism() + ";\r\n");
        }
        if (Asserts.isNotNull(config.getCheckpoint())) {
            sb.append("set "
                    + ExecutionCheckpointingOptions.CHECKPOINTING_INTERVAL.key()
                    + " = "
                    + config.getCheckpoint()
                    + ";\r\n");
        }
        if (Asserts.isNotNullString(config.getSavePointPath())) {
            sb.append("set " + SavepointConfigOptions.SAVEPOINT_PATH + " = " + config.getSavePointPath() + ";\r\n");
        }
        if (Asserts.isNotNull(config.getGatewayConfig())
                && Asserts.isNotNull(config.getGatewayConfig().getFlinkConfig().getConfiguration())) {
            for (Map.Entry<String, String> entry : config.getGatewayConfig()
                    .getFlinkConfig()
                    .getConfiguration()
                    .entrySet()) {
                sb.append("set " + entry.getKey() + " = " + entry.getValue() + ";\r\n");
            }
        }

        switch (GatewayType.get(config.getType())) {
            case YARN_PER_JOB:
            case YARN_APPLICATION:
                sb.append("set "
                        + DeploymentOptions.TARGET.key()
                        + " = "
                        + GatewayType.get(config.getType()).getLongValue()
                        + ";\r\n");
                if (Asserts.isNotNull(config.getGatewayConfig())) {
                    sb.append("set "
                            + YarnConfigOptions.PROVIDED_LIB_DIRS.key()
                            + " = "
                            + Collections.singletonList(
                                    config.getGatewayConfig().getClusterConfig().getFlinkLibPath())
                            + ";\r\n");
                }
                if (Asserts.isNotNull(config.getGatewayConfig())
                        && Asserts.isNotNullString(
                                config.getGatewayConfig().getFlinkConfig().getJobName())) {
                    sb.append("set "
                            + YarnConfigOptions.APPLICATION_NAME.key()
                            + " = "
                            + config.getGatewayConfig().getFlinkConfig().getJobName()
                            + ";\r\n");
                }
                break;
            default:
        }
        sb.append(statement);
        return sb.toString();
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public ExecutorConfig getExecutorConfig() {
        return executorConfig;
    }

    public void setExecutorConfig(ExecutorConfig executorConfig) {
        this.executorConfig = executorConfig;
    }

    public JobConfig getConfig() {
        return config;
    }

    public void setConfig(JobConfig config) {
        this.config = config;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public boolean isUseGateway() {
        return useGateway;
    }

    public boolean isUseStatementSet() {
        return useStatementSet;
    }

    public void setUseStatementSet(boolean useStatementSet) {
        this.useStatementSet = useStatementSet;
    }

    public GatewayType getRunMode() {
        return runMode;
    }

    public JobParam getJobParam() {
        return jobParam;
    }

    public void setJobParam(JobParam jobParam) {
        this.jobParam = jobParam;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }
}
