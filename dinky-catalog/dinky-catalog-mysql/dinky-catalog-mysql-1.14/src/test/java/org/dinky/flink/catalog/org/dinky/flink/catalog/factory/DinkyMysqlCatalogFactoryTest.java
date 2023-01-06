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

package com.dlink.flink.catalog.com.dlink.flink.catalog.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.dlink.flink.catalog.DlinkMysqlCatalog;
import com.dlink.flink.catalog.factory.DlinkMysqlCatalogFactoryOptions;

import org.apache.flink.table.catalog.Catalog;
import org.apache.flink.table.catalog.CommonCatalogOptions;
import org.apache.flink.table.factories.FactoryUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class DlinkMysqlCatalogFactoryTest {

    protected static String url;
    protected static DlinkMysqlCatalog catalog;

    protected static final String TEST_CATALOG_NAME = "dlink";
    protected static final String TEST_USERNAME = "dlink";
    protected static final String TEST_PWD = "dlink";

    @BeforeClass
    public static void setup() throws SQLException {
        url = "jdbc:mysql://10.1.51.25:3306/dlink?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC";

        catalog =
            new DlinkMysqlCatalog(
                TEST_CATALOG_NAME,
                url,
                TEST_USERNAME,
                TEST_PWD);
    }

    @Test
    public void test() {
        final Map<String, String> options = new HashMap<>();
        options.put(CommonCatalogOptions.CATALOG_TYPE.key(), DlinkMysqlCatalogFactoryOptions.IDENTIFIER);
        options.put(DlinkMysqlCatalogFactoryOptions.USERNAME.key(), TEST_USERNAME);
        options.put(DlinkMysqlCatalogFactoryOptions.PASSWORD.key(), TEST_PWD);
        options.put(DlinkMysqlCatalogFactoryOptions.URL.key(), url);

        final Catalog actualCatalog =
            FactoryUtil.createCatalog(
                TEST_CATALOG_NAME,
                options,
                null,
                Thread.currentThread().getContextClassLoader());

        checkEquals(catalog, (DlinkMysqlCatalog) actualCatalog);

        assertTrue(actualCatalog instanceof DlinkMysqlCatalog);
    }

    private static void checkEquals(DlinkMysqlCatalog c1, DlinkMysqlCatalog c2) {
        assertEquals(c1.getName(), c2.getName());
        assertEquals(c1.getDefaultDatabase(), c2.getDefaultDatabase());
        assertEquals(c1.getUser(), c2.getUser());
        assertEquals(c1.getPwd(), c2.getPwd());
        assertEquals(c1.getUrl(), c2.getUrl());
    }
}
