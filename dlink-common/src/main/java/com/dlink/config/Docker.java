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

package com.dlink.config;

import com.dlink.assertion.Asserts;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author ZackYoung
 * @since 0.7.1
 */
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
@Setter
public class Docker {
    private String instance;
    private String registryUrl;
    private String registryUsername;
    private String registryPassword;
    private String imageNamespace;
    private String imageStorehouse;
    private String imageDinkyVersion;

    public static Docker build(Map configMap) {
        if (Asserts.isNullMap(configMap)) {
            return null;
        }
        String instance1 = configMap.getOrDefault("docker.instance", "").toString();
        if ("".equals(instance1)) {
            return null;
        }
        return Docker.builder()
            .instance(instance1)
            .registryUrl(configMap.getOrDefault("docker.registry.url", "").toString())
            .registryUsername(configMap.getOrDefault("docker.registry.username", "").toString())
            .registryPassword(configMap.getOrDefault("docker.registry.password", "").toString())
            .imageNamespace(configMap.getOrDefault("docker.image.namespace", "").toString())
            .imageStorehouse(configMap.getOrDefault("docker.image.storehouse", "").toString())
            .imageDinkyVersion(configMap.getOrDefault("docker.image.dinkyVersion", "").toString())
            .build();
    }
}
