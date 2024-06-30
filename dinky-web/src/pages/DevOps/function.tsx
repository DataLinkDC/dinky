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

import { JOB_STATUS, JOB_SUBMIT_STATUS } from '@/pages/DevOps/constants';
import { Jobs } from '@/types/DevOps/data';
import { parseMilliSecondStr } from '@/utils/function';

/**
 * Checks if a job status indicates that the job is done.
 *
 * @param {string} type - The job status.
 * @returns {boolean} - True if the job status indicates that the job is done, false otherwise.
 */
export function isStatusDone(type: string) {
  if (!type) {
    return true;
  }
  switch (type) {
    case JOB_STATUS.FAILED:
    case JOB_STATUS.CANCELED:
    case JOB_STATUS.FINISHED:
    case JOB_STATUS.UNKNOWN:
    case JOB_SUBMIT_STATUS.SUCCESS:
    case JOB_SUBMIT_STATUS.FAILED:
    case JOB_SUBMIT_STATUS.CANCEL:
      return true;
    default:
      return false;
  }
}

export function isNotFinallyStatus(type: string) {
  if (!type) {
    return false;
  }
  switch (type) {
    case JOB_STATUS.RECONNECTING:
    case JOB_STATUS.UNKNOWN:
      return true;
    default:
      return false;
  }
}

export function getJobDuration(jobInstance: Jobs.JobInstance) {
  if (isStatusDone(jobInstance.status)) {
    return parseMilliSecondStr(jobInstance.duration);
  } else {
    const currentTimestamp = Date.now();
    const duration = currentTimestamp - new Date(jobInstance.createTime).getTime();
    return parseMilliSecondStr(duration);
  }
}
