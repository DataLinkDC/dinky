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
import org.dinky.data.job.JobStatement;
import org.dinky.data.job.JobStatementType;
import org.dinky.data.job.SqlType;
import org.dinky.data.model.LineageRel;
import org.dinky.data.result.ExplainResult;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.executor.Executor;
import org.dinky.explainer.mock.MockStatementExplainer;
import org.dinky.function.data.model.UDF;
import org.dinky.function.util.UDFUtil;
import org.dinky.interceptor.FlinkInterceptor;
import org.dinky.job.JobConfig;
import org.dinky.job.JobManager;
import org.dinky.job.JobParam;
import org.dinky.job.JobRunnerFactory;
import org.dinky.job.JobStatementPlan;
import org.dinky.job.builder.JobUDFBuilder;
import org.dinky.trans.Operations;
import org.dinky.utils.DinkyClassLoaderUtil;
import org.dinky.utils.LogUtil;
import org.dinky.utils.SqlUtil;

import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.streaming.api.graph.JSONGenerator;
import org.apache.flink.streaming.api.graph.StreamGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
    private JobManager jobManager;

    public Explainer(Executor executor, boolean useStatementSet, JobManager jobManager) {
        this.executor = executor;
        this.useStatementSet = useStatementSet;
        this.jobManager = jobManager;
    }

    public static Explainer build(JobManager jobManager) {
        return new Explainer(jobManager.getExecutor(), true, jobManager);
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

    public JobStatementPlan parseStatements(String[] statements) {
        JobStatementPlan jobStatementPlanWithMock = new JobStatementPlan();
        generateUDFStatement(jobStatementPlanWithMock);

        JobStatementPlan jobStatementPlan = executor.parseStatementIntoJobStatementPlan(statements);
        jobStatementPlanWithMock.getJobStatementList().addAll(jobStatementPlan.getJobStatementList());
        if (!jobManager.isPlanMode() && jobManager.getConfig().isMockSinkFunction()) {
            executor.setMockTest(true);
            MockStatementExplainer.build(executor.getCustomTableEnvironment())
                    .jobStatementPlanMock(jobStatementPlanWithMock);
        }
        return jobStatementPlanWithMock;
    }

    private void generateUDFStatement(JobStatementPlan jobStatementPlan) {
        List<String> udfStatements = new ArrayList<>();
        Optional.ofNullable(jobManager.getConfig().getUdfRefer())
                .ifPresent(t -> t.forEach((key, value) -> {
                    String sql = String.format("create temporary function %s as '%s'", value, key);
                    udfStatements.add(sql);
                }));
        for (String udfStatement : udfStatements) {
            jobStatementPlan.addJobStatement(udfStatement, JobStatementType.DDL, SqlType.CREATE);
        }
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
        JobStatementPlan jobStatementPlan;
        List<SqlExplainResult> sqlExplainRecords = new ArrayList<>();
        boolean correct = true;
        try {
            jobStatementPlan = parseStatements(SqlUtil.getStatements(statement));
            jobStatementPlan.buildFinalStatement();
            jobManager.setJobStatementPlan(jobStatementPlan);
        } catch (Exception e) {
            String error = LogUtil.getError("Exception in parsing FlinkSQL:\n" + SqlUtil.addLineNumber(statement), e);
            SqlExplainResult.Builder resultBuilder = SqlExplainResult.Builder.newBuilder();
            resultBuilder.error(error).parseTrue(false);
            sqlExplainRecords.add(resultBuilder.build());
            log.error("Failed parseStatements:", e);
            return new ExplainResult(false, sqlExplainRecords.size(), sqlExplainRecords);
        }
        JobRunnerFactory jobRunnerFactory = JobRunnerFactory.create(jobManager);
        for (JobStatement jobStatement : jobStatementPlan.getJobStatementList()) {
            SqlExplainResult sqlExplainResult = jobRunnerFactory
                    .getJobRunner(jobStatement.getStatementType())
                    .explain(jobStatement);
            if (!sqlExplainResult.isInvalid()) {
                sqlExplainRecords.add(sqlExplainResult);
            }
        }
        log.info(StrUtil.format("A total of {} FlinkSQL have been Explained.", sqlExplainRecords.size()));
        return new ExplainResult(correct, sqlExplainRecords.size(), sqlExplainRecords);
    }

    public ObjectNode getStreamGraph(String statement) {
        log.info("Start explain FlinkSQL...");
        JobStatementPlan jobStatementPlan = parseStatements(SqlUtil.getStatements(statement));
        jobStatementPlan.buildFinalStatement();
        log.info("Explain FlinkSQL successful");
        JobRunnerFactory jobRunnerFactory = JobRunnerFactory.create(jobManager);
        for (JobStatement jobStatement : jobStatementPlan.getJobStatementList()) {
            StreamGraph streamGraph = jobRunnerFactory
                    .getJobRunner(jobStatement.getStatementType())
                    .getStreamGraph(jobStatement);
            if (Asserts.isNotNull(streamGraph)) {
                JSONGenerator jsonGenerator = new JSONGenerator(streamGraph);
                String json = jsonGenerator.getJSON();
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode objectNode = mapper.createObjectNode();
                try {
                    objectNode = (ObjectNode) mapper.readTree(json);
                } catch (Exception e) {
                    log.error("Get stream graph json node error.", e);
                }
                return objectNode;
            }
        }
        throw new DinkyException("None of the StreamGraph were found.");
    }

    public JobPlanInfo getJobPlanInfo(String statement) {
        log.info("Start explain FlinkSQL...");
        JobStatementPlan jobStatementPlan = parseStatements(SqlUtil.getStatements(statement));
        jobStatementPlan.buildFinalStatement();
        log.info("Explain FlinkSQL successful");
        JobRunnerFactory jobRunnerFactory = JobRunnerFactory.create(jobManager);
        for (JobStatement jobStatement : jobStatementPlan.getJobStatementList()) {
            JobPlanInfo jobPlanInfo = jobRunnerFactory
                    .getJobRunner(jobStatement.getStatementType())
                    .getJobPlanInfo(jobStatement);
            if (Asserts.isNotNull(jobPlanInfo)) {
                return jobPlanInfo;
            }
        }
        throw new DinkyException("None of the JobPlanInfo were found.");
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
