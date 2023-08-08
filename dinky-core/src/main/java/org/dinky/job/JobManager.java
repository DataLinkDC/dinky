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

import static org.dinky.function.util.UDFUtil.GATEWAY_TYPE_MAP;
import static org.dinky.function.util.UDFUtil.SESSION;
import static org.dinky.function.util.UDFUtil.YARN;

import org.dinky.api.FlinkAPI;
import org.dinky.assertion.Asserts;
import org.dinky.constant.FlinkSQLConstant;
import org.dinky.context.CustomTableEnvironmentContext;
import org.dinky.context.DinkyClassLoaderContextHolder;
import org.dinky.context.FlinkUdfPathContextHolder;
import org.dinky.context.RowLevelPermissionsContext;
import org.dinky.data.model.FlinkUdfManifest;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.result.ErrorResult;
import org.dinky.data.result.ExplainResult;
import org.dinky.data.result.IResult;
import org.dinky.data.result.InsertResult;
import org.dinky.data.result.ResultBuilder;
import org.dinky.data.result.ResultPool;
import org.dinky.data.result.SelectResult;
import org.dinky.executor.EnvironmentSetting;
import org.dinky.executor.Executor;
import org.dinky.executor.ExecutorSetting;
import org.dinky.explainer.Explainer;
import org.dinky.function.constant.PathConstant;
import org.dinky.function.data.model.UDF;
import org.dinky.function.util.UDFUtil;
import org.dinky.gateway.Gateway;
import org.dinky.gateway.config.FlinkConfig;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.enums.ActionType;
import org.dinky.gateway.enums.GatewayType;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.gateway.result.TestResult;
import org.dinky.interceptor.FlinkInterceptor;
import org.dinky.interceptor.FlinkInterceptorResult;
import org.dinky.parser.SqlType;
import org.dinky.process.context.ProcessContextHolder;
import org.dinky.process.exception.DinkyException;
import org.dinky.process.model.ProcessEntity;
import org.dinky.trans.Operations;
import org.dinky.utils.LogUtil;
import org.dinky.utils.SqlUtil;
import org.dinky.utils.URLUtils;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.yarn.configuration.YarnConfigOptions;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;

