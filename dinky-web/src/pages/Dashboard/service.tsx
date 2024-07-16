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

import { DashboardData } from '@/pages/Dashboard/data';
import { addOrUpdateData, getData, removeById, removeData } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import {
  getDataByParamsReturnResult,
  handleAddOrUpdate,
  handleDeleteOperation,
  queryDataByParams
} from '@/services/BusinessCrud';
import { saveOrUpdateHandle } from '@/pages/RegCenter/DataSource/service';

export const addOrUpdate = (data: DashboardData) => {
  return handleAddOrUpdate(API_CONSTANTS.SAVE_DASHBOARD, data);
};
export const getDataList = () => {
  return getDataByParamsReturnResult(API_CONSTANTS.GET_DASHBOARD_LIST);
};
export const getDataDetailById = (id: number) => {
  return getDataByParamsReturnResult(API_CONSTANTS.GET_DASHBOARD_BY_ID, { id });
};
export const deleteData = (id: number) => {
  return handleDeleteOperation(API_CONSTANTS.DELETE_DASHBOARD, { id }, 'Dashboard');
};

export async function getMetricsLayoutByCascader() {
  return getDataByParamsReturnResult(API_CONSTANTS.GET_METRICS_LAYOUT_CASCADER);
}
