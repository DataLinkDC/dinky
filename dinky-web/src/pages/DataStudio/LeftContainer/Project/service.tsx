import {handleOption, queryDataByParams} from "@/services/BusinessCrud";
import {postAll} from "@/services/api";


export async function  getTaskData() {
  return  (await postAll('/api/catalogue/getCatalogueTreeData')).datas;
}
export function getTaskDetails(id:number) {
  return  queryDataByParams('/api/task',{id:id});
}