/**
 * JobManager
 *
 * @since 2021/5/25 15:27
 */
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

    public JobManager() {}

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

    private Executor createExecutor() {
        initEnvironmentSetting();
        if (!runMode.equals(GatewayType.LOCAL) && !useGateway && config.isUseRemote()) {
            executor =
                    Executor.buildRemoteExecutor(environmentSetting, config.getExecutorSetting());
        } else {
            if (ArrayUtil.isNotEmpty(config.getJarFiles())) {
                config.getExecutorSetting()
                        .getConfig()
                        .put(
                                PipelineOptions.JARS.key(),
                                Stream.of(config.getJarFiles())
                                        .map(FileUtil::getAbsolutePath)
                                        .collect(Collectors.joining(",")));
            }
            executor = Executor.buildLocalExecutor(config.getExecutorSetting());
        }
        executor.getSqlManager().registerSqlFragment(config.getVariables());
        return executor;
    }

    private void initEnvironmentSetting() {
        if (Asserts.isNotNullString(config.getAddress())) {
            environmentSetting =
                    EnvironmentSetting.build(config.getAddress(), config.getJarFiles());
        }
    }

    private void initExecutorSetting() {
        executorSetting = config.getExecutorSetting();
    }

    public boolean init() {
        if (!isPlanMode) {
            runMode = GatewayType.get(config.getType());
            useGateway = GatewayType.isDeployCluster(config.getType());
            handler = JobHandler.build();
        }
        useStatementSet = config.isUseStatementSet();
        useRestAPI = SystemConfiguration.getInstances().isUseRestAPI();
        sqlSeparator = SystemConfiguration.getInstances().getSqlSeparator();

        initExecutorSetting();
        createExecutor();
        return false;
    }

    private void addConfigurationClsAndJars(List<URL> jarList, List<URL> classpaths)
            throws Exception {
        Map<String, Object> temp = getFlinkConfigurationMap();
        temp.put(
                PipelineOptions.CLASSPATHS.key(),
                classpaths.stream().map(URL::toString).collect(Collectors.toList()));
        temp.put(
                PipelineOptions.JARS.key(),
                jarList.stream().map(URL::toString).collect(Collectors.toList()));
    }

    private Map<String, Object> getFlinkConfigurationMap() {
        Field configuration = null;
        try {
            configuration = StreamExecutionEnvironment.class.getDeclaredField("configuration");
            configuration.setAccessible(true);
            Configuration o =
                    (Configuration) configuration.get(executor.getStreamExecutionEnvironment());
            Field confData = Configuration.class.getDeclaredField("confData");
            confData.setAccessible(true);
            Map<String, Object> temp = (Map<String, Object>) confData.get(o);
            return temp;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initUDF(List<UDF> udfList) {
        if (Asserts.isNotNullCollection(udfList)) {
            initUDF(udfList, runMode, config.getTaskId());
        }
    }

    public void initUDF(List<UDF> udfList, GatewayType runMode, Integer taskId) {
        if (taskId == null) {
            taskId = -RandomUtil.randomInt(0, 1000);
        }
        ProcessEntity process = ProcessContextHolder.getProcess();

        // 这里要分开
        // 1. 得到jar包路径，注入remote环境
        Set<File> jarFiles = FlinkUdfPathContextHolder.getUdfFile();

        Set<File> otherPluginsFiles = FlinkUdfPathContextHolder.getOtherPluginsFiles();
        jarFiles.addAll(otherPluginsFiles);

        List<File> udfJars =
                Arrays.stream(UDFUtil.initJavaUDF(udfList, runMode, taskId))
                        .map(File::new)
                        .collect(Collectors.toList());
        jarFiles.addAll(udfJars);

        String[] jarPaths =
                CollUtil.removeNull(jarFiles).stream()
                        .map(File::getAbsolutePath)
                        .toArray(String[]::new);

        if (GATEWAY_TYPE_MAP.get(SESSION).contains(runMode)) {
            config.setJarFiles(jarPaths);
        }

        // 2.编译python
        String[] pyPaths =
                UDFUtil.initPythonUDF(
                        udfList,
                        runMode,
                        config.getTaskId(),
                        executor.getTableConfig().getConfiguration());

        executor.initUDF(jarPaths);
        if (ArrayUtil.isNotEmpty(pyPaths)) {
            for (String pyPath : pyPaths) {
                if (StrUtil.isNotBlank(pyPath)) {
                    FlinkUdfPathContextHolder.addPyUdfPath(new File(pyPath));
                }
            }
        }

        Set<File> pyUdfFile = FlinkUdfPathContextHolder.getPyUdfFile();
        executor.initPyUDF(
                SystemConfiguration.getInstances().getPythonHome(),
                pyUdfFile.stream().map(File::getAbsolutePath).toArray(String[]::new));
        if (GATEWAY_TYPE_MAP.get(YARN).contains(runMode)) {
            config.getGatewayConfig().setJarPaths(ArrayUtil.append(jarPaths, pyPaths));
        }

        try {
            List<URL> jarList = CollUtil.newArrayList(URLUtils.getURLs(jarFiles));
            // 3.写入udf所需文件
            writeManifest(taskId, jarList);

            addConfigurationClsAndJars(
                    jarList, CollUtil.newArrayList(URLUtils.getURLs(otherPluginsFiles)));
        } catch (Exception e) {
            logger.error("add configuration failed;reason:{}", LogUtil.getError(e));
            throw new RuntimeException(e);
        }

        process.info(
                StrUtil.format(
                        "A total of {} UDF have been Init.", udfList.size() + pyUdfFile.size()));
        process.info("Initializing Flink UDF...Finish");
    }

    private void writeManifest(Integer taskId, List<URL> jarPaths) {
        FlinkUdfManifest flinkUdfManifest = new FlinkUdfManifest();
        flinkUdfManifest.setJars(jarPaths);
        flinkUdfManifest.setPythonFiles(
                FlinkUdfPathContextHolder.getPyUdfFile().stream()
                        .map(URLUtil::getURL)
                        .collect(Collectors.toList()));

        FileUtil.writeUtf8String(
                JSONUtil.toJsonStr(flinkUdfManifest),
                PathConstant.getUdfPackagePath(taskId) + PathConstant.DEP_MANIFEST);
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
        CustomTableEnvironmentContext.clear();
        RowLevelPermissionsContext.clear();
        return false;
    }

    public void initClassLoader(JobConfig config) {
        if (CollUtil.isNotEmpty(config.getConfig())) {
            String pipelineJars = config.getConfig().get(PipelineOptions.JARS.key());
            String classpaths = config.getConfig().get(PipelineOptions.CLASSPATHS.key());
            // add custom jar path
            if (StrUtil.isNotBlank(pipelineJars)) {
                String[] paths = pipelineJars.split(",");
                for (String path : paths) {
                    File file = FileUtil.file(path);
                    if (!file.exists()) {
                        throw new DinkyException("file: " + path + " .not exists! ");
                    }
                    FlinkUdfPathContextHolder.addUdfPath(file);
                }
            }
            // add custom classpath
            if (StrUtil.isNotBlank(classpaths)) {
                String[] paths = pipelineJars.split(",");
                for (String path : paths) {
                    File file = FileUtil.file(path);
                    if (!file.exists()) {
                        throw new DinkyException("file: " + path + " .not exists! ");
                    }
                    FlinkUdfPathContextHolder.addOtherPlugins(file);
                }
            }
        }

        DinkyClassLoaderContextHolder.get()
                .addURL(
                        CollUtil.addAll(
                                FlinkUdfPathContextHolder.getUdfFile(),
                                FlinkUdfPathContextHolder.getOtherPluginsFiles()));
    }

    public JobResult executeSql(String statement) {
        initClassLoader(config);
        ProcessEntity process = ProcessContextHolder.getProcess();
        Job job = Job.init(runMode, config, executorSetting, executor, statement, useGateway);
        if (!useGateway) {
            job.setJobManagerAddress(environmentSetting.getAddress());
        }
        JobContextHolder.setJob(job);
        ready();
        String currentSql = "";
        JobParam jobParam =
                Explainer.build(executor, useStatementSet, sqlSeparator)
                        .pretreatStatements(SqlUtil.getStatements(statement, sqlSeparator));
        try {
            initUDF(jobParam.getUdfList(), runMode, config.getTaskId());

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
                    job.setResult(InsertResult.success(gatewayResult.getId()));
                    job.setJobId(gatewayResult.getId());
                    job.setJids(gatewayResult.getJids());
                    job.setJobManagerAddress(formatAddress(gatewayResult.getWebURL()));
                    if (gatewayResult.isSuccess()) {
                        job.setStatus(Job.JobStatus.SUCCESS);
                    } else {
                        job.setStatus(Job.JobStatus.FAILED);
                        job.setError(gatewayResult.getError());
                    }
                } else if (useStatementSet && !useGateway) {
                    List<String> inserts = new ArrayList<>();
                    for (StatementParam item : jobParam.getTrans()) {
                        if (item.getType().equals(SqlType.INSERT)
                                || item.getType().equals(SqlType.CTAS)) {
                            inserts.add(item.getValue());
                        }
                    }
                    if (inserts.size() > 0) {
                        currentSql = String.join(sqlSeparator, inserts);
                        // Remote mode can get the table result.
                        TableResult tableResult = executor.executeStatementSet(inserts);
                        if (tableResult.getJobClient().isPresent()) {
                            job.setJobId(tableResult.getJobClient().get().getJobID().toHexString());
                            job.setJids(
                                    new ArrayList<String>() {

                                        {
                                            add(job.getJobId());
                                        }
                                    });
                        }
                        if (config.isUseResult()) {
                            // Build insert result.
                            IResult result =
                                    ResultBuilder.build(
                                                    SqlType.INSERT,
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
                    currentSql = String.join(sqlSeparator, inserts);
                    GatewayResult gatewayResult = submitByGateway(inserts);
                    job.setResult(InsertResult.success(gatewayResult.getId()));
                    job.setJobId(gatewayResult.getId());
                    job.setJids(gatewayResult.getJids());
                    job.setJobManagerAddress(formatAddress(gatewayResult.getWebURL()));
                    if (gatewayResult.isSuccess()) {
                        job.setStatus(Job.JobStatus.SUCCESS);
                    } else {
                        job.setStatus(Job.JobStatus.FAILED);
                        job.setError(gatewayResult.getError());
                    }
                } else {
                    for (StatementParam item : jobParam.getTrans()) {
                        currentSql = item.getValue();
                        FlinkInterceptorResult flinkInterceptorResult =
                                FlinkInterceptor.build(executor, item.getValue());
                        if (Asserts.isNotNull(flinkInterceptorResult.getTableResult())) {
                            if (config.isUseResult()) {
                                IResult result =
                                        ResultBuilder.build(
                                                        item.getType(),
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
                                    job.setJobId(
                                            tableResult
                                                    .getJobClient()
                                                    .get()
                                                    .getJobID()
                                                    .toHexString());
                                    job.setJids(
                                            new ArrayList<String>() {

                                                {
                                                    add(job.getJobId());
                                                }
                                            });
                                }
                                if (config.isUseResult()) {
                                    IResult result =
                                            ResultBuilder.build(
                                                            item.getType(),
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
                                    SavepointRestoreSettings.forPath(
                                            config.getSavePointPath(), true));
                        }
                        gatewayResult =
                                Gateway.build(config.getGatewayConfig()).submitJobGraph(jobGraph);
                    }
                    job.setResult(InsertResult.success(gatewayResult.getId()));
                    job.setJobId(gatewayResult.getId());
                    job.setJids(gatewayResult.getJids());
                    job.setJobManagerAddress(formatAddress(gatewayResult.getWebURL()));

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
                        job.setJids(
                                new ArrayList<String>() {

                                    {
                                        add(job.getJobId());
                                    }
                                });
                    }
                    if (config.isUseResult()) {
                        IResult result =
                                ResultBuilder.build(
                                                SqlType.EXECUTE,
                                                config.getMaxRowNum(),
                                                config.isUseChangeLog(),
                                                config.isUseAutoCancel(),
                                                executor.getTimeZone())
                                        .getResult(null);
                        job.setResult(result);
                    }
                }
            }
            job.setEndTime(LocalDateTime.now());
            if (job.isFailed()) {
                failed();
            } else {
                job.setStatus(Job.JobStatus.SUCCESS);
                success();
            }
        } catch (Exception e) {
            String error =
                    LogUtil.getError(
                            "Exception in executing FlinkSQL:\n" + addLineNumber(currentSql), e);
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

    public String addLineNumber(String input) {
        String[] lines = input.split("\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            sb.append(String.format("%-4d", i + 1));
            sb.append("  ");
            sb.append(lines[i]);
            sb.append("\n");
        }
        return sb.toString();
    }

    private GatewayResult submitByGateway(List<String> inserts) {
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
                jobGraph.setSavepointRestoreSettings(
                        SavepointRestoreSettings.forPath(config.getSavePointPath(), true));
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
                result =
                        ResultBuilder.build(
                                        operationType,
                                        config.getMaxRowNum(),
                                        false,
                                        false,
                                        executor.getTimeZone())
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

    public ExplainResult explainSql(String statement) {
        return Explainer.build(executor, useStatementSet, sqlSeparator)
                .initialize(this, config, statement)
                .explainSql(statement);
    }

    public ObjectNode getStreamGraph(String statement) {
        return Explainer.build(executor, useStatementSet, sqlSeparator)
                .initialize(this, config, statement)
                .getStreamGraph(statement);
    }

    public String getJobPlanJson(String statement) {
        return Explainer.build(executor, useStatementSet, sqlSeparator)
                .initialize(this, config, statement)
                .getJobPlanInfo(statement)
                .getJsonPlan();
    }

    public boolean cancel(String jobId) {
        if (useGateway && !useRestAPI) {
            config.getGatewayConfig()
                    .setFlinkConfig(
                            FlinkConfig.build(jobId, ActionType.CANCEL.getValue(), null, null));
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
            config.getGatewayConfig()
                    .setFlinkConfig(
                            FlinkConfig.build(
                                    jobId, ActionType.SAVEPOINT.getValue(), savePointType, null));
            return Gateway.build(config.getGatewayConfig()).savepointJob(savePoint);
        } else {
            return FlinkAPI.build(config.getAddress())
                    .savepoints(jobId, savePointType, config.getConfig());
        }
    }

    public static void killCluster(GatewayConfig gatewayConfig, String appId) {
        gatewayConfig.getClusterConfig().setAppId(appId);
        Gateway.build(gatewayConfig).killCluster();
    }

    public static GatewayResult deploySessionCluster(GatewayConfig gatewayConfig) {
        return Gateway.build(gatewayConfig).deployCluster();
    }

    public JobResult executeJar() {
        ProcessEntity process = ProcessContextHolder.getProcess();
        Job job = Job.init(runMode, config, executorSetting, executor, null, useGateway);
        JobContextHolder.setJob(job);
        ready();
        try {
            GatewayResult gatewayResult = Gateway.build(config.getGatewayConfig()).submitJar();
            job.setResult(InsertResult.success(gatewayResult.getId()));
            job.setJobId(gatewayResult.getId());
            job.setJids(gatewayResult.getJids());
            job.setJobManagerAddress(formatAddress(gatewayResult.getWebURL()));
            job.setEndTime(LocalDateTime.now());

            if (gatewayResult.isSuccess()) {
                job.setStatus(Job.JobStatus.SUCCESS);
                success();
            } else {
                job.setError(gatewayResult.getError());
                job.setStatus(Job.JobStatus.FAILED);
                failed();
            }
        } catch (Exception e) {
            String error =
                    LogUtil.getError(
                            "Exception in executing Jar：\n"
                                    + config.getGatewayConfig().getAppConfig().getUserJarPath(),
                            e);
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
            sb.append(
                    "set "
                            + CoreOptions.DEFAULT_PARALLELISM.key()
                            + " = "
                            + config.getParallelism()
                            + ";\r\n");
        }
        if (Asserts.isNotNull(config.getCheckpoint())) {
            sb.append(
                    "set "
                            + ExecutionCheckpointingOptions.CHECKPOINTING_INTERVAL.key()
                            + " = "
                            + config.getCheckpoint()
                            + ";\r\n");
        }
        if (Asserts.isNotNullString(config.getSavePointPath())) {
            sb.append(
                    "set "
                            + SavepointConfigOptions.SAVEPOINT_PATH
                            + " = "
                            + config.getSavePointPath()
                            + ";\r\n");
        }
        if (Asserts.isNotNull(config.getGatewayConfig())
                && Asserts.isNotNull(
                        config.getGatewayConfig().getFlinkConfig().getConfiguration())) {
            for (Map.Entry<String, String> entry :
                    config.getGatewayConfig().getFlinkConfig().getConfiguration().entrySet()) {
                sb.append("set " + entry.getKey() + " = " + entry.getValue() + ";\r\n");
            }
        }

        switch (GatewayType.get(config.getType())) {
            case YARN_PER_JOB:
            case YARN_APPLICATION:
                sb.append(
                        "set "
                                + DeploymentOptions.TARGET.key()
                                + " = "
                                + GatewayType.get(config.getType()).getLongValue()
                                + ";\r\n");
                if (Asserts.isNotNull(config.getGatewayConfig())) {
                    sb.append(
                            "set "
                                    + YarnConfigOptions.PROVIDED_LIB_DIRS.key()
                                    + " = "
                                    + Collections.singletonList(
                                            config.getGatewayConfig()
                                                    .getClusterConfig()
                                                    .getFlinkLibPath())
                                    + ";\r\n");
                }
                if (Asserts.isNotNull(config.getGatewayConfig())
                        && Asserts.isNotNullString(
                                config.getGatewayConfig().getFlinkConfig().getJobName())) {
                    sb.append(
                            "set "
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

    public Executor getExecutor() {
        return executor;
    }
}
