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

import org.apache.flink.annotation.Experimental;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.fabric8.kubernetes.api.model.networking.v1.IngressTLS;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ingress spec.
 * {@see https://nightlies.apache.org/flink/flink-kubernetes-operator-docs-release-1.8/docs/operations/ingress/}
 *
 */
@Experimental
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class IngressSpec {

    /** Ingress template for the JobManager service. */
    private String template;

    /** Ingress className for the Flink deployment. */
    private String className;

    /** Ingress annotations. */
    private Map<String, String> annotations;

    /** Ingress labels. */
    private Map<String, String> labels;

    /** Ingress tls. */
    private List<IngressTLS> tls;
}
