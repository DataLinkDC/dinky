import request from 'umi-request';
import type { TableListParams } from './data.d';
import {ClusterTableListItem} from "./data.d";

export async function queryCluster(params?: TableListParams) {
    return request('/api/task', {
        method: 'POST',
        data: {
            ...params,
        },
    });
}

export async function removeCluster(params: number[]) {
    return request('/api/task', {
        method: 'DELETE',
        data: {
            ...params,
        },
    });
}

export async function submitCluster(params: number[]) {
  return request('/api/cluster/submit', {
    method: 'POST',
    data: {
      ...params,
    },
  });
}

export async function addOrUpdateCluster(params: ClusterTableListItem) {
    return request('/api/task', {
        method: 'PUT',
        data: {
            ...params,
        },
    });
}

