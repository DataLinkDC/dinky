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

import org.dinky.gateway.config.K8sConfig;
import org.dinky.gateway.kubernetes.decorate.DinkySqlConfigMapDecorate;
import org.dinky.utils.TextUtil;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.kubernetes.kubeclient.FlinkKubeClient;
import org.apache.flink.kubernetes.kubeclient.FlinkKubeClientFactory;
import org.apache.http.util.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import cn.hutool.core.io.FileUtil;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.OwnerReference;
import io.fabric8.kubernetes.api.model.OwnerReferenceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
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
    private K8sConfig k8sConfig;
    private String tmpConfDir;
    private DinkySqlConfigMapDecorate sqlFileDecorate;

    public K8sClientHelper(Configuration configuration, K8sConfig k8sConfig) {
        this.configuration = configuration;
        this.k8sConfig = k8sConfig;
        tmpConfDir = String.format("%s/tmp/kubernets/%s", System.getProperty("user.dir"), UUID.randomUUID());
        initKubeClient();
    }

    /**
     * initKubeClient
     */
    private void initKubeClient() {
        // k8s flink native client
        client = FlinkKubeClientFactory.getInstance().fromConfiguration(configuration, "client");
        // k8s fabric client
        if (TextUtils.isEmpty(k8sConfig.getKubeConfig())) {
            kubernetesClient = new DefaultKubernetesClient();
        } else {
            kubernetesClient = new DefaultKubernetesClient(Config.fromKubeconfig(k8sConfig.getKubeConfig()));
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
                .inNamespace(configuration.getString(KubernetesConfigOptions.NAMESPACE))
                .withName(configuration.getString(KubernetesConfigOptions.CLUSTER_ID))
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
        kubernetesClient.resourceList(resources).createOrReplace();
        return deployment;
    }

    /**
     * initPodTemplate
     * Preprocess the pod template
     * @param sqlFile
     * @return
     */
    public String decoratePodTemplate(File sqlFile) {
        Pod pod;
        // k8s pod template
        Map<String, String> cfg = new HashMap<>();
        // if the user has configured the pod template, combine user's configuration
        if (!TextUtil.isEmpty(k8sConfig.getPodTemplate())) {
            InputStream is = new ByteArrayInputStream(k8sConfig.getPodTemplate().getBytes(StandardCharsets.UTF_8));
            pod = kubernetesClient.pods().load(is).get();
        } else {
            // if the user has not configured the pod template, use the default configuration
            pod = new Pod();
        }

        // decorate the pod template
        sqlFileDecorate = new DinkySqlConfigMapDecorate(configuration, pod, sqlFile);
        Pod sqlDecoratedPod = sqlFileDecorate.decoratePodMount();

        // use snakyaml to serialize the pod
        Representer representer = new IgnoreNullRepresenter();
        // set the label of the Map type, only the map type will not print the class name when dumping
        representer.addClassTag(Pod.class, Tag.MAP);
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(representer, options);
        return yaml.dump(sqlDecoratedPod);
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
        try {
            return FileUtil.del(tmpConfDir);
        } catch (Exception e) {
            log.warn("failed delete kubernetes temp dir: " + tmpConfDir);
        }
        return false;
    }
}
