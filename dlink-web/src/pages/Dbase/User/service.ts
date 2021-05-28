import request from 'umi-request';
import type { TableListParams } from './data.d';
import {UserTableListItem} from "@/pages/Dbase/User/data";

export async function queryUser(params?: TableListParams) {
  return request('/api-user/users', {
    method: 'POST',
    data: {
      ...params,
    },
  });
}

export async function removeUser(params: number[]) {
  return request('/api-user/users', {
    method: 'DELETE',
    data: {
      ...params,
    },
  });
}

export async function addOrUpdateUser(params: UserTableListItem) {
  return request('/api-user/users', {
    method: 'PUT',
    data: {
      ...params,
    },
  });
}

