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

import { DefaultOptionType } from 'rc-select/es/Select';

export enum ClusterType {
  STANDALONE = 'standalone',
  YARN = 'yarn-application',
  YARN_SESSION = 'yarn-session',
  KUBERNETES_SESSION = 'kubernetes-session',
  KUBERNETES_APPLICATION = 'kubernetes-application',
  KUBERNETES_OPERATOR = 'kubernetes-application-operator',
  YARN_APPLICATION = 'yarn-application',
  LOCAL = 'local'
}

export const CLUSTER_TYPE_OPTIONS: DefaultOptionType[] = [
  {
    value: ClusterType.STANDALONE,
    label: 'Standalone',
    key: ClusterType.STANDALONE
  },
  {
    value: ClusterType.YARN_SESSION,
    label: 'Yarn Session',
    key: ClusterType.YARN_SESSION
  },
  {
    value: ClusterType.KUBERNETES_SESSION,
    label: 'Kubernetes Session',
    key: ClusterType.KUBERNETES_SESSION
  },
  {
    value: ClusterType.KUBERNETES_APPLICATION,
    label: 'Kubernetes Application',
    key: ClusterType.KUBERNETES_APPLICATION
  },
  {
    value: ClusterType.KUBERNETES_OPERATOR,
    label: 'Kubernetes Operator',
    key: ClusterType.KUBERNETES_OPERATOR
  },
  {
    value: ClusterType.YARN_APPLICATION,
    label: 'Yarn Application',
    key: ClusterType.YARN_APPLICATION
  },
  {
    value: ClusterType.LOCAL,
    label: 'Local',
    key: ClusterType.LOCAL
  }
];

/**
 * Cluster instance type
 */
export const CLUSTER_INSTANCE_TYPE = (hiddenOptions: string[] = []): DefaultOptionType[] => {
  return CLUSTER_TYPE_OPTIONS.filter((item) => !hiddenOptions.includes(item.value as string));
};

/**
 * Cluster config type
 * @param renderOptions
 * @constructor
 */
export const CLUSTER_CONFIG_TYPE = (renderOptions: string[] = []): DefaultOptionType[] => {
  return CLUSTER_TYPE_OPTIONS.filter((item) => renderOptions.includes(item.value as string));
};
