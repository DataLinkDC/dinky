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

import cn.hutool.core.util.ReflectUtil;
import org.apache.calcite.sql.SqlNode;
import org.apache.flink.table.delegation.Parser;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.planner.delegation.ParserImpl;
import org.apache.flink.table.planner.parse.CalciteParser;
import org.apache.flink.table.planner.parse.ExtendedParseStrategy;
import org.apache.flink.table.planner.parse.ExtendedParser;
import org.dinky.executor.CustomParser;
import org.dinky.trans.CreateTemporalTableFunctionParseStrategy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class CustomParserImpl implements CustomParser {

    private static final DinkyExtendedParser DINKY_EXTENDED_PARSER = DinkyExtendedParser.INSTANCE;
    private final Parser parser;
    private final Supplier<CalciteParser> calciteParserSupplier;


    public CustomParserImpl(Parser parser) {
        this.parser = parser;
        this.calciteParserSupplier = getCalciteParserSupplier(this.parser);
    }

    public static Supplier<CalciteParser> getCalciteParserSupplier(Parser parser) {
        if (parser instanceof ParserImpl) {
            ParserImpl parserImpl = (ParserImpl) parser;
            return (Supplier<CalciteParser>)ReflectUtil.getFieldValue(parserImpl, "calciteParserSupplier");
        } else {
            throw new RuntimeException("Unsupported parser type for getCalciteParserSupplier: " + parser.getClass().getName());
        }
    }

    @Override
    public List<Operation> parse(String statement) {
        Optional<Operation> command = DINKY_EXTENDED_PARSER.parse(statement);

        // note: null represent not custom parser;
        return command.map(Collections::singletonList).orElse(null);
    }

    public Parser getParser() {
        return parser;
    }

    @Override
    public SqlNode parseExpression(String sqlExpression) {
        return calciteParserSupplier.get().parseExpression(sqlExpression);
    }

    public static class DinkyExtendedParser extends ExtendedParser {
        public static final DinkyExtendedParser INSTANCE = new DinkyExtendedParser();

        private static final List<ExtendedParseStrategy> PARSE_STRATEGIES =
                Arrays.asList(CreateTemporalTableFunctionParseStrategy.INSTANCE);

        @Override
        public Optional<Operation> parse(String statement) {
            for (ExtendedParseStrategy strategy : PARSE_STRATEGIES) {
                if (strategy.match(statement)) {
                    return Optional.of(strategy.convert(statement));
                }
            }
            return Optional.empty();
        }
    }
}
