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

package org.dinky.gateway.kubernetes.watcher;

import org.apache.hadoop.util.StringUtils;

import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentCondition;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeploymentStatusWatcher implements Watcher<Deployment> {

    @Override
    public void eventReceived(Action action, Deployment deployment) {
        String deploymentName = deployment.getMetadata().getName();
        log.info("deployment name: {}, deployment action: {}", deploymentName, action);
        if (ObjectUtil.isNotNull(deployment.getStatus())
                && CollectionUtil.isNotEmpty(deployment.getStatus().getConditions())) {
            List<DeploymentCondition> conditions = deployment.getStatus().getConditions();
            conditions.forEach(condition -> {
                if (StringUtils.equalsIgnoreCase(condition.getStatus(), "true")) {
                    log.info(
                            "deployment name: {}, deployment status: {}, message: {}",
                            deploymentName,
                            condition.getStatus(),
                            condition.getMessage());
                } else {
                    log.warn(
                            "deployment name: {}, deployment status: {}, message: {}",
                            deploymentName,
                            condition.getStatus(),
                            condition.getMessage());
                }
            });
        }
    }

    @Override
    public void onClose(WatcherException cause) {
        if (cause != null) {
            log.error("Watcher closed due to exception: {}", cause.getMessage());
        } else {
            log.info("Watcher closed gracefully.");
        }
    }
}
