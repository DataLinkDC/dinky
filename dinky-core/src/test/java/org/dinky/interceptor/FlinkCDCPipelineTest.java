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

package org.dinky.interceptor;

import org.dinky.classloader.DinkyClassLoader;
import org.dinky.executor.Executor;
import org.dinky.executor.ExecutorConfig;
import org.dinky.executor.ExecutorFactory;

import org.junit.Ignore;
import org.junit.Test;

/**
 * FlinkCDCPipelineTest
 *
 */
public class FlinkCDCPipelineTest {

    @Ignore
    @Test
    public void mysqlTest() throws Exception {
        String statement = new StringBuilder()
                .append("source:\n" + "  type: mysql\n"
                        + "  hostname: localhost\n"
                        + "  port: 3306\n"
                        + "  username: root\n"
                        + "  password: 123456\n"
                        + "  tables: app_db.\\.*\n"
                        + "  server-id: 5400-5404\n"
                        + "  server-time-zone: UTC\n"
                        + "\n"
                        + "sink:\n"
                        + "  type: doris\n"
                        + "  fenodes: 127.0.0.1:8030\n"
                        + "  username: root\n"
                        + "  password: \"\"\n"
                        + "  table.create.properties.light_schema_change: true\n"
                        + "  table.create.properties.replication_num: 1\n"
                        + "\n"
                        + "pipeline:\n"
                        + "  name: Sync MySQL Database to Doris\n"
                        + "  parallelism: 2")
                .toString();

        ExecutorConfig executorConfig = ExecutorConfig.DEFAULT;
        Executor executor = ExecutorFactory.buildLocalExecutor(executorConfig, DinkyClassLoader.build());
        executor.executeSql(statement);
        executor.execute("");
    }
}
