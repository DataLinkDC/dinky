import { postAll } from '@/services/api';
import { handleGetOption, handleOption } from '@/services/BusinessCrud';
import { DIALECT } from '@/services/constants';

export async function explainSql(params: any) {
  return postAll('/api/task/explainSql', params);
}

export async function getJobPlan(title: string, params: any) {
  return handleOption('/api/task/getJobPlan', title, params);
}

export async function executeSql(title: string, id: number) {
  return handleGetOption('/api/task/submitTask', title, { id });
}

export function cancelTask(title: string, id: number) {
  return handleGetOption('api/task/cancel', title, { id });
}

export function onLineTask(id: number) {
  return handleGetOption('api/task/onLineTask', '', { taskId: id });
}

export function offLinelTask(id: number) {
  return handleGetOption('api/task/cancel', '', { taskId: id });
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
