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

package com.dlink.job;

import static com.dlink.function.util.UDFUtil.GATEWAY_TYPE_MAP;
import static com.dlink.function.util.UDFUtil.SESSION;
import static com.dlink.function.util.UDFUtil.YARN;

import com.dlink.api.FlinkAPI;
import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkSQLConstant;
import com.dlink.executor.EnvironmentSetting;
import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import com.dlink.explainer.Explainer;
import com.dlink.function.constant.PathConstant;
import com.dlink.function.context.UDFPathContextHolder;
import com.dlink.function.data.model.Env;
import com.dlink.function.data.model.UDF;
import com.dlink.function.util.UDFUtil;
import com.dlink.gateway.Gateway;
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.config.ActionType;
import com.dlink.gateway.config.FlinkConfig;
import com.dlink.gateway.config.GatewayConfig;
import com.dlink.gateway.result.GatewayResult;
import com.dlink.gateway.result.SavePointResult;
import com.dlink.gateway.result.TestResult;
import com.dlink.interceptor.FlinkInterceptor;
import com.dlink.interceptor.FlinkInterceptorResult;
import com.dlink.model.SystemConfiguration;
import com.dlink.parser.SqlType;
import com.dlink.process.context.ProcessContextHolder;
import com.dlink.process.model.ProcessEntity;
import com.dlink.result.ErrorResult;
import com.dlink.result.ExplainResult;
import com.dlink.result.IResult;
import com.dlink.result.InsertResult;
import com.dlink.result.ResultBuilder;
import com.dlink.result.ResultPool;
import com.dlink.result.SelectResult;
import com.dlink.session.ExecutorEntity;
import com.dlink.session.SessionConfig;
import com.dlink.session.SessionInfo;
import com.dlink.session.SessionPool;
import com.dlink.trans.Operations;
import com.dlink.utils.LogUtil;
import com.dlink.utils.SqlUtil;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.yarn.configuration.YarnConfigOptions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;

/**
 * JobManager
 *
 * @author wenmo
 * @since 2021/5/25 15:27
 **/
public class JobManager {

    private static final Logger logger = LoggerFactory.getLogger(JobManager.class);

    private JobHandler handler;
    private EnvironmentSetting environmentSetting;
    private ExecutorSetting executorSetting;
    private JobConfig config;
    private Executor executor;
    private Configuration configuration;
    private boolean useGateway = false;
    private boolean isPlanMode = false;
    private boolean useStatementSet = false;
    private boolean useRestAPI = false;
    private String sqlSeparator = FlinkSQLConstant.SEPARATOR;
    private GatewayType runMode = GatewayType.LOCAL;

    public JobManager() {
    }

    public void setUseGateway(boolean useGateway) {
        this.useGateway = useGateway;
    }

