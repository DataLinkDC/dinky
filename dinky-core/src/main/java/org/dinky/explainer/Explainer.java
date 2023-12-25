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

package org.dinky.explainer;

import org.dinky.assertion.Asserts;
import org.dinky.constant.FlinkSQLConstant;
import org.dinky.data.model.LineageRel;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.result.ExplainResult;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.executor.CustomTableEnvironment;
import org.dinky.executor.Executor;
import org.dinky.explainer.print_table.PrintStatementExplainer;
import org.dinky.function.data.model.UDF;
import org.dinky.function.util.UDFUtil;
import org.dinky.gateway.enums.GatewayType;
import org.dinky.interceptor.FlinkInterceptor;
import org.dinky.job.JobConfig;
import org.dinky.job.JobManager;
import org.dinky.job.JobParam;
import org.dinky.job.StatementParam;
import org.dinky.job.builder.JobUDFBuilder;
import org.dinky.parser.SqlType;
import org.dinky.trans.Operations;
import org.dinky.trans.parse.AddJarSqlParseStrategy;
import org.dinky.utils.DinkyClassLoaderUtil;
import org.dinky.utils.IpUtil;
import org.dinky.utils.LogUtil;
import org.dinky.utils.SqlUtil;
import org.dinky.utils.URLUtils;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Explainer
 *
 * @since 2021/6/22
 */
@Slf4j
public class Explainer {

    private Executor executor;
    private boolean useStatementSet;
    private String sqlSeparator;
    private ObjectMapper mapper = new ObjectMapper();
    private JobManager jobManager;

    public Explainer(Executor executor, boolean useStatementSet, JobManager jobManager) {
        this(executor, useStatementSet, FlinkSQLConstant.SEPARATOR, jobManager);
        init();
    }

    public Explainer(Executor executor, boolean useStatementSet, String sqlSeparator, JobManager jobManager) {
        this.executor = executor;
        this.useStatementSet = useStatementSet;
        this.sqlSeparator = sqlSeparator;
        this.jobManager = jobManager;
    }

    public void init() {
        sqlSeparator = SystemConfiguration.getInstances().getSqlSeparator();
    }

    public static Explainer build(
            Executor executor, boolean useStatementSet, String sqlSeparator, JobManager jobManager) {
        return new Explainer(executor, useStatementSet, sqlSeparator, jobManager);
    }

