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

package org.dinky.app.flinksql;

import org.dinky.app.db.DBUtil;
import org.dinky.app.model.StatementParam;
import org.dinky.app.model.SysConfig;
import org.dinky.app.util.FlinkAppUtil;
import org.dinky.assertion.Asserts;
import org.dinky.classloader.DinkyClassLoader;
import org.dinky.config.Dialect;
import org.dinky.constant.CustomerConfigureOptions;
import org.dinky.constant.FlinkSQLConstant;
import org.dinky.data.app.AppParamConfig;
import org.dinky.data.app.AppTask;
import org.dinky.data.constant.DirConstant;
import org.dinky.data.enums.GatewayType;
import org.dinky.data.job.SqlType;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.executor.Executor;
import org.dinky.executor.ExecutorConfig;
import org.dinky.executor.ExecutorFactory;
import org.dinky.resource.BaseResourceManager;
import org.dinky.trans.Operations;
import org.dinky.trans.dml.ExecuteJarOperation;
import org.dinky.trans.parse.AddFileSqlParseStrategy;
import org.dinky.trans.parse.AddJarSqlParseStrategy;
import org.dinky.trans.parse.ExecuteJarParseStrategy;
import org.dinky.url.RsURLStreamHandlerFactory;
import org.dinky.utils.FlinkStreamEnvironmentUtil;
import org.dinky.utils.SqlUtil;
import org.dinky.utils.ZipUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.Plan;
import org.apache.flink.api.dag.Pipeline;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.python.PythonOptions;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.TableResult;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * FlinkSQLFactory
 *
 * @since 2021/10/27
 */
@Slf4j
public class Submitter {
    public static Executor executor = null;

    private static void initSystemConfiguration() throws SQLException {
        SystemConfiguration systemConfiguration = SystemConfiguration.getInstances();
        List<SysConfig> sysConfigList = DBUtil.getSysConfigList();
        Map<String, String> configMap =
                CollUtil.toMap(sysConfigList, new HashMap<>(), SysConfig::getName, SysConfig::getValue);
        systemConfiguration.initSetConfiguration(configMap);
        systemConfiguration.initExpressionVariableList(configMap);
    }

    public static void submit(AppParamConfig config) throws SQLException {
        initSystemConfiguration();
        BaseResourceManager.initResourceManager();
        URL.setURLStreamHandlerFactory(new RsURLStreamHandlerFactory());
        log.info("{} Start Submit Job:{}", LocalDateTime.now(), config.getTaskId());

        AppTask appTask = DBUtil.getTask(config.getTaskId());

        ExecutorConfig executorConfig = ExecutorConfig.builder()
                .type(appTask.getType())
                .checkpoint(appTask.getCheckPoint())
                .parallelism(appTask.getParallelism())
                .useSqlFragment(appTask.getFragment())
                .useStatementSet(appTask.getStatementSet())
                .useBatchModel(appTask.getBatchModel())
                .savePointPath(appTask.getSavePointPath())
                .jobName(appTask.getName())
                // 此处不应该再设置config，否则破坏了正常配置优先级顺序
                // .config(JsonUtils.toMap(appTask.getConfigJson()))
                .build();

        executor = ExecutorFactory.buildAppStreamExecutor(
                executorConfig, new WeakReference<>(DinkyClassLoader.build()).get());

        // 加载第三方jar //TODO 这里有问题，需要修一修
        loadDep(appTask.getType(), config.getTaskId(), executorConfig);
        log.info("The job configuration is as follows: {}", executorConfig);

        String sql = readSql(executor);
        String[] statements = SqlUtil.getStatements(sql);
        Optional<JobClient> jobClient = Optional.empty();
        try {
            if (Dialect.FLINK_JAR == appTask.getDialect()) {
                jobClient = executeJarJob(appTask.getType(), executor, statements);
            } else {
                jobClient = executeJob(executor, statements);
            }
        } finally {
            log.info("Start Monitor Job");
            if (jobClient.isPresent()) {
                FlinkAppUtil.monitorFlinkTask(jobClient.get(), config.getTaskId());
            } else {
                log.error("jobClient is empty, can not  monitor job");
                // FlinkAppUtil.monitorFlinkTask(Submitter.executor, config.getTaskId());
            }
        }
    }

