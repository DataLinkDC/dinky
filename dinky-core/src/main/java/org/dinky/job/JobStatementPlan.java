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

    public JobStatementPlan() {}

    public List<JobStatement> getJobStatementList() {
        return jobStatementList;
    }

    public void addJobStatement(String statement, JobStatementType statementType, SqlType sqlType) {
        jobStatementList.add(new JobStatement(jobStatementList.size() + 1, statement, statementType, sqlType, false));
    }

    public void addJobStatementGenerated(String statement, JobStatementType statementType, SqlType sqlType) {
        jobStatementList.add(new JobStatement(jobStatementList.size() + 1, statement, statementType, sqlType, true));
    }

    public String getStatements() {
        return StringUtils.join(
                jobStatementList.stream().map(JobStatement::getStatement).collect(Collectors.toList()),
                FlinkSQLConstant.SEPARATOR);
    }

    public void buildFinalStatement() {
        if (jobStatementList.size() == 0) {
            return;
        }
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
            for (int i = 0; i < jobStatementList.size(); i++) {
                if (jobStatementList.get(i).getStatementType().equals(JobStatementType.SQL)) {
                    jobStatementList.get(i).asFinalExecutableStatement();
                    break;
                }
            }
        }
        if (createFunctionIndex >= 0) {
            jobStatementList.get(createFunctionIndex).asFinalCreateFunctionStatement();
        }
    }
}
