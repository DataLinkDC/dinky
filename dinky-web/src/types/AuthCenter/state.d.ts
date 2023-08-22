/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import { RowPermissions, SysMenu, UserBaseInfo } from '@/types/AuthCenter/data.d';
import { ContextMenuPosition } from '@/types/Public/state';
import { Key } from '@ant-design/pro-components';

/**
 * meuTree点击节点 单击和右击
 */
export interface MenuTreeClickNode {
  oneClickedNode: any;
  rightClickedNode: any;
}

/**
 * 菜单管理 的state
 */
export interface MenuState {
  sysMenuValue: Partial<SysMenu>;
  contextMenuPosition: ContextMenuPosition;
  selectedKeys: string[] | number[];
  clickNode: MenuTreeClickNode;
  menuTreeData: SysMenu[];
  addedMenuOpen: boolean;
  editMenuOpen: boolean;
  loading: boolean;
  contextMenuOpen: boolean;
  isEditDisabled: boolean;
  isRootMenu: boolean;
}

/**
 * 给角色分配菜单 的state
 */
export interface RoleAssignMenuState {
  loading: boolean;
  searchValue: string;
  selectValue: Key[];
  menuTreeData: {
    menus: SysMenu[];
    selectedMenuIds: number[];
  };
}

/**
 * 角色管理 的state
 */
export interface RoleListState {
  loading: boolean;
  value: Partial<UserBaseInfo.Role>;
  addedRoleOpen: boolean;
  editRoleOpen: boolean;
  assignMenuOpen: boolean;
  viewUsersOpen: boolean;
  roleUserList: UserBaseInfo.User[];
}

/**
 * 角色行权限 的state
 */
export interface RowPermissionsState {
  loading: boolean;
  value: Partial<RowPermissions>;
  addedRowPermissionsOpen: boolean;
  editRowPermissionsOpen: boolean;
}

/**
 * 租户分配用户 的state
 */
export interface TenantTransferState {
  targetKeys: string[];
  selectedKeys: string[];
  userList: UserBaseInfo.User[];
}

/**
 * 租户管理 的state
 */
export interface TenantListState {
  loading: boolean;
  value: Partial<UserBaseInfo.Tenant>;
  assignUserOpen: boolean;
  addedTenantOpen: boolean;
  editTenantOpen: boolean;
  viewUsersOpen: boolean;
  tenantUserList: UserBaseInfo.User[];
  tenantUserIds: string[];
}

/**
 * 给用户分配角色 的state
 */
export interface RoleTransferState {
  targetKeys: string[];
  selectedKeys: string[];
  roleList: UserBaseInfo.Role[];
}

export interface UserListState {
  loading: boolean;
  value: Partial<UserBaseInfo.User>;
  addedUserOpen: boolean;
  editUserOpen: boolean;
  assignRoleOpen: boolean;
  roleIds: string[];
  editPasswordOpen: boolean;
}
