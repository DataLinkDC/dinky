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

declare namespace API {
  type Result = {
    code: number;
    datas: any;
    msg: string;
    time: Date;
  };

  type CurrentUser = {
    user: UserBaseInfo.User;
    roleList?: UserBaseInfo.Role[];
    tenantList?: UserBaseInfo.Tenant[];
    currentTenant?: UserBaseInfo.Tenant;
  };

  type PageParams = {
    current?: number;
    pageSize?: number;
  };

  type LoginParams = {
    username?: string;
    password?: string;
    autoLogin?: boolean;
  };

  type ErrorResponse = {
    /** 业务约定的错误码 */
    errorCode: string;
    /** 业务上的错误信息 */
    errorMessage?: string;
    /** 业务上的请求是否成功 */
    success?: boolean;
  };

  type TenantRequest = {
    username?: string;
  };
}

declare namespace PublicParams {
  export type TableParams = {
    status?: string;
    name?: string;
    desc?: string;
    key?: number;
    pageSize?: number;
    currentPage?: number;
    filter?: Record<string, any[]>;
    sorter?: Record<string, any>;
  };
}
