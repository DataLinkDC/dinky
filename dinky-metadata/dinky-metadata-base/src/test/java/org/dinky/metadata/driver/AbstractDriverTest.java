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

package org.dinky.metadata.driver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.dinky.data.enums.ColumnType;
import org.dinky.data.model.Column;
import org.dinky.data.model.QueryData;
import org.dinky.data.model.Schema;
import org.dinky.data.model.Table;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.metadata.config.AbstractJdbcConfig;
import org.dinky.metadata.convert.ITypeConvert;
import org.dinky.metadata.query.IDBQuery;
import org.dinky.metadata.result.JdbcSelectResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractDriverTest {

    private Table table;

    @BeforeEach
    void setUp() {
        List<Column> columns = Arrays.asList(
                Column.builder()
                        .name("column1")
                        .type("int")
                        .javaType(ColumnType.INT)
                        .comment("comment abc")
                        .keyFlag(true)
                        .build(),
                Column.builder()
                        .name("column2")
                        .type("varchar")
                        .javaType(ColumnType.STRING)
                        .comment("comment 'abc'")
                        .keyFlag(true)
                        .build(),
                Column.builder()
                        .name("column3")
                        .type("double")
                        .javaType(ColumnType.DOUBLE)
                        .comment("comment \"abc\"")
                        .build());

        List<Column> columnWithoutKey = Arrays.asList(
                Column.builder()
                        .name("column1")
                        .type("int")
                        .javaType(ColumnType.INT)
                        .comment("comment abc")
                        .build(),
                Column.builder()
                        .name("column2")
                        .type("varchar")
                        .javaType(ColumnType.STRING)
                        .comment("comment 'abc'")
                        .build(),
                Column.builder()
                        .name("column3")
                        .type("double")
                        .javaType(ColumnType.DOUBLE)
                        .comment("comment \"abc\"")
                        .build());

        table = new Table("TableNameOrigin", "SchemaOrigin", columns);
    }

    @Test
    void getSqlSelect() {
        SubAbstractDriver ad = new SubAbstractDriver();
        String result = ad.getSqlSelect(table);
        assertThat(
                result,
                equalTo("SELECT\n    `column1`  --  comment abc \n"
                        + "    ,`column2`  --  comment abc \n"
                        + "    ,`column3`  --  comment abc \n"
                        + " FROM SchemaOrigin.TableNameOrigin;\n"));
    }

    private static class SubAbstractDriver extends AbstractDriver<AbstractJdbcConfig> {
        @Override
        public IDBQuery getDBQuery() {
            return null;
        }

        @Override
        public ITypeConvert getTypeConvert() {
            return null;
        }

        @Override
        public <T> Driver buildDriverConfig(String name, String type, T config) {
            return null;
        }

        @Override
        public String getType() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String test() {
            return null;
        }

        @Override
        public Driver connect() {
            return null;
        }

        @Override
        public void close() {}

        @Override
        public List<Schema> listSchemas() {
            return null;
        }

        @Override
        public boolean existSchema(String schemaName) {
            return false;
        }

        @Override
        public boolean createSchema(String schemaName) throws Exception {
            return false;
        }

        @Override
        public String generateCreateSchemaSql(String schemaName) {
            return null;
        }

        @Override
        public List<Table> listTables(String schemaName) {
            return null;
        }

        @Override
        public List<Column> listColumns(String schemaName, String tableName) {
            return null;
        }

        @Override
        public List<Column> listColumnsSortByPK(String schemaName, String tableName) {
            return null;
        }

        @Override
        public boolean createTable(Table table) throws Exception {
            return false;
        }

        @Override
        public boolean generateCreateTable(Table table) throws Exception {
            return false;
        }

        @Override
        public boolean dropTable(Table table) throws Exception {
            return false;
        }

        @Override
        public boolean truncateTable(Table table) throws Exception {
            return false;
        }

        @Override
        public String getCreateTableSql(Table table) {
            return null;
        }

        @Override
        public String getDropTableSql(Table table) {
            return null;
        }

        @Override
        public String getTruncateTableSql(Table table) {
            return null;
        }

        @Override
        public String generateCreateTableSql(Table table) {
            return null;
        }

        @Override
        public boolean execute(String sql) throws Exception {
            return false;
        }

        @Override
        public int executeUpdate(String sql) throws Exception {
            return 0;
        }

        @Override
        public JdbcSelectResult query(String sql, Integer limit) {
            return null;
        }

        @Override
        public StringBuilder genQueryOption(QueryData queryData) {
            return null;
        }

        @Override
        public JdbcSelectResult executeSql(String sql, Integer limit) {
            return null;
        }

        @Override
        public List<SqlExplainResult> explain(String sql) {
            return null;
        }

        @Override
        public Map<String, String> getFlinkColumnTypeConversion() {
            return null;
        }
    }
}
