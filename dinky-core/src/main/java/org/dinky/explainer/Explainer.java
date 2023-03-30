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
import org.dinky.context.DinkyClassLoaderContextHolder;
import org.dinky.context.JarPathContextHolder;
import org.dinky.executor.Executor;
import org.dinky.explainer.watchTable.WatchStatementExplainer;
import org.dinky.function.data.model.UDF;
import org.dinky.function.util.UDFUtil;
import org.dinky.interceptor.FlinkInterceptor;
import org.dinky.job.JobConfig;
import org.dinky.job.JobManager;
import org.dinky.job.JobParam;
import org.dinky.job.StatementParam;
import org.dinky.model.LineageRel;
import org.dinky.model.SystemConfiguration;
import org.dinky.parser.SqlType;
import org.dinky.parser.check.AddJarSqlParser;
import org.dinky.process.context.ProcessContextHolder;
import org.dinky.process.model.ProcessEntity;
import org.dinky.result.ExplainResult;
import org.dinky.result.SqlExplainResult;
import org.dinky.trans.Operations;
import org.dinky.utils.LogUtil;
import org.dinky.utils.SqlUtil;
import org.dinky.utils.URLUtils;

import org.apache.flink.runtime.rest.messages.JobPlanInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

/**
 * Explainer
 *
 * @author wenmo
 * @since 2021/6/22
 */
public class Explainer {

    private Executor executor;
    private boolean useStatementSet;
    private String sqlSeparator = FlinkSQLConstant.SEPARATOR;
    private ObjectMapper mapper = new ObjectMapper();

    public Explainer(Executor executor, boolean useStatementSet) {
        this.executor = executor;
        this.useStatementSet = useStatementSet;
        init();
    }

    public Explainer(Executor executor, boolean useStatementSet, String sqlSeparator) {
        this.executor = executor;
        this.useStatementSet = useStatementSet;
        this.sqlSeparator = sqlSeparator;
    }

    public void init() {
        sqlSeparator = SystemConfiguration.getInstances().getSqlSeparator();
    }

    public static Explainer build(Executor executor, boolean useStatementSet, String sqlSeparator) {
        return new Explainer(executor, useStatementSet, sqlSeparator);
    }

