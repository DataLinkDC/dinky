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

package org.dinky.utils;

import org.dinky.data.exception.BusException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests JDBC address handling for data source connection testing.
 */
public class JdbcAddressTest {

    @Test
    public void testConnectionAcceptsSupportedJdbcAddress() {
        String[] addresses = {
            "jdbc:mysql://localhost:3306/dinky?useSSL=false&serverTimezone=UTC",
            "jdbc:postgresql://localhost:5432/dinky",
            "jdbc:oracle:thin:@localhost:1521:orcl",
            "jdbc:oracle:thin:@//localhost:1521/orcl",
            "jdbc:hive2://localhost:10000/dinky",
            "jdbc:clickhouse://localhost:8123/dinky",
            "jdbc:sqlserver://localhost:1433;DatabaseName=dinky",
            "jdbc:phoenix:localhost:2181",
            "jdbc:presto://localhost:8080/dinky",
            "jdbc:mysql://127.0.0.1:3306/orders",
            "jdbc:pivotal://localhost:5432/dinky",
            "  jdbc:mysql://localhost:3306/dinky  "
        };
        for (String address : addresses) {
            Assert.assertTrue(
                    "Expected a supported address to be accepted: " + address, JdbcAddress.isWellDefined(address));
            JdbcAddress.requireWellDefined(address);
        }
    }

    @Test
    public void testConnectionRejectsBlankJdbcAddress() {
        assertRejected(null);
        assertRejected("");
        assertRejected("   ");
    }

    @Test
    public void testConnectionRejectsMalformedJdbcAddress() {
        assertRejected("jdbc:");
        assertRejected("jdbc:mysql:");
        assertRejected("jdbc:mysql://");
        assertRejected("jdbc:postgresql://");
        assertRejected("jdbc:oracle:thin:");
        assertRejected("jdbc:oracle:thin:@");
        assertRejected("not-a-jdbc-url");
        assertRejected("mysql://localhost:3306/dinky");
    }

    @Test
    public void testConnectionRejectsUnexpectedSchemeJdbcAddress() {
        assertRejected("http://localhost:3306/dinky");
        assertRejected("jdbc:unknown://localhost:3306/dinky");
        assertRejected("jdbc:other://localhost:5432/dinky");
    }

    private static void assertRejected(String address) {
        Assert.assertFalse(
                "Expected a non well-defined address to be rejected: " + address, JdbcAddress.isWellDefined(address));
        try {
            JdbcAddress.requireWellDefined(address);
            Assert.fail("Expected a non well-defined address to be rejected: " + address);
        } catch (BusException ex) {
            Assert.assertEquals(JdbcAddress.NOT_WELL_DEFINED_MESSAGE, ex.getMessage());
        }
    }
}
