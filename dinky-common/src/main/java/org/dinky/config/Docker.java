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

package org.dinky.config;

import org.dinky.assertion.Asserts;

import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** @since 0.7.1 */
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
    private String imageVersion;
    private String dockerfile;
    private String tag;

    public static Docker build(Map<String, Object> configMap) {
        if (Asserts.isNullMap(configMap)) {
            return null;
        }
        String instance1 = configMap.getOrDefault("docker.instance", "").toString();
        // eg tag: docker.io/dinky/flink:1.16.0
        String tag = configMap.getOrDefault("docker.image.tag", "").toString();
        String dockerfile =
                configMap.getOrDefault("docker.image.dockerfile", "").toString();
        if (StrUtil.hasBlank(instance1, tag)) {
            return null;
        }
        List<String> tagSplit = StrUtil.splitTrim(tag, "/");
        List<String> versionSplit = StrUtil.splitTrim(tag, ":");
        if (tagSplit.size() < 1 || versionSplit.size() < 1) {
            throw new RuntimeException("image tag eg:(docker.io/dinky/flink:1.16.0)");
        }
        if (tagSplit.size() == 2) {
            tagSplit.add(0, "docker.io");
        }
        tagSplit.set(2, tagSplit.get(2).replace(":" + versionSplit.get(1), ""));

        return Docker.builder()
                .instance(instance1)
                .registryUrl(configMap.getOrDefault("docker.registry.url", "").toString())
                .registryUsername(
                        configMap.getOrDefault("docker.registry.username", "").toString())
                .registryPassword(
                        configMap.getOrDefault("docker.registry.password", "").toString())
                .imageNamespace(tagSplit.get(1))
                .imageStorehouse(tagSplit.get(2))
                .imageVersion(versionSplit.get(1))
                .tag(tag)
                .dockerfile(dockerfile)
                .build();
    }
}
