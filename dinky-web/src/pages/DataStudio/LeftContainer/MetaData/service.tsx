import {queryDataByParams} from "@/services/BusinessCrud";

/*--- 刷新 元数据表 ---*/
export function showMetaDataTable(id: number) {
  return queryDataByParams('api/database/getSchemasAndTables', {id: id});
}

/*--- 清理 元数据表缓存 ---*/
export function clearMetaDataTable(id: number) {
  return queryDataByParams('api/database/unCacheSchemasAndTables', {id: id});
}
export function getDataBase() {
  return  queryDataByParams('api/database/listEnabledAll');
}
