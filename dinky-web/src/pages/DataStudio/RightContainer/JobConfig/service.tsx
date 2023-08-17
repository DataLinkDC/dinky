import {queryDataByParams} from "@/services/BusinessCrud";

export function getSessionData() {
  return queryDataByParams('api/cluster/listSessionEnable');
}
export function getEnvData() {
  return queryDataByParams('/api/task/listFlinkSQLEnv');
}export function getClusterConfigurationData() {
  return queryDataByParams('/api/clusterConfiguration/listEnabledAll');
}
