import {getData} from "@/services/api";
import {API_CONSTANTS} from "@/services/endpoints";

export async function  getMetricsLayout(params:{}) {
  return (await getData(API_CONSTANTS.GET_METRICS_LAYOUT_BY_NAME,params)).datas;
}
