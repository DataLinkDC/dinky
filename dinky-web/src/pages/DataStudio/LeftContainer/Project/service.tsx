import {handleOption, queryDataByParams} from "@/services/BusinessCrud";
import {postAll, putData, putDataJson} from "@/services/api";


export async function  getTaskData() {
  return (await postAll('/api/catalogue/getCatalogueTreeData')).datas;
}
export function getTaskDetails(id:number) {
  return queryDataByParams('/api/task',{id:id});
}
export function putTask(params:any) {
  return putDataJson('/api/task',params);
}
