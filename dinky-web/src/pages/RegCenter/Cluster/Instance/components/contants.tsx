/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {Tag} from 'antd';
import {l} from '@/utils/intl';
import React from 'react';
import {ClusterType} from "@/pages/RegCenter/Cluster/constants";

/**
 * Cluster instance type
 */
export const CLUSTER_INSTANCE_TYPE = [
    {value: ClusterType.STANDALONE, label: ClusterType.STANDALONE},
    {value: ClusterType.YARN_SESSION, label: ClusterType.YARN_SESSION},
    {value: ClusterType.KUBERNETES_SESSION, label: ClusterType.KUBERNETES_SESSION},
]

/**
 * Cluster instance  is auto registers
 */
export const CLUSTER_INSTANCE_AUTO_REGISTERS_ENUM = {
  true: {text: <Tag color={'success'}>{l('global.yes')}</Tag>, status: 'Success'},
  false: {text: <Tag color={'error'}>{l('global.no')}</Tag>, status: 'Error'},
}

/**
 * Cluster instance status enum
 */
export const CLUSTER_INSTANCE_STATUS_ENUM = {
  1: {text: <Tag color={'success'}>{l('global.table.status.normal')}</Tag>, status: 'Success'},
  0: {text: <Tag color={'error'}>{l('global.table.status.abnormal')}</Tag>, status: 'Error'},
}
