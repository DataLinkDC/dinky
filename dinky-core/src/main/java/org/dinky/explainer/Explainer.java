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
import org.dinky.data.enums.GatewayType;
import org.dinky.data.exception.DinkyException;
import org.dinky.data.model.LineageRel;
import org.dinky.data.result.ExplainResult;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.executor.Executor;
import org.dinky.explainer.mock.MockStatementExplainer;
import org.dinky.explainer.print_table.PrintStatementExplainer;
import org.dinky.function.data.model.UDF;
import org.dinky.function.util.UDFUtil;
import org.dinky.interceptor.FlinkInterceptor;
import org.dinky.job.JobConfig;
import org.dinky.job.JobManager;
import org.dinky.job.JobParam;
import org.dinky.job.StatementParam;
import org.dinky.job.builder.JobDDLBuilder;
import org.dinky.job.builder.JobExecuteBuilder;
import org.dinky.job.builder.JobTransBuilder;
import org.dinky.job.builder.JobUDFBuilder;
import org.dinky.parser.SqlType;
import org.dinky.trans.Operations;
import org.dinky.utils.DinkyClassLoaderUtil;
import org.dinky.utils.IpUtil;
import org.dinky.utils.SqlUtil;

import org.apache.flink.runtime.rest.messages.JobPlanInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Sets;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrBuilder;
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
    private ObjectMapper mapper = new ObjectMapper();
    private JobManager jobManager;

    public Explainer(Executor executor, boolean useStatementSet, JobManager jobManager) {
        this.executor = executor;
        this.useStatementSet = useStatementSet;
        this.jobManager = jobManager;
    }

    public static Explainer build(Executor executor, boolean useStatementSet, JobManager jobManager) {
        return new Explainer(executor, useStatementSet, jobManager);
    }

    public Explainer initialize(JobConfig config, String statement) {
        DinkyClassLoaderUtil.initClassLoader(config, jobManager.getDinkyClassLoader());
        String[] statements = SqlUtil.getStatements(SqlUtil.removeNote(statement));
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
        StrBuilder parsedSql = new StrBuilder();

        List<String> statementsWithUdf = Arrays.stream(statements).collect(Collectors.toList());
        Optional.ofNullable(jobManager.getConfig().getUdfRefer())
                .ifPresent(t -> t.forEach((key, value) -> {
                    String sql = String.format("create temporary function %s as '%s'", value, key);
                    statementsWithUdf.add(0, sql);
                }));

        List<SqlType> transSqlTypes = SqlType.getTransSqlTypes();
        Set<SqlType> transSqlTypeSet = Sets.newHashSet(transSqlTypes);
        for (String item : statementsWithUdf) {
            String statement = executor.pretreatStatement(item);
            parsedSql.append(statement).append(";\n");
            if (statement.isEmpty()) {
                continue;
            }
            SqlType operationType = Operations.getOperationType(statement);
            if (transSqlTypeSet.contains(operationType)) {
                trans.add(new StatementParam(statement, operationType));
                statementList.add(statement);
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
                ddl.add(new StatementParam(statement, operationType));
                statementList.add(statement);
            }
        }
        JobParam jobParam =
                new JobParam(statementList, ddl, trans, execute, CollUtil.removeNull(udfList), parsedSql.toString());

        MockStatementExplainer.build(executor.getCustomTableEnvironment())
                .isMockSink(jobManager.getConfig().isMockSinkFunction())
                .jobParamMock(jobParam);

        return jobParam;
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
        JobParam jobParam;
        List<SqlExplainResult> sqlExplainRecords = new ArrayList<>();
        boolean correct = true;
        try {
            jobParam = pretreatStatements(SqlUtil.getStatements(statement));
            jobManager.setJobParam(jobParam);
        } catch (Exception e) {
            SqlExplainResult.Builder resultBuilder = SqlExplainResult.Builder.newBuilder();
            resultBuilder.error(e.getMessage()).parseTrue(false);
            sqlExplainRecords.add(resultBuilder.build());
            log.error("Failed to pretreat statements:", e);
            return new ExplainResult(false, sqlExplainRecords.size(), sqlExplainRecords);
        }
        // step 1: explain and execute ddl
        List<SqlExplainResult> ddlSqlExplainResults =
                JobDDLBuilder.build(jobManager).explain();
        sqlExplainRecords.addAll(ddlSqlExplainResults);
        for (SqlExplainResult item : ddlSqlExplainResults) {
            if (!item.isParseTrue() || !item.isExplainTrue()) {
                correct = false;
            }
        }
        if (correct && !jobParam.getTrans().isEmpty()) {
            // step 2: explain modifyOptions
            sqlExplainRecords.addAll(JobTransBuilder.build(jobManager).explain());
            // step 3: explain pipeline
            sqlExplainRecords.addAll(JobExecuteBuilder.build(jobManager).explain());
        }
        int index = 1;
        for (SqlExplainResult item : sqlExplainRecords) {
            item.setIndex(index++);
            if (!item.isParseTrue() || !item.isExplainTrue()) {
                correct = false;
            }
        }
        log.info(StrUtil.format("A total of {} FlinkSQL have been Explained.", sqlExplainRecords.size()));
        return new ExplainResult(correct, sqlExplainRecords.size(), sqlExplainRecords);
    }

    public ObjectNode getStreamGraph(String statement) {
        try {
            JobParam jobParam = pretreatStatements(SqlUtil.getStatements(statement));
            jobManager.setJobParam(jobParam);
            // step 1: execute ddl
            JobDDLBuilder.build(jobManager).run();
            // step 2: get the stream graph of trans
            if (!jobParam.getTrans().isEmpty()) {
                return executor.getStreamGraphJsonNode(
                        JobTransBuilder.build(jobManager).getStreamGraph());
            }
            // step 3: get the stream graph of pipeline
            if (!jobParam.getExecute().isEmpty()) {
                return executor.getStreamGraphJsonNode(
                        JobExecuteBuilder.build(jobManager).getStreamGraph());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mapper.createObjectNode();
    }

    public JobPlanInfo getJobPlanInfo(String statement) {
        try {
            JobParam jobParam = pretreatStatements(SqlUtil.getStatements(statement));
            jobManager.setJobParam(jobParam);
            // step 1: execute ddl
            JobDDLBuilder.build(jobManager).run();
            // step 2: get the job plan info of trans
            if (!jobParam.getTrans().isEmpty()) {
                return JobTransBuilder.build(jobManager).getJobPlanInfo();
            }
            // step 3: get the job plan info of pipeline
            if (!jobParam.getExecute().isEmpty()) {
                return JobExecuteBuilder.build(jobManager).getJobPlanInfo();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        for (String item : SqlUtil.getStatements(statement)) {
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
                log.error("Exception occurred while fetching lineage information", e);
                throw new DinkyException("Exception occurred while fetching lineage information", e);
            }
        }
        return lineageRelList;
    }
}
