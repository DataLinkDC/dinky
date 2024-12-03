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

package org.dinky.flink.catalog.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.dinky.flink.catalog.DinkyPostgresCatalog;

import org.apache.flink.table.catalog.Catalog;
import org.apache.flink.table.catalog.CommonCatalogOptions;
import org.apache.flink.table.factories.FactoryUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class DinkyPostgresSQLCatalogFactoryTest {

    protected static String url;
    protected static DinkyPostgresCatalog catalog;

    protected static final String TEST_CATALOG_NAME = "dinky";
    protected static final String TEST_USERNAME = "dinky";
    protected static final String TEST_PWD = "dinky";

    @BeforeClass
    public static void setup() throws SQLException {
        url = "jdbc:postgresql://localhost:5432/dinky_logic?stringtype=unspecified";

        catalog = new DinkyPostgresCatalog(TEST_CATALOG_NAME, url, TEST_USERNAME, TEST_PWD);
    }

    @Test
    public void test() {
        final Map<String, String> options = new HashMap<>();
        options.put(CommonCatalogOptions.CATALOG_TYPE.key(), DinkyPostgresCatalogFactoryOptions.IDENTIFIER);
        options.put(DinkyPostgresCatalogFactoryOptions.USERNAME.key(), TEST_USERNAME);
        options.put(DinkyPostgresCatalogFactoryOptions.PASSWORD.key(), TEST_PWD);
        options.put(DinkyPostgresCatalogFactoryOptions.URL.key(), url);

        final Catalog actualCatalog = FactoryUtil.createCatalog(
                TEST_CATALOG_NAME, options, null, Thread.currentThread().getContextClassLoader());

        checkEquals(catalog, (DinkyPostgresCatalog) actualCatalog);

        assertTrue(actualCatalog instanceof DinkyPostgresCatalog);
    }

    private static void checkEquals(DinkyPostgresCatalog c1, DinkyPostgresCatalog c2) {
        assertEquals(c1.getName(), c2.getName());
        assertEquals(c1.getDefaultDatabase(), c2.getDefaultDatabase());
        assertEquals(c1.getUser(), c2.getUser());
        assertEquals(c1.getPwd(), c2.getPwd());
        assertEquals(c1.getUrl(), c2.getUrl());
    }
}
