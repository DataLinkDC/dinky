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

package com.dlink.interceptor;

import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import com.dlink.executor.LocalStreamExecutor;

import org.junit.Test;

/**
 * @author ZackYoung
 * @since 0.6.8
 */
public class CdcSourceTests {

    @Test
    public void printTest() throws Exception {
        String statement =
                new StringBuilder().append("EXECUTE CDCSOURCE jobname WITH (\n")
                        .append("  'connector' = 'mysql-cdc',\n").append("  'hostname' = '127.0.0.1',\n")
                        .append("  'port' = '3306',\n")
                        .append("  'username' = 'root',\n").append("  'password' = '123456',\n")
                        .append("  'checkpoint' = '3000',\n").append("  'scan.startup.mode' = 'initial',\n")
                        .append("  'parallelism' = '1',\n").append("  'source.server-time-zone' = 'UTC',\n")
                        .append("  'table-name' = 'dlink\\.dlink_flink_document',\n")
                        .append("  'sink.connector'='print'\n").append(")").toString();

        ExecutorSetting executorSetting = ExecutorSetting.DEFAULT;
        Executor executor = LocalStreamExecutor.buildLocalExecutor(executorSetting);
        executor.executeSql(statement);
        executor.execute("");
    }
}
