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
import org.dinky.function.constant.PathConstant;
import org.dinky.function.data.model.UDF;
import org.dinky.function.util.UDFUtil;
import org.dinky.job.Job;
import org.dinky.job.JobManager;
import org.dinky.trans.parse.AddFileSqlParseStrategy;
import org.dinky.trans.parse.AddJarSqlParseStrategy;
import org.dinky.utils.LogUtil;
import org.dinky.utils.SqlUtil;
import org.dinky.utils.URLUtils;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.table.catalog.FunctionLanguage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobDDLRunner extends AbstractJobRunner {

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
                    executeCreateFunction(jobStatement.getStatement());
                    break;
                }
            default:
                jobManager.getExecutor().executeSql(jobStatement.getStatement());
        }
    }

    @Override
    public SqlExplainResult explain(JobStatement jobStatement) {
        SqlExplainResult.Builder resultBuilder = SqlExplainResult.Builder.newBuilder();
        try {
            SqlExplainResult recordResult;
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
                        executeCreateFunction(jobStatement.getStatement());
                        recordResult = jobManager.getExecutor().explainSqlRecord(jobStatement.getStatement());
                        break;
                    }
                default:
                    recordResult = explainOtherDDL(jobStatement.getStatement());
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
        }
        resultBuilder.explainTime(LocalDateTime.now());
        return resultBuilder.build();
    }

    private void executeAdd(String statement) {
        Set<File> allFilePath = AddJarSqlParseStrategy.getAllFilePath(statement);
        allFilePath.forEach(t -> jobManager.getUdfPathContextHolder().addOtherPlugins(t));
        (jobManager.getExecutor().getDinkyClassLoader())
                .addURLs(URLUtils.getURLs(jobManager.getUdfPathContextHolder().getOtherPluginsFiles()));
        jobManager.getExecutor().addJar(ArrayUtil.toArray(allFilePath, File.class));
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

    private void executeCreateFunction(String udfStatement) {
        UDF udf = toUDF(udfStatement, jobManager.getExecutor().getDinkyClassLoader());
        if (udf != null) {
            // 创建文件路径快捷链接
            copyUdfFileLinkAndAddToClassloader(udf, udf.getName());
        }
        jobManager.getExecutor().executeSql(udfStatement);
    }

    /**
     * zh : 把编译打包好的udf文件，copy link 到当前任务下，并把产物添加到Classloader，最后add jar到各种执行模式
     *
     * @param udf     udf
     * @param udfName udf 名称
     */
    private void copyUdfFileLinkAndAddToClassloader(UDF udf, String udfName) {
        Integer jobId = Opt.ofNullable(jobManager.getJob()).map(Job::getId).orElse(null);
        File udfLinkFile;
        if (jobId == null) {
            udfLinkFile = new File(udf.getCompilePackagePath());
        } else {
            String udfFilePath = PathConstant.getTaskUdfPath(jobManager.getJob().getId());
            String udfPath = udf.getCompilePackagePath();
            String udfPathSuffix = FileUtil.getSuffix(udfPath);
            Path linkFilePath = new File(udfFilePath + udfName + "." + udfPathSuffix).toPath();
            try {
                FileUtil.mkParentDirs(linkFilePath);
                Files.createSymbolicLink(linkFilePath, new File(udfPath).toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            udfLinkFile = linkFilePath.toFile();
        }
        if (udf.getFunctionLanguage().equals(FunctionLanguage.PYTHON)) {
            jobManager.getUdfPathContextHolder().addPyUdfPath(udfLinkFile);
            jobManager
                    .getExecutor()
                    .initPyUDF(SystemConfiguration.getInstances().getPythonHome(), udfLinkFile.getAbsolutePath());
        } else {
            jobManager.getUdfPathContextHolder().addUdfPath(udfLinkFile);
            jobManager.getDinkyClassLoader().addURLs(CollUtil.newArrayList(udfLinkFile));
            jobManager.getExecutor().addJar(udfLinkFile);
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
}
