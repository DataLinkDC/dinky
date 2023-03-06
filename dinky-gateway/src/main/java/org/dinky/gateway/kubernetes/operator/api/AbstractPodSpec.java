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

package org.dinky.gateway.kubernetes.operator.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.fabric8.kubernetes.api.model.Pod;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbstractPodSpec {
    private Resource resource;
    private int replicas = 1;
    private Pod podTemplate;

    public AbstractPodSpec() {}

    @Data
    @AllArgsConstructor
    public static class Resource {
        private Double cpu;
        private String memory;

        public Resource() {}
    }
}
