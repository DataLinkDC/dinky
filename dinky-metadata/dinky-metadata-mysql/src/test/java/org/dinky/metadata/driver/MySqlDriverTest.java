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
import org.dinky.data.model.Table;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MySqlDriverTest {

    private Table table;

    @BeforeEach
    void setUp() {
        List<Column> columns =
                Arrays.asList(
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

        table = new Table("TableNameOrigin", "SchemaOrigin", columns);
    }

    @Test
    void genTable() {
        MySqlDriver sqlDriver = new MySqlDriver();
        String gen_table_sql = sqlDriver.getCreateTableSql(table);

        String expect =
                "CREATE TABLE IF NOT EXISTS `SchemaOrigin`.`TableNameOrigin` (\n"
                        + "  `column1`  int NOT  NULL  COMMENT 'comment abc',\n"
                        + "  `column2`  varchar NOT  NULL  COMMENT 'comment 'abc'',\n"
                        + "  `column3`  double NOT  NULL  COMMENT 'comment \"abc\"',\n"
                        + "  PRIMARY KEY (`column1`,`column2`)\n"
                        + ")\n"
                        + " ENGINE=null;";
        assertThat(gen_table_sql, equalTo(expect));
    }
}
