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

import java.util.List;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;

/**
 * MysqlTest
 *
 * @author wenmo
 * @since 2021/7/20 15:32
 */
public class MysqlTest {

    private static final String IP = "127.0.0.1";

    public Driver getDriver() {
        DriverConfig config = new DriverConfig();
        config.setName(UUID.randomUUID().toString());
        config.setType("Mysql");
        config.setIp(IP);
        config.setPort(3306);
        config.setUsername("dca");
        config.setPassword("dca");
        config.setUrl(
                "jdbc:mysql://"
                        + IP
                        + ":3306/dca?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&autoReconnect=true");
        return Driver.build(config);
    }

    @Ignore
    @Test
    public void connectTest() {
        DriverConfig config = new DriverConfig();
        config.setType("Mysql");
        config.setIp(IP);
        config.setPort(3306);
        config.setUsername("dca");
        config.setPassword("dca");
        config.setUrl(
                "jdbc:mysql://"
                        + IP
                        + ":3306/dca?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&autoReconnect=true");
        String test = Driver.build(config).test();
        System.out.println(test);
        System.out.println("end...");
    }

    @Ignore
    @Test
    public void schemaTest() {
        Driver driver = getDriver();
        List<Schema> schemasAndTables = driver.getSchemasAndTables();
        System.out.println("end...");
    }

    @Ignore
    @Test
    public void columnTest() {
        Driver driver = getDriver();
        List<Column> columns = driver.listColumns("dca", "MENU");
        System.out.println("end...");
    }

    @Ignore
    @Test
    public void queryTest() {
        Driver driver = getDriver();
        JdbcSelectResult query = driver.query("select * from MENU", 10);
        System.out.println("end...");
    }
}
