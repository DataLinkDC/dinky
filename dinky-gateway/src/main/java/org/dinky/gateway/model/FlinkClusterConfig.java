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

import org.dinky.gateway.config.AppConfig;
import org.dinky.gateway.config.ClusterConfig;
import org.dinky.gateway.config.FlinkConfig;
import org.dinky.gateway.config.K8sConfig;
import org.dinky.gateway.enums.GatewayType;

import lombok.Getter;
import lombok.Setter;

/** @since */
@Getter
@Setter
public class FlinkClusterConfig {
    private GatewayType type;
    private ClusterConfig clusterConfig;
    private FlinkConfig flinkConfig;
    private AppConfig appConfig;
    private K8sConfig kubernetesConfig;
}
