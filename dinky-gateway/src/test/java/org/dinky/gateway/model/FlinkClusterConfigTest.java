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

package org.dinky.gateway.model;

import java.io.IOException;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

public class FlinkClusterConfigTest {

    @Ignore
    @Test
    public void testParseCustomConfig() throws IOException {

        String clusterConfigJson = "{\n" + "    \"clusterConfig\":{\n"
                + "        \"hadoopConfigPath\":\"/opt/dinky-resources/hadoopConfDir\",\n"
                + "        \"flinkLibPath\":\"hdfs:///flink/flink1.14-lib\",\n"
                + "        \"flinkConfigPath\":\"/opt/flink/conf\",\n"
                + "        \"hadoopConfigList\":[\n"
                + "            {\n"
                + "                \"name\":\"yarn.timeline-service.enabled\",\n"
                + "                \"value\":\"false\"\n"
                + "            }\n"
                + "        ]\n"
                + "    },\n"
                + "    \"flinkConfig\":{\n"
                + "        \"configuration\":{\n"
                + "            \"jobmanager.memory.process.size\":\"2G\",\n"
                + "            \"taskmanager.memory.process.size\":\"2G\",\n"
                + "            \"taskmanager.memory.framework.heap.size\":\"1G\",\n"
                + "            \"taskmanager.numberOfTaskSlots\":\"2\",\n"
                + "            \"state.savepoints.dir\":\"hdfs:///flink/savepoint\",\n"
                + "            \"state.checkpoints.dir\":\"hdfs:///flink/checkpoint\"\n"
                + "        },\n"
                + "        \"flinkConfigList\":[\n"
                + "            {\n"
                + "                \"name\":\"yarn.application.queue\",\n"
                + "                \"value\":\"prod\"\n"
                + "            }\n"
                + "        ]\n"
                + "    },\n"
                + "    \"appConfig\":{\n"
                + "        \"userJarPath\":\"hdfs:///flink/dinky/jar/dinky-app-1.14-1.0.0-rc1-jar-with-dependencies.jar\"\n"
                + "    }\n"
                + "}";
        ObjectMapper objectMapper = new ObjectMapper();
        FlinkClusterConfig flinkClusterConfig = objectMapper.readValue(clusterConfigJson, FlinkClusterConfig.class);
        List<CustomConfig> flinkConfigList = flinkClusterConfig.getFlinkConfig().getFlinkConfigList();
        List<CustomConfig> hadoopConfigList =
                flinkClusterConfig.getClusterConfig().getHadoopConfigList();
        Assertions.assertThat(flinkConfigList.get(0).getName()).isEqualTo("yarn.application.queue");
        Assertions.assertThat(hadoopConfigList.get(0).getName()).isEqualTo("yarn.timeline-service.enabled");
    }
}
