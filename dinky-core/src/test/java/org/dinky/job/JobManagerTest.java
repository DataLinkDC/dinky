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

import static org.junit.jupiter.api.Assertions.*;

import org.dinky.data.enums.GatewayType;
import org.dinky.data.result.ExplainResult;
import org.dinky.executor.ExecutorConfig;
import org.dinky.explainer.lineage.LineageBuilder;
import org.dinky.explainer.lineage.LineageResult;

import org.apache.commons.io.IOUtils;
import org.apache.flink.shaded.guava31.com.google.common.io.Resources;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

class JobManagerTest {

    private JobConfig config;

    private JobManager jobManager;

    void initLocalStreamPlanEnvironment() {
        config = JobConfig.builder()
                .fragment(true)
                .statementSet(true)
                .type(GatewayType.LOCAL.getLongValue())
                .parallelism(1)
                .maxRowNum(100)
                .useAutoCancel(true)
                .useChangeLog(false)
                .useRemote(false)
                .useResult(false)
                .batchModel(false)
                .jobName("Test")
                .checkpoint(1000)
                .build();
        jobManager = JobManager.buildPlanMode(config);
    }

    void initLocalBatchPlanEnvironment() {
        config = JobConfig.builder()
                .fragment(true)
                .statementSet(true)
                .type(GatewayType.LOCAL.getLongValue())
                .parallelism(1)
                .maxRowNum(100)
                .useAutoCancel(true)
                .useChangeLog(false)
                .useRemote(false)
                .useResult(false)
                .batchModel(true)
                .jobName("Test")
                .checkpoint(1000)
                .build();
        jobManager = JobManager.buildPlanMode(config);
    }

    @Test
    void testExplainSql() throws Exception {
        checkExplainStreamSqlFromFile("flink/sql/statement-set-stream.sql", 16);
        checkExplainStreamSqlFromFile("flink/sql/variable.sql", 3);
        checkExplainBatchSqlFromFile("flink/sql/statement-set-batch.sql", 16);
    }

    @Test
    void testGetStreamGraph() throws Exception {
        checkGetStreamGraphFromFile("flink/sql/statement-set-stream.sql");
        checkGetStreamGraphFromFile("flink/sql/variable.sql");
        checkGetBatchStreamGraphFromFile("flink/sql/statement-set-batch.sql");
    }

    @Test
    void testGetJobPlanJson() throws Exception {
        checkGetStreamJobPlanJsonFromFile("flink/sql/statement-set-stream.sql");
        checkGetStreamJobPlanJsonFromFile("flink/sql/variable.sql");
        checkGetBatchJobPlanJsonFromFile("flink/sql/statement-set-batch.sql");
    }

    /*@Ignore
    @Test
    void testExecuteSql() throws Exception {
        checkStreamExecuteSqlFromFile("flink/sql/statement-set-stream.sql");
        checkBatchExecuteSqlFromFile("flink/sql/statement-set-batch.sql");
    }*/

    @Test
    void testLineageSqlSingle() throws Exception {
        String statement =
                IOUtils.toString(Resources.getResource("flink/sql/single-insert.sql"), StandardCharsets.UTF_8);
        LineageResult result = LineageBuilder.getColumnLineageByLogicalPlan(statement, ExecutorConfig.DEFAULT);
        assertNotNull(result);
        assertEquals(2, result.getTables().size());
        assertEquals(4, result.getRelations().size());
    }

    private void checkExplainStreamSqlFromFile(String path, int total) throws IOException {
        String statement = IOUtils.toString(Resources.getResource(path), StandardCharsets.UTF_8);
        initLocalStreamPlanEnvironment();
        checkExplainSql(statement, total);
        jobManager.close();
    }

    private void checkExplainBatchSqlFromFile(String path, int total) throws IOException {
        String statement = IOUtils.toString(Resources.getResource(path), StandardCharsets.UTF_8);
        initLocalBatchPlanEnvironment();
        checkExplainSql(statement, total);
        jobManager.close();
    }

    private void checkExplainSql(String statement, int total) {
        ExplainResult explainResult = jobManager.explainSql(statement);
        assertNotNull(explainResult);
        explainResult.getSqlExplainResults().forEach(sqlExplainResult -> {
            if (!sqlExplainResult.isParseTrue() || !sqlExplainResult.isExplainTrue()) {
                // Flink 1.14,1.15,1.16,1.17 not support RTAS
                if (sqlExplainResult.getError().contains("not support")
                        // Flink 1.14 not support show create view
                        || sqlExplainResult.getError().contains("SQL parse failed. Encountered \"VIEW\"")) {
                    sqlExplainResult.setParseTrue(true);
                    sqlExplainResult.setExplainTrue(true);
                } else {
                    throw new RuntimeException(sqlExplainResult.getError());
                }
            }
            assertTrue(sqlExplainResult.isParseTrue());
            assertTrue(sqlExplainResult.isExplainTrue());
        });
        assertEquals(total, explainResult.getTotal());
        assertTrue(explainResult.isCorrect());
    }

    private void checkGetStreamGraphFromFile(String path) throws IOException {
        String statement = IOUtils.toString(Resources.getResource(path), StandardCharsets.UTF_8);
        initLocalStreamPlanEnvironment();
        checkGetStreamGraph(statement);
        jobManager.close();
    }

    private void checkGetBatchStreamGraphFromFile(String path) throws IOException {
        String statement = IOUtils.toString(Resources.getResource(path), StandardCharsets.UTF_8);
        initLocalBatchPlanEnvironment();
        checkGetStreamGraph(statement);
        jobManager.close();
    }

    private void checkGetStreamGraph(String statement) {
        ObjectNode streamGraph = jobManager.getStreamGraph(statement);
        assertNotNull(streamGraph);
        assertNotNull(streamGraph.get("nodes"));
    }

    private void checkGetStreamJobPlanJsonFromFile(String path) throws IOException {
        String statement = IOUtils.toString(Resources.getResource(path), StandardCharsets.UTF_8);
        initLocalStreamPlanEnvironment();
        checkGetJobPlanJson(statement);
        jobManager.close();
    }

    private void checkGetBatchJobPlanJsonFromFile(String path) throws IOException {
        String statement = IOUtils.toString(Resources.getResource(path), StandardCharsets.UTF_8);
        initLocalBatchPlanEnvironment();
        checkGetJobPlanJson(statement);
        jobManager.close();
    }

    private void checkGetJobPlanJson(String statement) {
        String jobPlanJson = jobManager.getJobPlanJson(statement);
        assertNotNull(jobPlanJson);
    }

    private void checkStreamExecuteSqlFromFile(String path) throws Exception {
        String statement = IOUtils.toString(Resources.getResource(path), StandardCharsets.UTF_8);
        initLocalStreamPlanEnvironment();
        checkExecuteSql(statement);
        jobManager.close();
    }

    private void checkBatchExecuteSqlFromFile(String path) throws Exception {
        String statement = IOUtils.toString(Resources.getResource(path), StandardCharsets.UTF_8);
        initLocalBatchPlanEnvironment();
        checkExecuteSql(statement);
        jobManager.close();
    }

    private void checkExecuteSql(String statement) throws Exception {
        JobResult jobResult = jobManager.executeSql(statement);
        assertNotNull(jobResult);
        assertTrue(jobResult.isSuccess());
    }
}
