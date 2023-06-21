import {getData, queryList} from "@/services/api";
import {API_CONSTANTS} from "@/services/constants";

export async function getMetricsLayout() {
  return getData(API_CONSTANTS.GET_METRICS_LAYOUT)
}
