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

import org.apache.flink.table.catalog.UnresolvedIdentifier;
import org.apache.flink.table.expressions.ResolvedExpression;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.RowType;

import java.util.List;

import javax.annotation.Nullable;

public class ParserWrapper implements ExtendedParser {

    private CustomParser customParser;

    public ParserWrapper(CustomParser customParser) {
        this.customParser = customParser;
    }

    @Override
    public List<Operation> parse(String statement) {
        List<Operation> result = customParser.parse(statement);
        if (result != null) {
            return result;
        }

        return customParser.getParser().parse(statement);
    }

    @Override
    public UnresolvedIdentifier parseIdentifier(String identifier) {
        return customParser.getParser().parseIdentifier(identifier);
    }

    @Override
    public ResolvedExpression parseSqlExpression(
            String sqlExpression, RowType inputRowType, @Nullable LogicalType outputType) {
        return customParser.getParser().parseSqlExpression(sqlExpression, inputRowType, outputType);
    }

    @Override
    public String[] getCompletionHints(String statement, int position) {
        return customParser.getParser().getCompletionHints(statement, position);
    }

    @Override
    public CustomParser getCustomParser() {
        return customParser;
    }
}
