import request from 'umi-request';
import type { TableListParams } from './data.d';
import {TaskTableListItem} from "./data.d";

export async function queryTask(params?: TableListParams) {
    return request('/api/task', {
        method: 'POST',
        data: {
            ...params,
        },
    });
}

export async function removeTask(params: number[]) {
    return request('/api/task', {
        method: 'DELETE',
        data: {
            ...params,
        },
    });
}

export async function submitTask(params: number[]) {
  return request('/api/task/submit', {
    method: 'POST',
    data: {
      ...params,
    },
  });
}

export async function addOrUpdateTask(params: TaskTableListItem) {
    return request('/api/task', {
        method: 'PUT',
        data: {
            ...params,
        },
    });
}

