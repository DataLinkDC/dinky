import {handleGetOption, handleOption, queryDataByParams} from "@/services/BusinessCrud";
import {l} from "@/utils/intl";

/*--- 刷新 元数据表 ---*/
export async function  showMetaDataTable(id: number) {
  return (await handleGetOption('api/database/getSchemasAndTables',l("pages.metadata.DataSearch"), {id: id})).datas;
}

/*--- 清理 元数据表缓存 ---*/
export function clearMetaDataTable(id: number) {
  return queryDataByParams('api/database/unCacheSchemasAndTables', {id: id});
}
export function getDataBase() {
  return  queryDataByParams('api/database/listEnabledAll');
}
