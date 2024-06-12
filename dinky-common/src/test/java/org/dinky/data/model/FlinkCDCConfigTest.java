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

package org.dinky.data.model;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class FlinkCDCConfigTest {

    private FlinkCDCConfig flinkCDCConfig;

    @Before
    public void setUp() {
        Map<String, String> sinkConfig = new HashMap<>();
        sinkConfig.put("connector", "blackhole");
        sinkConfig.put("rows-per-second", "1000");
        sinkConfig.put("sink.table.replace.pattern", "t_(.*?)_");
        sinkConfig.put("sink.table.replace.with", "ods_jxc_$1_");
        flinkCDCConfig = new FlinkCDCConfig(
                "mysql",
                "localhost",
                3306,
                "root",
                "123456",
                10,
                1,
                "testdb",
                "test_schema",
                "test_table",
                "initial",
                Collections.emptyMap(),
                Collections.emptyMap(),
                sinkConfig,
                Collections.emptyMap(),
                Collections.emptyList(),
                Collections.emptyMap());
    }

    @Test
    public void testGetSinkConfigurationStringWithValidConfig() {
        String expectedConfig = "'connector' = 'blackhole',\n" + "'rows-per-second' = '1000'";
        assertEquals(expectedConfig, flinkCDCConfig.getSinkConfigurationString());
    }

    @Test
    public void testGetSinkConfigurationStringWithEmptyConfig() {
        flinkCDCConfig.setSink(Collections.emptyMap());
        assertEquals("", flinkCDCConfig.getSinkConfigurationString());
    }

    @Test
    public void testGetSinkConfigurationStringWithNullConfig() {
        flinkCDCConfig.setSink(null);
        assertEquals("", flinkCDCConfig.getSinkConfigurationString());
    }

    @Test
    public void testGetSinkConfigurationStringIgnoresSkipKeys() {
        Map<String, String> sinkConfig = new HashMap<>();
        sinkConfig.put("connector", "blackhole"); // Let's assume this should not be skipped
        sinkConfig.put("skip-key", "true"); // Let's assume this should be skipped
        flinkCDCConfig.setSink(sinkConfig);

        // We expect only the "connector" key to be included
        String expectedConfig = "'connector' = 'blackhole',\n" + "'skip-key' = 'true'";
        assertEquals(expectedConfig, flinkCDCConfig.getSinkConfigurationString());
    }
}
