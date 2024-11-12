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

import static org.dinky.function.util.UDFUtil.*;

import org.dinky.assertion.Asserts;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.executor.CustomTableEnvironment;
import org.dinky.function.data.model.UDF;
import org.dinky.function.util.UDFUtil;
import org.dinky.job.JobBuilder;
import org.dinky.job.JobManager;
import org.dinky.job.StatementParam;
import org.dinky.parser.SqlType;
import org.dinky.trans.ddl.CustomSetOperation;
import org.dinky.trans.parse.AddFileSqlParseStrategy;
import org.dinky.trans.parse.AddJarSqlParseStrategy;
import org.dinky.utils.LogUtil;
import org.dinky.utils.SqlUtil;
import org.dinky.utils.URLUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * JobDDLBuilder
 *
 */
@Slf4j
public class JobDDLBuilder extends JobBuilder {

    public JobDDLBuilder(JobManager jobManager) {
        super(jobManager);
    }

    public static JobDDLBuilder build(JobManager jobManager) {
        return new JobDDLBuilder(jobManager);
    }

    @Override
    public void run() throws Exception {
        List<UDF> udfList = new ArrayList<>();
        for (StatementParam item : jobParam.getDdl()) {
            jobManager.setCurrentSql(item.getValue());
            switch (item.getType()) {
                case SET:
                    executeSet(item.getValue());
                    break;
                case ADD:
                    executeAdd(item.getValue());
                    break;
                case ADD_FILE:
                    executeAddFile(item.getValue());
                    break;
                case ADD_JAR:
                    executeAddJar(item.getValue());
                    break;
                case CREATE:
                    if (UDFUtil.isUdfStatement(item.getValue())) {
                        udfList.add(UDFUtil.toUDF(item.getValue(), executor.getDinkyClassLoader()));
                    } else {
                        executor.executeSql(item.getValue());
                    }
                    break;
                default:
                    executor.executeSql(item.getValue());
            }
        }
        if (!udfList.isEmpty()) {
            executeCreateFunction(udfList);
        }
    }

    @Override
    public List<SqlExplainResult> explain() {
        List<SqlExplainResult> sqlExplainResults = new ArrayList<>();
        if (Asserts.isNullCollection(jobParam.getDdl())) {
            return sqlExplainResults;
        }
        List<UDF> udfList = new ArrayList<>();
        List<String> udfStatements = new ArrayList<>();
        for (StatementParam item : jobParam.getDdl()) {
            jobManager.setCurrentSql(item.getValue());
            SqlExplainResult.Builder resultBuilder = SqlExplainResult.Builder.newBuilder();
            try {
                SqlExplainResult recordResult = null;
                switch (item.getType()) {
                    case SET:
                        recordResult = explainSet(item.getValue());
                        break;
                    case ADD:
                        recordResult = explainAdd(item.getValue());
                        break;
                    case ADD_FILE:
                        recordResult = explainAddFile(item.getValue());
                        break;
                    case ADD_JAR:
                        recordResult = explainAddJar(item.getValue());
                        break;
                    case CREATE:
                        if (UDFUtil.isUdfStatement(item.getValue())) {
                            udfList.add(UDFUtil.toUDF(item.getValue(), executor.getDinkyClassLoader()));
                            udfStatements.add(item.getValue());
                        } else {
                            recordResult = explainOtherDDL(item.getValue());
                        }
                        break;
                    default:
                        recordResult = explainOtherDDL(item.getValue());
                }
                if (Asserts.isNull(recordResult) || recordResult.isInvalid()) {
                    continue;
                }
                resultBuilder = SqlExplainResult.newBuilder(recordResult)
                        .type(item.getType().getType());
            } catch (Exception e) {
                String error = StrFormatter.format(
                        "Exception in executing FlinkSQL:\n{}\n{}",
                        SqlUtil.addLineNumber(item.getValue()),
                        LogUtil.getError(e));
                resultBuilder
                        .type(item.getType().getType())
                        .error(error)
                        .explainTrue(false)
                        .explainTime(LocalDateTime.now())
                        .sql(item.getValue());
                log.error(error);
                sqlExplainResults.add(resultBuilder.build());
            }
            resultBuilder
                    .type(item.getType().getType())
                    .explainTrue(true)
                    .explainTime(LocalDateTime.now())
                    .sql(item.getValue());
            sqlExplainResults.add(resultBuilder.build());
        }
        if (!udfList.isEmpty()) {
            SqlExplainResult.Builder resultBuilder = SqlExplainResult.Builder.newBuilder();
            String udfStatement = StringUtils.join(udfStatements, ";\n");
            try {
                explainCreateFunction(udfList);
            } catch (Exception e) {
                String error = StrFormatter.format(
                        "Exception in executing CreateFunction:\n{}\n{}",
                        SqlUtil.addLineNumber(udfStatement),
                        LogUtil.getError(e));
                resultBuilder
                        .type(SqlType.CREATE.getType())
                        .error(error)
                        .explainTrue(false)
                        .explainTime(LocalDateTime.now())
                        .sql(udfStatement);
                log.error(error);
                sqlExplainResults.add(resultBuilder.build());
            }
            resultBuilder
                    .type(SqlType.CREATE.getType())
                    .explainTrue(true)
                    .explainTime(LocalDateTime.now())
                    .sql(udfStatement);
            sqlExplainResults.add(resultBuilder.build());
        }
        return sqlExplainResults;
    }

