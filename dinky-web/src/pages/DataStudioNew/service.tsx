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

import { handleOption } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { queryDataByParams } from '@/services/BusinessCrud';

export async function explainSql(title: string, params: any) {
  return handleOption(API_CONSTANTS.EXPLAIN_SQL, title, params);
}
export async function debugTask(title: string, params: any) {
  return handleOption(API_CONSTANTS.DEBUG_TASK, title, params);
}

export function getUserData(params: any) {
  return queryDataByParams(API_CONSTANTS.GET_USER_LIST_BY_TENANTID, params);
}
