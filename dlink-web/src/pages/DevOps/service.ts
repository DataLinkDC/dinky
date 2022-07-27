import {getData, postAll} from "@/components/Common/crud";

export function getStatusCount() {
  return getData("api/jobInstance/getStatusCount");
}

export function getJobInfoDetail(id: number) {
  return getData("api/jobInstance/getJobInfoDetail", {id});
}

export function refreshJobInfoDetail(id: number) {
  return getData("api/jobInstance/refreshJobInfoDetail", {id});
}

export function getLineage(id: number) {
  return getData("api/jobInstance/getLineage", {id});
}

export function getJobManagerInfo(address: string) {
  return getData("api/jobInstance/getJobManagerInfo", {address});
}

export function getTaskManagerInfo(address: string) {
  return getData("api/jobInstance/getTaskManagerInfo", {address});
}

export function selectSavePointRestartTask(id: number, isOnLine: boolean, savePointPath: string) {
  return getData("api/task/selectSavePointRestartTask", {id, isOnLine, savePointPath});
}

/**
 * queryOneClickOperatingTaskStatus 一键操作状态查询
 * */
export function queryOneClickOperatingTaskStatus() {
  return getData("api/task/queryOneClickOperatingTaskStatus", {});
}

/**
 * onClickOperatingTask 一键操作保存
 * */
export function onClickOperatingTask(params: any) {
  return postAll("api/task/onClickOperatingTask", params);
}

/**
 * queryOnClickOperatingTask 查询对应操作的任务列表
 * */
export function queryOnClickOperatingTask(params: { operating: string, catalogueId: number | null }) {
  return getData("api/task/queryOnClickOperatingTask", params);
}

/**
 * queryAllCatalogue 查询对应操作的任务列表树形
 * */
export function queryAllCatalogue(params: { operating: string }) {
  return getData("api/task/queryAllCatalogue", params);
}


