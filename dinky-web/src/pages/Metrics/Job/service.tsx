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

import { MetricsLayout } from '@/pages/Metrics/Job/data';
import { putDataAsArray, queryList } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';

/**
 * 获取 运行的 flink任务 列表
 * @returns {Promise<any>}
 */
export async function getFlinkRunTask() {
  return queryList(API_CONSTANTS.JOB_INSTANCE, {
    filter: {},
    currentPage: 1,
    status: 'RUNNING',
    sorter: { id: 'descend' }
  });
}

export async function saveFlinkMetrics(jobList: MetricsLayout[]) {
  return await putDataAsArray(API_CONSTANTS.SAVE_FLINK_METRICS, jobList);
}
