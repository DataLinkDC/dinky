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

import { JobMetricsItem } from '@/pages/DevOps/JobDetail/data';
import { getData, putDataAsArray } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';

export async function getMetricsLayout(params: {}) {
  return (await getData(API_CONSTANTS.METRICS_LAYOUT_GET_BY_NAME, params)).data;
}

export async function getMetricsData(params: {}) {
  return (await getData(API_CONSTANTS.MONITOR_GET_FLINK_DATA, params)).data;
}

export async function putMetricsLayout(layoutName: string, params: JobMetricsItem[]) {
  return (await putDataAsArray(API_CONSTANTS.SAVE_FLINK_METRICS + layoutName, params)).data;
}
