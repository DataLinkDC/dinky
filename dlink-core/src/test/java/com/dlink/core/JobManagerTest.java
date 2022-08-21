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

package com.dlink.core;

import com.dlink.job.JobConfig;
import com.dlink.job.JobManager;
import com.dlink.job.JobResult;
import com.dlink.result.ResultPool;
import com.dlink.result.SelectResult;

import java.util.HashMap;

import org.junit.Test;

/**
 * JobManagerTest
 *
 * @author wenmo
 * @since 2021/6/3
 **/
public class JobManagerTest {

    @Test
    public void cancelJobSelect() {

        JobConfig config = new JobConfig("session-yarn", true, true, true, true, "s1", 2,
            null, null, null, "测试", false, false, false, 100, 0,
            1, 0, null, new HashMap<>(), new HashMap<>());
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
        System.out.println(result.isSuccess());
    }
}
