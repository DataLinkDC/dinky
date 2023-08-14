import {queryDataByParams} from "@/services/BusinessCrud";

export function getConsoleData() {
  return  queryDataByParams('api/process/getConsoleByUserId');
}
