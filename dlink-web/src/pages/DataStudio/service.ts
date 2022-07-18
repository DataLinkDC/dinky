import request from 'umi-request';
import {CAParam, StudioMetaStoreParam, StudioParam} from "@/components/Studio/StudioEdit/data";

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

export async function getStreamGraph(params: StudioParam) {
  return request<API.Result>('/api/studio/getStreamGraph', {
    method: 'POST',
    data: {
      ...params,
    },
  });
}

export async function getJobPlan(params: StudioParam) {
  return request<API.Result>('/api/studio/getJobPlan', {
    method: 'POST',
    data: {
      ...params,
    },
  });
}

export async function getJobData(jobId: string) {
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

export async function getLineage(params: CAParam) {
  return request<API.Result>('/api/studio/getLineage', {
    method: 'POST',
    data: {
      ...params,
    },
  });
}

export async function getMSCatalogs(params: StudioMetaStoreParam) {
  return request<API.Result>('/api/studio/getMSCatalogs', {
    method: 'POST',
    data: {
      ...params,
    },
  });
}

export async function getMSSchemaInfo(params: StudioMetaStoreParam) {
  return request<API.Result>('/api/studio/getMSSchemaInfo', {
    method: 'POST',
    data: {
      ...params,
    },
  });
}
