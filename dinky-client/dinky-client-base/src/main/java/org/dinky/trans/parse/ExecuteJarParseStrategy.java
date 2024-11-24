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

package org.dinky.trans.parse;

import org.dinky.trans.dml.ExecuteJarOperation;

import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.planner.parse.AbstractRegexParseStrategy;

import java.util.regex.Pattern;

public class ExecuteJarParseStrategy extends AbstractRegexParseStrategy {
    private static final String PATTERN_STR = "^EXECUTE\\s+JAR\\s+WITH\\s*\\(.+\\)";
    private static final Pattern PATTERN = Pattern.compile(PATTERN_STR, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    public static final ExecuteJarParseStrategy INSTANCE = new ExecuteJarParseStrategy();

    public ExecuteJarParseStrategy() {
        super(PATTERN);
    }

    @Override
    public Operation convert(String statement) {
        return new ExecuteJarOperation(statement);
    }

    @Override
    public String[] getHints() {
        return new String[0];
    }
}
