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

import { TaskDataType } from '@/pages/DataStudio/model';
import { postAll, putDataJson } from '@/services/api';
import { queryDataByParams } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';

export async function getTaskData() {
  return (await postAll(API_CONSTANTS.CATALOGUE_GET_CATALOGUE_TREE_DATA)).data;
}
export function getTaskDetails(id: number): Promise<TaskDataType | undefined> {
  return queryDataByParams(API_CONSTANTS.TASK, { id: id });
}
export function putTask(params: any) {
  return putDataJson(API_CONSTANTS.TASK, params);
}
