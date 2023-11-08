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

package org.dinky.trans.ddl;

import org.dinky.executor.CustomTableEnvironment;
import org.dinky.trans.AbstractOperation;
import org.dinky.trans.ExtendOperation;
import org.dinky.trans.parse.CreateTemporalTableFunctionParseStrategy;

import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.expressions.Expression;
import org.apache.flink.table.expressions.ValueLiteralExpression;
import org.apache.flink.table.functions.TemporalTableFunction;

import java.util.Optional;

public class CreateTemporalTableFunctionOperation extends AbstractOperation implements ExtendOperation {

    public CreateTemporalTableFunctionOperation() {}

    public CreateTemporalTableFunctionOperation(String statement) {
        super(statement);
    }

    @Override
    public String asSummaryString() {
        return statement;
    }

    @Override
    public Optional<? extends TableResult> execute(CustomTableEnvironment tEnv) {
        TemporalTable temporalTable = TemporalTable.build(statement);
        Expression timeColumn = new ValueLiteralExpression(temporalTable.getTimeColumn());
        Expression targetColumn = new ValueLiteralExpression(temporalTable.getTargetColumn());
        TemporalTableFunction ttf =
                tEnv.from(temporalTable.getTableName()).createTemporalTableFunction(timeColumn, targetColumn);

        if ("TEMPORARY SYSTEM".equalsIgnoreCase(temporalTable.getFunctionType())) {
            tEnv.createTemporarySystemFunction(temporalTable.getFunctionName(), ttf);
        } else {
            tEnv.createTemporaryFunction(temporalTable.getFunctionName(), ttf);
        }
        return Optional.of(TABLE_RESULT_OK);
    }

    public static class TemporalTable {
        private String statement;
        private String functionType;
        private boolean exists;
        private String functionName;
        private String tableName;
        private String timeColumn;
        private String targetColumn;

        private TemporalTable(
                String statement,
                String functionType,
                String exists,
                String functionName,
                String timeColumn,
                String targetColumn,
                String tableName) {
            this.functionType = functionType;
            this.exists = "IF NOT EXISTS".equalsIgnoreCase(exists.trim());
            this.statement = statement;
            this.functionName = functionName;
            this.tableName = tableName;
            this.timeColumn = timeColumn;
            this.targetColumn = targetColumn;
        }

        public static TemporalTable build(String statement) {
            String[] info = CreateTemporalTableFunctionParseStrategy.getInfo(statement);
            return new TemporalTable(statement, info[0], info[1], info[2], info[3], info[4], info[5]);
        }

        public String getStatement() {
            return statement;
        }

        public void setStatement(String statement) {
            this.statement = statement;
        }

        public String getFunctionName() {
            return functionName;
        }

        public void setFunctionName(String functionName) {
            this.functionName = functionName;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getTimeColumn() {
            return timeColumn;
        }

        public void setTimeColumn(String timeColumn) {
            this.timeColumn = timeColumn;
        }

        public String getTargetColumn() {
            return targetColumn;
        }

        public void setTargetColumn(String targetColumn) {
            this.targetColumn = targetColumn;
        }

        public String getFunctionType() {
            return functionType;
        }

        public void setFunctionType(String functionType) {
            this.functionType = functionType;
        }

        public boolean isExists() {
            return exists;
        }

        public void setExists(boolean exists) {
            this.exists = exists;
        }
    }
}