    public boolean isUseGateway() {
        return useGateway;
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

    public void setUseStatementSet(boolean useStatementSet) {
        this.useStatementSet = useStatementSet;
    }

    public boolean isUseRestAPI() {
        return useRestAPI;
    }

    public void setUseRestAPI(boolean useRestAPI) {
        this.useRestAPI = useRestAPI;
    }

    public String getSqlSeparator() {
        return sqlSeparator;
    }

    public void setSqlSeparator(String sqlSeparator) {
        this.sqlSeparator = sqlSeparator;
    }

    public JobManager(JobConfig config) {
        this.config = config;
    }

    public static JobManager build() {
        JobManager manager = new JobManager();
        manager.init();
        return manager;
    }

    public static JobManager build(JobConfig config) {
        initGatewayConfig(config);
        JobManager manager = new JobManager(config);
        manager.init();
        return manager;
    }

    public static JobManager buildPlanMode(JobConfig config) {
        JobManager manager = new JobManager(config);
        manager.setPlanMode(true);
        manager.init();
        ProcessContextHolder.getProcess().info("Build Flink plan mode success.");
        return manager;
    }

    private static void initGatewayConfig(JobConfig config) {
        if (useGateway(config.getType())) {
            Asserts.checkNull(config.getGatewayConfig(), "GatewayConfig 不能为空");
            config.getGatewayConfig().setType(GatewayType.get(config.getType()));
            config.getGatewayConfig().setTaskId(config.getTaskId());
            config.getGatewayConfig().getFlinkConfig().setJobName(config.getJobName());
            config.getGatewayConfig().getFlinkConfig().setSavePoint(config.getSavePointPath());
            config.setUseRemote(false);
        }
    }

    public static boolean useGateway(String type) {
        return (GatewayType.YARN_PER_JOB.equalsValue(type) || GatewayType.YARN_APPLICATION.equalsValue(type)
                || GatewayType.KUBERNETES_APPLICATION.equalsValue(type));
    }

    private Executor createExecutor() {
        initEnvironmentSetting();
        if (!runMode.equals(GatewayType.LOCAL) && !useGateway && config.isUseRemote()) {
            executor = Executor.buildRemoteExecutor(environmentSetting, config.getExecutorSetting());
            return executor;
        } else {
            if (ArrayUtil.isNotEmpty(config.getJarFiles())) {
                config.getExecutorSetting().getConfig().put(PipelineOptions.JARS.key(),
                        Stream.of(config.getJarFiles()).map(FileUtil::getAbsolutePath)
                                .collect(Collectors.joining(",")));
            }

            executor = Executor.buildLocalExecutor(config.getExecutorSetting());
            return executor;
        }
    }

    private Executor createExecutorWithSession() {
        if (config.isUseSession()) {
            ExecutorEntity executorEntity = SessionPool.get(config.getSession());
            if (Asserts.isNotNull(executorEntity)) {
                executor = executorEntity.getExecutor();
                config.setSessionConfig(executorEntity.getSessionConfig());
                initEnvironmentSetting();
                executor.update(executorSetting);
            } else {
                createExecutor();
                SessionPool.push(new ExecutorEntity(config.getSession(), executor));
            }
        } else {
            createExecutor();
        }
        executor.getSqlManager().registerSqlFragment(config.getVariables());
        return executor;
    }

    private void initEnvironmentSetting() {
        if (Asserts.isNotNullString(config.getAddress())) {
            environmentSetting = EnvironmentSetting.build(config.getAddress(), config.getJarFiles());
        }
    }

    private void initExecutorSetting() {
        executorSetting = config.getExecutorSetting();
    }

    public boolean init() {
        if (!isPlanMode) {
            runMode = GatewayType.get(config.getType());
            useGateway = useGateway(config.getType());
            handler = JobHandler.build();
        }
        useStatementSet = config.isUseStatementSet();
        useRestAPI = SystemConfiguration.getInstances().isUseRestAPI();
        sqlSeparator = SystemConfiguration.getInstances().getSqlSeparator();

        initExecutorSetting();
        initUDF();

        return false;
    }

    private void initUDF() {
        ProcessEntity process = ProcessContextHolder.getProcess();
        // 这里要分开
        // 1. 得到jar包路径，注入remote环境
        List<UDF> udfList = config.getUdfList();
        Set<String> libUDFs = UDFPathContextHolder.get();
        List<String> libHDFList = new ArrayList<>();
        if (CollUtil.isNotEmpty(libUDFs)) {
            libUDFs.forEach(path -> {
                String destPath = PathConstant.getUdfPackagePath(config.getTaskId()) + "udf-lib-"
                        + FileUtil.getName(path);
                FileUtil.copy(path, destPath, true);
                libHDFList.add(destPath);
            });
        } else if (CollUtil.isEmpty(udfList)) {
            createExecutorWithSession();
            return;
        }

        CollUtil.addAll(libHDFList, UDFUtil.initJavaUDF(udfList, runMode, config.getTaskId()));

        String[] jarPaths = CollUtil.removeEmpty(libHDFList).toArray(new String[]{});

        if (GATEWAY_TYPE_MAP.get(SESSION).contains(runMode)) {
            config.setJarFiles(jarPaths);
        }
        // 2.实例化remote环境
        createExecutorWithSession();
        // 3.编译python
        String[] pyPaths = UDFUtil.initPythonUDF(udfList, runMode, config.getTaskId(),
                executor.getTableConfig().getConfiguration());

        executor.initUDF(jarPaths);
        executor.initPyUDF(Env.getPath(), pyPaths);
        if (GATEWAY_TYPE_MAP.get(YARN).contains(runMode)) {
            config.getGatewayConfig().setJarPaths(ArrayUtil.append(jarPaths, pyPaths));
        }
        process.info("Initializing Flink UDF...Finish");
    }

    private boolean ready() {
        return handler.init();
    }

    private boolean success() {
        return handler.success();
    }

    private boolean failed() {
        return handler.failed();
    }

    public boolean close() {
        JobContextHolder.clear();
        return false;
    }

    public JobResult executeSql(String statement) {
        ProcessEntity process = ProcessContextHolder.getProcess();
        Job job = Job.init(runMode, config, executorSetting, executor, statement, useGateway);
        if (!useGateway) {
            job.setJobManagerAddress(environmentSetting.getAddress());
        }
        JobContextHolder.setJob(job);
        ready();
        String currentSql = "";
        JobParam jobParam = Explainer.build(executor, useStatementSet, sqlSeparator)
                .pretreatStatements(SqlUtil.getStatements(statement, sqlSeparator));
        try {
            for (StatementParam item : jobParam.getDdl()) {
                currentSql = item.getValue();
                executor.executeSql(item.getValue());
            }
            if (jobParam.getTrans().size() > 0) {
                // Use statement set or gateway only submit inserts.
                if (useStatementSet && useGateway) {
                    List<String> inserts = new ArrayList<>();
                    for (StatementParam item : jobParam.getTrans()) {
                        inserts.add(item.getValue());
                    }

                    // Use statement set need to merge all insert sql into a sql.
                    currentSql = String.join(sqlSeparator, inserts);
                    GatewayResult gatewayResult = submitByGateway(inserts);
                    // Use statement set only has one jid.
                    job.setResult(InsertResult.success(gatewayResult.getAppId()));
                    job.setJobId(gatewayResult.getAppId());
                    job.setJids(gatewayResult.getJids());
                    job.setJobManagerAddress(formatAddress(gatewayResult.getWebURL()));
                } else if (useStatementSet && !useGateway) {
                    List<String> inserts = new ArrayList<>();
                    for (StatementParam item : jobParam.getTrans()) {
                        if (item.getType().isInsert()) {
                            inserts.add(item.getValue());
                        }
                    }
                    if (inserts.size() > 0) {
                        currentSql = String.join(sqlSeparator, inserts);
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
                            IResult result = ResultBuilder
                                    .build(SqlType.INSERT, config.getMaxRowNum(), config.isUseChangeLog(),
                                            config.isUseAutoCancel(), executor.getTimeZone())
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
                    currentSql = String.join(sqlSeparator, inserts);
                    GatewayResult gatewayResult = submitByGateway(inserts);
                    job.setResult(InsertResult.success(gatewayResult.getAppId()));
                    job.setJobId(gatewayResult.getAppId());
                    job.setJids(gatewayResult.getJids());
                    job.setJobManagerAddress(formatAddress(gatewayResult.getWebURL()));
                } else {
                    for (StatementParam item : jobParam.getTrans()) {
                        currentSql = item.getValue();
                        FlinkInterceptorResult flinkInterceptorResult = FlinkInterceptor.build(executor,
                                item.getValue());
                        if (Asserts.isNotNull(flinkInterceptorResult.getTableResult())) {
                            if (config.isUseResult()) {
                                IResult result = ResultBuilder
                                        .build(item.getType(), config.getMaxRowNum(), config.isUseChangeLog(),
                                                config.isUseAutoCancel(), executor.getTimeZone())
                                        .getResult(flinkInterceptorResult.getTableResult());
                                job.setResult(result);
                            }
                        } else {
                            if (!flinkInterceptorResult.isNoExecute()) {
                                TableResult tableResult = executor.executeSql(item.getValue());
                                if (tableResult.getJobClient().isPresent()) {
                                    job.setJobId(tableResult.getJobClient().get().getJobID().toHexString());
                                    job.setJids(new ArrayList<String>() {

                                        {
                                            add(job.getJobId());
                                        }
                                    });
                                }
                                if (config.isUseResult()) {
                                    IResult result = ResultBuilder.build(item.getType(), config.getMaxRowNum(),
                                            config.isUseChangeLog(), config.isUseAutoCancel(),
                                            executor.getTimeZone()).getResult(tableResult);
                                    job.setResult(result);
                                }
                            }
                        }
                        // Only can submit the first of insert sql, when not use statement set.
                        break;
                    }
                }
            }
            if (jobParam.getExecute().size() > 0) {
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
                        gatewayResult = Gateway.build(config.getGatewayConfig()).submitJar();
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
                    job.setResult(InsertResult.success(gatewayResult.getAppId()));
                    job.setJobId(gatewayResult.getAppId());
                    job.setJids(gatewayResult.getJids());
                    job.setJobManagerAddress(formatAddress(gatewayResult.getWebURL()));
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
                        IResult result = ResultBuilder
                                .build(SqlType.EXECUTE, config.getMaxRowNum(), config.isUseChangeLog(),
                                        config.isUseAutoCancel(), executor.getTimeZone())
                                .getResult(null);
                        job.setResult(result);
                    }
                }
            }
            job.setEndTime(LocalDateTime.now());
            job.setStatus(Job.JobStatus.SUCCESS);
            success();
        } catch (Exception e) {
            String error = LogUtil.getError("Exception in executing FlinkSQL:\n" + currentSql, e);
            job.setEndTime(LocalDateTime.now());
            job.setStatus(Job.JobStatus.FAILED);
            job.setError(error);
            process.error(error);
            failed();
        } finally {
            close();
        }
        return job.getJobResult();
    }