    private void executeSet(String statement) {
        CustomSetOperation customSetOperation = new CustomSetOperation(statement);
        customSetOperation.execute(executor.getCustomTableEnvironment());
    }

    private void executeAdd(String statement) {
        AddJarSqlParseStrategy.getAllFilePath(statement)
                .forEach(t -> jobManager.getUdfPathContextHolder().addOtherPlugins(t));
        (executor.getDinkyClassLoader())
                .addURLs(URLUtils.getURLs(jobManager.getUdfPathContextHolder().getOtherPluginsFiles()));
    }

    private void executeAddFile(String statement) {
        AddFileSqlParseStrategy.getAllFilePath(statement)
                .forEach(t -> jobManager.getUdfPathContextHolder().addFile(t));
        (executor.getDinkyClassLoader())
                .addURLs(URLUtils.getURLs(jobManager.getUdfPathContextHolder().getFiles()));
    }

    private void executeAddJar(String statement) {
        Configuration combinationConfig = getCombinationConfig();
        FileSystem.initialize(combinationConfig, null);
        executor.executeSql(statement);
    }

    private void executeCreateFunction(List<UDF> udfList) {
        Integer taskId = config.getTaskId();
        if (taskId == null) {
            taskId = -RandomUtil.randomInt(0, 1000);
        }
        // 1. Obtain the path of the jar package and inject it into the remote environment
        List<File> jarFiles =
                new ArrayList<>(jobManager.getUdfPathContextHolder().getAllFileSet());

        String[] userCustomUdfJarPath = UDFUtil.initJavaUDF(udfList, taskId);
        String[] jarPaths = CollUtil.removeNull(jarFiles).stream()
                .map(File::getAbsolutePath)
                .toArray(String[]::new);
        if (GATEWAY_TYPE_MAP.get(SESSION).contains(runMode)) {
            config.setJarFiles(jarPaths);
        }

        // 2.Compile Python
        String[] pyPaths = UDFUtil.initPythonUDF(
                udfList, runMode, config.getTaskId(), executor.getTableConfig().getConfiguration());

        executor.initUDF(userCustomUdfJarPath);
        executor.initUDF(jarPaths);

        if (ArrayUtil.isNotEmpty(pyPaths)) {
            for (String pyPath : pyPaths) {
                if (StrUtil.isNotBlank(pyPath)) {
                    jarFiles.add(new File(pyPath));
                    jobManager.getUdfPathContextHolder().addPyUdfPath(new File(pyPath));
                }
            }
        }
        if (ArrayUtil.isNotEmpty(userCustomUdfJarPath)) {
            for (String jarPath : userCustomUdfJarPath) {
                if (StrUtil.isNotBlank(jarPath)) {
                    jarFiles.add(new File(jarPath));
                    jobManager.getUdfPathContextHolder().addUdfPath(new File(jarPath));
                }
            }
        }

        Set<File> pyUdfFile = jobManager.getUdfPathContextHolder().getPyUdfFile();
        executor.initPyUDF(
                SystemConfiguration.getInstances().getPythonHome(),
                pyUdfFile.stream().map(File::getAbsolutePath).toArray(String[]::new));
        if (GATEWAY_TYPE_MAP.get(YARN).contains(runMode)) {
            config.getGatewayConfig().setJarPaths(ArrayUtil.append(jarPaths, pyPaths));
        }

        try {
            List<URL> jarList = CollUtil.newArrayList(URLUtils.getURLs(jarFiles));
            // 3.Write the required files for UDF
            UDFUtil.writeManifest(taskId, jarList, jobManager.getUdfPathContextHolder());
            UDFUtil.addConfigurationClsAndJars(
                    jobManager.getExecutor().getCustomTableEnvironment(),
                    jarList,
                    CollUtil.newArrayList(URLUtils.getURLs(jarFiles)));
        } catch (Exception e) {
            throw new RuntimeException("add configuration failed: ", e);
        }

        log.info(StrUtil.format("A total of {} UDF have been Init.", udfList.size() + pyUdfFile.size()));
        log.info("Initializing Flink UDF...Finish");
    }

