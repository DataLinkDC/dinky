import {handleOption, queryDataByParams} from "@/services/BusinessCrud";
import {postAll} from "@/services/api";


export function getTaskData() {
  return  postAll('/api/catalogue/getCatalogueTreeData');
}
export function getTaskDetails(id:number) {
  return  queryDataByParams('/api/task',{id:id});
}