    private GatewayResult submitByGateway(List<String> inserts) {
        GatewayResult gatewayResult = null;

        // Use gateway need to build gateway config, include flink configeration.
        config.addGatewayConfig(executor.getSetConfig());

        if (runMode.isApplicationMode()) {
            // Application mode need to submit dlink-app.jar that in the hdfs or image.
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

    private String formatAddress(String webURL) {
        if (Asserts.isNotNullString(webURL)) {
            return webURL.replaceAll("http://", "");
        } else {
            return "";
        }
    }

    public IResult executeDDL(String statement) {
        String[] statements = SqlUtil.getStatements(statement, sqlSeparator);
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
                LocalDateTime startTime = LocalDateTime.now();
                TableResult tableResult = executor.executeSql(newStatement);
                result = ResultBuilder.build(operationType, config.getMaxRowNum(), false, false, executor.getTimeZone())
                        .getResult(tableResult);
                result.setStartTime(startTime);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ErrorResult();
    }

    public static SelectResult getJobData(String jobId) {
        return ResultPool.get(jobId);
    }

    public static SessionInfo createSession(String session, SessionConfig sessionConfig, String createUser) {
        if (SessionPool.exist(session)) {
            return SessionPool.getInfo(session);
        }
        Executor sessionExecutor = null;
        if (sessionConfig.isUseRemote()) {
            sessionExecutor = Executor.buildRemoteExecutor(EnvironmentSetting.build(sessionConfig.getAddress()),
                    ExecutorSetting.DEFAULT);
        } else {
            sessionExecutor = Executor.buildLocalExecutor(sessionConfig.getExecutorSetting());
        }
        ExecutorEntity executorEntity = new ExecutorEntity(session, sessionConfig, createUser, LocalDateTime.now(),
                sessionExecutor);
        SessionPool.push(executorEntity);
        return SessionInfo.build(executorEntity);
    }

    public static List<SessionInfo> listSession(String createUser) {
        return SessionPool.filter(createUser);
    }

    public ExplainResult explainSql(String statement) {
        return Explainer.build(executor, useStatementSet, sqlSeparator).explainSql(statement);
    }

    public ObjectNode getStreamGraph(String statement) {
        return Explainer.build(executor, useStatementSet, sqlSeparator).getStreamGraph(statement);
    }

    public String getJobPlanJson(String statement) {
        return Explainer.build(executor, useStatementSet, sqlSeparator).getJobPlanInfo(statement).getJsonPlan();
    }

    public boolean cancel(String jobId) {
        if (useGateway && !useRestAPI) {
            config.getGatewayConfig().setFlinkConfig(FlinkConfig.build(jobId, ActionType.CANCEL.getValue(),
                    null, null));
            Gateway.build(config.getGatewayConfig()).savepointJob();
            return true;
        } else {
            try {
                return FlinkAPI.build(config.getAddress()).stop(jobId);
            } catch (Exception e) {
                logger.error("停止作业时集群不存在: " + e);
            }
            return false;
        }
    }

    public SavePointResult savepoint(String jobId, String savePointType, String savePoint) {
        if (useGateway && !useRestAPI) {
            config.getGatewayConfig().setFlinkConfig(FlinkConfig.build(jobId, ActionType.SAVEPOINT.getValue(),
                    savePointType, null));
            return Gateway.build(config.getGatewayConfig()).savepointJob(savePoint);
        } else {
            return FlinkAPI.build(config.getAddress()).savepoints(jobId, savePointType);
        }
    }

    public JobResult executeJar() {
        ProcessEntity process = ProcessContextHolder.getProcess();
        Job job = Job.init(runMode, config, executorSetting, executor, null, useGateway);
        JobContextHolder.setJob(job);
        ready();
        try {
            GatewayResult gatewayResult = Gateway.build(config.getGatewayConfig()).submitJar();
            job.setResult(InsertResult.success(gatewayResult.getAppId()));
            job.setJobId(gatewayResult.getAppId());
            job.setJids(gatewayResult.getJids());
            job.setJobManagerAddress(formatAddress(gatewayResult.getWebURL()));
            job.setEndTime(LocalDateTime.now());
            job.setStatus(Job.JobStatus.SUCCESS);
            success();
        } catch (Exception e) {
            String error = LogUtil.getError(
                    "Exception in executing Jar：\n" + config.getGatewayConfig().getAppConfig().getUserJarPath(), e);
            job.setEndTime(LocalDateTime.now());
            job.setStatus(Job.JobStatus.FAILED);
            job.setError(error);
            failed();
            process.error(error);
        } finally {
            close();
        }
        return job.getJobResult();
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
            sb.append("set " + ExecutionCheckpointingOptions.CHECKPOINTING_INTERVAL.key() + " = "
                    + config.getCheckpoint() + ";\r\n");
        }
        if (Asserts.isNotNullString(config.getSavePointPath())) {
            sb.append("set " + SavepointConfigOptions.SAVEPOINT_PATH + " = " + config.getSavePointPath() + ";\r\n");
        }
        if (Asserts.isNotNull(config.getGatewayConfig())
                && Asserts.isNotNull(config.getGatewayConfig().getFlinkConfig().getConfiguration())) {
            for (Map.Entry<String, String> entry : config.getGatewayConfig().getFlinkConfig().getConfiguration()
                    .entrySet()) {
                sb.append("set " + entry.getKey() + " = " + entry.getValue() + ";\r\n");
            }
        }

        switch (GatewayType.get(config.getType())) {
            case YARN_PER_JOB:
            case YARN_APPLICATION:
                sb.append("set " + DeploymentOptions.TARGET.key() + " = "
                        + GatewayType.get(config.getType()).getLongValue() + ";\r\n");
                if (Asserts.isNotNull(config.getGatewayConfig())) {
                    sb.append("set " + YarnConfigOptions.PROVIDED_LIB_DIRS.key() + " = "
                            + Collections.singletonList(config.getGatewayConfig().getClusterConfig().getFlinkLibPath())
                            + ";\r\n");
                }
                if (Asserts.isNotNull(config.getGatewayConfig())
                        && Asserts.isNotNullString(config.getGatewayConfig().getFlinkConfig().getJobName())) {
                    sb.append("set " + YarnConfigOptions.APPLICATION_NAME.key() + " = "
                            + config.getGatewayConfig().getFlinkConfig().getJobName() + ";\r\n");
                }
                break;
            default:
        }
        sb.append(statement);
        return sb.toString();
    }

    public Executor getExecutor() {
        return executor;
    }
}
