/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { request } from '@umijs/max';

// ================================ About Account ================================
/**  GET /api/current */
export async function currentUser(options?: { [key: string]: any }) {
  return request<API.Result>('/api/current', {
    method: 'GET',
    ...(options || {}),
  });
}

/**  DELETE /api/outLogin */
export async function outLogin(options?: { [key: string]: any }) {
  return request<Record<string, any>>('/api/outLogin', {
    method: 'DELETE',
    ...(options || {}),
  });
}

/**  POST /api/login */
export async function login(body: API.LoginParams, options?: { [key: string]: any }) {
  return request<API.Result>('/api/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/**  GET /api/current */
export function chooseTenantSubmit(params: { tenantId: number }) {
  return request<API.Result>('/api/chooseTenant', {
    method: 'GET',
    params: {
      ...(params || {}),
    },
  });
}

// ============================ CRUD REQUEST ============================
export async function queryData(url: string, params?: PublicParams.TableParams) {
  return request(url, {
    method: 'POST',
    data: {
      ...params,
    },
  });
}

export async function getData(url: string, params?: any) {
  return request(url, {
    method: 'GET',
    params: {
      ...params,
    },
  });
}

export async function removeData(url: string, params: any[]) {
  return request(url, {
    method: 'DELETE',
    data: {
      ...params,
    },
  });
}

export async function addOrUpdateData(url: string, params: any) {
  return request(url, {
    method: 'PUT',
    data: {
      ...params,
    },
  });
}

export async function postDataArray(url: string, params: number[]) {
  return request(url, {
    method: 'POST',
    data: {
      ...params,
    },
  });
}

export async function postAll(url: string, params?: any) {
  return request(url, {
    method: 'POST',
    data: {
      ...params,
    },
  });
}

export async function getInfoById(url: string, id: number) {
  return request(url, {
    method: 'GET',
    params: {
      id: id,
    },
  });
}

// =========================== Business ===================================
