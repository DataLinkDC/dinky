import request from 'umi-request';
import {StudioParam} from "@/components/Studio/StudioEdit/data";

export async function executeSql(params: StudioParam) {
  return request<API.Result>('/api/studio/executeSql', {
    method: 'POST',
    data: {
      ...params,
    },
  });
}

export async function getCatalogueTreeData(params?: StudioParam) {
  return request<API.Result>('/api/catalogue/getCatalogueTreeData', {
    method: 'POST',
    data: {
      ...params,
    },
  });
}
