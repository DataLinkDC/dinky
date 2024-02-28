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

import { getDataByParams, queryDataByParams } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';

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

export function querySuggessionData(params: any) {
  return getDataByParams(API_CONSTANTS.SUGGESTION_QUERY_ALL_SUGGESTIONS, params);
}