    private static String readSql(Executor executor) {
        Configuration configuration =
                (Configuration) executor.getStreamExecutionEnvironment().getConfiguration();
        String sqlFileName = configuration.get(CustomerConfigureOptions.EXEC_SQL_FILE);
        String confDir = configuration.get(CustomerConfigureOptions.DINKY_CONF_DIR);
        File sqlFile = new File(sqlFileName);
        if (!sqlFile.exists()) {
            sqlFile = new File(confDir, sqlFileName);
            if (!sqlFile.exists()) {
                log.error("sql file not found,current dir:{},conf dir:{}", DirConstant.getRootPath(), confDir);
                throw new RuntimeException("sql file not found");
            }
        }
        log.info("start read sql file path:{}", sqlFile.getAbsolutePath());
        return FileUtil.readString(sqlFile, Charset.defaultCharset());
    }

    private static void loadDep(String type, Integer taskId, ExecutorConfig executorConfig) {
        String dinkyAddr = SystemConfiguration.getInstances().getDinkyAddr().getValue();
        if (StringUtils.isBlank(dinkyAddr)) {
            return;
        }

        if (GatewayType.get(type).isKubernetesApplicationMode()) {
            try {
                String httpJar = dinkyAddr + "/download/downloadDepJar/" + taskId;
                log.info("下载依赖 http-url为：{}", httpJar);
                String flinkHome = System.getenv("FLINK_HOME");
                String usrlib = flinkHome + "/usrlib";
                FileUtils.forceMkdir(new File(usrlib));
                String depZip = flinkHome + "/dep.zip";
                String depPath = flinkHome + "/dep";
                downloadFile(httpJar, depZip);
                if (FileUtil.exist(depPath)) {
                    ZipUtils.unzip(depZip, depPath);
                    log.info("download dep success, include :{}", String.join(",", FileUtil.listFileNames(depPath)));
                    // move all jar
                    if (FileUtil.isDirectory(depPath + "/jar/")) {
                        FileUtil.listFileNames(depPath + "/jar").forEach(f -> {
                            FileUtil.move(FileUtil.file(depPath + "/jar/" + f), FileUtil.file(usrlib + "/" + f), true);
                        });
                        if (FileUtil.isDirectory(usrlib)) {
                            URL[] jarUrls = FileUtil.listFileNames(usrlib).stream()
                                    .map(f -> URLUtil.getURL(FileUtil.file(usrlib, f)))
                                    .toArray(URL[]::new);
                            addURLs(jarUrls);
                            executor.getCustomTableEnvironment()
                                    .addJar(FileUtil.file(usrlib).listFiles());
                        }
                    }
                    if (FileUtil.isDirectory(depPath + "/py/")) {
                        URL[] pyUrls = FileUtil.listFileNames(depPath + "/py/").stream()
                                .map(f -> URLUtil.getURL(FileUtil.file(depPath + "/py/", f)))
                                .toArray(URL[]::new);
                        if (ArrayUtil.isNotEmpty(pyUrls)) {
                            executor.getCustomTableEnvironment()
                                    .addConfiguration(
                                            PythonOptions.PYTHON_FILES,
                                            Arrays.stream(pyUrls)
                                                    .map(URL::toString)
                                                    .collect(Collectors.joining(",")));
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void addURLs(URL[] jarUrls) {
        Thread.currentThread().setContextClassLoader(new DinkyClassLoader(new URL[] {}));
        URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
        try {
            Method add = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            add.setAccessible(true);
            for (URL jarUrl : jarUrls) {
                add.invoke(urlClassLoader, jarUrl);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean downloadFile(String url, String path) throws IOException {
        try {
            HttpUtil.downloadFile(url, path);
            return true;
        } catch (Exception e) {
            log.error("download failed, Reason:", e);
            return false;
        }
    }

    @SneakyThrows
    public static Optional<JobClient> executeJarJob(String type, Executor executor, String[] statements) {
        Optional<JobClient> jobClient = Optional.empty();

        for (String statement : statements) {
            if (ExecuteJarParseStrategy.INSTANCE.match(statement)) {
                ExecuteJarOperation executeJarOperation = new ExecuteJarOperation(statement);

                ReadableConfig configuration =
                        executor.getStreamExecutionEnvironment().getConfiguration();
                List<String> jars = configuration.get(PipelineOptions.JARS);
                List<URL> jarsUrl = jars.stream().map(URLUtil::getURL).collect(Collectors.toList());
                Pipeline pipeline = executeJarOperation.getStreamGraph(executor.getCustomTableEnvironment(), jarsUrl);
                if (pipeline instanceof StreamGraph) {
                    // stream job
                    StreamGraph streamGraph = (StreamGraph) pipeline;
                    streamGraph
                            .getExecutionConfig()
                            .configure(configuration, Thread.currentThread().getContextClassLoader());
                    streamGraph.getCheckpointConfig().configure(configuration);
                    streamGraph.setJobName(executor.getExecutorConfig().getJobName());
                    String savePointPath = executor.getExecutorConfig().getSavePointPath();
                    if (Asserts.isNotNullString(savePointPath)) {
                        streamGraph.setSavepointRestoreSettings(SavepointRestoreSettings.forPath(
                                savePointPath,
                                configuration.get(SavepointConfigOptions.SAVEPOINT_IGNORE_UNCLAIMED_STATE)));
                    }
                } else if (pipeline instanceof Plan) {
                    // batch job
                    Plan plan = (Plan) pipeline;
                    plan.getExecutionConfig()
                            .configure(configuration, Thread.currentThread().getContextClassLoader());
                    plan.setJobName(executor.getExecutorConfig().getJobName());
                }

                JobClient client =
                        FlinkStreamEnvironmentUtil.executeAsync(pipeline, executor.getStreamExecutionEnvironment());
                jobClient = Optional.of(client);
                break;
            }
            if (Operations.getOperationType(statement) == SqlType.ADD) {
                File[] info = AddJarSqlParseStrategy.getInfo(statement);
                Arrays.stream(info).forEach(executor.getDinkyClassLoader().getUdfPathContextHolder()::addOtherPlugins);
                if (GatewayType.get(type).isKubernetesApplicationMode()) {
                    executor.addJar(info);
                }
            } else if (Operations.getOperationType(statement) == SqlType.ADD_FILE) {
                File[] info = AddFileSqlParseStrategy.getInfo(statement);
                Arrays.stream(info).forEach(executor.getDinkyClassLoader().getUdfPathContextHolder()::addFile);
                if (GatewayType.get(type).isKubernetesApplicationMode()) {
                    executor.addJar(info);
                }
            }
        }
        return jobClient;
    }

    public static Optional<JobClient> executeJob(Executor executor, String[] statements) {
        Optional<JobClient> jobClient = Optional.empty();

        ExecutorConfig executorConfig = executor.getExecutorConfig();
        List<StatementParam> ddl = new ArrayList<>();
        List<StatementParam> trans = new ArrayList<>();
        List<StatementParam> execute = new ArrayList<>();

        for (String item : statements) {
            if (item.isEmpty()) {
                continue;
            }

            SqlType operationType = Operations.getOperationType(item);
            if (operationType.equals(SqlType.INSERT) || operationType.equals(SqlType.SELECT)) {
                trans.add(new StatementParam(item, operationType));
                if (!executorConfig.isUseStatementSet()) {
                    break;
                }
            } else if (operationType.equals(SqlType.EXECUTE)) {
                execute.add(new StatementParam(item, operationType));
                if (!executorConfig.isUseStatementSet()) {
                    break;
                }
            } else {
                ddl.add(new StatementParam(item, operationType));
            }
        }

        for (StatementParam item : ddl) {
            log.info("Executing FlinkSQL: {}", item.getValue());
            executor.executeSql(item.getValue());
            log.info("Execution succeeded.");
        }

        if (!trans.isEmpty()) {
            if (executorConfig.isUseStatementSet()) {
                List<String> inserts = new ArrayList<>();
                for (StatementParam item : trans) {
                    if (item.getType().equals(SqlType.INSERT)) {
                        inserts.add(item.getValue());
                    }
                }
                log.info("Executing FlinkSQL statement set: {}", String.join(FlinkSQLConstant.SEPARATOR, inserts));
                TableResult tableResult = executor.executeStatementSet(inserts);
                jobClient = tableResult.getJobClient();
                log.info("Execution succeeded.");
            } else {
                // UseStatementSet defaults to true, where the logic is never executed
                StatementParam item = trans.get(0);
                log.info("Executing FlinkSQL: {}", item.getValue());
                TableResult tableResult = executor.executeSql(item.getValue());
                jobClient = tableResult.getJobClient();
                log.info("Execution succeeded.");
            }
        }

        if (!execute.isEmpty()) {
            List<String> executes = new ArrayList<>();
            for (StatementParam item : execute) {
                executes.add(item.getValue());
                executor.executeSql(item.getValue());
                if (!executorConfig.isUseStatementSet()) {
                    break;
                }
            }

            log.info(
                    "The FlinkSQL statement set is being executed： {}",
                    String.join(FlinkSQLConstant.SEPARATOR, executes));
            try {
                JobClient client = executor.executeAsync(executorConfig.getJobName());
                jobClient = Optional.of(client);
                log.info("The execution was successful");
            } catch (Exception e) {
                log.error("Execution failed, {}", e.getMessage(), e);
            }
        }
        log.info("{} The task is successfully submitted", LocalDateTime.now());
        return jobClient;
    }
}
