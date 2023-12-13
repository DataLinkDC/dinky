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

package org.dinky.operations;

import org.dinky.parser.DinkyExtendedParser;

import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.planner.parse.ExtendedParseStrategy;

import java.util.Optional;

public class DinkyParser extends DinkyExtendedParser {
    private final TableEnvironment tableEnvironment;

    public DinkyParser(TableEnvironment tableEnvironment) {
        this.tableEnvironment = tableEnvironment;
    }

    @Override
    public Optional<Operation> parse(String statement) {
        for (ExtendedParseStrategy strategy : PARSE_STRATEGIES) {
            if (strategy.match(statement)) {
                return Optional.of(new DinkyExecutableOperation(this.tableEnvironment, strategy.convert(statement)));
            }
        }
        return Optional.empty();
    }
}
