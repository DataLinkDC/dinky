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

package org.dinky.job;

import static org.junit.jupiter.api.Assertions.*;

import org.dinky.data.enums.GatewayType;

import org.apache.flink.configuration.RestOptions;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class JobConfigTest {

    @Test
    void setAddress() {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setAddress("127.0.0.1:8888");
        jobConfig.setType(GatewayType.LOCAL.getValue());
        Map<String, String> config = new HashMap<>();
        config.put(RestOptions.PORT.key(), "9999");

        jobConfig.setConfigJson(config);
        jobConfig.setAddress("127.0.0.1:7777");
        assertEquals("127.0.0.1:9999", jobConfig.getAddress());

        jobConfig.setAddress("127.0.0.2");
        assertEquals("127.0.0.2:9999", jobConfig.getAddress());

        config.remove(RestOptions.PORT.key());
        jobConfig.setAddress("127.0.0.2:6666");
        assertEquals("127.0.0.2:6666", jobConfig.getAddress());

        jobConfig.setType(GatewayType.STANDALONE.getLongValue());
        jobConfig.setAddress("127.0.0.3:6666");
        assertEquals("127.0.0.3:6666", jobConfig.getAddress());
    }
}
