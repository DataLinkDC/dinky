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
import org.dinky.trans.ddl.CreateAggTableOperation;
import org.dinky.utils.SqlSegmentUtil;

import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.planner.parse.AbstractRegexParseStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * CreateAggTableSelectSqlParser
 *
 * @since 2021/6/14 16:56
 */
public class CreateAggTableSelectSqlParseStrategy extends AbstractRegexParseStrategy {
    private static final String PATTERN_STR = "(create\\s+aggtable)(.+)(as\\s+select)(.+)";
    private static final Pattern PATTERN = Pattern.compile(PATTERN_STR, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    public static final CreateAggTableSelectSqlParseStrategy INSTANCE = new CreateAggTableSelectSqlParseStrategy();

    public CreateAggTableSelectSqlParseStrategy() {
        super(PATTERN);
    }

    public static CreateAggTableOperation.AggTable getInfo(String statement) {
        List<SqlSegment> segments = new ArrayList<>();
        segments.add(new SqlSegment("(create\\s+aggtable)(.+)(as\\s+select)", "[,]"));
        segments.add(new SqlSegment("(select)(.+)(from)", "[,]"));
        segments.add(new SqlSegment(
                "(from)(.+?)( where | on | having | group\\s+by | order\\s+by | agg\\s+by" + " | ENDOFSQL)",
                "(,|\\s+left\\s+join\\s+|\\s+right\\s+join\\s+|\\s+inner\\s+join\\s+)"));
        segments.add(new SqlSegment(
                "(where|on|having)(.+?)( group\\s+by | order\\s+by | agg\\s+by | ENDOFSQL)", "(and|or)"));
        segments.add(new SqlSegment("(group\\s+by)(.+?)( order\\s+by | agg\\s+by | ENDOFSQL)", "[,]"));
        segments.add(new SqlSegment("(order\\s+by)(.+?)( agg\\s+by | ENDOFSQL)", "[,]"));
        segments.add(new SqlSegment("(agg\\s+by)(.+?)( ENDOFSQL)", "[,]"));
        Map<String, List<String>> splitSql2Segment = SqlSegmentUtil.splitSql2Segment(segments, statement);
        return CreateAggTableOperation.AggTable.build(statement, splitSql2Segment);
    }

    @Override
    public Operation convert(String statement) {
        return new CreateAggTableOperation(statement);
    }

    @Override
    public String[] getHints() {
        return new String[0];
    }
}
