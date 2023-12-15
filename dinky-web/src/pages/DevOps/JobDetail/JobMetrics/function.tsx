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

/**
 * Checks if a job status indicates that the job is done.
 *
 * @returns {boolean} - True if the job status indicates that the job is done, false otherwise.
 * @param list
 */
export function buildMetricsTarget(list?: JobMetricsItem[]) {
  if (!list) return {};
  const result: Record<string, JobMetricsItem[]> = {};
  list.forEach((metrics) => {
    if (!(metrics.vertices in result)) {
      result[metrics.vertices] = [];
    }
    result[metrics.vertices].push(metrics);
  });
  return result;
}
