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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.dinky.data.model.Column;
import org.dinky.data.model.QueryData;
import org.dinky.data.model.Table;
import org.dinky.data.types.DataTypes;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MySqlDriverTest {

    private Table table;

    @BeforeEach
    void setUp() {
        List<Column> columns = Arrays.asList(
                Column.builder()
                        .name("column1")
                        .type("int")
                        .dataType(DataTypes.INT.toColumnType(false, 10))
                        .comment("comment abc")
                        .keyFlag(true)
                        .build(),
                Column.builder()
                        .name("column2")
                        .type("varchar")
                        .dataType(DataTypes.STRING.toColumnType(false, 50))
                        .comment("comment 'abc'")
                        .keyFlag(true)
                        .build(),
                Column.builder()
                        .name("column3")
                        .type("double")
                        .dataType(DataTypes.DOUBLE.toColumnType(true, 10))
                        .comment("comment \"abc\"")
                        .build());

        table = new Table("TableNameOrigin", "SchemaOrigin", columns);
    }

    @Test
    void genTable() {
        MySqlDriver sqlDriver = new MySqlDriver();
        String gen_table_sql = sqlDriver.getCreateTableSql(table);

        String expect = "CREATE TABLE IF NOT EXISTS `SchemaOrigin`.`TableNameOrigin` (\n"
                + "  `column1`  int NOT  NULL  COMMENT 'comment abc',\n"
                + "  `column2`  varchar NOT  NULL  COMMENT 'comment 'abc'',\n"
                + "  `column3`  double NOT  NULL  COMMENT 'comment \"abc\"',\n"
                + "  PRIMARY KEY (`column1`,`column2`)\n"
                + ")\n"
                + " ENGINE=InnoDB;";
        assertThat(gen_table_sql, equalTo(expect));
    }

    @Test
    void testGenQueryOptionWithLimitStartAndEnd() {
        // 测试使用limitStart和limitEnd的查询
        MySqlDriver driver = new MySqlDriver();
        QueryData queryData = new QueryData();
        queryData.setSchemaName("test_schema");
        queryData.setTableName("test_table");

        QueryData.Option option = new QueryData.Option();
        option.setLimitStart(10);
        option.setLimitEnd(20);
        queryData.setOption(option);

        StringBuilder result = driver.genQueryOption(queryData);

        assertNotNull(result);
        String expected = "select * from `test_schema`.`test_table` limit 10,20";
        assertEquals(expected, result.toString());
    }

    @Test
    void testGenQueryOptionWithAllOptions() {
        // 测试包含所有选项的复杂查询
        MySqlDriver driver = new MySqlDriver();
        QueryData queryData = new QueryData();
        queryData.setSchemaName("test_schema");
        queryData.setTableName("test_table");

        QueryData.Option option = new QueryData.Option();
        option.setWhere("created_date > '2023-01-01'");
        option.setOrder("id DESC");
        option.setLimitStart(5);
        option.setLimitEnd(15);
        queryData.setOption(option);

        StringBuilder result = driver.genQueryOption(queryData);

        assertNotNull(result);
        String expected =
                "select * from `test_schema`.`test_table` where created_date > '2023-01-01' order by id DESC limit 5,15";
        assertEquals(expected, result.toString());
    }
}
