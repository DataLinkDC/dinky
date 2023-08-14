import {StudioParam} from "@/pages/DataStudio/data.d";
import {postAll} from "@/services/api";
import {handleGetOption, handleOption, handlePutData} from "@/services/BusinessCrud";
import {DIALECT, RUN_MODE} from "@/services/constants";
import {l} from "@/utils/intl";

export async function explainSql(params: any) {
  return postAll('/api/studio/explainSql', params);
}
export async function getJobPlan(title:string,params: any) {
  return handleOption('/api/studio/getJobPlan',title, params);
}
export async function executeSql(title:string,params: any) {
  return handleOption('/api/studio/executeSql',title, params);
}
export function offLineTask(title:string,id: number, type: string) {
  return handleGetOption('api/task/offLineTask',title,{id, type});
}

export const isSql = (dialect: string) => {
  if (!dialect){
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
export const isOnline = (type: string) => {
  switch (type) {
    case RUN_MODE.LOCAL:
    case RUN_MODE.STANDALONE:
    case RUN_MODE.YARN_SESSION:
    case RUN_MODE.KUBERNETES_SESSION:
      return true;
    default:
      return false;
  }
}
