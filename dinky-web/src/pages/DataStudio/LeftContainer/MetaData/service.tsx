import {handleGetOption, handleOption, queryDataByParams} from "@/services/BusinessCrud";
import {l} from "@/utils/intl";

/*--- 刷新 元数据表 ---*/
export function showMetaDataTable(id: number) {
  return handleGetOption('api/database/getSchemasAndTables',l("pages.metadata.DataSearch"), {id: id});
}

/*--- 清理 元数据表缓存 ---*/
export function clearMetaDataTable(id: number) {
  return queryDataByParams('api/database/unCacheSchemasAndTables', {id: id});
}
export function getDataBase() {
  return  queryDataByParams('api/database/listEnabledAll');
}
