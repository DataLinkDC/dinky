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

import org.dinky.data.enums.GatewayType;
import org.dinky.data.result.ResultPool;
import org.dinky.data.result.SelectResult;
import org.dinky.job.JobConfig;
import org.dinky.job.JobManager;
import org.dinky.job.JobResult;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JobManagerTest
 *
 * @since 2021/6/3
 */
@Ignore
public class JobManagerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobManagerTest.class);

    @Ignore
    @Test
    public void cancelJobSelect() throws Exception {
        JobConfig config = JobConfig.builder()
                .type(GatewayType.YARN_SESSION.getLongValue())
                .useResult(true)
                .useChangeLog(true)
                .useAutoCancel(true)
                .clusterId(2)
                .jobName("Test")
                .fragment(false)
                .statementSet(false)
                .batchModel(false)
                .maxRowNum(100)
                .parallelism(1)
                .build();
        if (config.isUseRemote()) {
            config.setAddress("192.168.123.157:8081");
        }
        JobManager jobManager = JobManager.build(config);
        String sql1 = "CREATE TABLE Orders (\n"
                + "    order_number BIGINT,\n"
                + "    price        DECIMAL(32,2),\n"
                + "    order_time   TIMESTAMP(3)\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '1'\n"
                + ");";
        String sql3 = "select order_number,price,order_time from Orders";
        String sql = sql1 + sql3;
        JobResult result = jobManager.executeSql(sql);
        SelectResult selectResult = ResultPool.get(result.getJobId());
        LOGGER.info("sql:{}, execute result:{}", sql, result.isSuccess());
    }
}
