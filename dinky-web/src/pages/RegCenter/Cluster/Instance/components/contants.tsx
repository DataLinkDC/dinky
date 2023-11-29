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

import { ClusterType } from '@/pages/RegCenter/Cluster/constants';
import { l } from '@/utils/intl';
import { Tag } from 'antd';
import { DefaultOptionType } from 'rc-select/es/Select';

/**
 * Cluster instance type
 */
export const CLUSTER_INSTANCE_TYPE: DefaultOptionType[] = [
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
    value: ClusterType.YARN_APPLICATION,
    label: 'Yarn Application',
    key: ClusterType.YARN_APPLICATION
  },
  {
    value: ClusterType.LOACL,
    label: 'Local',
    key: ClusterType.LOACL
  }
];

/**
 * Cluster instance status enum
 */
export const CLUSTER_INSTANCE_STATUS_ENUM = {
  1: {
    text: <Tag color={'success'}>{l('global.table.status.normal')}</Tag>,
    status: 'Success'
  },
  0: {
    text: <Tag color={'error'}>{l('global.table.status.abnormal')}</Tag>,
    status: 'Error'
  }
};
