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


// @ts-ignore
/* eslint-disable */


import {request2} from "@/components/Common/crud";
import {request} from "umi";

/** 获取当前的用户 GET /api/currentUser */
export async function currentUser(options?: { [key: string]: any }) {
  return request2<API.Result>('/api/current', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 退出登录接口 POST /api-uaa/oauth/remove/token?token= */
export async function outLogin(options?: { [key: string]: any }) {
  return request2<Record<string, any>>('/api/outLogin', {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 登录接口 POST /api-uaa/oauth/token */
export async function login(body: API.LoginParams, options?: { [key: string]: any }) {
  const tenantId = localStorage.getItem('dlink-tenantId') || '';
  const authHeader = { tenantId };
  return request<API.Result>('/api/login', {
    method: 'POST',
    data: body,
    requestInterceptors:[
      {
        tenantId
      }
    ],
    ...authHeader,
    ...(options  || {}),
  });
}

/** 此处后端没有提供注释 GET /api/notices */
export async function getNotices(options?: { [key: string]: any }) {
  return request2<API.NoticeIconList>('/api/notices', {
    method: 'GET',
    ...(options || {}),
  });
}