    private Configuration getCombinationConfig() {
        CustomTableEnvironment cte = executor.getCustomTableEnvironment();
        Configuration rootConfig = cte.getRootConfiguration();
        Configuration config = cte.getConfig().getConfiguration();
        Configuration combinationConfig = new Configuration();
        combinationConfig.addAll(rootConfig);
        combinationConfig.addAll(config);
        return combinationConfig;
    }

    private SqlExplainResult explainSet(String statement) {
        SqlExplainResult.Builder resultBuilder = SqlExplainResult.Builder.newBuilder();
        CustomSetOperation customSetOperation = new CustomSetOperation(statement);
        String explain = customSetOperation.explain(executor.getCustomTableEnvironment());
        customSetOperation.execute(executor.getCustomTableEnvironment());
        return resultBuilder.parseTrue(true).explainTrue(true).explain(explain).build();
    }

    private SqlExplainResult explainAdd(String statement) {
        SqlExplainResult.Builder resultBuilder = SqlExplainResult.Builder.newBuilder();
        executeAdd(statement);
        String explain = Arrays.toString(
                URLUtils.getURLs(jobManager.getUdfPathContextHolder().getOtherPluginsFiles()));
        return resultBuilder.parseTrue(true).explainTrue(true).explain(explain).build();
    }

    private SqlExplainResult explainAddFile(String statement) {
        SqlExplainResult.Builder resultBuilder = SqlExplainResult.Builder.newBuilder();
        executeAddFile(statement);
        String explain = Arrays.toString(
                URLUtils.getURLs(jobManager.getUdfPathContextHolder().getFiles()));
        return resultBuilder.parseTrue(true).explainTrue(true).explain(explain).build();
    }

    private SqlExplainResult explainAddJar(String statement) {
        SqlExplainResult sqlExplainResult = executor.explainSqlRecord(statement);
        executeAddJar(statement);
        return sqlExplainResult;
    }

    private SqlExplainResult explainCreateFunction(List<UDF> udfList) {
        SqlExplainResult.Builder resultBuilder = SqlExplainResult.Builder.newBuilder();
        executeCreateFunction(udfList);
        String explain = udfList.toString();
        return resultBuilder.parseTrue(true).explainTrue(true).explain(explain).build();
    }

    private SqlExplainResult explainOtherDDL(String statement) {
        SqlExplainResult sqlExplainResult = executor.explainSqlRecord(statement);
        executor.executeSql(statement);
        return sqlExplainResult;
    }
}
