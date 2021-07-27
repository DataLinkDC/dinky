import request from 'umi-request';
import {CAParam, StudioParam} from "@/components/Studio/StudioEdit/data";

export async function executeSql(params: StudioParam) {
  return request<API.Result>('/api/studio/executeSql', {
    method: 'POST',
    data: {
      ...params,
    },
  });
}

export async function executeDDL(params: StudioParam) {
  return request<API.Result>('/api/studio/executeDDL', {
    method: 'POST',
    data: {
      ...params,
    },
  });
}

export async function explainSql(params: StudioParam) {
  return request<API.Result>('/api/studio/explainSql', {
    method: 'POST',
    data: {
      ...params,
    },
  });
}

export async function getJobData(jobId:string) {
  return request<API.Result>('/api/studio/getJobData', {
    method: 'GET',
    params: {
      jobId,
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

export async function getCAByStatement(params: CAParam) {
  return request<API.Result>('/api/studio/getCAByStatement', {
    method: 'POST',
    data: {
      ...params,
    },
  });
}
