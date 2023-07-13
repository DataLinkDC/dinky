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

import org.dinky.app.db.DBConfig;
import org.dinky.app.db.DBUtil;
import org.dinky.assertion.Asserts;
import org.dinky.constant.FlinkSQLConstant;
import org.dinky.executor.Executor;
import org.dinky.executor.ExecutorSetting;
import org.dinky.interceptor.FlinkInterceptor;
import org.dinky.parser.SqlType;
import org.dinky.trans.Operations;
import org.dinky.utils.SqlUtil;
import org.dinky.utils.ZipUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.python.PythonOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.URLUtil;

/**
 * FlinkSQLFactory
 *
 * @since 2021/10/27
 */
public class Submitter {

    private static final Logger logger = LoggerFactory.getLogger(Submitter.class);
    private static final String NULL = "null";

    private static String getQuerySQL(Integer id) throws SQLException {
        if (id == null) {
            throw new SQLException("请指定任务ID");
        }
        return "select statement from dinky_task_statement where id = " + id;
    }

    private static String getTaskInfo(Integer id) throws SQLException {
        if (id == null) {
            throw new SQLException("请指定任务ID");
        }
        return "select id, name as jobName, type,check_point as checkPoint,"
                + "save_point_path as savePointPath, parallelism,fragment as useSqlFragment,statement_set as useStatementSet,config_json as config,"
                + " env_id as envId,batch_model AS useBatchModel from dinky_task where id = "
                + id;
    }

    private static String getFlinkSQLStatement(Integer id, DBConfig config) {
        String statement = "";
        try {
            statement = DBUtil.getOneByID(getQuerySQL(id), config);
        } catch (IOException | SQLException e) {
            logger.error(
                    "{} --> 获取 FlinkSQL 配置异常，ID 为 {}, 连接信息为：{} ,异常信息为：{} ",
                    LocalDateTime.now(),
                    id,
                    config.toString(),
                    e.getMessage(),
                    e);
        }
        return statement;
    }

    public static Map<String, String> getTaskConfig(Integer id, DBConfig config) {
        Map<String, String> task = new HashMap<>();
        try {
            task = DBUtil.getMapByID(getTaskInfo(id), config);
        } catch (IOException | SQLException e) {
            logger.error(
                    "{} --> 获取 FlinkSQL 配置异常，ID 为 {}, 连接信息为：{} ,异常信息为：{} ",
                    LocalDateTime.now(),
                    id,
                    config.toString(),
                    e.getMessage(),
                    e);
        }
        return task;
    }

    public static List<String> getStatements(String sql) {
        return Arrays.asList(SqlUtil.getStatements(sql));
    }

    public static String getDbSourceSqlStatements(DBConfig dbConfig, Integer id) {
        String sql = "select name,flink_config from dinky_database where enabled = 1";
        String sqlCheck = "select fragment from dinky_task where id = " + id;
        try {
            // 首先判断是否开启了全局变量
            String fragment = DBUtil.getOneByID(sqlCheck, dbConfig);
            if ("1".equals(fragment)) {
                return DBUtil.getDbSourceSQLStatement(sql, dbConfig);
            } else {
                // 全局变量未开启，返回空字符串
                logger.info("任务 {} 未开启全局变量，不进行变量加载。");
                return "";
            }
        } catch (IOException | SQLException e) {
            logger.error(
                    "{} --> 获取 数据源信息异常，请检查数据库连接，连接信息为：{} ,异常信息为：{}",
                    LocalDateTime.now(),
                    dbConfig.toString(),
                    e.getMessage(),
                    e);
        }

        return "";
    }

