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

package com.dlink.flink.catalog;

import static org.apache.flink.table.api.config.ExecutionConfigOptions.TABLE_EXEC_RESOURCE_DEFAULT_PARALLELISM;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;

import org.junit.Before;
import org.junit.Test;

public class DlinkMysqlCatalogTest {

    protected static String url;
    protected static DlinkMysqlCatalog catalog;

    protected static final String TEST_CATALOG_NAME = "dlink";
    protected static final String TEST_USERNAME = "dlink";
    protected static final String TEST_PWD = "dlink";

    private TableEnvironment tableEnv;

    @Before
    public void setup() {
        url = "jdbc:mysql://127.0.0.1:3306/dlink?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC";
        catalog =
            new DlinkMysqlCatalog(
                TEST_CATALOG_NAME,
                url,
                TEST_USERNAME,
                TEST_PWD);

        this.tableEnv = TableEnvironment.create(EnvironmentSettings.inStreamingMode());
        tableEnv.getConfig()
            .getConfiguration()
            .setInteger(TABLE_EXEC_RESOURCE_DEFAULT_PARALLELISM.key(), 1);
    }

    @Test
    public void testSqlCatalog() {
        String createSql = "create catalog myCatalog \n"
            + " with('type'='dlink_mysql',\n"
            + " 'username'='dlink',\n"
            + " 'password'='dlink',\n"
            + " 'url'='jdbc:mysql://127.0.0.1:3306/"
            + "dlink?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC')";
        tableEnv.executeSql(createSql);
        tableEnv.executeSql("use catalog myCatalog");
    }
}
