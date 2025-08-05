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

package org.dinky.parser;

import org.dinky.executor.CustomParser;

import org.apache.calcite.sql.SqlNode;
import org.apache.flink.table.delegation.Parser;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.planner.calcite.FlinkPlannerImpl;
import org.apache.flink.table.planner.delegation.ParserImpl;
import org.apache.flink.table.planner.parse.CalciteParser;
import org.apache.flink.table.planner.parse.ExtendedParser;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import cn.hutool.core.util.ReflectUtil;

public class CustomParserImpl implements CustomParser {

    private final Parser parser;

    public CustomParserImpl(Parser parser) {
        this.parser = parser;
    }

    @Override
    public List<Operation> parse(String statement) {
        Optional<Operation> command = getDinkyParser().parse(statement);

        // note: null represent not custom parser;
        return command.map(Collections::singletonList).orElse(null);
    }

    @Override
    public Parser getParser() {
        return parser;
    }

    @Override
    public ExtendedParser getDinkyParser() {
        return DinkyExtendedParser.INSTANCE;
    }

    @Override
    public SqlNode parseExpression(String sqlExpression) {
        ParserImpl parserImpl = (ParserImpl) parser;
        Supplier<CalciteParser> calciteParserSupplier =
                (Supplier<CalciteParser>) ReflectUtil.getFieldValue(parserImpl, "calciteParserSupplier");
        return calciteParserSupplier.get().parseExpression(sqlExpression);
    }

    @Override
    public SqlNode parseSql(String statement) {
        ParserImpl parserImpl = (ParserImpl) parser;
        Supplier<CalciteParser> calciteParserSupplier =
                (Supplier<CalciteParser>) ReflectUtil.getFieldValue(parserImpl, "calciteParserSupplier");
        return calciteParserSupplier.get().parse(statement);
    }

    @Override
    public SqlNode validate(SqlNode sqlNode) {
        ParserImpl parserImpl = (ParserImpl) parser;
        Supplier<FlinkPlannerImpl> validatorSupplier =
                (Supplier<FlinkPlannerImpl>) ReflectUtil.getFieldValue(parserImpl, "validatorSupplier");
        FlinkPlannerImpl flinkPlanner = validatorSupplier.get();
        return flinkPlanner.validate(sqlNode);
    }
}