    public Explainer initialize(JobManager jobManager, JobConfig config, String statement) {
        jobManager.initClassLoader(config);
        String[] statements = SqlUtil.getStatements(SqlUtil.removeNote(statement), sqlSeparator);
        jobManager.initUDF(parseUDFFromStatements(statements));
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
                AddJarSqlParser.getAllFilePath(statement)
                        .forEach(JarPathContextHolder::addOtherPlugins);
                DinkyClassLoaderContextHolder.get()
                        .addURL(URLUtils.getURLs(JarPathContextHolder.getOtherPluginsFiles()));
            } else if (operationType.equals(SqlType.INSERT)
                    || operationType.equals(SqlType.SELECT)
                    || operationType.equals(SqlType.SHOW)
                    || operationType.equals(SqlType.DESCRIBE)
                    || operationType.equals(SqlType.DESC)) {
                trans.add(new StatementParam(statement, operationType));
                statementList.add(statement);
                if (!useStatementSet) {
                    break;
                }
            } else if (operationType.equals(SqlType.EXECUTE)) {
                execute.add(new StatementParam(statement, operationType));
            } else if (operationType.equals(SqlType.WATCH)) {
                WatchStatementExplainer watchStatementExplainer =
                        new WatchStatementExplainer(statement);
                String tableName = watchStatementExplainer.getTableName();
                ddl.add(
                        new StatementParam(
                                watchStatementExplainer.getCreateStatement(tableName),
                                SqlType.CREATE));
            } else {
                UDF udf = UDFUtil.toUDF(statement);
                if (Asserts.isNotNull(udf)) {
                    udfList.add(UDFUtil.toUDF(statement));
                }
                ddl.add(new StatementParam(statement, operationType));
                statementList.add(statement);
            }
        }
        return new JobParam(statementList, ddl, trans, execute, CollUtil.removeNull(udfList));
    }

    public List<UDF> parseUDFFromStatements(String[] statements) {
        List<UDF> udfList = new ArrayList<>();
        for (String statement : statements) {
            if (statement.isEmpty()) {
                continue;
            }
            UDF udf = UDFUtil.toUDF(statement);
            if (Asserts.isNotNull(udf)) {
                udfList.add(UDFUtil.toUDF(statement));
            }
        }
        return udfList;
    }

    public ExplainResult explainSql(String statement) {
        ProcessEntity process = ProcessContextHolder.getProcess();
        process.info("Start explain FlinkSQL...");
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
                String error = LogUtil.getError(e);
                record.setError(error);
                record.setExplainTrue(false);
                record.setExplainTime(LocalDateTime.now());
                record.setSql(item.getValue());
                record.setIndex(index);
                sqlExplainRecords.add(record);
                correct = false;
                process.error(error);
                break;
            }
            record.setExplainTrue(true);
            record.setExplainTime(LocalDateTime.now());
            record.setSql(item.getValue());
            record.setIndex(index++);
            sqlExplainRecords.add(record);
        }
        if (correct && jobParam.getTrans().size() > 0) {
            if (useStatementSet) {
                SqlExplainResult record = new SqlExplainResult();
                List<String> inserts = new ArrayList<>();
                for (StatementParam item : jobParam.getTrans()) {
                    if (item.getType().equals(SqlType.INSERT)) {
                        inserts.add(item.getValue());
                    }
                }
                if (inserts.size() > 0) {
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
                        process.error(error);
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
                        String error = LogUtil.getError(e);
                        record.setError(error);
                        record.setParseTrue(false);
                        record.setExplainTrue(false);
                        correct = false;
                        process.error(error);
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
                String error = LogUtil.getError(e);
                record.setError(error);
                record.setExplainTrue(false);
                record.setExplainTime(LocalDateTime.now());
                record.setSql(item.getValue());
                record.setIndex(index);
                sqlExplainRecords.add(record);
                correct = false;
                process.error(error);
                break;
            }
            record.setExplainTrue(true);
            record.setExplainTime(LocalDateTime.now());
            record.setSql(item.getValue());
            record.setIndex(index++);
            sqlExplainRecords.add(record);
        }
        process.info(
                StrUtil.format(
                        "A total of {} FlinkSQL have been Explained.", sqlExplainRecords.size()));
        return new ExplainResult(correct, sqlExplainRecords.size(), sqlExplainRecords);
    }

    public ObjectNode getStreamGraph(String statement) {
        JobParam jobParam = pretreatStatements(SqlUtil.getStatements(statement, sqlSeparator));
        if (jobParam.getDdl().size() > 0) {
            for (StatementParam statementParam : jobParam.getDdl()) {
                executor.executeSql(statementParam.getValue());
            }
        }
        if (jobParam.getTrans().size() > 0) {
            return executor.getStreamGraph(jobParam.getTransStatement());
        } else if (jobParam.getExecute().size() > 0) {
            List<String> datastreamPlans = new ArrayList<>();
            for (StatementParam item : jobParam.getExecute()) {
                datastreamPlans.add(item.getValue());
            }
            return executor.getStreamGraphFromDataStream(datastreamPlans);
        } else {
            return mapper.createObjectNode();
        }
    }

    public JobPlanInfo getJobPlanInfo(String statement) {
        JobParam jobParam = pretreatStatements(SqlUtil.getStatements(statement, sqlSeparator));
        if (jobParam.getDdl().size() > 0) {
            for (StatementParam statementParam : jobParam.getDdl()) {
                executor.executeSql(statementParam.getValue());
            }
        }
        if (jobParam.getTrans().size() > 0) {
            return executor.getJobPlanInfo(jobParam.getTransStatement());
        } else if (jobParam.getExecute().size() > 0) {
            List<String> datastreamPlans = new ArrayList<>();
            for (StatementParam item : jobParam.getExecute()) {
                datastreamPlans.add(item.getValue());
            }
            return executor.getJobPlanInfoFromDataStream(datastreamPlans);
        } else {
            throw new RuntimeException(
                    "Creating job plan fails because this job doesn't contain an insert statement.");
        }
    }

    public List<LineageRel> getLineage(String statement) {
        JobConfig jobConfig =
                new JobConfig(
                        "local",
                        false,
                        false,
                        true,
                        useStatementSet,
                        1,
                        executor.getTableConfig().getConfiguration().toMap());
        JobManager jm = JobManager.buildPlanMode(jobConfig);
        this.initialize(jm, jobConfig, statement);

        String[] sqls = SqlUtil.getStatements(statement, sqlSeparator);
        List<LineageRel> lineageRelList = new ArrayList<>();
        for (String item : sqls) {
            String sql = "";
            try {
                sql = FlinkInterceptor.pretreatStatement(executor, item);
                if (Asserts.isNullString(sql)) {
                    continue;
                }
                SqlType operationType = Operations.getOperationType(sql);
                if (operationType.equals(SqlType.INSERT)) {
                    lineageRelList.addAll(executor.getLineage(sql));
                } else if (!operationType.equals(SqlType.SELECT)) {
                    executor.executeSql(sql);
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        return lineageRelList;
    }
}
