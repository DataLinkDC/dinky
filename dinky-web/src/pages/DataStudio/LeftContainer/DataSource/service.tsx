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

import { handleGetOption, queryDataByParams } from '@/services/BusinessCrud';
import { l } from '@/utils/intl';

/*--- 刷新 元数据表 ---*/
export async function showDataSourceTable(id: string) {
  try {
    const result = await handleGetOption(
      'api/database/getSchemasAndTables',
      l('pages.metadata.DataSearch'),
      { id: id }
    );
    return result?.data;
  } catch (e) {
    console.error(e);
    return null;
  }
}

/*--- 清理 元数据表缓存 ---*/
export function clearDataSourceTable(id: string) {
  return queryDataByParams('api/database/unCacheSchemasAndTables', { id: id });
}
export function getDataSourceList() {
  return queryDataByParams('api/database/listEnabledAll');
}
