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

import org.dinky.constant.FlinkSQLConstant;
import org.dinky.data.exception.DinkyException;
import org.dinky.data.job.JobStatement;
import org.dinky.data.job.JobStatementType;
import org.dinky.data.job.SqlType;
import org.dinky.function.util.UDFUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JobStatementPlan {

    private List<JobStatement> jobStatementList = new ArrayList<>();
    private boolean isSubmissionMode;

    public JobStatementPlan() {}

    public List<JobStatement> getJobStatementList() {
        return jobStatementList;
    }

    public boolean isSubmissionMode() {
        return isSubmissionMode;
    }

    public void setSubmissionMode(boolean isSubmissionMode) {
        this.isSubmissionMode = isSubmissionMode;
    }

    public void addJobStatement(String statement, JobStatementType statementType, SqlType sqlType) {
        jobStatementList.add(new JobStatement(jobStatementList.size() + 1, statement, statementType, sqlType));
    }

    public String getStatements() {
        return StringUtils.join(
                jobStatementList.stream().map(JobStatement::getStatement).collect(Collectors.toList()),
                FlinkSQLConstant.SEPARATOR);
    }

    public void buildFinalStatement() {
        checkStatement();

        int executableIndex = -1;
        int createFunctionIndex = -1;
        for (int i = 0; i < jobStatementList.size(); i++) {
            if (jobStatementList.get(i).getSqlType().isPipeline()) {
                executableIndex = i;
            } else if (UDFUtil.isUdfStatement(jobStatementList.get(i).getStatement())) {
                createFunctionIndex = i;
            }
        }
        if (executableIndex >= 0) {
            jobStatementList.get(executableIndex).asFinalExecutableStatement();
        } else {
            // If there is no INSERT/CTAS/RTAS statement, use the first SELECT/WITH/SHOW/DESC SQL statement as the final
            // statement.
            for (int i = 0; i < jobStatementList.size(); i++) {
                if (jobStatementList.get(i).getStatementType().equals(JobStatementType.SQL)) {
                    jobStatementList.get(i).asFinalExecutableStatement();
                    break;
                }
            }
        }
        // CTF statement needs to be executed together to compile one file.
        // todo: support sorted CTF statements execute.
        if (createFunctionIndex >= 0) {
            jobStatementList.get(createFunctionIndex).asFinalCreateFunctionStatement();
        }
    }

    public void checkStatement() {
        checkEmptyStatement();
        checkSubmissionStatement();
        checkPipelineStatement();
    }

    private void checkEmptyStatement() {
        if (jobStatementList.isEmpty()) {
            throw new DinkyException("None of valid statement is executed. Please check your statements.");
        }
        boolean hasSqlStatement = false;
        for (JobStatement jobStatement : jobStatementList) {
            if (jobStatement.getStatement().trim().isEmpty()) {
                throw new DinkyException("The statement cannot be empty. Please check your statements.");
            }
            if (jobStatement.getStatementType().equals(JobStatementType.SQL)
                    || jobStatement.getStatementType().equals(JobStatementType.PIPELINE)) {
                hasSqlStatement = true;
            }
        }
        if (!hasSqlStatement) {
            throw new DinkyException(
                    "The statements must contain at least one INSERT/CTAS/RTAS/SELECT/WITH/SHOW/DESC statement.");
        }
    }

    private void checkSubmissionStatement() {
        if (isSubmissionMode) {
            for (JobStatement jobStatement : jobStatementList) {
                if (jobStatement.getStatementType().equals(JobStatementType.SQL)) {
                    if (!jobStatement.getSqlType().isSinkyModify()) {
                        throw new DinkyException(
                                "The submission mode cannot contain one statement which is not a sink operation."
                                        + "\nThe valid statement is: " + jobStatement.getStatement());
                    }
                }
            }
        }
    }

    private void checkPipelineStatement() {
        int pipelineStatement = 0;
        for (JobStatement jobStatement : jobStatementList) {
            if (jobStatement.getStatementType().equals(JobStatementType.PIPELINE)) {
                pipelineStatement++;
            }
        }
        if (pipelineStatement > 1) {
            throw new DinkyException("Only one pipeline statement is supported for execution.");
        }
    }
}
