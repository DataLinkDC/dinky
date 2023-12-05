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

package org.dinky.data.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.dinky.data.enums.ColumnType;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** */
class TableTest {

    private Table table;
    private Table tableWithoutKey;
    private String flinkConfig;

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
        tableWithoutKey = new Table("TableNameOrigin", "SchemaOrigin", columnWithoutKey);

        flinkConfig = "#{schemaName}=schemaName, #{tableName}=tableName, #{abc}=abc, #{}=null, bcd=bcd";
    }

    @Test
    void getFlinkDDL() {
        String result = table.getFlinkDDL(flinkConfig, "NewTableName");
        assertThat(
                result,
                equalTo("CREATE TABLE IF NOT EXISTS NewTableName (\n"
                        + "    `column1` INT NOT NULL COMMENT 'comment abc',\n"
                        + "    `column2` STRING COMMENT 'comment abc',\n"
                        + "    `column3` DOUBLE NOT NULL COMMENT 'comment abc',\n"
                        + "    PRIMARY KEY ( `column1`,`column2` ) NOT ENFORCED\n"
                        + ") WITH (\n"
                        + "#{schemaName}=schemaName, #{tableName}=tableName, #{abc}=abc,"
                        + " #{}=null, bcd=bcd)\n"));

        result = tableWithoutKey.getFlinkDDL(flinkConfig, "NewTableNameWithoutKey");
        assertThat(
                result,
                equalTo("CREATE TABLE IF NOT EXISTS NewTableNameWithoutKey (\n"
                        + "    `column1` INT NOT NULL COMMENT 'comment abc',\n"
                        + "    `column2` STRING COMMENT 'comment abc',\n"
                        + "    `column3` DOUBLE NOT NULL COMMENT 'comment abc') WITH (\n"
                        + "#{schemaName}=schemaName, #{tableName}=tableName, #{abc}=abc,"
                        + " #{}=null, bcd=bcd)\n"));
    }

    @Test
    void getFlinkTableWith() {
        String result = table.getFlinkTableWith(flinkConfig);
        assertThat(
                result,
                equalTo("SchemaOrigin=schemaName, TableNameOrigin=tableName, #{abc}=abc, #{}=null, " + "bcd=bcd"));
    }

    @Test
    void getFlinkTableSql() {
        String result = table.getFlinkTableSql("CatalogName", flinkConfig);
        assertThat(
                result,
                equalTo("DROP TABLE IF EXISTS TableNameOrigin;\n"
                        + "CREATE TABLE IF NOT EXISTS TableNameOrigin (\n"
                        + "    `column1` INT NOT NULL COMMENT 'comment abc',\n"
                        + "    `column2` STRING COMMENT 'comment abc',\n"
                        + "    `column3` DOUBLE NOT NULL COMMENT 'comment abc',\n"
                        + "    PRIMARY KEY ( `column1`,`column2` ) NOT ENFORCED\n"
                        + ") WITH (\n"
                        + "SchemaOrigin=schemaName, TableNameOrigin=tableName, #{abc}=abc,"
                        + " #{}=null, bcd=bcd)\n"));
    }
}
