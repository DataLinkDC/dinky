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

import org.dinky.data.exception.DinkyException;
import org.dinky.data.job.JobStatementType;
import org.dinky.data.job.SqlType;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class JobStatementPlanTest {

    @Test
    void testEmptyStatements() {
        JobStatementPlan jobStatementPlan = new JobStatementPlan();
        checkInvalidStatement(jobStatementPlan, "None of valid statement is executed. Please check your statements.");
    }

    @Test
    void testEmptyStatement() {
        JobStatementPlan jobStatementPlan = new JobStatementPlan();
        jobStatementPlan.addJobStatement("", JobStatementType.SQL, SqlType.UNKNOWN);
        checkInvalidStatement(jobStatementPlan, "The statement cannot be empty. Please check your statements.");
    }

    @Test
    void testNoSqlStatement() {
        JobStatementPlan jobStatementPlan = new JobStatementPlan();
        jobStatementPlan.addJobStatement("set 'parallelism.default' = '2';\n", JobStatementType.DDL, SqlType.SET);
        checkInvalidStatement(
                jobStatementPlan,
                "The statements must contain at least one INSERT/CTAS/RTAS/SELECT/WITH/SHOW/DESC statement.");
    }

    @Test
    void testSubmissionWithQueryStatement() {
        JobStatementPlan jobStatementPlan = new JobStatementPlan();
        jobStatementPlan.setSubmissionMode(true);
        jobStatementPlan.addJobStatement("select 'A' as name;\n", JobStatementType.SQL, SqlType.SET);
        checkInvalidStatement(
                jobStatementPlan,
                "The submission mode cannot contain one statement which is not a sink operation."
                        + "\nThe valid statement is: select 'A' as name;\n");
    }

    @Test
    void testOnePipelineStatement() {
        JobStatementPlan jobStatementPlan = new JobStatementPlan();
        jobStatementPlan.addJobStatement("EXECUTE CDCSOURCE cdc {...};\n", JobStatementType.PIPELINE, SqlType.EXECUTE);
        jobStatementPlan.addJobStatement("EXECUTE CDCSOURCE cdc2 {...};\n", JobStatementType.PIPELINE, SqlType.EXECUTE);
        checkInvalidStatement(jobStatementPlan, "Only one pipeline statement is supported for execution.");
    }

    private void checkInvalidStatement(JobStatementPlan jobStatementPlan, String errorMsg) {
        Assertions.assertThatThrownBy(() -> {
                    jobStatementPlan.checkStatement();
                })
                .isExactlyInstanceOf(DinkyException.class)
                .hasMessage(errorMsg);
    }
}
