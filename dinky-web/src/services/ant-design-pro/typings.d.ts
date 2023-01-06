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

declare namespace API {
  type Result = {
    code: number;
    datas: any;
    msg: string;
  };

  type CurrentUser = {
    id?: number;
    username?: string;
    password?: string;
    nickname?: string;
    worknum?: string;
    avatar?: string;
    mobile?: string;
    enabled?: boolean;
    isDelete?: boolean;
    isAdmin?: boolean;
    createTime?: Date;
    updateTime?: Date;
    roleList?: Role[];
    tenantList?: Tenant[];
    currentTenant?: Tenant;
  };
  type Role = {
    id?: number;
    tenantId?: number;
    roleCode?: string;
    roleName?: string;
    note?: string;
    isDelete?: boolean;
    createTime?: Date;
    updateTime?: Date;
    tenant?: Tenant;
  };

  type Tenant = {
    id?: number;
    tenantCode?: string;
    note?: string;
    isDelete?: boolean;
    createTime?: Date;
    updateTime?: Date;
  };


  /*type LoginResult = {
    code?: number;
    datas?: {
      access_token?: string;
      expires_in?: number;
      refresh_token?: string;
      scope?: string;
      token_type?: string;
    };
    msg?: string;
  };*/

  type LoginResult = {
    access_token?: string;
    expires_in?: number;
    refresh_token?: string;
    scope?: string;
    token_type?: string;
  };

  type PageParams = {
    current?: number;
    pageSize?: number;
  };

  type RuleListItem = {
    key?: number;
    disabled?: boolean;
    href?: string;
    avatar?: string;
    name?: string;
    owner?: string;
    desc?: string;
    callNo?: number;
    status?: number;
    updatedAt?: string;
    createdAt?: string;
    progress?: number;
  };

  type RuleList = {
    data?: RuleListItem[];
    /** 列表的内容总数 */
    total?: number;
    success?: boolean;
  };

  type FakeCaptcha = {
    code?: number;
    status?: string;
  };

  type LoginParams = {
    username?: string;
    password?: string;
    autoLogin?: boolean;
    tenantId?: number;
    type?: string;
    grant_type?: string;
  };

  type ErrorResponse = {
    /** 业务约定的错误码 */
    errorCode: string;
    /** 业务上的错误信息 */
    errorMessage?: string;
    /** 业务上的请求是否成功 */
    success?: boolean;
  };

  type NoticeIconList = {
    data?: NoticeIconItem[];
    /** 列表的内容总数 */
    total?: number;
    success?: boolean;
  };

  type NoticeIconItemType = 'notification' | 'message' | 'event';

  type NoticeIconItem = {
    id?: string;
    extra?: string;
    key?: string;
    read?: boolean;
    avatar?: string;
    title?: string;
    status?: string;
    datetime?: string;
    description?: string;
    type?: NoticeIconItemType;
  };
}
