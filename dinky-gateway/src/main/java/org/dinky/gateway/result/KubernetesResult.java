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

package org.dinky.gateway.result;

import org.dinky.data.enums.GatewayType;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

/**
 * KubernetesResult
 *
 * @since 2021/12/26 15:06
 */
@Getter
@ApiModel(value = "KubernetesResult", description = "Result of Kubernetes operation")
public class KubernetesResult extends AbstractGatewayResult {

    @ApiModelProperty(
            value = "Cluster ID",
            dataType = "String",
            example = "cluster123",
            notes = "Unique identifier for the Kubernetes cluster")
    private String clusterId;

    @ApiModelProperty(
            value = "Web URL",
            dataType = "String",
            example = "https://k8s-dashboard.example.com",
            notes = "URL for accessing the Kubernetes web dashboard")
    private String webURL;

    @ApiModelProperty(
            value = "Job IDs",
            dataType = "List<String>",
            example = "[\"job1\", \"job2\"]",
            notes = "List of job identifiers associated with the Kubernetes cluster")
    private List<String> jids;

    public KubernetesResult(GatewayType type, LocalDateTime startTime) {
        super(type, startTime);
    }

    @Override
    public String getId() {
        return clusterId;
    }

    @Override
    public KubernetesResult setId(String id) {
        this.clusterId = id;
        return this;
    }

    public KubernetesResult setWebURL(String webURL) {
        this.webURL = webURL;
        return this;
    }

    @Override
    public List<String> getJids() {
        return jids;
    }

    public KubernetesResult setJids(List<String> jids) {
        this.jids = jids;
        return this;
    }

    public static KubernetesResult build(GatewayType type) {
        return new KubernetesResult(type, LocalDateTime.now());
    }
}
