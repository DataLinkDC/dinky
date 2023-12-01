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
import org.dinky.metadata.config.AbstractJdbcConfig;
import org.dinky.metadata.config.DriverConfig;
import org.dinky.metadata.driver.Driver;
import org.dinky.metadata.result.JdbcSelectResult;

import java.util.List;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OracleTest
 *
 * @since 2021/7/21 16:14
 */
@Ignore
public class OracleTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OracleTest.class);

    private static final String IP = "127.0.0.1";

    public Driver getDriver() {
        DriverConfig<AbstractJdbcConfig> config = new DriverConfig<>();
        config.setName(UUID.randomUUID().toString());
        config.setType("Oracle");
        config.setConnectConfig(AbstractJdbcConfig.builder()
                .ip(IP)
                .port(3306)
                .username("dca")
                .password("dca")
                .url("jdbc:oracle:thin:@" + IP + ":1521:orcl")
                .build());
        return Driver.build(config);
    }

    @Ignore
    @Test
    public void connectTest() {
        DriverConfig<AbstractJdbcConfig> config = new DriverConfig<>();
        config.setType("Oracle");
        config.setConnectConfig(AbstractJdbcConfig.builder()
                .ip(IP)
                .port(3306)
                .username("dca")
                .password("dca")
                .url("jdbc:oracle:thin:@" + IP + ":1521:orcl")
                .build());
        String test = Driver.build(config).test();
        LOGGER.info(test);
        LOGGER.info("end...");
    }

    @Ignore
    @Test
    public void schemaTest() {
        Driver driver = getDriver();
        List<Schema> schemasAndTables = driver.getSchemasAndTables();
        LOGGER.info("end...");
    }

    @Ignore
    @Test
    public void columnTest() {
        Driver driver = getDriver();
        List<Column> columns = driver.listColumns("CDR", "PAT_INFO");
        LOGGER.info("end...");
    }

    @Ignore
    @Test
    public void queryTest() {
        Driver driver = getDriver();
        JdbcSelectResult selectResult = driver.query("select * from CDR.PAT_INFO where ROWNUM<10", 10);
        LOGGER.info("end...");
    }
}
