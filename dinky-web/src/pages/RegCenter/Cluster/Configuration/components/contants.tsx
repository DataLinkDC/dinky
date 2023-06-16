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

import {RUN_MODE} from "@/services/constants";
import {Tag} from 'antd';
import {l} from '@/utils/intl';
import React from 'react';

/**
 * Cluster config type
 */
export const CLUSTER_CONFIG_TYPE = [
    {value: RUN_MODE.YARN, label: 'Flink On Yarn'},
    {value: RUN_MODE.KUBERNETES_APPLICATION, label: 'Flink Kubernetes Native'},
    {value: RUN_MODE.KUBERNETES_APPLICATION_OPERATOR, label: 'Flink Kubernetes Operator'},
]

