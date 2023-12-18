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

import { JobMetricsItem, MetricsTimeFilter } from '@/pages/DevOps/JobDetail/data';
import { getData, putDataAsArray } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';

export const refeshJobInstance = (id: string, isForce?: boolean) => {
  return getData(API_CONSTANTS.REFRESH_JOB_DETAIL, { id, isForce });
};

export async function getMetricsLayout(layoutName: string) {
  return getData(API_CONSTANTS.METRICS_LAYOUT_GET_BY_NAME, { layoutName: layoutName });
}

export async function getMetricsData(time: MetricsTimeFilter, flinkJobIds: string) {
  return getData(API_CONSTANTS.MONITOR_GET_FLINK_DATA, {
    startTime: time.startTime,
    endTime: time.endTime,
    flinkJobIds: flinkJobIds
  });
}

export async function putMetricsLayout(layoutName: string, params: JobMetricsItem[]) {
  return putDataAsArray(API_CONSTANTS.SAVE_FLINK_METRICS + layoutName, params);
}

export async function getFLinkVertices(address: string, jobId: string, verticeId: string) {
  return getData(API_CONSTANTS.GET_JOB_METRICS_ITEMS, { address, jobId, verticeId });
}
