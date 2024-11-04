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

import org.dinky.context.CustomTableEnvironmentContext;
import org.dinky.context.RowLevelPermissionsContext;
import org.dinky.data.enums.GatewayType;
import org.dinky.data.result.ExplainResult;

import org.apache.commons.io.IOUtils;
import org.apache.flink.shaded.guava31.com.google.common.io.Resources;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JobManagerTest {

    private JobConfig config;

    private JobManager jobManager;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {}

    @AfterEach
    void tearDown() throws Exception {
        CustomTableEnvironmentContext.clear();
        RowLevelPermissionsContext.clear();
    }

    void initLocalStreamEnvironment() {
        config = JobConfig.builder()
                .fragment(true)
                .statementSet(true)
                .type(GatewayType.LOCAL.getLongValue())
                .parallelism(1)
                .maxRowNum(100)
                .useAutoCancel(true)
                .useChangeLog(false)
                .useRemote(false)
                .useResult(true)
                .batchModel(false)
                .jobName("Test")
                .checkpoint(1000)
                .build();
        jobManager = JobManager.buildPlanMode(config);
    }

    @Test
    void testExplainSingleSql() throws Exception {
        initLocalStreamEnvironment();
        String statement =
                IOUtils.toString(Resources.getResource("flink/sql/single-insert.sql"), StandardCharsets.UTF_8);
        ExplainResult explainResult = jobManager.explainSql(statement);
        assertNotNull(explainResult);
        assertTrue(explainResult.isCorrect());
        assertEquals(3, explainResult.getTotal());
    }

    @Test
    void testExplainStatementSet() throws IOException {
        initLocalStreamEnvironment();
        String statement =
                IOUtils.toString(Resources.getResource("flink/sql/statement-set-insert.sql"), StandardCharsets.UTF_8);
        ExplainResult explainResult = jobManager.explainSql(statement);
        assertNotNull(explainResult);
        assertTrue(explainResult.isCorrect());
        assertEquals(4, explainResult.getTotal());
    }
}
