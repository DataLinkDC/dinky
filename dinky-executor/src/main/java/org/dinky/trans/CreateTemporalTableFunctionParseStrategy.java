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

package org.dinky.trans;

import org.dinky.trans.ddl.CreateTemporalTableFunctionOperation;

import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.planner.parse.AbstractRegexParseStrategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CREATE TEMPORAL [TEMPORARY|TEMPORARY SYSTEM] FUNCTION [IF NOT EXISTS]
 * [catalog_name.][db_name.]function_name
 *
 * <pre><code>
 *   create temporal temporary function if not exists rates as select update_time, currency from tableName
 *   </code></pre>
 */
public class CreateTemporalTableFunctionParseStrategy extends AbstractRegexParseStrategy {
    private static final String PATTERN_STR =
            "^CREATE\\s+TEMPORAL(?:\\s+(TEMPORARY|TEMPORARY SYSTEM))?\\s+FUNCTION"
                    + "(?:\\s(IF NOT EXISTS))?\\s+(\\w+)\\s+AS\\s+SELECT\\s+(\\w+)\\s*,\\s*(\\w+)\\s+FROM\\s+(\\w+)\\s*$";
    private static final Pattern PATTERN =
            Pattern.compile(PATTERN_STR, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    public static final CreateTemporalTableFunctionParseStrategy INSTANCE =
            new CreateTemporalTableFunctionParseStrategy();

    protected CreateTemporalTableFunctionParseStrategy() {
        super(PATTERN);
    }

    public static String[] getInfo(String statement) {
        Matcher matcher = PATTERN.matcher(statement);
        matcher.find();

        String functionType = matcher.group(1) == null ? "" : matcher.group(1).trim();
        String exist = matcher.group(2) == null ? "" : matcher.group(2).trim();
        String functionName = matcher.group(3).trim();
        String timeColumn = matcher.group(4).trim();
        String targetColumn = matcher.group(5).trim();
        String tableName = matcher.group(6).trim();
        return new String[] {
            functionType, exist, functionName, timeColumn, targetColumn, tableName
        };
    }

    @Override
    public Operation convert(String statement) {
        return new CreateTemporalTableFunctionOperation(statement);
    }

    @Override
    public String[] getHints() {
        return new String[0];
    }
}
