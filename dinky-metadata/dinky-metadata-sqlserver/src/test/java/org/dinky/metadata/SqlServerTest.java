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

import org.dinky.data.model.Column;
import org.dinky.data.model.Schema;
import org.dinky.data.model.Table;
import org.dinky.metadata.driver.Driver;
import org.dinky.metadata.driver.DriverConfig;
import org.dinky.metadata.result.JdbcSelectResult;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class SqlServerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlServerTest.class);

    private Driver driver;

    @Before
    public void init() {
        DriverConfig config = new DriverConfig();
        config.setName(UUID.randomUUID().toString());
        config.setType("SqlServer");
        config.setIp("192.168.68.133");
        config.setPort(1433);
        config.setUsername("sa");
        config.setPassword("OcP2020123");
        config.setUrl("jdbc:sqlserver://192.168.68.133:1433;DatabaseName=test");
        try {
            driver = Driver.build(config);
        } catch (Exception e) {
            LOGGER.error("连接创建失败:" + e.getMessage());
        }
    }

    @Ignore
    @Test
    public void test() throws SQLException {
        // test
        String test = driver.test();
        LOGGER.info(test);
        LOGGER.info("schema && table...");
        testSchema();
        LOGGER.info("columns...");
        testColumns();
        LOGGER.info("query...");
        query();
    }

    @Ignore
    @Test
    public void testSchema() {
        // schema && table
        List<Schema> schemasAndTables = driver.getSchemasAndTables();
        for (Schema schemasAndTable : schemasAndTables) {
            List<Table> tables = schemasAndTable.getTables();
            for (Table table : tables) {
                LOGGER.info(table.getName() + "  " + table.getSchema());
            }
        }
    }

    @Ignore
    @Test
    public void testColumns() {
        // columns
        List<Column> columns = driver.listColumns("dbo", "t_user");
        for (Column column : columns) {
            LOGGER.info(column.getName() + " " + column.getType() + " " + column.getComment());
        }
    }

    @Ignore
    @Test
    public void query() {
        JdbcSelectResult selectResult = driver.query("select * from t_user", 10);
        List<LinkedHashMap<String, Object>> rowData = selectResult.getRowData();
        for (LinkedHashMap<String, Object> rowDatum : rowData) {
            LOGGER.info(String.valueOf(rowDatum));
        }
    }
}
