import request from 'umi-request';
import {StudioParam} from "@/components/FlinkSqlEditor/data";

export async function executeSql(params: StudioParam) {
  return request('/api-dlink/studio/executeSql', {
    method: 'POST',
    data: {
      ...params,
    },
  });
}

