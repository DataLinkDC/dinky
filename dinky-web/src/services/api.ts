/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { request } from '@umijs/max';

import { METHOD_CONSTANTS } from '@/services/constants';
import { PublicParams } from '@/services/data';

// ============================ CRUD REQUEST ============================

/**
 * query list
 * @param url
 * @param params
 */
export async function queryList(url: string, params?: PublicParams.TableParams) {
  return request(url, {
    method: METHOD_CONSTANTS.POST,
    data: {
      ...params
    }
  });
}

/**
 * add or update data
 * @param url
 * @param params
 */
export async function addOrUpdateData(url: string, params: any) {
  return request(url, {
    method: METHOD_CONSTANTS.PUT,
    data: {
      ...params
    }
  });
}

/**
 * delete data by id
 * @param url
 * @param params
 */
export async function removeById(url: string, params: any) {
  return request(url, {
    method: METHOD_CONSTANTS.DELETE,
    params: {
      ...params
    }
  });
}

export async function getData(url: string, params?: any) {
  return request(url, {
    method: METHOD_CONSTANTS.GET,
    params: {
      ...params
    }
  });
}

export async function removeData(url: string, params: [any]) {
  return request(url, {
    method: METHOD_CONSTANTS.DELETE,
    data: {
      ...params
    }
  });
}
export function getSseData(url: string) {
  return new EventSource(url);
}

export async function putData(url: string, params: any) {
  return request(url, {
    method: METHOD_CONSTANTS.PUT,
    params: {
      ...params
    }
  });
}
export async function putDataJson(url: string, params: any) {
  return request(url, {
    method: METHOD_CONSTANTS.PUT,
    data: {
      ...params
    }
  });
}
export async function putDataAsArray(url: string, data: any[]) {
  return request(url, {
    method: METHOD_CONSTANTS.PUT,
    data: data
  });
}

export async function postDataArray(url: string, params: number[]) {
  return request(url, {
    method: METHOD_CONSTANTS.POST,
    data: {
      ...params
    }
  });
}

export async function postAll(url: string, params?: any) {
  return request(url, {
    method: METHOD_CONSTANTS.POST,
    data: params
  });
}

export async function getInfoById(url: string, id: number) {
  return request(url, {
    method: METHOD_CONSTANTS.GET,
    params: {
      id: id
    }
  });
}

export async function updateDataByParams(url: string, params: any) {
  return request(url, {
    method: METHOD_CONSTANTS.PUT,
    params: {
      ...params
    }
  });
}

export async function getDataByRequestBody(url: string, body: any) {
  return request(url, {
    method: METHOD_CONSTANTS.POST,
    data: { ...body }
  });
}
