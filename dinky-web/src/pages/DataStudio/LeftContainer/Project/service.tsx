import { postAll, putDataJson } from '@/services/api';
import { queryDataByParams } from '@/services/BusinessCrud';
import {TaskDataType} from "@/pages/DataStudio/model";

export async function getTaskData() {
  return (await postAll('/api/catalogue/getCatalogueTreeData')).datas;
}
export function getTaskDetails(id: number) : Promise<TaskDataType | undefined> {
  return queryDataByParams('/api/task', { id: id });
}
export function putTask(params: any) {
  return putDataJson('/api/task', params);
}
