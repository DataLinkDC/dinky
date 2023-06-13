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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/** */
class FlinkCDCConfigTest {

    @Test
    void getSinkConfigurationString() {
        Map<String, String> sinkConfig = new HashMap<>();
        sinkConfig.put(FlinkCDCConfig.SINK_DB, "sink_db");
        sinkConfig.put("propertyOne", "propertyOneValue");
        sinkConfig.put("propertyTwo", "propertyTwoValue");

        FlinkCDCConfig flinkCDCConfig =
                new FlinkCDCConfig(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        sinkConfig,
                        null,
                        null);
        String sinkConfigureStr = flinkCDCConfig.getSinkConfigurationString();
        assertThat(
                sinkConfigureStr,
                equalTo(
                        "'propertyOne' = 'propertyOneValue',\n"
                                + "'propertyTwo' = 'propertyTwoValue'"));
    }
}
