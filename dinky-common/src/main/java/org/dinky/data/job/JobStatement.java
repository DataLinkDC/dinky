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

package org.dinky.data.job;

public class JobStatement {

    private int index;
    private String statement;
    private JobStatementType statementType;
    private SqlType sqlType;
    private boolean isFinalExecutableStatement;
    private boolean isFinalCreateFunctionStatement;

    public JobStatement(int index, String statement, JobStatementType statementType, SqlType sqlType) {
        this.index = index;
        this.statement = statement;
        this.statementType = statementType;
        this.sqlType = sqlType;
        this.isFinalExecutableStatement = false;
        this.isFinalCreateFunctionStatement = false;
    }

    public int getIndex() {
        return index;
    }

    public String getStatement() {
        return statement;
    }

    public JobStatementType getStatementType() {
        return statementType;
    }

    public SqlType getSqlType() {
        return sqlType;
    }

    public boolean isFinalExecutableStatement() {
        return isFinalExecutableStatement;
    }

    public void asFinalExecutableStatement() {
        isFinalExecutableStatement = true;
    }

    public boolean isFinalCreateFunctionStatement() {
        return isFinalCreateFunctionStatement;
    }

    public void asFinalCreateFunctionStatement() {
        isFinalCreateFunctionStatement = true;
    }

    public static JobStatement generateJobStatement(
            int index, String statement, JobStatementType statementType, SqlType sqlType) {
        return new JobStatement(index, statement, statementType, sqlType);
    }
}
