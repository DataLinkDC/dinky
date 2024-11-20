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

import org.dinky.connector.mock.sink.MockDynamicTableSinkFactory;
import org.dinky.data.job.JobStatement;
import org.dinky.data.job.JobStatementType;
import org.dinky.data.job.SqlType;
import org.dinky.executor.CustomTableEnvironment;
import org.dinky.job.JobStatementPlan;
import org.dinky.utils.JsonUtils;

import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlInsert;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.dialect.AnsiSqlDialect;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.sql.parser.ddl.SqlCreateTable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockStatementExplainer {

    // Because calcite cannot parse flink sql ddl, a table environment is designed here for flink sql ddl pars
    private final CustomTableEnvironment tableEnv;
    private final SqlParser.Config calciteConfig;
    private final String DROP_TABLE_SQL_TEMPLATE = "DROP TABLE IF EXISTS {0}";
    private final String MOCK_SQL_TEMPLATE = "CREATE TABLE {0} ({1}) WITH ({2})";

    public static MockStatementExplainer build(CustomTableEnvironment tableEnv) {
        return new MockStatementExplainer(tableEnv);
    }

    public MockStatementExplainer(CustomTableEnvironment tableEnv) {
        this.tableEnv = tableEnv;
        this.calciteConfig = SqlParser.config().withLex(Lex.JAVA);
    }

    public void jobStatementPlanMock(JobStatementPlan jobStatementPlan) {
        mockSink(jobStatementPlan);
    }

    /**
     * The connector of insert tables will be changed to {@link MockDynamicTableSinkFactory}
     *
     * @param jobStatementPlan JobStatementPlan
     */
    private void mockSink(JobStatementPlan jobStatementPlan) {
        // Get table names that need to be mocked, and modify insert statement.
        Set<String> tablesNeedMock = getTableNamesNeedMockAndModifyTrans(jobStatementPlan);
        // mock insert table ddl
        List<JobStatement> jobStatementList = jobStatementPlan.getJobStatementList();
        for (int i = 0; i < jobStatementList.size(); i++) {
            if (!jobStatementList.get(i).getSqlType().equals(SqlType.CREATE)) {
                continue;
            }
            SqlNode sqlNode = tableEnv.parseSql(jobStatementList.get(i).getStatement());
            if (sqlNode instanceof SqlCreateTable) {
                SqlCreateTable sqlCreateTable = (SqlCreateTable) sqlNode;
                String tableName = sqlCreateTable.getTableName().toString();
                if (tablesNeedMock.contains(tableName)) {
                    // generate mock statement
                    jobStatementList.set(
                            i,
                            JobStatement.generateJobStatement(
                                    jobStatementList.get(i).getIndex(),
                                    getSinkMockDdlStatement(
                                            tableName,
                                            sqlCreateTable.getColumnList().toString()),
                                    JobStatementType.DDL,
                                    jobStatementList.get(i).getSqlType()));
                }
            }
        }
        log.debug("Mock sink succeed: {}", JsonUtils.toJsonString(jobStatementPlan));
    }

    /**
     * get tables names of insert statements, these tables will be mocked
     *
     * @param jobStatementPlan JobStatementPlan
     * @return a hash set, which contains all insert table names
     */
    private Set<String> getTableNamesNeedMockAndModifyTrans(JobStatementPlan jobStatementPlan) {
        Set<String> insertTables = new HashSet<>();
        List<JobStatement> jobStatementList = jobStatementPlan.getJobStatementList();
        for (int i = 0; i < jobStatementList.size(); i++) {
            if (jobStatementList.get(i).getSqlType().equals(SqlType.INSERT)) {
                try {
                    SqlInsert sqlInsert =
                            (SqlInsert) SqlParser.create(jobStatementList.get(i).getStatement(), calciteConfig)
                                    .parseQuery();
                    insertTables.add(sqlInsert.getTargetTable().toString());
                    SqlInsert mockedInsertTrans = new SqlInsert(
                            sqlInsert.getParserPosition(),
                            SqlNodeList.EMPTY,
                            new SqlIdentifier(
                                    generateMockedTableIdentifier(
                                            sqlInsert.getTargetTable().toString()),
                                    SqlParserPos.ZERO),
                            sqlInsert.getSource(),
                            sqlInsert.getTargetColumnList());
                    jobStatementList.set(
                            i,
                            JobStatement.generateJobStatement(
                                    jobStatementList.get(i).getIndex(),
                                    mockedInsertTrans
                                            .toSqlString(AnsiSqlDialect.DEFAULT)
                                            .toString(),
                                    JobStatementType.SQL,
                                    jobStatementList.get(i).getSqlType()));
                } catch (Exception e) {
                    log.error(
                            "Statement parse error, statement: {}",
                            jobStatementList.get(i).getStatement());
                }
            }
        }
        return insertTables;
    }

    /**
     * get mocked ddl statement
     *
     * @param tableName    table name
     * @param columns columns
     * @return ddl that connector is changed as well as other options not changed
     */
    private String getSinkMockDdlStatement(String tableName, String columns) {
        String mockedOption = "'connector'='" + MockDynamicTableSinkFactory.IDENTIFIER + "'";
        return MessageFormat.format(
                MOCK_SQL_TEMPLATE,
                StringUtils.join(generateMockedTableIdentifier(tableName), "."),
                columns,
                mockedOption);
    }

    /**
     * generate table identifier with mocked prefix info
     * @param tableName table name
     * @return table identifier with mocked prefix info
     */
    private List<String> generateMockedTableIdentifier(String tableName) {
        List<String> names = new ArrayList<>();
        names.add("default_catalog");
        names.add("default_database");
        names.add("mock_sink_" + tableName);
        return names;
    }
}
