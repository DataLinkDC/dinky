import {DashboardData} from "@/pages/Dashboard/data";
import {addOrUpdateData, getData, removeById, removeData} from "@/services/api";
import {API_CONSTANTS} from "@/services/endpoints";


export const addOrUpdate = (data: DashboardData) => {
  return addOrUpdateData("/api/Dashboard/saveOrUpdate", data)
}
export const getDataList = () => {
  return getData("/api/Dashboard/getDashboardList")
}
export const getDataDetailById = (id:number) => {
  return getData("/api/Dashboard/getDashboardById",{id})
}
export const deleteData = (id: number) => {
  return removeById("/api/Dashboard/delete", {id})
}


export async function getMetricsLayoutByCascader() {
  return getData(API_CONSTANTS.GET_METRICS_LAYOUT_CASCADER);
}
