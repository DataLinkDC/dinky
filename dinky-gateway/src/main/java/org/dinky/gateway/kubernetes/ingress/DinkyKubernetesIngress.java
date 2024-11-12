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

package org.dinky.gateway.kubernetes.ingress;

import static org.dinky.assertion.Asserts.checkNotNull;

import org.dinky.gateway.kubernetes.utils.K8sClientHelper;

import java.util.Map;

import com.google.common.collect.Maps;

import cn.hutool.core.text.StrFormatter;
import io.fabric8.kubernetes.api.model.OwnerReference;
import io.fabric8.kubernetes.api.model.OwnerReferenceBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.fabric8.kubernetes.api.model.networking.v1.IngressBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DinkyKubernetesIngress {

    private final K8sClientHelper k8sClientHelper;

    public DinkyKubernetesIngress(K8sClientHelper k8sClientHelper) {
        this.k8sClientHelper = k8sClientHelper;
    }

    public void configureIngress(String clusterId, String domain, String namespace) {
        log.info("Dinky ingress configure ingress for cluster {} in namespace {}", clusterId, namespace);
        OwnerReference ownerReference = getOwnerReference(namespace, clusterId);
        Ingress ingress = new IngressBuilder()
                .withNewMetadata()
                .withName(clusterId)
                .addToAnnotations(buildIngressAnnotations(clusterId, namespace))
                .addToLabels(buildIngressLabels(clusterId))
                .addToOwnerReferences(ownerReference) // Add OwnerReference
                .endMetadata()
                .withNewSpec()
                .addNewRule()
                .withHost(domain)
                .withNewHttp()
                .addNewPath()
                .withPath(StrFormatter.format("/{}/{}/", namespace, clusterId))
                .withPathType("ImplementationSpecific")
                .withNewBackend()
                .withNewService()
                .withName(StrFormatter.format("{}-rest", clusterId))
                .withNewPort()
                .withNumber(8081)
                .endPort()
                .endService()
                .endBackend()
                .endPath()
                .addNewPath()
                .withPath(StrFormatter.format("/{}/{}(/|$)(.*)", namespace, clusterId))
                .withPathType("ImplementationSpecific")
                .withNewBackend()
                .withNewService()
                .withName(StrFormatter.format("{}-rest", clusterId))
                .withNewPort()
                .withNumber(8081)
                .endPort()
                .endService()
                .endBackend()
                .endPath()
                .endHttp()
                .endRule()
                .endSpec()
                .build();
        try (KubernetesClient kubernetesClient = k8sClientHelper.getKubernetesClient()) {
            kubernetesClient.network().v1().ingresses().inNamespace(namespace).create(ingress);
        }
    }

    private Map<String, String> buildIngressAnnotations(String clusterId, String namespace) {
        Map<String, String> annotations = Maps.newConcurrentMap();
        annotations.put("nginx.ingress.kubernetes.io/rewrite-target", "/$2");
        annotations.put("nginx.ingress.kubernetes.io/proxy-body-size", "1024m");
        // Build the configuration snippet
        String configurationSnippet =
                "rewrite ^(/" + clusterId + ")$ $1/ permanent; " + "sub_filter '<base href=\"./\">' '<base href=\"/"
                        + namespace + "/" + clusterId + "/\">'; " + "sub_filter_once off;";
        annotations.put("nginx.ingress.kubernetes.io/configuration-snippet", configurationSnippet);
        annotations.put("kubernetes.io/ingress.class", "nginx");
        return annotations;
    }

    private Map<String, String> buildIngressLabels(String clusterId) {
        Map<String, String> labels = Maps.newConcurrentMap();
        labels.put("app", clusterId);
        labels.put("type", "flink-native-kubernetes");
        labels.put("component", "ingress");
        return labels;
    }

    private OwnerReference getOwnerReference(String namespace, String clusterId) {
        log.info("Dinky ingress get owner reference for cluster {} in namespace {}", clusterId, namespace);
        KubernetesClient client = k8sClientHelper.getKubernetesClient();
        Deployment deployment = client.apps()
                .deployments()
                .inNamespace(namespace)
                .withName(clusterId)
                .get();
        checkNotNull(
                deployment,
                StrFormatter.format("Deployment with name {} not found in namespace {}", clusterId, namespace));
        return new OwnerReferenceBuilder()
                .withUid(deployment.getMetadata().getUid())
                .withApiVersion("apps/v1")
                .withKind("Deployment")
                .withName(clusterId)
                .withController(true)
                .withBlockOwnerDeletion(true)
                .build();
    }
}
