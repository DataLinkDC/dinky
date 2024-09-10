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

package org.dinky.executor;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.kubernetes.kubeclient.FlinkKubeClient;
import org.apache.flink.kubernetes.kubeclient.FlinkKubeClientFactory;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.util.URLUtil;

public class ClusterDescriptorAdapterImpl extends ClusterDescriptorAdapter {

    public ClusterDescriptorAdapterImpl() {}

    public ClusterDescriptorAdapterImpl(YarnClusterDescriptor yarnClusterDescriptor) {
        super(yarnClusterDescriptor);
    }

    @Override
    public void addShipFiles(List<File> shipFiles) {
        yarnClusterDescriptor.addShipFiles(shipFiles.stream()
                .map(file -> new Path(URLUtil.getURL(file).toString()))
                .collect(Collectors.toList()));
    }

    @Override
    public KubernetesClusterDescriptor createKubernetesClusterDescriptor(
            Configuration configuration, FlinkKubeClient flinkKubeClient) {
        return new KubernetesClusterDescriptor(configuration, FlinkKubeClientFactory.getInstance());
    }
}
