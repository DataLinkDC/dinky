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

declare namespace UserBaseInfo {
  export type User = {
    id: number;
    username: string;
    nickname?: string;
    password?: string;
    avatar?: string;
    worknum?: string;
    mobile?: string;
    createTime?: Date;
    updateTime?: Date;
    enabled: boolean;
    isDelete: boolean;
    isAdmin?: boolean;
  };

  export type ChangePasswordParams = {
    username: string;
    password?: string;
    newPassword?: string;
    newPasswordCheck?: string;
  };

  export type Tenant = {
    id: number;
    tenantCode: string;
    isDelete: boolean;
    note?: string;
    createTime?: Date;
    updateTime?: Date;
  };

  export type Role = {
    id: number;
    tenantId: number;
    tenant: Tenant;
    roleCode?: string;
    roleName?: string;
    namespaceIds?: string;
    namespaces?: NameSpace[];
    isDelete: boolean;
    note?: string;
    createTime?: Date;
    updateTime?: Date;
  };

  export type NameSpace = {
    id: number;
    tenantId: number;
    tenant: Tenant;
    namespaceCode?: string;
    enabled: boolean;
    note?: string;
    createTime?: Date;
    updateTime?: Date;
  };
}
