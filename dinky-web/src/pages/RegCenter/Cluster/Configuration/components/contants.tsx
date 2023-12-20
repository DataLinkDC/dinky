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

import { FormConfig } from '@/pages/RegCenter/Cluster/Configuration/components/data';
import { ClusterType } from '@/pages/RegCenter/Cluster/constants';
import { l } from '@/utils/intl';
import { DefaultOptionType } from 'rc-select/es/Select';

/**
 * Cluster config type
 */
export const CLUSTER_CONFIG_TYPE: DefaultOptionType[] = [
  {
    value: ClusterType.YARN,
    label: 'Flink On Yarn',
    key: ClusterType.YARN
  },
  {
    value: ClusterType.KUBERNETES_NATIVE,
    label: 'Kubernetes Native',
    key: ClusterType.KUBERNETES_NATIVE
  },
  {
    value: ClusterType.KUBERNETES_OPERATOR,
    label: 'Kubernetes Operator',
    key: ClusterType.KUBERNETES_OPERATOR
  }
];

export const FLINK_CONFIG_LIST: FormConfig[] = [
  {
    name: 'jobmanager.memory.process.size',
    label: l('rc.cc.jmMem'),
    placeholder: l('rc.cc.jmMem'),
    tooltip: l('rc.cc.jmMemHelp')
  },
  {
    name: 'taskmanager.memory.process.size',
    label: l('rc.cc.tmMem'),
    placeholder: l('rc.cc.tmMem'),
    tooltip: l('rc.cc.tmMemHelp')
  },
  {
    name: 'taskmanager.memory.framework.heap.size',
    label: l('rc.cc.tmHeap'),
    placeholder: l('rc.cc.tmHeap'),
    tooltip: l('rc.cc.tmHeapHelp')
  },
  {
    name: 'taskmanager.numberOfTaskSlots',
    label: l('rc.cc.tsNum'),
    placeholder: l('rc.cc.tsNum'),
    tooltip: l('rc.cc.tsNumHelp')
  },
  {
    name: 'state.savepoints.dir',
    label: l('rc.cc.spDir'),
    placeholder: l('rc.cc.spDir'),
    tooltip: l('rc.cc.spDirHelp')
  },
  {
    name: 'state.checkpoints.dir',
    label: l('rc.cc.ckpDir'),
    placeholder: l('rc.cc.ckpDir'),
    tooltip: l('rc.cc.ckpDirHelp')
  }
];

export const DOCKER_CONFIG_LIST: FormConfig[] = [
  {
    name: 'dinky.remote.addr',
    label: l('rc.cc.docker.dinky.addr'),
    placeholder: l('rc.cc.docker.dinky.addrHelp'),
    tooltip: l('rc.cc.docker.dinky.addrHelp'),
    defaultValue: '127.0.0.1:8888'
  },
  {
    name: 'docker.instance',
    label: l('rc.cc.docker.instance'),
    placeholder: l('rc.cc.docker.instanceHelp'),
    tooltip: l('rc.cc.docker.instanceHelp')
  },
  {
    name: 'docker.registry.url',
    label: l('rc.cc.docker.url'),
    placeholder: l('rc.cc.docker.urlHelp'),
    tooltip: l('rc.cc.docker.urlHelp')
  },
  {
    name: 'docker.registry.username',
    label: l('rc.cc.docker.username'),
    placeholder: l('rc.cc.docker.usernameHelp'),
    tooltip: l('rc.cc.docker.usernameHelp')
  },
  {
    name: 'docker.registry.password',
    label: l('rc.cc.docker.password'),
    placeholder: l('rc.cc.docker.passwordHelp'),
    tooltip: l('rc.cc.docker.passwordHelp')
  },
  {
    name: 'docker.image.tag',
    label: l('rc.cc.docker.tag'),
    placeholder: l('rc.cc.docker.tagHelp'),
    tooltip: l('rc.cc.docker.tagHelp')
  },
  {
    name: 'docker.image.dockerfile',
    label: l('rc.cc.docker.file'),
    placeholder: l('rc.cc.docker.fileHelp'),
    tooltip: l('rc.cc.docker.fileHelp')
  }
];

export const KUBERNETES_CONFIG_LIST: FormConfig[] = [
  {
    name: 'kubernetes.namespace',
    label: l('rc.cc.k8s.namespace'),
    placeholder: l('rc.cc.k8s.namespaceHelp'),
    tooltip: l('rc.cc.k8s.namespaceHelp'),
    rules: [{ required: true }]
  },
  {
    name: 'kubernetes.service.account',
    label: l('rc.cc.k8s.account'),
    placeholder: l('rc.cc.k8s.accountHelp'),
    tooltip: l('rc.cc.k8s.accountHelp'),
    rules: [{ required: true }]
  },
  {
    name: 'kubernetes.container.image',
    label: l('rc.cc.k8s.image'),
    placeholder: l('rc.cc.k8s.imageHelp'),
    tooltip: l('rc.cc.k8s.imageHelp'),
    rules: [{ required: true }]
  },
  {
    name: 'kubernetes.jobmanager.cpu',
    label: l('rc.cc.k8s.jmCpu'),
    placeholder: l('rc.cc.k8s.jmCpuHelp'),
    tooltip: l('rc.cc.k8s.jmCpuHelp')
  },
  {
    name: 'kubernetes.taskmanager.cpu',
    label: l('rc.cc.k8s.tmCpu'),
    placeholder: l('rc.cc.k8s.tmCpuHelp'),
    tooltip: l('rc.cc.k8s.tmCpuHelp')
  }
];
