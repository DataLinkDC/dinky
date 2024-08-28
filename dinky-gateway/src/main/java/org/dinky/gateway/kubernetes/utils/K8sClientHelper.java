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

package org.dinky.gateway.kubernetes.utils;

import org.dinky.gateway.kubernetes.decorate.DinkySqlConfigMapDecorate;
import org.dinky.utils.TextUtil;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.kubernetes.kubeclient.FlinkKubeClient;
import org.apache.flink.kubernetes.kubeclient.FlinkKubeClientFactory;
import org.apache.flink.kubernetes.kubeclient.decorators.ExternalServiceDecorator;
import org.apache.http.util.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.OwnerReference;
import io.fabric8.kubernetes.api.model.OwnerReferenceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.utils.Serialization;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * K8sClientHelper
 * manage k8s client
 */
@Data
@Slf4j
public class K8sClientHelper {

    private FlinkKubeClient client;
    private KubernetesClient kubernetesClient;
    protected Configuration configuration;
    private DinkySqlConfigMapDecorate sqlFileDecorate;

    public K8sClientHelper(Configuration configuration, String kubeConfig) {
        this.configuration = configuration;
        initKubeClient(kubeConfig);
    }

    public Optional<Deployment> getJobService(String clusterId) {
        String serviceName = ExternalServiceDecorator.getExternalServiceName(clusterId);
        Deployment deployment = kubernetesClient
                .apps()
                .deployments()
                .inNamespace(configuration.get(KubernetesConfigOptions.NAMESPACE))
                .withName(configuration.get(KubernetesConfigOptions.CLUSTER_ID))
                .get();
        if (deployment == null) {
            log.debug("Service {} does not exist", serviceName);
            return Optional.empty();
        }
        return Optional.of(deployment);
    }

    public boolean getClusterIsPresent(String clusterId) {
        return getJobService(clusterId).isPresent();
    }

    /**
     * initKubeClient
     */
    private void initKubeClient(String kubeConfig) {
        // k8s flink native client
        client = FlinkKubeClientFactory.getInstance().fromConfiguration(configuration, "client");
        // k8s fabric client
        if (TextUtils.isEmpty(kubeConfig)) {
            kubernetesClient = new DefaultKubernetesClient();
        } else {
            kubernetesClient = new DefaultKubernetesClient(Config.fromKubeconfig(kubeConfig));
        }
    }

    /**
     * createDinkyResource
     * Create the resources required by Dinky,
     * and append the owner attribute of the deployment
     * to automatically clear the related resources
     * when the deployment is deleted
     */
    public Deployment createDinkyResource() {
        log.info("createDinkyResource");
        Deployment deployment = kubernetesClient
                .apps()
                .deployments()
                .inNamespace(configuration.get(KubernetesConfigOptions.NAMESPACE))
                .withName(configuration.get(KubernetesConfigOptions.CLUSTER_ID))
                .get();
        List<HasMetadata> resources = getSqlFileDecorate().buildResources();
        // set owner reference
        OwnerReference deploymentOwnerReference = new OwnerReferenceBuilder()
                .withName(deployment.getMetadata().getName())
                .withApiVersion(deployment.getApiVersion())
                .withUid(deployment.getMetadata().getUid())
                .withKind(deployment.getKind())
                .withController(true)
                .withBlockOwnerDeletion(true)
                .build();

        resources.forEach(resource ->
                resource.getMetadata().setOwnerReferences(Collections.singletonList(deploymentOwnerReference)));
        // create resources
        resources.forEach(resource -> log.info(Serialization.asYaml(resource)));
        kubernetesClient.resourceList(resources).createOrReplace();
        return deployment;
    }

    /**
     * initPodTemplate
     * Preprocess the pod template
     * @param sqlStatement
     * @return
     */
    public Pod decoratePodTemplate(String sqlStatement, String podTemplate) {
        Pod pod;
        // if the user has configured the pod template, combine user's configuration
        if (!TextUtil.isEmpty(podTemplate)) {
            InputStream is = new ByteArrayInputStream(podTemplate.getBytes(StandardCharsets.UTF_8));
            pod = kubernetesClient.pods().load(is).get();
        } else {
            // if the user has not configured the pod template, use the default configuration
            pod = new Pod();
        }
        if (TextUtil.isEmpty(sqlStatement)) {
            log.warn("Sql statement is Empty !!!!, will not decorate podTemplate");
            return pod;
        } else {
            // decorate the pod template
            sqlFileDecorate = new DinkySqlConfigMapDecorate(configuration, pod, sqlStatement);
            return sqlFileDecorate.decoratePodMount();
        }
    }

    /**
     * dumpPod2Str
     *
     * */
    public String dumpPod2Str(Pod pod) {
        // use snakyaml to serialize the pod
        Representer representer = new IgnoreNullRepresenter();
        // set the label of the Map type, only the map type will not print the class name when dumping
        representer.addClassTag(Pod.class, Tag.MAP);
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(representer, options);
        return yaml.dump(pod);
    }
    /**
     * close
     * delete the temporary directory and close the client
     * @return
     */
    public boolean close() {
        if (client != null) {
            client.close();
        }
        if (kubernetesClient != null) {
            kubernetesClient.close();
        }
        return true;
    }
}
