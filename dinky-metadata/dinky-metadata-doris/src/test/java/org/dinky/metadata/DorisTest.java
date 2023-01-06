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

package org.dinky.metadata;

import org.dinky.metadata.driver.Driver;
import org.dinky.metadata.driver.DriverConfig;
import org.dinky.metadata.result.JdbcSelectResult;
import org.dinky.model.Column;
import org.dinky.model.Schema;
import org.dinky.model.Table;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class DorisTest {

    private Driver driver;

    @Before
    public void init() {
        DriverConfig config = new DriverConfig();
        config.setName(UUID.randomUUID().toString());
        config.setType("Doris");
        config.setIp("192.168.68.133");
        config.setPort(9030);
        config.setUsername("root");
        config.setPassword("123456");
        config.setUrl("jdbc:mysql://192.168.68.133:9030/test");
        try {
            driver = Driver.build(config);
        } catch (Exception e) {
            System.err.println("连接创建失败");
            e.printStackTrace();
        }
    }

    @Test
    public void test() throws SQLException {
        // test
        String test = driver.test();
        System.out.println(test);
        System.out.println("schema && table -----");
        testSchema();
        System.out.println("columns -----");
        testColumns();
        System.out.println("query -----");
        query();
    }

    @Test
    public void testSchema() {
        // schema && table
        List<Schema> schemasAndTables = driver.getSchemasAndTables();
        for (Schema schemasAndTable : schemasAndTables) {
            List<Table> tables = schemasAndTable.getTables();
            for (Table table : tables) {
                System.out.println(table.getName() + "  " + table.getSchema());
            }
        }
    }

    @Test
    public void testColumns() {
        // columns
        List<Column> columns = driver.listColumns("test", "scoreinfo");
        for (Column column : columns) {
            System.out.println(column.getName() + " " + column.getType() + column.getComment());
        }
    }

    @Test
    public void query() {
        JdbcSelectResult selectResult = driver.query("select * from scoreinfo ", 10);
        List<LinkedHashMap<String, Object>> rowData = selectResult.getRowData();
        for (LinkedHashMap<String, Object> rowDatum : rowData) {
            System.out.println(rowDatum);
        }
    }
}
