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

package org.dinky.job.runner;

import static org.dinky.function.util.UDFUtil.*;

import org.dinky.assertion.Asserts;
import org.dinky.data.job.JobStatement;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.executor.CustomTableEnvironment;
import org.dinky.function.data.model.UDF;
import org.dinky.function.util.UDFUtil;
import org.dinky.job.JobManager;
import org.dinky.trans.parse.AddFileSqlParseStrategy;
import org.dinky.trans.parse.AddJarSqlParseStrategy;
import org.dinky.utils.LogUtil;
import org.dinky.utils.SqlUtil;
import org.dinky.utils.URLUtils;

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
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobDDLRunner extends AbstractJobRunner {

    private List<String> udfStatements = new ArrayList<>();

    public JobDDLRunner(JobManager jobManager) {
        this.jobManager = jobManager;
    }

    @Override
    public void run(JobStatement jobStatement) throws Exception {
        jobManager.setCurrentSql(jobStatement.getStatement());
        switch (jobStatement.getSqlType()) {
            case ADD:
                executeAdd(jobStatement.getStatement());
                break;
            case ADD_FILE:
                executeAddFile(jobStatement.getStatement());
                break;
            case ADD_JAR:
                executeAddJar(jobStatement.getStatement());
                break;
            case CREATE:
                if (UDFUtil.isUdfStatement(jobStatement.getStatement())) {
                    udfStatements.add(jobStatement.getStatement());
                    break;
                }
            default:
                jobManager.getExecutor().executeSql(jobStatement.getStatement());
        }
        if (jobStatement.isFinalCreateFunctionStatement() && !udfStatements.isEmpty()) {
            executeCreateFunction(udfStatements);
        }
    }

    @Override
    public SqlExplainResult explain(JobStatement jobStatement) {
        SqlExplainResult.Builder resultBuilder = SqlExplainResult.Builder.newBuilder();
        try {
            SqlExplainResult recordResult = null;
            switch (jobStatement.getSqlType()) {
                case ADD:
                    recordResult = explainAdd(jobStatement.getStatement());
                    break;
                case ADD_FILE:
                    recordResult = explainAddFile(jobStatement.getStatement());
                    break;
                case ADD_JAR:
                    recordResult = explainAddJar(jobStatement.getStatement());
                    break;
                case CREATE:
                    if (UDFUtil.isUdfStatement(jobStatement.getStatement())) {
                        udfStatements.add(jobStatement.getStatement());
                        recordResult = jobManager.getExecutor().explainSqlRecord(jobStatement.getStatement());
                        break;
                    }
                default:
                    recordResult = explainOtherDDL(jobStatement.getStatement());
            }
            if (jobStatement.isFinalCreateFunctionStatement() && !udfStatements.isEmpty()) {
                explainCreateFunction(jobStatement);
            }
            if (Asserts.isNull(recordResult)) {
                return resultBuilder.invalid().build();
            }
            resultBuilder = SqlExplainResult.newBuilder(recordResult);
            resultBuilder
                    .explainTrue(true)
                    .type(jobStatement.getSqlType().getType())
                    .sql(jobStatement.getStatement())
                    .index(jobStatement.getIndex());
        } catch (Exception e) {
            String error = LogUtil.getError(
                    "Exception in explaining FlinkSQL:\n" + SqlUtil.addLineNumber(jobStatement.getStatement()), e);
            resultBuilder
                    .error(error)
                    .explainTrue(false)
                    .type(jobStatement.getSqlType().getType())
                    .sql(jobStatement.getStatement())
                    .index(jobStatement.getIndex());
            log.error(error);
        } finally {
            resultBuilder.explainTime(LocalDateTime.now());
            return resultBuilder.build();
        }
    }

    private void executeAdd(String statement) {
        Set<File> allFilePath = AddJarSqlParseStrategy.getAllFilePath(statement);
        allFilePath.forEach(t -> jobManager.getUdfPathContextHolder().addOtherPlugins(t));
        (jobManager.getExecutor().getDinkyClassLoader())
                .addURLs(URLUtils.getURLs(jobManager.getUdfPathContextHolder().getOtherPluginsFiles()));
    }

    private void executeAddFile(String statement) {
        Set<File> allFilePath = AddFileSqlParseStrategy.getAllFilePath(statement);
        allFilePath.forEach(t -> jobManager.getUdfPathContextHolder().addFile(t));
        (jobManager.getExecutor().getDinkyClassLoader())
                .addURLs(URLUtils.getURLs(jobManager.getUdfPathContextHolder().getFiles()));
        jobManager.getExecutor().addJar(ArrayUtil.toArray(allFilePath, File.class));
    }

    private void executeAddJar(String statement) {
        Set<File> allFilePath = AddFileSqlParseStrategy.getAllFilePath(statement);
        Configuration combinationConfig = getCombinationConfig();
        FileSystem.initialize(combinationConfig, null);
        jobManager.getExecutor().addJar(ArrayUtil.toArray(allFilePath, File.class));
        jobManager.getExecutor().executeSql(statement);
    }

    private void executeCreateFunction(List<String> udfStatements) {
        List<UDF> udfList = new ArrayList<>();
        for (String statement : udfStatements) {
            UDF udf = toUDF(statement, jobManager.getExecutor().getDinkyClassLoader());
            if (Asserts.isNotNull(udf)) {
                udfList.add(UDFUtil.toUDF(statement, jobManager.getExecutor().getDinkyClassLoader()));
            }
        }
        if (!udfList.isEmpty()) {
            compileUDF(udfList);
        }
        for (String statement : udfStatements) {
            jobManager.getExecutor().executeSql(statement);
        }
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
        SqlExplainResult sqlExplainResult = jobManager.getExecutor().explainSqlRecord(statement);
        executeAddJar(statement);
        return sqlExplainResult;
    }

    private SqlExplainResult explainCreateFunction(JobStatement jobStatement) {
        udfStatements.add(jobStatement.getStatement());
        SqlExplainResult sqlExplainResult = explainOtherDDL(jobStatement.getStatement());
        if (jobStatement.isFinalCreateFunctionStatement()) {
            executeCreateFunction(udfStatements);
        }
        return sqlExplainResult;
    }

    private SqlExplainResult explainOtherDDL(String statement) {
        SqlExplainResult sqlExplainResult = jobManager.getExecutor().explainSqlRecord(statement);
        jobManager.getExecutor().executeSql(statement);
        return sqlExplainResult;
    }

    private Configuration getCombinationConfig() {
        CustomTableEnvironment cte = jobManager.getExecutor().getCustomTableEnvironment();
        Configuration rootConfig = cte.getRootConfiguration();
        Configuration config = cte.getConfig().getConfiguration();
        Configuration combinationConfig = new Configuration();
        combinationConfig.addAll(rootConfig);
        combinationConfig.addAll(config);
        return combinationConfig;
    }

    private void compileUDF(List<UDF> udfList) {
        Integer taskId = jobManager.getConfig().getTaskId();
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
        if (GATEWAY_TYPE_MAP.get(SESSION).contains(jobManager.getRunMode())) {
            jobManager.getConfig().setJarFiles(jarPaths);
        }

        // 2.Compile Python
        String[] pyPaths = UDFUtil.initPythonUDF(
                udfList,
                jobManager.getRunMode(),
                jobManager.getConfig().getTaskId(),
                jobManager.getExecutor().getTableConfig().getConfiguration());

        jobManager.getExecutor().initUDF(userCustomUdfJarPath);
        jobManager.getExecutor().initUDF(jarPaths);

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
        jobManager
                .getExecutor()
                .initPyUDF(
                        SystemConfiguration.getInstances().getPythonHome(),
                        pyUdfFile.stream().map(File::getAbsolutePath).toArray(String[]::new));
        if (GATEWAY_TYPE_MAP.get(YARN).contains(jobManager.getRunMode())) {
            jobManager.getConfig().getGatewayConfig().setJarPaths(ArrayUtil.append(jarPaths, pyPaths));
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
}
