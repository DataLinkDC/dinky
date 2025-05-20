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

package org.dinky.cluster;

import org.dinky.data.enums.GatewayType;

import com.fasterxml.jackson.databind.JsonNode;

public class FlinkClusterInstanceConfig {
    private GatewayType clusterType;
    private String hosts;
    private String host;
    // "{\"applicationId\":\"application_1745048813572_0007\",\"resourceManager\":\"127.0.0.1:8032,127.0.0.2:8032\"}",
    private JsonNode config;

    public FlinkClusterInstanceConfig() {}

    public FlinkClusterInstanceConfig(GatewayType clusterType, String hosts, String host, JsonNode config) {
        this.clusterType = clusterType;
        this.hosts = hosts;
        this.host = host;
        this.config = config;
    }

    public GatewayType getClusterType() {
        return clusterType;
    }

    public String getHosts() {
        return hosts;
    }

    public String getHost() {
        return host;
    }

    public JsonNode getConfig() {
        return config;
    }

    public static FlinkClusterInstanceConfig build(
            GatewayType clusterType, String hosts, String host, JsonNode config) {
        return new FlinkClusterInstanceConfig(clusterType, hosts, host, config);
    }
}
