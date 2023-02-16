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
import org.dinky.metadata.driver.ClickHouseDriver;
import org.dinky.metadata.driver.Driver;
import org.dinky.metadata.driver.DriverConfig;
import org.dinky.metadata.result.JdbcSelectResult;
import org.dinky.model.Column;
import org.dinky.model.Schema;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import cn.hutool.core.collection.CollectionUtil;

/**
 * ClickhouseTest
 *
 * @author heyang
 * @since 2022/4/21 1:06
 */
@Ignore
public class ClickHouseTest {

    private static final String IP = "192.168.2.101";
    private static String url = "jdbc:clickhouse://" + IP + ":8123/default";
    private ClickHouseDriver clickHouseDriver = new ClickHouseDriver();

    public Driver getDriver() {
        DriverConfig config = new DriverConfig();
        config.setType(clickHouseDriver.getType());
        config.setName(clickHouseDriver.getName());
        config.setIp(IP);
        config.setPort(8123);
        config.setUrl(url);
        return Driver.build(config);
    }

    @Ignore
    @Test
    public void connectTest() {
        String test = getDriver().test();
        Assert.assertSame("ClickHouse test connect fail!", CommonConstant.HEALTHY, test);
    }

    @Ignore
    @Test
    public void schemaTest() {
        List<Schema> schemasAndTables = getDriver().getSchemasAndTables();
        Assert.assertTrue(CollectionUtil.isNotEmpty(schemasAndTables));
    }

    @Ignore
    @Test
    public void columnTest() {
        Driver driver = getDriver();
        List<Column> columns = driver.listColumns("mym", "student");
        Assert.assertTrue(CollectionUtil.isNotEmpty(columns));
    }

    @Ignore
    @Test
    public void queryTest() {
        Driver driver = getDriver();
        JdbcSelectResult selectResult = driver.query("select * from student", 10);
        Assert.assertNotNull(selectResult);
    }
}