    public Explainer initialize(JobConfig config, String statement) {
        DinkyClassLoaderUtil.initClassLoader(config, jobManager.getDinkyClassLoader());
        String[] statements = SqlUtil.getStatements(SqlUtil.removeNote(statement), sqlSeparator);
        List<UDF> udfs = parseUDFFromStatements(statements);
        jobManager.setJobParam(new JobParam(udfs));
        try {
            JobUDFBuilder.build(jobManager).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public JobParam pretreatStatements(String[] statements) {
        List<StatementParam> ddl = new ArrayList<>();
        List<StatementParam> trans = new ArrayList<>();
        List<StatementParam> execute = new ArrayList<>();
        List<String> statementList = new ArrayList<>();
        List<UDF> udfList = new ArrayList<>();
        for (String item : statements) {
            String statement = executor.pretreatStatement(item);
            if (statement.isEmpty()) {
                continue;
            }
            SqlType operationType = Operations.getOperationType(statement);
            if (operationType.equals(SqlType.ADD)) {
                AddJarSqlParseStrategy.getAllFilePath(statement)
                        .forEach(t -> jobManager.getUdfPathContextHolder().addOtherPlugins(t));
                (executor.getDinkyClassLoader())
                        .addURLs(URLUtils.getURLs(
                                jobManager.getUdfPathContextHolder().getOtherPluginsFiles()));
            } else if (operationType.equals(SqlType.ADD_JAR)) {
                Configuration combinationConfig = getCombinationConfig();
                FileSystem.initialize(combinationConfig, null);
                ddl.add(new StatementParam(statement, operationType));
                statementList.add(statement);
            } else if (operationType.equals(SqlType.INSERT)
                    || operationType.equals(SqlType.SELECT)
                    || operationType.equals(SqlType.WITH)
                    || operationType.equals(SqlType.SHOW)
                    || operationType.equals(SqlType.DESCRIBE)
                    || operationType.equals(SqlType.DESC)
                    || operationType.equals(SqlType.CTAS)) {
                trans.add(new StatementParam(statement, operationType));
                statementList.add(statement);
                if (!useStatementSet) {
                    break;
                }
            } else if (operationType.equals(SqlType.EXECUTE)) {
                execute.add(new StatementParam(statement, operationType));
            } else if (operationType.equals(SqlType.PRINT)) {
                Map<String, String> config = this.executor.getExecutorConfig().getConfig();
                String host = config.getOrDefault("dinky.dinkyHost", IpUtil.getHostIp());
                int port = Integer.parseInt(config.getOrDefault("dinky.dinkyPrintPort", "7125"));
                String[] tableNames = PrintStatementExplainer.getTableNames(statement);
                for (String tableName : tableNames) {
                    trans.add(new StatementParam(
                            PrintStatementExplainer.getCreateStatement(tableName, host, port), SqlType.CTAS));
                }
            } else {
                UDF udf = UDFUtil.toUDF(statement, jobManager.getDinkyClassLoader());
                if (Asserts.isNotNull(udf)) {
                    udfList.add(udf);
                }
                ddl.add(new StatementParam(statement, operationType));
                statementList.add(statement);
            }
        }
        return new JobParam(statementList, ddl, trans, execute, CollUtil.removeNull(udfList));
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

    public List<UDF> parseUDFFromStatements(String[] statements) {
        List<UDF> udfList = new ArrayList<>();
        for (String statement : statements) {
            if (statement.isEmpty()) {
                continue;
            }
            UDF udf = UDFUtil.toUDF(statement, jobManager.getDinkyClassLoader());
            if (Asserts.isNotNull(udf)) {
                udfList.add(udf);
            }
        }
        return udfList;
    }

    public ExplainResult explainSql(String statement) {
        log.info("Start explain FlinkSQL...");
        JobParam jobParam = pretreatStatements(SqlUtil.getStatements(statement, sqlSeparator));
        List<SqlExplainResult> sqlExplainRecords = new ArrayList<>();
        int index = 1;
        boolean correct = true;
        for (StatementParam item : jobParam.getDdl()) {
            SqlExplainResult record = new SqlExplainResult();
            try {
                record = executor.explainSqlRecord(item.getValue());
                if (Asserts.isNull(record)) {
                    continue;
                }
                executor.executeSql(item.getValue());
            } catch (Exception e) {
                String error = StrFormatter.format(
                        "Exception in executing FlinkSQL:\n{}\n{}",
                        SqlUtil.addLineNumber(item.getValue()),
                        e.getMessage());
                record.setError(error);
                record.setExplainTrue(false);
                record.setExplainTime(LocalDateTime.now());
                record.setSql(item.getValue());
                record.setIndex(index);
                sqlExplainRecords.add(record);
                correct = false;
                log.error(error);
                break;
            }
            record.setExplainTrue(true);
            record.setExplainTime(LocalDateTime.now());
            record.setSql(item.getValue());
            record.setIndex(index++);
            sqlExplainRecords.add(record);
        }
        if (correct && !jobParam.getTrans().isEmpty()) {
            if (useStatementSet) {
                List<String> inserts = new ArrayList<>();
                for (StatementParam item : jobParam.getTrans()) {
                    if (item.getType().equals(SqlType.INSERT) || item.getType().equals(SqlType.CTAS)) {
                        inserts.add(item.getValue());
                    }
                }
                if (!inserts.isEmpty()) {
                    SqlExplainResult record = new SqlExplainResult();
                    String sqlSet = String.join(";\r\n ", inserts);
                    try {
                        record.setExplain(executor.explainStatementSet(inserts));
                        record.setParseTrue(true);
                        record.setExplainTrue(true);
                    } catch (Exception e) {
                        String error = LogUtil.getError(e);
                        record.setError(error);
                        record.setParseTrue(false);
                        record.setExplainTrue(false);
                        correct = false;
                        log.error(error);
                    } finally {
                        record.setType("Modify DML");
                        record.setExplainTime(LocalDateTime.now());
                        record.setSql(sqlSet);
                        record.setIndex(index);
                        sqlExplainRecords.add(record);
                    }
                }
            } else {
                for (StatementParam item : jobParam.getTrans()) {
                    SqlExplainResult record = new SqlExplainResult();
                    try {
                        record = executor.explainSqlRecord(item.getValue());
                        record.setParseTrue(true);
                        record.setExplainTrue(true);
                    } catch (Exception e) {
                        String error = StrFormatter.format(
                                "Exception in executing FlinkSQL:\n{}\n{}",
                                SqlUtil.addLineNumber(item.getValue()),
                                e.getMessage());
                        record.setError(error);
                        record.setParseTrue(false);
                        record.setExplainTrue(false);
                        correct = false;
                        log.error(error);
                    } finally {
                        record.setType("Modify DML");
                        record.setExplainTime(LocalDateTime.now());
                        record.setSql(item.getValue());
                        record.setIndex(index++);
                        sqlExplainRecords.add(record);
                    }
                }
            }
        }
        for (StatementParam item : jobParam.getExecute()) {
            SqlExplainResult record = new SqlExplainResult();
            try {
                record = executor.explainSqlRecord(item.getValue());
                if (Asserts.isNull(record)) {
                    record = new SqlExplainResult();
                } else {
                    executor.executeSql(item.getValue());
                }
                record.setType("DATASTREAM");
                record.setParseTrue(true);
            } catch (Exception e) {
                String error = StrFormatter.format(
                        "Exception in executing FlinkSQL:\n{}\n{}",
                        SqlUtil.addLineNumber(item.getValue()),
                        e.getMessage());
                record.setError(error);
                record.setExplainTrue(false);
                record.setExplainTime(LocalDateTime.now());
                record.setSql(item.getValue());
                record.setIndex(index);
                sqlExplainRecords.add(record);
                correct = false;
                log.error(error);
                break;
            }
            record.setExplainTrue(true);
            record.setExplainTime(LocalDateTime.now());
            record.setSql(item.getValue());
            record.setIndex(index++);
            sqlExplainRecords.add(record);
        }
        log.info(StrUtil.format("A total of {} FlinkSQL have been Explained.", sqlExplainRecords.size()));
        return new ExplainResult(correct, sqlExplainRecords.size(), sqlExplainRecords);
    }

    public ObjectNode getStreamGraph(String statement) {
        JobParam jobParam = pretreatStatements(SqlUtil.getStatements(statement, sqlSeparator));
        jobParam.getDdl().forEach(statementParam -> executor.executeSql(statementParam.getValue()));

        if (!jobParam.getTrans().isEmpty()) {
            return executor.getStreamGraph(jobParam.getTransStatement());
        }

        if (!jobParam.getExecute().isEmpty()) {
            List<String> datastreamPlans =
                    jobParam.getExecute().stream().map(StatementParam::getValue).collect(Collectors.toList());
            return executor.getStreamGraphFromDataStream(datastreamPlans);
        }
        return mapper.createObjectNode();
    }

    public JobPlanInfo getJobPlanInfo(String statement) {
        JobParam jobParam = pretreatStatements(SqlUtil.getStatements(statement, sqlSeparator));
        jobParam.getDdl().forEach(statementParam -> executor.executeSql(statementParam.getValue()));

        if (!jobParam.getTrans().isEmpty()) {
            return executor.getJobPlanInfo(jobParam.getTransStatement());
        }

        if (!jobParam.getExecute().isEmpty()) {
            List<String> datastreamPlans =
                    jobParam.getExecute().stream().map(StatementParam::getValue).collect(Collectors.toList());
            return executor.getJobPlanInfoFromDataStream(datastreamPlans);
        }
        throw new RuntimeException("Creating job plan fails because this job doesn't contain an insert statement.");
    }

    public List<LineageRel> getLineage(String statement) {
        JobConfig jobConfig = JobConfig.builder()
                .type(GatewayType.LOCAL.getLongValue())
                .useRemote(false)
                .fragment(true)
                .statementSet(useStatementSet)
                .parallelism(1)
                .configJson(executor.getTableConfig().getConfiguration().toMap())
                .build();
        jobManager.setConfig(jobConfig);
        jobManager.setExecutor(executor);
        this.initialize(jobConfig, statement);

        List<LineageRel> lineageRelList = new ArrayList<>();
        for (String item : SqlUtil.getStatements(statement, sqlSeparator)) {
            try {
                String sql = FlinkInterceptor.pretreatStatement(executor, item);
                if (Asserts.isNullString(sql)) {
                    continue;
                }
                SqlType operationType = Operations.getOperationType(sql);
                if (operationType.equals(SqlType.INSERT)) {
                    lineageRelList.addAll(executor.getLineage(sql));
                } else if (!operationType.equals(SqlType.SELECT) && !operationType.equals(SqlType.PRINT)) {
                    executor.executeSql(sql);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                return lineageRelList;
            }
        }
        return lineageRelList;
    }
}
