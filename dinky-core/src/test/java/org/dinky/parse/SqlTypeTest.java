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

package org.dinky.parse;

import org.dinky.parser.SqlType;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class SqlTypeTest {

    @Test
    public void match() {
        test(
                "CREATE TABLE Orders (\n"
                        + "    order_number BIGINT,\n"
                        + "    price        DECIMAL(32,2),\n"
                        + "    buyer        ROW<first_name STRING, last_name STRING>,\n"
                        + "    order_time   TIMESTAMP(3)\n"
                        + ") WITH (\n"
                        + "  'connector' = 'datagen',\n"
                        + "  'rows-per-second' = '1'\n"
                        + ");",
                SqlType.CREATE,
                true);
        test("select * from Orders", SqlType.SELECT, true);
        test("DROP...", SqlType.DROP, true);
        test("ALTER...", SqlType.ALTER, true);
        test("INSERT...", SqlType.INSERT, true);
        test("DESC...", SqlType.DESC, true);
        test("DESCRIBE...", SqlType.DESCRIBE, true);
        test("EXPLAIN...", SqlType.EXPLAIN, true);
        test("USE...", SqlType.USE, true);
        test("SHOW...", SqlType.SHOW, true);
        test("LOAD...", SqlType.LOAD, true);
        test("UNLOAD...", SqlType.UNLOAD, true);
        test("SET...", SqlType.SET, true);
        test("RESET...", SqlType.RESET, true);
        test("EXECUTE...", SqlType.EXECUTE, true);
        test("ADD jar ...", SqlType.ADD_JAR, true);
        test("ADD customjar ...", SqlType.ADD, true);
        test("PRINT...", SqlType.PRINT, true);

        String sql = "CREATE TABLE print_OrdersView WITH ('connector' = 'printnet', 'port'='7125',"
                + " 'hostName' = '172.26.16.1', 'sink.parallelism'='1')\n"
                + "AS SELECT * FROM OrdersView; ";
        test(sql, SqlType.CTAS, true);
        test(sql, SqlType.CREATE, false);
    }

    private void test(String sql, SqlType sqlType, boolean result) {
        Assertions.assertThat(sqlType.match(sql)).isEqualTo(result);
    }
}
