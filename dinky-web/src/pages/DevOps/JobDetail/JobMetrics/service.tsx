import { JobMetricsItem } from '@/pages/DevOps/JobDetail/data';
import { getData, putDataAsArray } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';

export async function getMetricsLayout(params: {}) {
  return (await getData(API_CONSTANTS.METRICS_LAYOUT_GET_BY_NAME, params)).datas;
}

export async function getMetricsData(params: {}) {
  return (await getData(API_CONSTANTS.MONITOR_GET_FLINK_DATA, params)).datas;
}

export async function putMetricsLayout(layoutName: string, params: JobMetricsItem[]) {
  return (await putDataAsArray(API_CONSTANTS.SAVE_FLINK_METRICS + layoutName, params)).datas;
}
