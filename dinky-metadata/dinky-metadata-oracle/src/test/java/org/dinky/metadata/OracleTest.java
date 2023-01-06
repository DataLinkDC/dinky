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

package com.dlink.metadata;

import com.dlink.metadata.driver.Driver;
import com.dlink.metadata.driver.DriverConfig;
import com.dlink.metadata.result.JdbcSelectResult;
import com.dlink.model.Column;
import com.dlink.model.Schema;

import java.util.List;
import java.util.UUID;

import org.junit.Test;

/**
 * OracleTest
 *
 * @author wenmo
 * @since 2021/7/21 16:14
 **/
public class OracleTest {

    private static final String IP = "127.0.0.1";

    public Driver getDriver() {
        DriverConfig config = new DriverConfig();
        config.setName(UUID.randomUUID().toString());
        config.setType("Oracle");
        config.setIp(IP);
        config.setPort(1521);
        config.setUsername("cdr");
        config.setPassword("cdr");
        config.setUrl("jdbc:oracle:thin:@" + IP + ":1521:orcl");
        return Driver.build(config);
    }

    @Test
    public void connectTest() {
        DriverConfig config = new DriverConfig();
        config.setType("Oracle");
        config.setIp(IP);
        config.setPort(1521);
        config.setUsername("cdr");
        config.setPassword("cdr");
        config.setUrl("jdbc:oracle:thin:@" + IP + ":1521:orcl");
        String test = Driver.build(config).test();
        System.out.println(test);
        System.out.println("end...");
    }

    @Test
    public void schemaTest() {
        Driver driver = getDriver();
        List<Schema> schemasAndTables = driver.getSchemasAndTables();
        System.out.println("end...");
    }

    @Test
    public void columnTest() {
        Driver driver = getDriver();
        List<Column> columns = driver.listColumns("CDR", "PAT_INFO");
        System.out.println("end...");
    }

    @Test
    public void queryTest() {
        Driver driver = getDriver();
        JdbcSelectResult selectResult = driver.query("select * from CDR.PAT_INFO where ROWNUM<10", 10);
        System.out.println("end...");
    }
}
