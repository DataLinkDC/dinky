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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import org.dinky.data.constant.CommonConstant;
import org.dinky.data.model.Column;
import org.dinky.data.model.Schema;
import org.dinky.metadata.config.AbstractJdbcConfig;
import org.dinky.metadata.config.DriverConfig;
import org.dinky.metadata.driver.Driver;
import org.dinky.metadata.driver.MySqlDriver;
import org.dinky.metadata.result.JdbcSelectResult;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * MysqlTest
 *
 * @since 2021/7/20 15:32
 */
public class MysqlTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlTest.class);

    private static final String IP = "127.0.0.1";

    public Driver getDriver() {
        DriverConfig<AbstractJdbcConfig> config = new DriverConfig<>();
        config.setName(UUID.randomUUID().toString());
        config.setType("Mysql");
        config.setConnectConfig(AbstractJdbcConfig.builder()
                .ip(IP)
                .port(3306)
                .username("dca")
                .password("dca")
                .url("jdbc:mysql://"
                        + IP
                        + ":3306/dca?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8"
                        + "&serverTimezone=UTC&autoReconnect=true")
                .build());
        return Driver.build(config);
    }

    @Test
    public void connectTest() throws SQLException {
        DriverConfig<AbstractJdbcConfig> config = new DriverConfig<>();
        config.setType("Mysql");
        config.setName("name");
        config.setConnectConfig(AbstractJdbcConfig.builder()
                .ip(IP)
                .port(3306)
                .username("dca")
                .password("dca")
                .url("jdbc:mysql://"
                        + IP
                        + ":3306/dca?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8"
                        + "&serverTimezone=UTC&autoReconnect=true")
                .build());

        String test;
        try (MockedStatic<DriverManager> driverManager = Mockito.mockStatic(DriverManager.class)) {
            Connection conn = mock(Connection.class);
            driverManager
                    .when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(conn);
            doNothing().when(conn).close();
            try (MockedStatic<Driver> driver = Mockito.mockStatic(Driver.class)) {
                MySqlDriver sqlDriver = new MySqlDriver();
                DruidDataSource druidDataSource = mock(DruidDataSource.class);
                MySqlDriver spySqlDriver = spy(sqlDriver);
                doReturn(druidDataSource).when(spySqlDriver).createDataSource();
                spySqlDriver.buildDriverConfig(config.getName(), config.getType(), config.getConnectConfig());
                driver.when(() -> Driver.build(config)).thenReturn(spySqlDriver);
                test = Driver.build(config).test();
            }
        }
        assertThat(test, equalTo(CommonConstant.HEALTHY));
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
        List<Column> columns = driver.listColumns("dca", "MENU");
        LOGGER.info("end...");
    }

    @Ignore
    @Test
    public void queryTest() {
        Driver driver = getDriver();
        JdbcSelectResult query = driver.query("select * from MENU", 10);
        LOGGER.info("end...");
    }
}
