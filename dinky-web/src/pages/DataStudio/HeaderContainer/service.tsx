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

import { handleGetOption, handleOption } from '@/services/BusinessCrud';
import { DIALECT } from '@/services/constants';

export async function explainSql(title: string, params: any) {
  return handleOption('/api/task/explainSql', title, params);
}

export async function getJobPlan(title: string, params: any) {
  return handleOption('/api/task/getJobPlan', title, params);
}

export async function debugTask(title: string, params: any) {
  return handleOption('/api/task/debugTask', title, params);
}

export async function executeSql(title: string, id: number) {
  return handleGetOption('/api/task/submitTask', title, { id });
}

export function cancelTask(title: string, id: number) {
  return handleGetOption('api/task/cancel', title, { id });
}

export function changeTaskLife(title = '', id: number, life: number) {
  return handleGetOption('api/task/changeTaskLife', title, { taskId: id, lifeCycle: life });
}

export const isSql = (dialect: string) => {
  if (!dialect) {
    return false;
  }
  switch (dialect.toLowerCase()) {
    case DIALECT.SQL:
    case DIALECT.MYSQL:
    case DIALECT.ORACLE:
    case DIALECT.SQLSERVER:
    case DIALECT.POSTGRESQL:
    case DIALECT.CLICKHOUSE:
    case DIALECT.PHOENIX:
    case DIALECT.DORIS:
    case DIALECT.HIVE:
    case DIALECT.STARROCKS:
    case DIALECT.PRESTO:
      return true;
    default:
      return false;
  }
};
