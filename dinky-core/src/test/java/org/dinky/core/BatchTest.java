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

package org.dinky.core;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;

import org.junit.Ignore;
import org.junit.Test;

/**
 * BatchTest
 *
 * @since 2022/2/7 23:15
 */
@Ignore
public class BatchTest {

    @Ignore
    @Test
    public void batchTest() {
        String source = "CREATE TABLE Orders (\n"
                + "    order_number BIGINT,\n"
                + "    price        DECIMAL(32,2),\n"
                + "    buyer        ROW<first_name STRING, last_name STRING>,\n"
                + "    order_time   TIMESTAMP(3)\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'number-of-rows' = '100'\n"
                + ")";
        String select = "select order_number,price,order_time from Orders";
        // LocalEnvironment environment = ExecutionEnvironment.createLocalEnvironment();
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                // .inStreamingMode() // 声明为流任务
                .inBatchMode() // 声明为批任务
                .build();

        TableEnvironment tEnv = TableEnvironment.create(settings);
        tEnv.executeSql(source);
        TableResult tableResult = tEnv.executeSql(select);
        tableResult.print();
    }
}
