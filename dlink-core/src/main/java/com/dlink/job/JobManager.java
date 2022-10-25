/*
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

import com.dlink.api.FlinkAPI;
import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkSQLConstant;
import com.dlink.executor.EnvironmentSetting;
import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import com.dlink.explainer.Explainer;
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
import com.dlink.pool.ClassEntity;
import com.dlink.pool.ClassPool;
import com.dlink.process.context.ProcessContextHolder;
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
import com.dlink.utils.UDFUtil;

import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.optimizer.CompilerException;
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
import java.util.function.BinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

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
    private boolean useGateway = false;
    private boolean isPlanMode = false;
    private boolean useStatementSet = false;
    private boolean useRestAPI = false;
    private String sqlSeparator = FlinkSQLConstant.SEPARATOR;
    private GatewayType runMode = GatewayType.LOCAL;

    public JobManager() {
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
        manager.executor.initUDF(config.getJarFiles());
        manager.executor.initPyUDF(config.getPyFiles());

        if (config.getGatewayConfig() != null) {
            config.getGatewayConfig().setJarPaths(config.getJarFiles());
        }
        return manager;
    }

    public static JobManager buildPlanMode(JobConfig config) {
        JobManager manager = new JobManager(config);
        manager.setPlanMode(true);
        manager.init();
        manager.executor.initUDF(config.getJarFiles());
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
        return (GatewayType.YARN_PER_JOB.equalsValue(type) || GatewayType.YARN_APPLICATION.equalsValue(type) || GatewayType.KUBERNETES_APPLICATION.equalsValue(type));
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
            sessionExecutor = Executor.buildRemoteExecutor(EnvironmentSetting.build(sessionConfig.getAddress()), ExecutorSetting.DEFAULT);
        } else {
            sessionExecutor = Executor.buildLocalExecutor(sessionConfig.getExecutorSetting());
        }
        ExecutorEntity executorEntity = new ExecutorEntity(session, sessionConfig, createUser, LocalDateTime.now(), sessionExecutor);
        SessionPool.push(executorEntity);
        return SessionInfo.build(executorEntity);
    }

    public static List<SessionInfo> listSession(String createUser) {
        return SessionPool.filter(createUser);
    }

    public static TestResult testGateway(GatewayConfig gatewayConfig) {
        return Gateway.build(gatewayConfig).test();
    }

    public static List<String> getUDFClassName(String statement) {
        Pattern pattern = Pattern.compile("function (.*?)'(.*?)'", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(statement);
        List<String> classNameList = new ArrayList<>();
        while (matcher.find()) {
            classNameList.add(matcher.group(2));
        }
        return classNameList;
    }

    public static void initUDF(String className, String code) {
        if (ClassPool.exist(ClassEntity.build(className, code))) {
            UDFUtil.initClassLoader(className);
        } else {
            UDFUtil.buildClass(code);
        }
    }

    public static void initMustSuccessUDF(String className, String code) {
        if (ClassPool.exist(ClassEntity.build(className, code))) {
            UDFUtil.initClassLoader(className);
        } else {
            // 如果编译失败，返回异常。因为必须用到的函数，也必须编译成功
            if (!UDFUtil.buildClass(code)) {
                throw new CompilerException(String.format("class:%s 编译异常,请检查代码", className));
            }
        }
    }

    private static String getExecuteSqlError(String statement, Exception e) {
        return LogUtil.getError("Exception in executing FlinkSQL:\n" + statement, e);
    }

    public boolean isUseGateway() {
        return useGateway;
    }

    public void setUseGateway(boolean useGateway) {
        this.useGateway = useGateway;
    }

    public boolean isPlanMode() {
        return isPlanMode;
    }

    public void setPlanMode(boolean planMode) {
        isPlanMode = planMode;
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

    private Executor createExecutor() {
        initEnvironmentSetting();
        if (!runMode.equals(GatewayType.LOCAL) && !useGateway && config.isUseRemote()) {
            executor = Executor.buildRemoteExecutor(environmentSetting, config.getExecutorSetting());
        } else {
            executor = Executor.buildLocalExecutor(config.getExecutorSetting());
        }
        return executor;
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
            environmentSetting = EnvironmentSetting.build(config.getAddress());
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
        createExecutorWithSession();
        return false;
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
        Job job = Job.init(runMode, config, executorSetting, executor, statement, useGateway);
        if (!useGateway) {
            job.setJobManagerAddress(environmentSetting.getAddress());
        }
        JobContextHolder.setJob(job);
        ready();

        JobParam jobParam = Explainer.build(executor, useStatementSet, sqlSeparator).pretreatStatements(SqlUtil.getStatements(statement, sqlSeparator));
        try {
            executeDdlSql(jobParam);
            executeTransSql(job, jobParam);
            execute(job, jobParam);
            job.setStatus(Job.JobStatus.SUCCESS);
            success();
        } catch (Exception e) {
            job.setError(e.getMessage());
            job.setStatus(Job.JobStatus.FAILED);
            failed();
        }
        job.setEndTime(LocalDateTime.now());
        close();
        return job.getJobResult();
    }

    private void execute(Job job, JobParam jobParam) throws Exception {
        if (jobParam.getExecute().isEmpty()) {
            return;
        }

        if (!useGateway) {
            executeWithoutGateway(job, jobParam);
            return;
        }

        for (StatementParam item : jobParam.getExecute()) {
            executeFlinkSql(item);
            if (!useStatementSet) {
                break;
            }
        }

        config.addGatewayConfig(executor.getSetConfig());

        GatewayResult gatewayResult;
        if (runMode.isApplicationMode()) {
            gatewayResult = Gateway.build(config.getGatewayConfig()).submitJar();
        } else {
            StreamGraph streamGraph = executor.getStreamGraph();
            streamGraph.setJobName(config.getJobName());
            JobGraph jobGraph = streamGraph.getJobGraph();
            if (Asserts.isNotNullString(config.getSavePointPath())) {
                jobGraph.setSavepointRestoreSettings(SavepointRestoreSettings.forPath(config.getSavePointPath()));
            }
            gatewayResult = Gateway.build(config.getGatewayConfig()).submitJobGraph(jobGraph);
        }

        job.setResult(InsertResult.success(gatewayResult.getAppId()));
        job.setJobId(gatewayResult.getAppId());
        job.setJids(gatewayResult.getJids());
        job.setJobManagerAddress(formatAddress(gatewayResult.getWebURL()));
    }

    private void executeWithoutGateway(Job job, JobParam jobParam) throws Exception {
        for (StatementParam item : jobParam.getExecute()) {
            executeFlinkSql(item);
            if (!useStatementSet) {
                break;
            }
        }

        JobClient jobClient = executor.executeAsync(config.getJobName());
        if (Asserts.isNotNull(jobClient)) {
            job.setJobId(jobClient.getJobID().toHexString());
            job.setJids(Stream.of(job.getJobId()).collect(Collectors.toList()));
        }

        if (config.isUseResult()) {
            IResult result = ResultBuilder.build(SqlType.EXECUTE, config.getMaxRowNum(), config.isUseChangeLog(), config.isUseAutoCancel(), executor.getTimeZone()).getResult(null);
            job.setResult(result);
        }
    }

    private void executeTransSql(Job job, JobParam jobParam) {
        if (jobParam.getTrans().isEmpty()) {
            return;
        }

        // Use statement set or gateway only submit inserts.
        if (useStatementSet && useGateway) {
            executeTransSqlWithStatementSetAndGateway(job, jobParam);
        } else if (useStatementSet) {
            executeTransSqlWithStatementSet(job, jobParam);
        } else if (useGateway) {
            executeTransSqlWithGateway(job, jobParam);
        } else {
            executeTransSqlWithoutStatementSetAndGateway(job, jobParam);
        }

    }

    private void executeTransSqlWithoutStatementSetAndGateway(Job job, JobParam jobParam) {
        // Only can submit the first of insert sql, when not use statement set.
        jobParam.getTrans().stream().findFirst().ifPresent(statementParam -> {
            final String currentSql = statementParam.getValue();
            try {
                final ResultBuilder resultBuilder = ResultBuilder.build(statementParam.getType(), config.getMaxRowNum(), config.isUseChangeLog(), config.isUseAutoCancel(), executor.getTimeZone());

                FlinkInterceptorResult flinkInterceptorResult = FlinkInterceptor.build(executor, currentSql);
                TableResult tableResult;
                if (Asserts.isNotNull(flinkInterceptorResult.getTableResult())) {
                    tableResult = flinkInterceptorResult.getTableResult();
                } else {
                    if (flinkInterceptorResult.isNoExecute()) {
                        return;
                    }

                    tableResult = executor.executeSql(currentSql);
                    tableResult.getJobClient().ifPresent(t -> {
                        job.setJobId(t.getJobID().toHexString());
                        job.setJids(Stream.of(job.getJobId()).collect(Collectors.toList()));
                    });
                }

                if (config.isUseResult()) {
                    IResult result = resultBuilder.getResult(tableResult);
                    job.setResult(result);
                }
            } catch (Exception e) {
                String error = getExecuteSqlError(currentSql, e);
                throw new ExecuteFlinkSqlException(error, e);
            }
        });
    }

    private void executeTransSqlWithGateway(Job job, JobParam jobParam) {
        List<String> inserts = new ArrayList<>();
        try {
            // Only can submit the first of insert sql, when not use statement set.
            jobParam.getTrans().stream().findFirst().ifPresent(t -> inserts.add(t.getValue()));
            GatewayResult gatewayResult = submitByGateway(inserts);
            job.setResult(InsertResult.success(gatewayResult.getAppId()));
            job.setJobId(gatewayResult.getAppId());
            job.setJids(gatewayResult.getJids());
            job.setJobManagerAddress(formatAddress(gatewayResult.getWebURL()));
        } catch (Exception e) {
            String currentSql = String.join(sqlSeparator, inserts);
            String error = getExecuteSqlError(currentSql, e);
            throw new ExecuteFlinkSqlException(error, e);
        }
    }

    private void executeTransSqlWithStatementSet(Job job, JobParam jobParam) {
        List<String> inserts = new ArrayList<>();
        try {
            inserts = jobParam.getTrans().stream().filter(item -> item.getType().isInsert()).map(StatementParam::getValue).collect(Collectors.toList());

            if (inserts.isEmpty()) {
                return;
            }

            // Remote mode can get the table result.
            TableResult tableResult = executor.executeStatementSet(inserts);
            tableResult.getJobClient().ifPresent(jobClient -> {
                job.setJobId(jobClient.getJobID().toHexString());
                job.setJids(Stream.of(job.getJobId()).collect(Collectors.toList()));
            });

            if (config.isUseResult()) {
                // Build insert result.
                IResult result = ResultBuilder.build(SqlType.INSERT, config.getMaxRowNum(), config.isUseChangeLog(), config.isUseAutoCancel(), executor.getTimeZone()).getResult(tableResult);
                job.setResult(result);
            }
        } catch (Exception e) {
            String currentSql = String.join(sqlSeparator, inserts);
            String error = getExecuteSqlError(currentSql, e);
            throw new ExecuteFlinkSqlException(error, e);
        }
    }

    private void executeTransSqlWithStatementSetAndGateway(Job job, JobParam jobParam) {
        List<String> inserts = new ArrayList<>();
        try {
            for (StatementParam item : jobParam.getTrans()) {
                inserts.add(item.getValue());
            }

            // Use statement set need to merge all insert sql into a sql.
            GatewayResult gatewayResult = submitByGateway(inserts);
            // Use statement set only has one jid.
            job.setResult(InsertResult.success(gatewayResult.getAppId()));
            job.setJobId(gatewayResult.getAppId());
            job.setJids(gatewayResult.getJids());
            job.setJobManagerAddress(formatAddress(gatewayResult.getWebURL()));
        } catch (Exception e) {
            String currentSql = String.join(sqlSeparator, inserts);
            String error = getExecuteSqlError(currentSql, e);
            throw new ExecuteFlinkSqlException(error, e);
        }
    }

    private void executeDdlSql(JobParam jobParam) {
        for (StatementParam item : jobParam.getDdl()) {
            executeFlinkSql(item);
        }
    }

    private void executeFlinkSql(StatementParam item) {
        try {
            executor.executeSql(item.getValue());
        } catch (Exception e) {
            String error = getExecuteSqlError(item.getValue(), e);
            throw new ExecuteFlinkSqlException(error, e);
        }
    }

    private GatewayResult submitByGateway(List<String> inserts) {
        // Use gateway need to build gateway config, include flink configuration.
        config.addGatewayConfig(executor.getSetConfig());

        final Gateway gateway = Gateway.build(config.getGatewayConfig());
        if (runMode.isApplicationMode()) {
            // Application mode need to submit dlink-app.jar that in the hdfs or image.
            return gateway.submitJar();
        }

        JobGraph jobGraph = executor.getJobGraphFromInserts(inserts);
        if (Asserts.isNotNullString(config.getSavePointPath())) {
            // Perjob mode need to set savepoint restore path, when recovery from savepoint.
            jobGraph.setSavepointRestoreSettings(SavepointRestoreSettings.forPath(config.getSavePointPath(), true));
        }
        // Perjob mode need to submit job graph.
        return gateway.submitJobGraph(jobGraph);
    }

    private String formatAddress(String webURL) {
        if (Asserts.isNotNullString(webURL)) {
            return webURL.replace("http://", "");
        } else {
            return "";
        }
    }

    public IResult executeDDL(String statement) {
        String[] statements = SqlUtil.getStatements(statement, sqlSeparator);
        try {
            for (String item : statements) {
                String newStatement = executor.pretreatStatement(item);
                SqlType operationType = Operations.getOperationType(newStatement);
                if (SqlType.INSERT == operationType || SqlType.SELECT == operationType || SqlType.UNKNOWN == operationType) {
                    continue;
                }

                TableResult tableResult = executor.executeSql(newStatement);
                IResult result = ResultBuilder.build(operationType, config.getMaxRowNum(), false, false, executor.getTimeZone()).getResult(tableResult);
                result.setStartTime(LocalDateTime.now());
                return result;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return new ErrorResult();
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
            config.getGatewayConfig().setFlinkConfig(FlinkConfig.build(jobId, ActionType.CANCEL.getValue(), null, null));
            Gateway.build(config.getGatewayConfig()).savepointJob();
            return true;
        } else {
            try {
                return FlinkAPI.build(config.getAddress()).stop(jobId);
            } catch (Exception e) {
                logger.error(String.format("停止作业时集群不存在: %s", e));
            }
            return false;
        }
    }

    public SavePointResult savepoint(String jobId, String savePointType, String savePoint) {
        if (useGateway && !useRestAPI) {
            config.getGatewayConfig().setFlinkConfig(FlinkConfig.build(jobId, ActionType.SAVEPOINT.getValue(), savePointType, null));
            return Gateway.build(config.getGatewayConfig()).savepointJob(savePoint);
        } else {
            return FlinkAPI.build(config.getAddress()).savepoints(jobId, savePointType);
        }
    }

    public JobResult executeJar() {
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
            String error = LogUtil.getError("Exception in executing Jar：\n" + config.getGatewayConfig().getAppConfig().getUserJarPath(), e);
            job.setEndTime(LocalDateTime.now());
            job.setStatus(Job.JobStatus.FAILED);
            job.setError(error);
            failed();
            close();
        }
        close();
        return job.getJobResult();
    }

    public String exportSql(String sql) {
        StringBuilder sb = new StringBuilder();

        final String format = "set %s = %s;\n";
        BinaryOperator<String> generateStatement = (key, value) -> String.format(format, key, value);

        if (Asserts.isNotNullString(config.getJobName())) {
            sb.append(generateStatement.apply(PipelineOptions.NAME.key(), config.getJobName()));
        }

        if (Asserts.isNotNull(config.getParallelism())) {
            sb.append(generateStatement.apply(CoreOptions.DEFAULT_PARALLELISM.key(), String.valueOf(config.getParallelism())));
        }

        if (Asserts.isNotNull(config.getCheckpoint())) {
            sb.append(generateStatement.apply(ExecutionCheckpointingOptions.CHECKPOINTING_INTERVAL.key(), String.valueOf(config.getCheckpoint())));
        }

        if (Asserts.isNotNullString(config.getSavePointPath())) {
            sb.append(generateStatement.apply(SavepointConfigOptions.SAVEPOINT_PATH.toString(), config.getSavePointPath()));
        }

        if (Asserts.isNotNull(config.getGatewayConfig()) && Asserts.isNotNull(config.getGatewayConfig().getFlinkConfig().getConfiguration())) {
            for (Map.Entry<String, String> entry : config.getGatewayConfig().getFlinkConfig().getConfiguration().entrySet()) {
                sb.append(generateStatement.apply(entry.getKey(), entry.getValue()));
            }
        }

        GatewayType gatewayType = GatewayType.get(config.getType());
        if (gatewayType == GatewayType.YARN_PER_JOB || gatewayType == GatewayType.YARN_APPLICATION) {
            sb.append(generateStatement.apply(DeploymentOptions.TARGET.key(), GatewayType.get(config.getType()).getLongValue()));

            if (Asserts.isNotNull(config.getGatewayConfig())) {
                sb.append(generateStatement.apply(YarnConfigOptions.PROVIDED_LIB_DIRS.key(), Collections.singletonList(config.getGatewayConfig().getClusterConfig().getFlinkLibPath()).toString()));

                if (Asserts.isNotNullString(config.getGatewayConfig().getFlinkConfig().getJobName())) {
                    sb.append(generateStatement.apply(YarnConfigOptions.APPLICATION_NAME.key(), config.getGatewayConfig().getFlinkConfig().getJobName()));
                }
            }
        }

        String statement = executor.pretreatStatement(sql);
        sb.append(statement);
        return sb.toString();
    }

    static class ExecuteFlinkSqlException extends RuntimeException {
        public ExecuteFlinkSqlException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
