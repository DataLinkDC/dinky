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

package com.dlink;

import com.dlink.metadata.driver.Driver;
import com.dlink.metadata.driver.DriverConfig;
import com.dlink.metadata.result.JdbcSelectResult;
import com.dlink.model.Column;
import com.dlink.model.Schema;
import com.dlink.model.Table;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class PrestoTest {

    private Driver driver;

    @Before
    public void init() {
        DriverConfig config = new DriverConfig();
        config.setName(UUID.randomUUID().toString());
        config.setType("Presto");
        config.setIp("10.168.100.115");
        config.setUsername("presto");
        config.setUrl("jdbc:presto://10.168.100.115:2080");
        try {
            driver = Driver.build(config);
        } catch (Exception e) {
            System.err.println("连接创建失败:" + e.getMessage());
        }
    }

    @Test
    public void test() throws SQLException {
        // test
        String test = driver.test();
        System.out.println(test);
        System.out.println("schema && table...");
        testSchema();
        System.out.println("columns...");
        testColumns();
        System.out.println("query...");
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
        List<Column> columns = driver.listColumns("hive.lake", "test");
        for (Column column : columns) {
            System.out.println(column.getName() + " " + column.getType() + " " + column.getComment());
        }
    }

    @Test
    public void query() {
        JdbcSelectResult selectResult = driver.query("select * from hive.lake.test", 10);
        List<LinkedHashMap<String, Object>> rowData = selectResult.getRowData();
        for (LinkedHashMap<String, Object> rowDatum : rowData) {
            System.out.println(rowDatum);
        }
    }
}
