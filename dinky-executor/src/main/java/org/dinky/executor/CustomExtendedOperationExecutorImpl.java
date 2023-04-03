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

package org.dinky.executor;

import org.dinky.trans.ddl.CreateTemporalTableFunctionOperation;

import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.expressions.Expression;
import org.apache.flink.table.expressions.ValueLiteralExpression;
import org.apache.flink.table.functions.TemporalTableFunction;
import org.apache.flink.table.operations.Operation;

import java.util.Optional;

public class CustomExtendedOperationExecutorImpl implements CustomExtendedOperationExecutor {

    private Executor executor;

    public CustomExtendedOperationExecutorImpl(Executor executor) {
        this.executor = executor;
    }

    @Override
    public Optional<? extends TableResult> executeOperation(Operation operation) {
        if (operation instanceof CreateTemporalTableFunctionOperation) {
            return executeCreateTemporalTableFunctionOperation(
                    (CreateTemporalTableFunctionOperation) operation);
        }

        // note: null result represent not in custom operator,
        return null;
    }

    public Optional<? extends TableResult> executeCreateTemporalTableFunctionOperation(
            CreateTemporalTableFunctionOperation operation) {
        String statement = operation.getStatement();
        CreateTemporalTableFunctionOperation.TemporalTable temporalTable =
                CreateTemporalTableFunctionOperation.TemporalTable.build(statement);
        CustomTableEnvironment env = executor.getCustomTableEnvironment();
        CustomTableEnvironmentImpl customTableEnvironmentImpl = ((CustomTableEnvironmentImpl) env);
        Expression timeColumn = new ValueLiteralExpression(temporalTable.getTimeColumn());
        Expression targetColumn = new ValueLiteralExpression(temporalTable.getTargetColumn());
        TemporalTableFunction ttf =
                customTableEnvironmentImpl
                        .from(temporalTable.getTableName())
                        .createTemporalTableFunction(timeColumn, targetColumn);

        if (temporalTable.getFunctionType().toUpperCase().equals("TEMPORARY SYSTEM")) {
            customTableEnvironmentImpl.createTemporarySystemFunction(
                    temporalTable.getFunctionName(), ttf);
        } else {
            customTableEnvironmentImpl.createTemporaryFunction(
                    temporalTable.getFunctionName(), ttf);
        }
        return Optional.of(CustomTableResultImpl.TABLE_RESULT_OK);
    }
}
