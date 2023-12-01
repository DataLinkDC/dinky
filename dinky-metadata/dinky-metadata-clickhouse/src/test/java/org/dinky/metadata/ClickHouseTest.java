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
import org.dinky.metadata.driver.ClickHouseDriver;
import org.dinky.metadata.driver.Driver;
import org.dinky.metadata.result.JdbcSelectResult;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.text.StrFormatter;

/**
 * ClickhouseTest
 *
 * @since 2022/4/21 1:06
 */
@Ignore
public class ClickHouseTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClickHouseTest.class);

    private static final String IP = "127.0.0.1";
    private static final int PORT = 8123;
    private static final String url = StrFormatter.format("jdbc:clickhouse://{}:{}/default", IP, PORT);

    private final ClickHouseDriver clickHouseDriver = new ClickHouseDriver();

    public Driver getDriver() {
        DriverConfig<AbstractJdbcConfig> config = new DriverConfig<>();
        config.setType(clickHouseDriver.getType());
        config.setName(clickHouseDriver.getName());
        config.setConnectConfig(
                AbstractJdbcConfig.builder().ip(IP).port(PORT).url(url).build());
        return Driver.build(config);
    }

    @Ignore
    @Test
    public void connectTest() {
        String test = getDriver().test();
        // LOGGER.info(test);
        // LOGGER.info("end...");
    }

    @Ignore
    @Test
    public void schemaTest() {
        List<Schema> schemasAndTables = getDriver().getSchemasAndTables();
        // LOGGER.info(JsonUtils.toJsonString(schemasAndTables));
        // LOGGER.info("end...");
    }

    @Ignore
    @Test
    public void columnTest() {
        Driver driver = getDriver();
        List<Column> columns = driver.listColumns("xxx", "xxx");
        // LOGGER.info(JsonUtils.toJsonString(columns));
        // LOGGER.info("end...");
    }

    @Ignore
    @Test
    public void queryTest() {
        Driver driver = getDriver();
        JdbcSelectResult query = driver.query("select * from xxx", 10);
        // LOGGER.info(JsonUtils.toJsonString(query));
        // LOGGER.info("end...");
    }
}
