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

import org.dinky.parser.SqlSegment;
import org.dinky.trans.ddl.CustomSetOperation;
import org.dinky.utils.SqlSegmentUtil;

import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.planner.parse.AbstractRegexParseStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * SetSqlParser
 *
 * @since 2021/10/21 18:41
 */
public class SetSqlParseStrategy extends AbstractRegexParseStrategy {
    //    private static final String PATTERN_STR =
    // "SET(\\s+(?<key>[^'\\s]+)\\s*=\\s*('(?<quotedVal>[^']*)'|(?<val>\\S+)))?";
    private static final String PATTERN_STR = "(set)(.+)";
    private static final Pattern PATTERN = Pattern.compile(PATTERN_STR, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    public static final SetSqlParseStrategy INSTANCE = new SetSqlParseStrategy();

    public SetSqlParseStrategy() {
        super(PATTERN);
    }

    public static Map<String, List<String>> getInfo(String statement) {
        // SET(\s+(\S+)\s*=(.*))?
        List<SqlSegment> segments = new ArrayList<>();
        segments.add(new SqlSegment("(set)\\s+(.+)(\\s*=)", "[.]"));
        segments.add(new SqlSegment("(=)\\s*(.*)($)", ","));
        return SqlSegmentUtil.splitSql2Segment(segments, statement);
    }

    @Override
    public Operation convert(String statement) {
        return new CustomSetOperation(statement);
    }

    @Override
    public String[] getHints() {
        return new String[0];
    }
}
