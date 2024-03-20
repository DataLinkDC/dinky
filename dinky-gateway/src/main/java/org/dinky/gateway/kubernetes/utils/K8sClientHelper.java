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

import org.apache.flink.configuration.ConfigOption;
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
import cn.hutool.core.lang.Assert;
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

    private void initKubeClient() {
        client = FlinkKubeClientFactory.getInstance().fromConfiguration(configuration, "client");
        if (TextUtils.isEmpty(k8sConfig.getKubeConfig())) {
            kubernetesClient = new DefaultKubernetesClient();
        } else {
            kubernetesClient = new DefaultKubernetesClient(Config.fromKubeconfig(k8sConfig.getKubeConfig()));
        }
    }

    public void createDinkyResource(Deployment deployment) {
        List<HasMetadata> resources = getSqlFileDecorate().buildResources();

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
        kubernetesClient.resourceList(resources).createOrReplace();
    }

    public Map<String, String> initPodTemplate(File sqlFile) {
        Pod pod;
        Map<String, String> cfg = new HashMap<>();

        if (!TextUtil.isEmpty(k8sConfig.getPodTemplate())) {
            InputStream is =
                    new ByteArrayInputStream(k8sConfig.getTmPodTemplate().getBytes(StandardCharsets.UTF_8));
            pod = kubernetesClient.pods().load(is).get();
        } else {
            pod = new Pod();
        }
        sqlFileDecorate = new DinkySqlConfigMapDecorate(configuration, pod, sqlFile);
        Pod sqlDecoratedPod = sqlFileDecorate.decoratePodMount();

        // 使用SnakeYAML生成YAML字符串
        Representer representer = new IgnoreNullRepresenter();
        // 设置Map类型的标签, 只有map类型不会在dump时打印类名
        representer.addClassTag(Pod.class, Tag.MAP);
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(representer, options);
        String dump = yaml.dump(sqlDecoratedPod);

        cfg.put(
                KubernetesConfigOptions.KUBERNETES_POD_TEMPLATE.key(),
                preparPodTemplate(dump, KubernetesConfigOptions.KUBERNETES_POD_TEMPLATE));
        cfg.put(
                KubernetesConfigOptions.JOB_MANAGER_POD_TEMPLATE.key(),
                preparPodTemplate(k8sConfig.getJmPodTemplate(), KubernetesConfigOptions.JOB_MANAGER_POD_TEMPLATE));
        cfg.put(
                KubernetesConfigOptions.TASK_MANAGER_POD_TEMPLATE.key(),
                preparPodTemplate(k8sConfig.getTmPodTemplate(), KubernetesConfigOptions.TASK_MANAGER_POD_TEMPLATE));
        cfg.put(
                KubernetesConfigOptions.KUBE_CONFIG_FILE.key(),
                preparPodTemplate(k8sConfig.getKubeConfig(), KubernetesConfigOptions.KUBE_CONFIG_FILE));
        return cfg;
    }

    private String preparPodTemplate(String podTemplate, ConfigOption<String> option) {
        if (!TextUtil.isEmpty(podTemplate)) {
            String filePath = String.format("%s/%s.yaml", tmpConfDir, option.key());
            if (FileUtil.exist(filePath)) {
                Assert.isTrue(FileUtil.del(filePath));
            }
            FileUtil.writeUtf8String(podTemplate, filePath);
            return filePath;
        }
        return null;
    }

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
