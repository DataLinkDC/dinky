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
import org.dinky.classloader.DinkyClassLoader;
import org.dinky.context.CustomTableEnvironmentContext;
import org.dinky.context.FlinkUdfPathContextHolder;
import org.dinky.context.RowLevelPermissionsContext;
import org.dinky.data.annotations.ProcessStep;
import org.dinky.data.enums.GatewayType;
import org.dinky.data.enums.ProcessStepType;
import org.dinky.data.enums.Status;
import org.dinky.data.exception.BusException;
import org.dinky.data.job.JobStatement;
import org.dinky.data.job.SqlType;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.result.ErrorResult;
import org.dinky.data.result.ExplainResult;
import org.dinky.data.result.IResult;
import org.dinky.data.result.ResultBuilder;
import org.dinky.data.result.ResultPool;
import org.dinky.data.result.SelectResult;
import org.dinky.executor.Executor;
import org.dinky.executor.ExecutorConfig;
import org.dinky.executor.ExecutorFactory;
import org.dinky.explainer.Explainer;
import org.dinky.function.util.UDFUtil;
import org.dinky.gateway.Gateway;
import org.dinky.gateway.config.FlinkConfig;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.enums.ActionType;
import org.dinky.gateway.enums.SavePointType;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.gateway.result.TestResult;
import org.dinky.job.builder.JobJarStreamGraphBuilder;
import org.dinky.job.runner.JobJarRunner;
import org.dinky.trans.Operations;
import org.dinky.trans.parse.AddFileSqlParseStrategy;
import org.dinky.trans.parse.AddJarSqlParseStrategy;
import org.dinky.utils.DinkyClassLoaderUtil;
import org.dinky.utils.FlinkStreamEnvironmentUtil;
import org.dinky.utils.JsonUtils;
import org.dinky.utils.LogUtil;
import org.dinky.utils.SqlUtil;
import org.dinky.utils.URLUtils;

