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

import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.planner.parse.ExtendedParseStrategy;
import org.apache.flink.table.planner.parse.ExtendedParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

public class DinkyExtendedParser extends ExtendedParser {
    public static final DinkyExtendedParser INSTANCE = new DinkyExtendedParser();

    protected static final List<ExtendedParseStrategy> PARSE_STRATEGIES = loadExtendedStrategies();

    @Override
    public Optional<Operation> parse(String statement) {
        for (ExtendedParseStrategy strategy : PARSE_STRATEGIES) {
            if (strategy.match(statement)) {
                return Optional.of(strategy.convert(statement));
            }
        }
        return Optional.empty();
    }

    private static List<ExtendedParseStrategy> loadExtendedStrategies() {
        // load ExtendedParserStrategy class with ServiceLoader
        List<ExtendedParseStrategy> parseStrategies = new ArrayList<>();
        ServiceLoader<ExtendedParseStrategy> extendedParseStrategies = ServiceLoader.load(ExtendedParseStrategy.class);
        for (ExtendedParseStrategy extendedParseStrategy : extendedParseStrategies) {
            parseStrategies.add(extendedParseStrategy);
        }
        return parseStrategies;
    }
}
