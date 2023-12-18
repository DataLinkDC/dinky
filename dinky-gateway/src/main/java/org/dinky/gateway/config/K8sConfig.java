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

package org.dinky.gateway.config;

import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "K8sConfig", description = "Configuration for running Flink jobs on Kubernetes")
public class K8sConfig {

    @ApiModelProperty(
            value = "Kubernetes configuration",
            dataType = "Map<String, String>",
            example = "{\"key1\": \"value1\", \"key2\": \"value2\"}",
            notes = "Kubernetes configuration properties")
    private Map<String, String> configuration = new HashMap<>();

    @ApiModelProperty(
            value = "Docker configuration for Kubernetes pods",
            dataType = "Map<String, Object>",
            example = "{\"key1\": \"value1\", \"key2\": 123}",
            notes = "Docker configuration properties for pods")
    private Map<String, Object> dockerConfig = new HashMap<>();

    @ApiModelProperty(
            value = "Pod template for Flink jobs",
            dataType = "String",
            example = "flink-pod-template.yaml",
            notes = "YAML file containing the pod template for Flink jobs")
    private String podTemplate;

    @ApiModelProperty(
            value = "JobManager pod template for Flink jobs",
            dataType = "String",
            example = "jm-pod-template.yaml",
            notes = "YAML file containing the pod template for the JobManager in Flink jobs")
    private String jmPodTemplate;

    @ApiModelProperty(
            value = "TaskManager pod template for Flink jobs",
            dataType = "String",
            example = "tm-pod-template.yaml",
            notes = "YAML file containing the pod template for TaskManagers in Flink jobs")
    private String tmPodTemplate;

    @ApiModelProperty(value = "KubeConfig", dataType = "String", example = "kubeconfig.yaml", notes = "KubeConfig file")
    private String kubeConfig;
}
