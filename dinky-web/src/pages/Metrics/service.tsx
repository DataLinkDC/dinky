import { getData } from '@/services/api';
import {API_CONSTANTS} from "@/services/endpoints";

export async function getMetricsLayout() {
  return getData(API_CONSTANTS.GET_METRICS_LAYOUT);
}
