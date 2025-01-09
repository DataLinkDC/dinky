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

import org.dinky.metadata.config.AbstractJdbcConfig;
import org.dinky.metadata.config.DriverConfig;
import org.dinky.metadata.driver.Driver;
import org.dinky.metadata.result.JdbcSelectResult;
import org.dinky.data.model.Column;
import org.dinky.data.model.Schema;
import org.dinky.data.model.Table;

import java.util.List;
import java.util.UUID;

import org.junit.Test;

/**
 * MysqlTest
 *
 * @author wenmo
 * @since 2021/7/20 15:32
 **/
public class DmTest {

    private static final String IP = "192.168.210.77";

    public Driver getDriver() {
        DriverConfig<AbstractJdbcConfig> config = new DriverConfig<>();
        config.setName(UUID.randomUUID().toString());
        config.setType("dm");
        config.setConnectConfig(AbstractJdbcConfig.builder()
                .ip(IP)
                .port(5236)
                .username("SFTOA")
                .password("123456789")
                .url("jdbc:dm://" + IP
                        + ":5236/SFTOA?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&autoReconnect=true")
                .build());
        return Driver.build(config);
    }

    @Test
    public void connectTest() {
        String test = getDriver().test();
        System.out.println(test);
        System.out.println("end...");
    }

    @Test
    public void schemaTest() {
        Driver driver = getDriver();
        List<Schema> schemasAndTables = driver.getSchemasAndTables();
        Table table = driver.getTable("SFTOA", "EGOV_DISPATCH");
        String sql = table.getFlinkTableSql("SFTOA", "");
        String select = driver.getSqlSelect(table);
        String ddl = driver.getCreateTableSql(table);
        driver.close();
        System.out.println("end...");
    }

    @Test
    public void columnTest() {
        Driver driver = getDriver();
        List<Column> columns = driver.listColumns("dca", "MENU");
        System.out.println("end...");
    }

    @Test
    public void queryTest() {
        Driver driver = getDriver();
        JdbcSelectResult query = driver.query("select * from MENU", 10);
        System.out.println("end...");
    }
}
