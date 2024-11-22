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

import {
  getDataByParams,
  handleGetOption,
  handleOption,
  queryDataByParams
} from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { TaskState } from '@/pages/DataStudio/type';
import { postAll } from '@/services/api';

export async function explainSql(title: string, params: any) {
  return handleOption(API_CONSTANTS.EXPLAIN_SQL, title, params);
}

export async function debugTask(title: string, params: any) {
  return handleOption(API_CONSTANTS.DEBUG_TASK, title, params);
}

export function getUserData(params: any) {
  return queryDataByParams(API_CONSTANTS.GET_USER_LIST_BY_TENANTID, params);
}

export async function getJobPlan(title: string, params: any) {
  return handleOption(API_CONSTANTS.GET_JOB_PLAN, title, params);
}

export function cancelTask(
  title: string,
  id: number,
  withSavePoint: boolean = true,
  forceCancel: boolean = true
) {
  return handleGetOption(API_CONSTANTS.CANCEL_JOB, title, { id, withSavePoint, forceCancel });
}

export async function executeSql(title: string, id: number) {
  return handleGetOption(API_CONSTANTS.SUBMIT_TASK, title, { id });
}
export function restartTask(id: number, savePointPath: string, title: string) {
  return handleGetOption(API_CONSTANTS.RESTART_TASK, title, { id, savePointPath });
}

export function savePointTask(title: string, taskId: number, savePointType: string) {
  return handleGetOption(API_CONSTANTS.SAVEPOINT, title, { taskId, savePointType });
}

export function changeTaskLife(title = '', id: number, life: number) {
  return handleGetOption(API_CONSTANTS.CHANGE_TASK_LIFE, title, { taskId: id, lifeCycle: life });
}

export function getTaskDetails(id: number): Promise<TaskState | undefined> {
  return queryDataByParams(API_CONSTANTS.TASK, { id: id });
}

export function getSessionData() {
  return queryDataByParams(API_CONSTANTS.CLUSTER_INSTANCE_SESSION);
}

export function getEnvData() {
  return queryDataByParams(API_CONSTANTS.LIST_FLINK_SQL_ENV);
}

export function getClusterConfigurationData() {
  return queryDataByParams(API_CONSTANTS.CLUSTER_CONFIGURATION_LIST_ENABLE_ALL);
}

export function getFlinkConfigs() {
  return queryDataByParams(API_CONSTANTS.FLINK_CONF_CONFIG_OPTIONS);
}

export function getFlinkUdfOptions() {
  return queryDataByParams(API_CONSTANTS.ALL_UDF_LIST);
}

export function querySuggestionData(params: any) {
  return getDataByParams(API_CONSTANTS.SUGGESTION_QUERY_ALL_SUGGESTIONS, params);
}

export async function getTaskSortTypeData() {
  return (await postAll(API_CONSTANTS.CATALOGUE_GET_CATALOGUE_SORT_TYPE_DATA)).data;
}
