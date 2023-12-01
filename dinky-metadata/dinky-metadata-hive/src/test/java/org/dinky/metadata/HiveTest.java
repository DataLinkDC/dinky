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
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MysqlTest
 *
 * @since 2021/7/20 15:32
 */
@Ignore
public class HiveTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HiveTest.class);

    private static final String IP = "cdh1";
    private static final Integer PORT = 10000;
    private static final String hiveDB = "test";
    private static final String username = "zhumingye";
    private static final String passwd = "123456";
    private static final String hive = "Hive";

    private static String url = "jdbc:hive2://" + IP + ":" + PORT + "/" + hiveDB;

    public Driver getDriver() {
        DriverConfig<AbstractJdbcConfig> config = new DriverConfig<>();
        config.setType(hive);
        config.setName(hive);
        config.setConnectConfig(AbstractJdbcConfig.builder()
                .ip(IP)
                .port(PORT)
                .username(username)
                .password(passwd)
                .url(url)
                .build());
        return Driver.build(config);
    }

    @Ignore
    @Test
    public void connectTest() {
        DriverConfig<AbstractJdbcConfig> config = new DriverConfig<>();
        config.setType(hive);
        config.setName(hive);
        config.setConnectConfig(AbstractJdbcConfig.builder()
                .ip(IP)
                .port(PORT)
                .username(username)
                .password(passwd)
                .url(url)
                .build());
        String test = Driver.build(config).test();
        LOGGER.info(test);
        LOGGER.info("end...");
    }

    @Ignore
    @Test
    public void getDBSTest() {
        Driver driver = getDriver();
        List<Schema> schemasAndTables = driver.listSchemas();
        schemasAndTables.forEach(schema -> {
            LOGGER.info(schema.getName() + "\t\t" + schema.getTables().toString());
        });
        LOGGER.info("end...");
    }

    @Ignore
    @Test
    public void getTablesByDBTest() throws Exception {
        Driver driver = getDriver();
        driver.execute("use odsp ");
        List<Table> tableList = driver.listTables(hiveDB);
        tableList.forEach(schema -> {
            LOGGER.info(schema.getName());
        });
        LOGGER.info("end...");
    }

    @Ignore
    @Test
    public void getColumnsByTableTest() {
        Driver driver = getDriver();
        List<Column> columns = driver.listColumns(hiveDB, "biz_college_planner_mysql_language_score_item");
        for (Column column : columns) {
            LOGGER.info(column.getName() + " \t " + column.getType() + " \t " + column.getComment());
        }
        LOGGER.info("end...");
    }

    @Ignore
    @Test
    public void getCreateTableTest() throws Exception {
        Driver driver = getDriver();
        Table driverTable = driver.getTable(hiveDB, "biz_college_planner_mysql_language_score_item");
        String createTableSql = driver.getCreateTableSql(driverTable);
        LOGGER.info(createTableSql);
        LOGGER.info("end...");
    }

    @Ignore
    @Test
    public void getTableExtenedInfoTest() throws Exception {
        Driver driver = getDriver();
        Table driverTable = driver.getTable(hiveDB, "employees");
        for (Column column : driverTable.getColumns()) {
            LOGGER.info(column.getName() + "\t\t" + column.getType() + "\t\t" + column.getComment());
        }
    }

    /**
     * @Author: zhumingye
     * @return:
     */
    @Ignore
    @Test
    public void multipleSQLTest() throws Exception {
        Driver driver = getDriver();
        String sql = "select\n"
                + "   date_format(create_time,'yyyy-MM') as  pay_success_time,\n"
                + "   sum(pay_amount)/100 as amount\n"
                + "from\n"
                + "    odsp.pub_pay_mysql_pay_order\n"
                + "    group by date_format(create_time,'yyyy-MM') ;\n"
                + "select\n"
                + "   *\n"
                + "from\n"
                + "    odsp.pub_pay_mysql_pay_order ;";
        JdbcSelectResult selectResult = driver.executeSql(sql, 100);
        for (LinkedHashMap<String, Object> rowDatum : selectResult.getRowData()) {
            Set<Map.Entry<String, Object>> entrySet = rowDatum.entrySet();
            for (Map.Entry<String, Object> stringObjectEntry : entrySet) {
                LOGGER.info(stringObjectEntry.getKey() + "\t\t" + stringObjectEntry.getValue());
            }
        }
    }
}
