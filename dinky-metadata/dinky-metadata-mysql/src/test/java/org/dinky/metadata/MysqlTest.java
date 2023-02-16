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

import org.dinky.constant.CommonConstant;
import org.dinky.metadata.driver.Driver;
import org.dinky.metadata.driver.DriverConfig;
import org.dinky.metadata.result.JdbcSelectResult;
import org.dinky.model.Column;
import org.dinky.model.Schema;

import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.collection.CollectionUtil;

/**
 * MysqlTest
 *
 * @author wenmo
 * @since 2021/7/20 15:32
 */
@Ignore
public class MysqlTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlTest.class);

    private static final String IP = "127.0.0.1";

    public Driver getDriver() {
        DriverConfig config = new DriverConfig();
        config.setName(UUID.randomUUID().toString());
        config.setType("Mysql");
        config.setIp(IP);
        config.setPort(3306);
        config.setUsername("dinky");
        config.setPassword("dinky");
        config.setUrl(
                "jdbc:mysql://"
                        + IP
                        + ":3306/dinky?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&autoReconnect=true");
        return Driver.build(config);
    }

    @Ignore
    @Test
    public void connectTest() {
        Driver driver = getDriver();
        String test = driver.test();
        Assert.assertSame("mysql test connect fail!", CommonConstant.HEALTHY, test);
        LOGGER.info("end...");
    }

    @Ignore
    @Test
    public void schemaTest() {
        Driver driver = getDriver();
        List<Schema> schemasAndTables = driver.getSchemasAndTables();
        Assert.assertTrue(CollectionUtil.isNotEmpty(schemasAndTables));
        LOGGER.info("end...");
    }

    @Ignore
    @Test
    public void columnTest() {
        Driver driver = getDriver();
        List<Column> columns = driver.listColumns("dinky", "dinky_user");
        Assert.assertTrue(CollectionUtil.isNotEmpty(columns));
        LOGGER.info("end...");
    }

    @Ignore
    @Test
    public void queryTest() {
        Driver driver = getDriver();
        JdbcSelectResult query = driver.query("select * from dinky_user", 10);
        Assert.assertNotNull(query);
        LOGGER.info("end...");
    }
}
