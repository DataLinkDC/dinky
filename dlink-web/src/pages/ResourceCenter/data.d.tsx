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


export type UserTableListItem = {
  id?: number;
  enabled?: boolean;
  isDelete?: string;
  createTime?: Date;
  updateTime?: Date;
  username?: string;
  nickname?: string;
  password?: string;
  avatar?: string;
  worknum?: string;
  mobile?: string;
};

export type PasswordItem = {
  username: string;
  password?: string;
  newPassword?: string;
  newPasswordCheck?: string;
};


export type TenantTableListItem = {
  id?: number;
  tenantCode?: string;
  isDelete?: boolean;
  note?: string;
  createTime?: Date;
  updateTime?: Date;
};


export type RoleTableListItem = {
  id?: number;
  tenantId?: number;
  tenant: TenantTableListItem;
  roleCode?: string;
  roleName?: string;
  namespaceIds?: string;
  namespaces?: NameSpaceTableListItem[];
  isDelete?: boolean;
  note?: string;
  createTime?: Date;
  updateTime?: Date;
};


export type NameSpaceTableListItem = {
  id?: number;
  tenantId?: number;
  tenant: TenantTableListItem;
  namespaceCode?: string;
  enabled?: boolean;
  note?: string;
  createTime?: Date;
  updateTime?: Date;
};