    public static void submit(Integer id, DBConfig dbConfig, String dinkyAddr) {
        logger.info(LocalDateTime.now() + "开始提交作业 -- " + id);
        if (NULL.equals(dinkyAddr)) {
            dinkyAddr = "";
        }
        StringBuilder sb = new StringBuilder();
        Map<String, String> taskConfig = Submitter.getTaskConfig(id, dbConfig);

        if (Asserts.isNotNull(taskConfig.get("envId"))) {
            String envId = getFlinkSQLStatement(Integer.valueOf(taskConfig.get("envId")), dbConfig);
            if (Asserts.isNotNullString(envId)) {
                sb.append(envId);
            }
            sb.append("\n");
        }
        // 添加数据源全局变量
        sb.append(getDbSourceSqlStatements(dbConfig, id));
        // 添加自定义全局变量信息
        sb.append(getFlinkSQLStatement(id, dbConfig));
        List<String> statements = Submitter.getStatements(sb.toString());
        ExecutorSetting executorSetting = ExecutorSetting.build(taskConfig);

        // 加载第三方jar
        loadDep(taskConfig.get("type"), id, dinkyAddr, executorSetting);

        logger.info("作业配置如下： {}", executorSetting);
        Executor executor = Executor.buildAppStreamExecutor(executorSetting);
        List<StatementParam> ddl = new ArrayList<>();
        List<StatementParam> trans = new ArrayList<>();
        List<StatementParam> execute = new ArrayList<>();
        for (String item : statements) {
            String statement = FlinkInterceptor.pretreatStatement(executor, item);
            if (statement.isEmpty()) {
                continue;
            }
            SqlType operationType = Operations.getOperationType(statement);
            if (operationType.equals(SqlType.INSERT) || operationType.equals(SqlType.SELECT)) {
                trans.add(new StatementParam(statement, operationType));
                if (!executorSetting.isUseStatementSet()) {
                    break;
                }
            } else if (operationType.equals(SqlType.EXECUTE)) {
                execute.add(new StatementParam(statement, operationType));
                if (!executorSetting.isUseStatementSet()) {
                    break;
                }
            } else {
                ddl.add(new StatementParam(statement, operationType));
            }
        }
        for (StatementParam item : ddl) {
            logger.info("正在执行 FlinkSQL： " + item.getValue());
            executor.submitSql(item.getValue());
            logger.info("执行成功");
        }
        if (trans.size() > 0) {
            if (executorSetting.isUseStatementSet()) {
                List<String> inserts = new ArrayList<>();
                for (StatementParam item : trans) {
                    if (item.getType().equals(SqlType.INSERT)) {
                        inserts.add(item.getValue());
                    }
                }
                logger.info(
                        "正在执行 FlinkSQL 语句集： " + String.join(FlinkSQLConstant.SEPARATOR, inserts));
                executor.submitStatementSet(inserts);
                logger.info("执行成功");
            } else {
                for (StatementParam item : trans) {
                    logger.info("正在执行 FlinkSQL： " + item.getValue());
                    executor.submitSql(item.getValue());
                    logger.info("执行成功");
                    break;
                }
            }
        }
        if (execute.size() > 0) {
            List<String> executes = new ArrayList<>();
            for (StatementParam item : execute) {
                executes.add(item.getValue());
                executor.executeSql(item.getValue());
                if (!executorSetting.isUseStatementSet()) {
                    break;
                }
            }
            logger.info("正在执行 FlinkSQL 语句集： " + String.join(FlinkSQLConstant.SEPARATOR, executes));
            try {
                executor.execute(executorSetting.getJobName());
                logger.info("执行成功");
            } catch (Exception e) {
                logger.error("执行失败, {}", e.getMessage(), e);
            }
        }
        logger.info("{}任务提交成功", LocalDateTime.now());
    }

    private static void loadDep(
            String type, Integer taskId, String dinkyAddr, ExecutorSetting executorSetting) {
        if (StringUtils.isBlank(dinkyAddr)) {
            return;
        }
        if ("kubernetes-application".equals(type)) {
            try {
                String httpJar = "http://" + dinkyAddr + "/download/downloadDepJar/" + taskId;
                logger.info("下载依赖 http-url为：{}", httpJar);
                String flinkHome = System.getenv("FLINK_HOME");
                String usrlib = flinkHome + "/usrlib";
                FileUtils.forceMkdir(new File(usrlib));
                String depZip = flinkHome + "/dep.zip";

                boolean exists = downloadFile(httpJar, depZip);
                if (exists) {
                    String depPath = flinkHome + "/dep";
                    ZipUtils.unzip(depZip, depPath);
                    // move all jar
                    FileUtil.listFileNames(depPath + "/jar")
                            .forEach(
                                    f -> {
                                        FileUtil.moveContent(
                                                FileUtil.file(depPath + "/jar/" + f),
                                                FileUtil.file(usrlib + "/" + f),
                                                true);
                                    });
                    URL[] jarUrls =
                            FileUtil.listFileNames(usrlib).stream()
                                    .map(f -> URLUtil.getURL(FileUtil.file(usrlib, f)))
                                    .toArray(URL[]::new);
                    URL[] pyUrls =
                            FileUtil.listFileNames(depPath + "/py/").stream()
                                    .map(f -> URLUtil.getURL(FileUtil.file(depPath + "/py/", f)))
                                    .toArray(URL[]::new);

                    addURLs(jarUrls);
                    executorSetting
                            .getConfig()
                            .put(
                                    PipelineOptions.JARS.key(),
                                    Arrays.stream(jarUrls)
                                            .map(URL::toString)
                                            .collect(Collectors.joining(";")));
                    if (ArrayUtil.isNotEmpty(pyUrls)) {
                        executorSetting
                                .getConfig()
                                .put(
                                        PythonOptions.PYTHON_FILES.key(),
                                        Arrays.stream(jarUrls)
                                                .map(URL::toString)
                                                .collect(Collectors.joining(",")));
                    }
                }
            } catch (IOException e) {
                logger.error("");
                throw new RuntimeException(e);
            }
        }
        executorSetting.getConfig().put("python.files", "./python_udf.zip");
    }

    private static void addURLs(URL[] jarUrls) {
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Method add = null;
        try {
            add = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
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
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            // 设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            // 获取输入流
            InputStream inputStream = conn.getInputStream();
            // 获取输出流
            FileOutputStream outputStream = new FileOutputStream(path);
            // 每次下载1024位
            byte[] b = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, len);
            }
            inputStream.close();
            outputStream.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