import org.apache.flink.api.dag.Pipeline;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.runtime.jobgraph.jsonplan.JsonPlanGenerator;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.yarn.configuration.YarnConfigOptions;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrFormatter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class JobManager {
    private JobHandler handler;
    private ExecutorConfig executorConfig;
    private JobConfig config;
    private Executor executor;
    private boolean useGateway = false;
    private boolean isPlanMode = false;
    private boolean useStatementSet = false;
    private boolean useMockSinkFunction = false;
    private boolean useRestAPI = false;
    private GatewayType runMode = GatewayType.LOCAL;
    private JobParam jobParam = null;
    private JobStatementPlan jobStatementPlan;
    private String currentSql = "";
    private final WeakReference<DinkyClassLoader> dinkyClassLoader = new WeakReference<>(DinkyClassLoader.build());
    private Job job;

    public JobManager() {}

    public JobParam getJobParam() {
        return jobParam;
    }

    public void setJobParam(JobParam jobParam) {
        this.jobParam = jobParam;
    }

    public JobConfig getConfig() {
        return config;
    }

    public void setConfig(JobConfig config) {
        this.config = config;
    }

    public GatewayType getRunMode() {
        return runMode;
    }

    public void setCurrentSql(String currentSql) {
        this.currentSql = currentSql;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void setPlanMode(boolean planMode) {
        isPlanMode = planMode;
    }

    public boolean isPlanMode() {
        return isPlanMode;
    }

    public boolean isUseStatementSet() {
        return useStatementSet;
    }

    public boolean isUseRestAPI() {
        return useRestAPI;
    }

    public boolean isUseGateway() {
        return useGateway;
    }

    public JobStatementPlan getJobStatementPlan() {
        return jobStatementPlan;
    }

    // return dinkyclassloader
    public DinkyClassLoader getDinkyClassLoader() {
        return Asserts.isNotNull(dinkyClassLoader.get()) ? dinkyClassLoader.get() : DinkyClassLoader.build();
    }

    // return udfPathContextHolder
    public FlinkUdfPathContextHolder getUdfPathContextHolder() {
        return getDinkyClassLoader().getUdfPathContextHolder();
    }

    // return job
    public Job getJob() {
        return job;
    }

    // set job
    public void setJob(Job job) {
        this.job = job;
    }

    private JobManager(JobConfig config) {
        this.config = config;
    }

    public static JobManager build(JobConfig config) {
        JobManager manager = new JobManager(config);
        manager.init();
        return manager;
    }

    public static JobManager buildPlanMode(JobConfig config) {
        JobManager manager = new JobManager(config);
        manager.setPlanMode(true);
        manager.init();
        log.info("Build Flink plan mode success.");
        return manager;
    }

    public void init() {
        if (!isPlanMode) {
            runMode = GatewayType.get(config.getType());
            useGateway = GatewayType.isDeployCluster(config.getType());
            handler = JobHandler.build();
        }
        useStatementSet = config.isStatementSet();
        useMockSinkFunction = config.isMockSinkFunction();
        useRestAPI = SystemConfiguration.getInstances().isUseRestAPI();
        executorConfig = config.getExecutorSetting();
        executorConfig.setPlan(isPlanMode);
        executor = ExecutorFactory.buildExecutor(executorConfig, getDinkyClassLoader());
        DinkyClassLoaderUtil.initClassLoader(config, getDinkyClassLoader());
    }

    private boolean ready() {
        return isPlanMode || handler.init(job);
    }

    private boolean success() {
        return isPlanMode || handler.success();
    }

    private boolean failed() {
        return isPlanMode || handler.failed();
    }

    public boolean close() {
        CustomTableEnvironmentContext.clear();
        RowLevelPermissionsContext.clear();
        try {
            getExecutor().getDinkyClassLoader().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public ObjectNode getJarStreamGraphJson(String statement) {
        Pipeline pipeline = JobJarStreamGraphBuilder.build(this).getJarStreamGraph(statement, getDinkyClassLoader());
        Configuration configuration = Configuration.fromMap(getExecutorConfig().getConfig());
        JobGraph jobGraph = FlinkStreamEnvironmentUtil.getJobGraph(pipeline, configuration);
        return JsonUtils.parseObject(JsonPlanGenerator.generatePlan(jobGraph));
    }

    @ProcessStep(type = ProcessStepType.SUBMIT_EXECUTE)
    public JobResult executeJarSql(String statement) throws Exception {
        List<String> statements = Arrays.stream(SqlUtil.getStatements(statement))
                .map(t -> executor.pretreatStatement(t))
                .collect(Collectors.toList());
        statement = String.join(";\n", statements);
        jobStatementPlan = Explainer.build(this).parseStatements(SqlUtil.getStatements(statement));
        jobStatementPlan.setSubmissionMode(config.isSubmissionMode());
        jobStatementPlan.buildFinalStatement();
        job = Job.build(runMode, config, executorConfig, executor, statement, useGateway);
        ready();
        try {
            // Only one is executed.
            for (JobStatement jobStatement : jobStatementPlan.getJobStatementList()) {
                JobJarRunner jobJarRunner = new JobJarRunner(this);
                jobJarRunner.run(jobStatement);
                break;
            }
            if (job.isFailed()) {
                failed();
            } else {
                job.setStatus(Job.JobStatus.SUCCESS);
                success();
            }
        } catch (Exception e) {
            String error =
                    LogUtil.getError("Exception in executing FlinkJarSQL:\n" + SqlUtil.addLineNumber(statement), e);
            job.setEndTime(LocalDateTime.now());
            job.setStatus(Job.JobStatus.FAILED);
            job.setError(error);
            failed();
            throw new Exception(error, e);
        } finally {
            close();
        }
        return job.getJobResult();
    }

    @ProcessStep(type = ProcessStepType.SUBMIT_EXECUTE)
    public JobResult executeSql(String statement) throws Exception {
        job = Job.build(runMode, config, executorConfig, executor, statement, useGateway);
        ready();
        try {
            jobStatementPlan = Explainer.build(this).parseStatements(SqlUtil.getStatements(statement));
            jobStatementPlan.setSubmissionMode(config.isSubmissionMode());
            jobStatementPlan.buildFinalStatement();
            JobRunnerFactory jobRunnerFactory = JobRunnerFactory.create(this);
            for (JobStatement jobStatement : jobStatementPlan.getJobStatementList()) {
                jobRunnerFactory.getJobRunner(jobStatement.getStatementType()).run(jobStatement);
            }
            job.setEndTime(LocalDateTime.now());
            if (job.isFailed()) {
                failed();
            } else {
                job.setStatus(Job.JobStatus.SUCCESS);
                success();
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("Only insert statement is supported now")) {
                throw new BusException(Status.OPERATE_NOT_SUPPORT_QUERY.getMessage());
            }
            String error = StrFormatter.format(
                    "Exception in executing FlinkSQL:\n{}\n{}", SqlUtil.addLineNumber(currentSql), errorMessage);
            job.setEndTime(LocalDateTime.now());
            job.setStatus(Job.JobStatus.FAILED);
            job.setError(error);
            failed();
            throw new Exception(error, e);
        } finally {
            close();
        }
        return job.getJobResult();
    }

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
                } else if (operationType.equals(SqlType.ADD) || operationType.equals(SqlType.ADD_JAR)) {
                    Set<File> allFilePath = AddJarSqlParseStrategy.getAllFilePath(item);
                    getExecutor().getDinkyClassLoader().addURLs(allFilePath);
                } else if (operationType.equals(SqlType.ADD_FILE)) {
                    Set<File> allFilePath = AddFileSqlParseStrategy.getAllFilePath(item);
                    getExecutor().getDinkyClassLoader().addURLs(allFilePath);
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

    public static SelectResult getJobData(String jobId) {
        SelectResult selectResult = ResultPool.get(jobId);
        if (Objects.isNull(selectResult) || selectResult.isDestroyed()) {
            JobReadHandler readHandler = JobHandler.build().getReadHandler();
            return readHandler.readResultDataFromStorage(Integer.parseInt(jobId));
        }
        return selectResult;
    }

    public ExplainResult explainSql(String statement) {
        return Explainer.build(this).explainSql(statement);
    }

    public ObjectNode getStreamGraph(String statement) {
        return Explainer.build(this).getStreamGraph(statement);
    }

    public String getJobPlanJson(String statement) {
        return Explainer.build(this).getJobPlanInfo(statement).getJsonPlan();
    }

    public boolean cancelNormal(String jobId) {
        try {
            return FlinkAPI.build(config.getAddress()).stop(jobId);
        } catch (Exception e) {
            log.error("stop flink job failed:", e);
            throw new BusException(e.getMessage());
        }
    }

    public SavePointResult savepoint(String jobId, SavePointType savePointType, String savePoint) {
        if (useGateway && !useRestAPI) {
            config.getGatewayConfig()
                    .setFlinkConfig(
                            FlinkConfig.build(jobId, ActionType.SAVEPOINT.getValue(), savePointType.getValue(), null));
            return Gateway.build(config.getGatewayConfig()).savepointJob(savePoint);
        } else {
            return FlinkAPI.build(config.getAddress()).savepoints(jobId, savePointType, config.getConfigJson());
        }
    }

    public static void killCluster(GatewayConfig gatewayConfig, String appId) {
        gatewayConfig.getClusterConfig().setAppId(appId);
        Gateway.build(gatewayConfig).killCluster();
    }

    public static GatewayResult deploySessionCluster(GatewayConfig gatewayConfig) {
        return Gateway.build(gatewayConfig).deployCluster(UDFUtil.createFlinkUdfPathContextHolder());
    }

    public static TestResult testGateway(GatewayConfig gatewayConfig) {
        return Gateway.build(gatewayConfig).test();
    }

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

    public List<URL> getAllFileSet() {
        return CollUtil.isEmpty(getUdfPathContextHolder().getAllFileSet())
                ? Collections.emptyList()
                : Arrays.asList(URLUtils.getURLs(
                        getUdfPathContextHolder().getAllFileSet().toArray(new File[0])));
    }
}
