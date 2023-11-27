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

import { BaseBeanColumns, ExcludeNameAndEnableColumns } from '@/types/Public/data';

export type SysMenu = BaseBeanColumns & {
  parentId: number; // 父级
  orderNum: number; // 排序
  path: string; // 路由
  component: string; // 组件
  type: string; // C菜单 F按钮 M目录
  display: boolean; // 菜单状态(0显示 1隐藏)
  perms: string; // 权限标识
  icon: string; // 图标
  rootMenu: boolean;
  note: string;
  children: SysMenu[];
};

declare namespace UserBaseInfo {
  export type User = ExcludeNameAndEnableColumns & {
    username: string;
    nickname?: string;
    password?: string;
    avatar?: string;
    worknum?: string;
    userType: number;
    mobile?: string;
    enabled: boolean;
    isDelete: boolean;
    superAdminFlag: boolean;
    tenantAdminFlag?: boolean;
  };

  export type ChangePasswordParams = {
    id: number;
    username: string;
    password: string;
    newPassword: string;
    newPasswordCheck: string;
  };

  export type Tenant = ExcludeNameAndEnableColumns & {
    tenantCode: string;
    isDelete: boolean;
    note?: string;
  };

  export type Role = ExcludeNameAndEnableColumns & {
    tenantId: number;
    tenant: Tenant;
    roleCode?: string;
    roleName?: string;
    isDelete: boolean;
    note?: string;
  };
}

export type SaTokenInfo = {
  tokenName: string;
  tokenValue: string;
  isLogin: boolean;
  loginId: number;
  loginType: string;
  tokenTimeout: number;
  sessionTimeout: number;
  tokenSessionTimeout: number;
  tokenActivityTimeout: number;
  loginDevice: string;
  tag: string;
};

export type RowPermissions = ExcludeNameAndEnableColumns & {
  roleId: number;
  roleCode: string;
  roleName: string;
  tableName: string;
  expression: string;
};

export type LoginLog = {
  id: number;
  userId: number;
  username: string;
  ip: string;
  loginType: string;
  status: number;
  msg: string;
  isDeleted: boolean;
  accessTime: Date;
  createTime: Date;
  updateTime: Date;
};

export type OperateLog = {
  id: number;
  moduleName: string;
  businessType: number;
  method: string;
  requestMethod: string;
  operateName: string;
  operateUserId: number;
  operateUrl: string;
  operateIp: string;
  operateLocation: string;
  operateParam: string;
  jsonResult: string;
  status: number;
  errorMsg: string;
  operateTime: Date;
};

export interface SysToken extends ExcludeNameAndEnableColumns {
  tokenValue: string;
  userId: number;
  userName: string;
  tenantId: number;
  tenantCode: string;
  roleId: string;
  roleName: string;
  expireType: number;
  expireTimeRange: [Date, Date];
  expireStartTime: Date;
  expireEndTime: Date;
  expireTime: any;
  creator: number;
  updator: number;
}
