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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

/**
 * MysqlTest
 *
 * @author wenmo
 * @since 2021/7/20 15:32
 */
@Ignore
public class HiveTest {

    private static final String IP = "cdh1";
    private static final Integer PORT = 10000;
    private static final String hiveDB = "test";
    private static final String username = "zhumingye";
    private static final String passwd = "123456";
    private static final String hive = "Hive";

    private static String url = "jdbc:hive2://" + IP + ":" + PORT + "/" + hiveDB;

    public Driver getDriver() {
        DriverConfig config = new DriverConfig();
        config.setType(hive);
        config.setName(hive);
        config.setIp(IP);
        config.setPort(PORT);
        config.setUsername(username);
        config.setPassword(passwd);
        config.setUrl(url);
        return Driver.build(config);
    }

    @Ignore
    @Test
    public void connectTest() {
        DriverConfig config = new DriverConfig();
        config.setType(hive);
        config.setName(hive);
        config.setIp(IP);
        config.setPort(PORT);
        config.setUsername(username);
        config.setPassword(passwd);
        config.setUrl(url);
        String test = Driver.build(config).test();
        System.out.println(test);
        System.err.println("end...");
    }

    @Ignore
    @Test
    public void getDBSTest() {
        Driver driver = getDriver();
        List<Schema> schemasAndTables = driver.listSchemas();
        schemasAndTables.forEach(
                schema -> {
                    System.out.println(schema.getName() + "\t\t" + schema.getTables().toString());
                });
        System.err.println("end...");
    }

    @Ignore
    @Test
    public void getTablesByDBTest() throws Exception {
        Driver driver = getDriver();
        driver.execute("use odsp ");
        List<Table> tableList = driver.listTables(hiveDB);
        tableList.forEach(
                schema -> {
                    System.out.println(schema.getName());
                });
        System.err.println("end...");
    }

    @Ignore
    @Test
    public void getColumnsByTableTest() {
        Driver driver = getDriver();
        List<Column> columns =
                driver.listColumns(hiveDB, "biz_college_planner_mysql_language_score_item");
        for (Column column : columns) {
            System.out.println(
                    column.getName() + " \t " + column.getType() + " \t " + column.getComment());
        }
        System.err.println("end...");
    }

    @Ignore
    @Test
    public void getCreateTableTest() throws Exception {
        Driver driver = getDriver();
        Table driverTable =
                driver.getTable(hiveDB, "biz_college_planner_mysql_language_score_item");
        String createTableSql = driver.getCreateTableSql(driverTable);
        System.out.println(createTableSql);
        System.err.println("end...");
    }

    @Ignore
    @Test
    public void getTableExtenedInfoTest() throws Exception {
        Driver driver = getDriver();
        Table driverTable = driver.getTable(hiveDB, "employees");
        for (Column column : driverTable.getColumns()) {
            System.out.println(
                    column.getName() + "\t\t" + column.getType() + "\t\t" + column.getComment());
        }
    }

    /**
     * @Author: zhumingye
     *
     * @date: 202/3/23 @Description: 测试hive多条SQL执行 @Param:
     * @return:
     */
    @Ignore
    @Test
    public void multipleSQLTest() throws Exception {
        Driver driver = getDriver();
        String sql =
                "select\n"
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
                System.out.println(
                        stringObjectEntry.getKey() + "\t\t" + stringObjectEntry.getValue());
            }
        }
    }
}
