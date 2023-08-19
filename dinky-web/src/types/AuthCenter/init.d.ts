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

import {InitContextMenuPosition} from "@/types/Public/state.d";
import {
    MenuState,
    RoleAssignMenuState,
    RoleListState, RoleTransferState,
    RowPermissionsState, TenantListState,
    TenantTransferState, UserListState
} from "@/types/AuthCenter/state";
import {UserBaseInfo} from "@/types/AuthCenter/data";


/**
 * 初始化menu state
 * @returns {MenuState}
 * @constructor
 */
export const InitMenuState = (): MenuState => {
    return {
        formValue: {},
        contextMenuPosition: InitContextMenuPosition,
        selectedKeys: [],
        clickNode: {
            oneClickedNode: {},
            rightClickedNode: {},
        },
        menuTreeData: [],
        addedMenuOpen: false,
        editMenuOpen: false,
        loading: false,
        contextMenuOpen: false,
        isEditDisabled: true,
        isRootMenu: false,
    }
};


/**
 * 初始化角色分配菜单state
 * @type {{menuTreeData: {selectedMenuIds: any[], menus: any[]}, selectValue: any[], loading: boolean, searchValue: string}}
 */
export const InitRoleAssignMenuState : RoleAssignMenuState ={
    loading: false,
    searchValue: '',
    selectValue: [],
    menuTreeData: {
        menus: [],
        selectedMenuIds: [],
    }
}

/**
 * 初始化角色列表state
 * @type {{assignMenuOpen: boolean, editRoleOpen: boolean, addedRoleOpen: boolean, viewUsersOpen: boolean, roleUserList: any[], loading: boolean, value: {}}}
 */
export const InitRoleListState : RoleListState = {
    loading: false,
    value: {},
    addedRoleOpen: false,
    editRoleOpen: false,
    assignMenuOpen: false,
    viewUsersOpen: false,
    roleUserList: [],
}

/**
 * 初始化行权限state
 * @type {{addedRowPermissionsOpen: boolean, editRowPermissionsOpen: boolean, loading: boolean, value: {}}}
 */
export const InitRowPermissionsState: RowPermissionsState ={
    loading: false,
    value: {},
    addedRowPermissionsOpen: false,
    editRowPermissionsOpen: false,
}

/**
 * 初始化租户分配用户 state
 * @type {{userList: any[], selectedKeys: any[], targetKeys: any[]}}
 */
export const InitTenantTransferState: TenantTransferState = {
    targetKeys: [],
    selectedKeys: [],
    userList: [],
}

/**
 * 初始化租户管理 state
 * @type {{addedTenantOpen: boolean, tenantUserList: any[], tenantUserIds: any[], viewUsersOpen: boolean, loading: boolean, value: {}, editTenantOpen: boolean, assignUserOpen: boolean}}
 */
export const InitTenantListState: TenantListState = {
    loading: false,
    value: {},
    addedTenantOpen: false,
    editTenantOpen: false,
    assignUserOpen: false,
    viewUsersOpen: false,
    tenantUserList: [],
    tenantUserIds: [],
}

/**
 * 初始化用户分配角色 state
 * @type {{selectedKeys: any[], targetKeys: any[], roleList: any[]}}
 */
export const InitRoleTransferState: RoleTransferState = {
    targetKeys: [],
    selectedKeys: [],
    roleList: [],
}

export const InitUserListState: UserListState = {
    loading: false,
    value: {},
    addedUserOpen: false,
    editUserOpen: false,
    assignRoleOpen: false,
    roleList: [],
    editPasswordOpen: false,
}