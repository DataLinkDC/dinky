import { MetricsLayout } from '@/pages/Metrics/Job/data';
import { putDataAsArray, queryList } from '@/services/api';
import {API_CONSTANTS} from "@/services/endpoints";

/**
 * 获取 运行的 flink任务 列表
 * @returns {Promise<any>}
 */
export async function getFlinkRunTask() {
  return queryList(API_CONSTANTS.GET_JOB_LIST, {
    filter: {},
    currentPage: 1,
    status: 'RUNNING',
    sorter: { id: 'descend' },
  });
}

export async function saveFlinkMetrics(jobList: MetricsLayout[]) {
  return await putDataAsArray(API_CONSTANTS.SAVE_FLINK_METRICS, jobList);
}
