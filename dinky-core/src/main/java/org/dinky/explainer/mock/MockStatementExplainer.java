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

package org.dinky.explainer.mock;

import org.dinky.assertion.Asserts;
import org.dinky.connector.mock.sink.MockDynamicTableSinkFactory;
import org.dinky.job.JobParam;
import org.dinky.job.StatementParam;
import org.dinky.parser.SqlType;
import org.dinky.utils.JsonUtils;

import org.apache.calcite.sql.SqlInsert;
import org.apache.calcite.sql.parser.SqlParser;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockStatementExplainer {

    public static final String PATTERN_STR =
            "CREATE\\s+TABLE\\s+(\\w+)\\s*\\(\\s*([\\s\\S]*?)\\s*\\)\\s*WITH\\s*\\(\\s*([\\s\\S]*?)\\s*\\)";
    public static final Pattern PATTERN = Pattern.compile(PATTERN_STR, Pattern.CASE_INSENSITIVE);
    public static final String MOCK_SQL_TEMPLATE = "CREATE TABLE {0} ({1}) WITH ({2})";

    /**
     * The connector of insert tables will be changed to {@link MockDynamicTableSinkFactory}
     *
     * @param jobParam job param
     */
    public static void jobParamMock(JobParam jobParam) {
        // Based on insert statements, get table names need to be mocked
        Set<String> tablesNeedMock = getMockedTableNames(jobParam.getTrans());
        // mock insert table ddl
        List<StatementParam> mockedDdl = new ArrayList<>();
        for (StatementParam ddl : jobParam.getDdl()) {
            // table name check
            String tableName =
                    getDdlTableName(ddl.getValue().replaceAll("\\n", " ").replaceAll(" +", " "));
            // mock connector
            if (Asserts.isNotNull(tableName) && tablesNeedMock.contains(tableName.toUpperCase())) {
                mockedDdl.add(new StatementParam(getSinkMockDdlStatement(ddl.getValue()), SqlType.CREATE));
            } else {
                mockedDdl.add(ddl);
            }
        }
        jobParam.setDdl(mockedDdl);
        log.info("Mock succeed: {}", JsonUtils.toJsonString(jobParam));
    }

    /**
     * get tables names of insert statements, these tables will be mocked
     *
     * @param transStatements trans statement that contains all insert statements
     * @return a hash set, which contains all insert table names
     */
    private static Set<String> getMockedTableNames(List<StatementParam> transStatements) {
        Set<String> insertTables = new HashSet<>();
        for (StatementParam statement : transStatements) {
            if (statement.getType().equals(SqlType.INSERT)) {
                try {
                    SqlInsert sqlInsert =
                            (SqlInsert) SqlParser.create(statement.getValue()).parseQuery();
                    insertTables.add(sqlInsert.getTargetTable().toString());
                } catch (Exception e) {
                    log.error("Statement parse error, statement: {}", statement.getValue());
                }
            }
        }
        return insertTables;
    }

    /**
     * get table name from ddl
     *
     * @param ddl ddl statement
     * @return table name
     */
    private static String getDdlTableName(String ddl) {
        Matcher matcher = PATTERN.matcher(ddl);
        if (matcher.find()) {
            // table name and columns
            return matcher.group(1);
        }
        return "";
    }

    /**
     * get mocked ddl statement
     *
     * @param ddl ddl statement
     * @return ddl that connector is changed as well as other options not changed
     */
    private static String getSinkMockDdlStatement(String ddl) {
        Matcher matcher = PATTERN.matcher(ddl);
        if (matcher.find()) {
            // table name and columns
            String tableName = matcher.group(1);
            String columns = matcher.group(2);
            // with clause
            Map<String, String> withClauseMap = parseWithClause(matcher.group(3));
            // connector mock
            withClauseMap.put("connector", MockDynamicTableSinkFactory.IDENTIFIER);
            List<String> withOptionList = new ArrayList<>(withClauseMap.size());
            for (Map.Entry<String, String> entry : withClauseMap.entrySet()) {
                withOptionList.add("'" + entry.getKey() + "' = '" + entry.getValue() + "'");
            }
            String mockedWithOption = String.join(", ", withOptionList);
            return MessageFormat.format(MOCK_SQL_TEMPLATE, tableName, columns, mockedWithOption);
        }
        return ddl;
    }

    /**
     * parse with clause
     *
     * @param withClause with clause string
     * @return a hash map contains with clause information
     */
    private static Map<String, String> parseWithClause(String withClause) {
        Map<String, String> options = new HashMap<>();
        String[] keyValuePairs = withClause.split(",");
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replaceAll("['\"]", "");
                String value = keyValue[1].trim().replaceAll("['\"]", "");
                options.put(key, value);
            }
        }
        return options;
    }
}
