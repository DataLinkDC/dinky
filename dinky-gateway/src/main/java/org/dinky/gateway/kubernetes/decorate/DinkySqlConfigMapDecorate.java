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

package org.dinky.gateway.kubernetes.decorate;

import org.dinky.constant.CustomerConfigureOptions;
import org.dinky.gateway.kubernetes.utils.DinkyKubernetsConstants;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.kubernetes.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KeyToPath;
import io.fabric8.kubernetes.api.model.KeyToPathBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DinkySqlConfigMapDecorate {
    private Pod podWithoutMainContainer;
    private Container mainContainer;
    private final Configuration configuration;
    private final String sqlStatement;

    public DinkySqlConfigMapDecorate(Configuration configuration, Pod pod, String sqlStatement) {
        if (!configuration.contains(KubernetesConfigOptions.CLUSTER_ID)) {
            throw new IllegalArgumentException(KubernetesConfigOptions.CLUSTER_ID.key() + " must not be blank.");
        }
        this.configuration = configuration;
        this.sqlStatement = sqlStatement;
        decorateFlinkPod(pod);
    }

    public Pod decoratePodMount() {
        final Pod mountedPod = decoratePodVolume();

        final Container mountedMainContainer = new ContainerBuilder(mainContainer)
                .addNewVolumeMount()
                .withName(DinkyKubernetsConstants.DINKY_CONF_VOLUME)
                .withMountPath(configuration.get(CustomerConfigureOptions.DINKY_CONF_DIR))
                .endVolumeMount()
                .build();

        return new PodBuilder(mountedPod)
                .editOrNewSpec()
                .addToContainers(mountedMainContainer)
                .endSpec()
                .build();
    }

    private void decorateFlinkPod(Pod pod) {
        final List<Container> otherContainers = new ArrayList<>();
        podWithoutMainContainer = new PodBuilder(pod).build();
        if (null != podWithoutMainContainer.getSpec()) {
            for (Container container : podWithoutMainContainer.getSpec().getContainers()) {
                if (Constants.MAIN_CONTAINER_NAME.equals(container.getName())) {
                    mainContainer = container;
                } else {
                    otherContainers.add(container);
                }
            }
            podWithoutMainContainer.getSpec().setContainers(otherContainers);
        } else {
            podWithoutMainContainer.setSpec(new PodSpecBuilder().build());
        }

        if (mainContainer == null) {
            log.info(
                    "Could not find main container {} in pod template, using empty one to initialize.",
                    Constants.MAIN_CONTAINER_NAME);
            mainContainer = new ContainerBuilder()
                    .withName(Constants.MAIN_CONTAINER_NAME)
                    .build();
        }
    }

    private Pod decoratePodVolume() {

        final List<KeyToPath> keyToPaths = Collections.singletonList(new KeyToPathBuilder()
                .withKey(configuration.get(CustomerConfigureOptions.EXEC_SQL_FILE))
                .withPath(configuration.get(CustomerConfigureOptions.EXEC_SQL_FILE))
                .build());

        final Volume sqlConfigVolume = new VolumeBuilder()
                .withName(DinkyKubernetsConstants.DINKY_CONF_VOLUME)
                .withNewConfigMap()
                .withName(dinkyConfMapName())
                .withItems(keyToPaths)
                .endConfigMap()
                .build();

        return new PodBuilder(podWithoutMainContainer)
                .editSpec()
                .addNewVolumeLike(sqlConfigVolume)
                .endVolume()
                .endSpec()
                .build();
    }

    public List<HasMetadata> buildResources() {
        final String clusterId = configuration.get(KubernetesConfigOptions.CLUSTER_ID);

        final Map<String, String> data = new HashMap<>();
        data.put(configuration.get(CustomerConfigureOptions.EXEC_SQL_FILE), sqlStatement);

        final Map<String, String> commonLabels = new HashMap<>();
        commonLabels.put(Constants.LABEL_TYPE_KEY, Constants.LABEL_TYPE_NATIVE_TYPE);
        commonLabels.put(Constants.LABEL_APP_KEY, clusterId);

        final ConfigMap flinkConfConfigMap = new ConfigMapBuilder()
                .withApiVersion(Constants.API_VERSION)
                .withNewMetadata()
                .withName(dinkyConfMapName())
                .withLabels(Collections.unmodifiableMap(commonLabels))
                .withNamespace(configuration.get(KubernetesConfigOptions.NAMESPACE))
                .endMetadata()
                .addToData(data)
                .build();

        return Collections.singletonList(flinkConfConfigMap);
    }

    private String dinkyConfMapName() {
        return DinkyKubernetsConstants.DINKY_CONF_VOLUME_PERFIX + configuration.get(KubernetesConfigOptions.CLUSTER_ID);
    }
}
