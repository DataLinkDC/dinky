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
import org.dinky.metadata.config.AbstractJdbcConfig;
import org.dinky.metadata.config.DriverConfig;
import org.dinky.metadata.driver.Driver;
import org.dinky.metadata.result.JdbcSelectResult;

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class PhoenixTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhoenixTest.class);

    private Driver driver;

    @Before
    public void init() {
        DriverConfig<AbstractJdbcConfig> config = new DriverConfig<>();
        config.setName("phoenix");
        config.setType("Phoenix");
        config.setConnectConfig(AbstractJdbcConfig.builder()
                .url("jdbc:phoenix:zxbd-test-hbase:2181")
                .build());
        try {
            driver = Driver.build(config);
        } catch (Exception e) {
            LOGGER.error("连接创建失败:" + e.getMessage());
        }
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
    public void testListTables() {
        List<Table> tables = driver.listTables("");
        for (Table table : tables) {
            LOGGER.info(table.getName() + "  " + table.getSchema());
        }
    }

    @Ignore
    @Test
    public void testColumns() {
        // columns
        List<Column> columns = driver.listColumns(null, "ODS_OUTP_PRESC");
        for (Column column : columns) {
            LOGGER.info(column.getName() + " " + column.getType() + " " + column.getComment());
        }
    }

    @Ignore
    @Test
    public void query() {
        JdbcSelectResult selectResult = driver.query("select * from ODS_OUTP_PRESC ", 10);
        List<LinkedHashMap<String, Object>> rowData = selectResult.getRowData();
        for (LinkedHashMap<String, Object> rowDatum : rowData) {
            LOGGER.info(String.valueOf(rowDatum));
        }
    }
}
