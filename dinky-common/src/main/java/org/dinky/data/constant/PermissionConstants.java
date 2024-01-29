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

package org.dinky.data.constant;

/**
 * PermissionConstants
 */
public class PermissionConstants {
    /**
     * super admin role
     */
    public static final String ROLE_SUPER_ADMIN = "super_admin";

    /**
     * registration alert group
     */
    public static final String REGISTRATION_ALERT_GROUP_ADD = "registration:alert:group:add";

    public static final String REGISTRATION_ALERT_GROUP_EDIT = "registration:alert:group:edit";

    public static final String REGISTRATION_ALERT_GROUP_DELETE = "registration:alert:group:delete";

    /**
     * registration alert instance
     */
    public static final String REGISTRATION_ALERT_INSTANCE_ADD = "registration:alert:instance:add";

    public static final String REGISTRATION_ALERT_INSTANCE_EDIT = "registration:alert:instance:edit";

    public static final String REGISTRATION_ALERT_INSTANCE_DELETE = "registration:alert:instance:delete";

    /**
     * registration alert template
     */
    public static final String REGISTRATION_ALERT_TEMPLATE_ADD = "registration:alert:template:add";

    public static final String REGISTRATION_ALERT_TEMPLATE_EDIT = "registration:alert:template:edit";

    public static final String REGISTRATION_ALERT_TEMPLATE_DELETE = "registration:alert:template:delete";

    /**
     * catalog
     * todo: add catalog permission
     */

    /**
     * cluster config
     */
    public static final String REGISTRATION_CLUSTER_CONFIG_ADD = "registration:cluster:config:add";

    public static final String REGISTRATION_CLUSTER_CONFIG_EDIT = "registration:cluster:config:edit";
    public static final String REGISTRATION_CLUSTER_CONFIG_DELETE = "registration:cluster:config:delete";
    // heartbeats
    public static final String REGISTRATION_CLUSTER_CONFIG_HEARTBEATS = "registration:cluster:config:heartbeat";
    // deploy
    public static final String REGISTRATION_CLUSTER_CONFIG_DEPLOY = "registration:cluster:config:deploy";

    /**
     * cluster instance
     */
    public static final String REGISTRATION_CLUSTER_INSTANCE_ADD = "registration:cluster:instance:add";

    public static final String REGISTRATION_CLUSTER_INSTANCE_EDIT = "registration:cluster:instance:edit";

    public static final String REGISTRATION_CLUSTER_INSTANCE_DELETE = "registration:cluster:instance:delete";
    // heartbeats
    public static final String REGISTRATION_CLUSTER_INSTANCE_HEARTBEATS = "registration:cluster:instance:heartbeat";
    // recycle
    public static final String REGISTRATION_CLUSTER_INSTANCE_RECYCLE = "registration:cluster:instance:recycle";
    // killCluster
    public static final String REGISTRATION_CLUSTER_INSTANCE_KILL = "registration:cluster:instance:kill";

    /**
     * data source
     */
    public static final String REGISTRATION_DATA_SOURCE_LIST = "registration:datasource:list";

    public static final String REGISTRATION_DATA_SOURCE_ADD = "registration:datasource:list:add";
    public static final String REGISTRATION_DATA_SOURCE_EDIT = "registration:datasource:list:edit";
    public static final String REGISTRATION_DATA_SOURCE_DELETE = "registration:datasource:list:delete";
    public static final String REGISTRATION_DATA_SOURCE_COPY = "registration:datasource:list:copy";
    public static final String REGISTRATION_DATA_SOURCE_CHECK_HEARTBEAT = "registration:datasource:list:heartbeat";
    public static final String REGISTRATION_DATA_SOURCE_DETAIL_TREE = "registration:datasource:detail:tree";
    public static final String REGISTRATION_DATA_SOURCE_DETAIL_DESC = "registration:datasource:detail:desc";
    public static final String REGISTRATION_DATA_SOURCE_DETAIL_QUERY = "registration:datasource:detail:query";
    public static final String REGISTRATION_DATA_SOURCE_DETAIL_GENSQL = "registration:datasource:detail:gensql";
    public static final String REGISTRATION_DATA_SOURCE_DETAIL_CONSOLE = "registration:datasource:detail:console";
    public static final String REGISTRATION_DATA_SOURCE_DETAIL_REFRESH = "registration:datasource:detail:refresh";

    /**
     * document
     */
    public static final String REGISTRATION_DOCUMENT_ADD = "registration:document:add";

    public static final String REGISTRATION_DOCUMENT_EDIT = "registration:document:edit";
    public static final String REGISTRATION_DOCUMENT_DELETE = "registration:document:delete";

    /**
     * fragment
     */
    public static final String REGISTRATION_FRAGMENT_ADD = "registration:fragment:add";

    public static final String REGISTRATION_FRAGMENT_EDIT = "registration:fragment:edit";
    public static final String REGISTRATION_FRAGMENT_DELETE = "registration:fragment:delete";

    /**
     * git project
     */
    public static final String REGISTRATION_GIT_PROJECT_ADD = "registration:gitproject:add";

    public static final String REGISTRATION_GIT_PROJECT_EDIT = "registration:gitproject:edit";
    public static final String REGISTRATION_GIT_PROJECT_DELETE = "registration:gitproject:delete";
    // build
    public static final String REGISTRATION_GIT_PROJECT_BUILD = "registration:gitproject:build";
    // showlog
    public static final String REGISTRATION_GIT_PROJECT_SHOW_LOG = "registration:gitproject:showLog";

    /**
     * udf
     */
    public static final String REGISTRATION_UDF_TEMPLATE_ADD = "registration:udf:template:add";

    public static final String REGISTRATION_UDF_TEMPLATE_EDIT = "registration:udf:template:edit";
    public static final String REGISTRATION_UDF_TEMPLATE_DELETE = "registration:udf:template:delete";

    /**
     * RESOURCE
     *
     */
    public static final String REGISTRATION_RESOURCE_ADD_FOLDER = "registration:resource:addFolder";

    public static final String REGISTRATION_RESOURCE_RENAME = "registration:resource:rename";
    public static final String REGISTRATION_RESOURCE_DELETE = "registration:resource:delete";
    // upload
    public static final String REGISTRATION_RESOURCE_UPLOAD = "registration:resource:upload";

    /**
     * auth menu
     */
    public static final String AUTH_MENU_ADD_ROOT = "auth:menu:createRoot";

    public static final String AUTH_MENU_ADD_SUB = "auth:menu:addSub";
    public static final String AUTH_MENU_EDIT = "auth:menu:edit";

    public static final String AUTH_MENU_DELETE = "auth:menu:delete";

    /**
     * AUTH ROLE
     */
    public static final String AUTH_ROLE_ADD = "auth:role:add";

    public static final String AUTH_ROLE_EDIT = "auth:role:edit";
    // 将 add 和 edit 合并为一个 集合
    public static final String[] AUTH_ROLE_ADD_EDIT = {AUTH_ROLE_ADD, AUTH_ROLE_EDIT};
    public static final String AUTH_ROLE_DELETE = "auth:role:delete";
    //  分配菜单
    public static final String AUTH_ROLE_ASSIGN_MENU = "auth:role:assignMenu";
    // 查看用户列表
    public static final String AUTH_ROLE_VIEW_USER_LIST = "auth:role:viewUser";

    /**
     * Row Permissions
     */
    public static final String AUTH_ROW_PERMISSIONS_ADD = "auth:rowPermissions:add";

    public static final String AUTH_ROW_PERMISSIONS_EDIT = "auth:rowPermissions:edit";

    public static final String AUTH_ROW_PERMISSIONS_DELETE = "auth:rowPermissions:delete";

    /**
     * token
     */
    public static final String AUTH_TOKEN_ADD = "auth:token:add";

    public static final String AUTH_TOKEN_EDIT = "auth:token:edit";

    public static final String AUTH_TOKEN_DELETE = "auth:token:delete";

    /**
     * user
     */
    public static final String AUTH_USER_ADD = "auth:user:add";

    public static final String AUTH_USER_EDIT = "auth:user:edit";

    public static final String AUTH_USER_DELETE = "auth:user:delete";
    // 修改密码
    public static final String AUTH_USER_CHANGE_PASSWORD = "auth:user:changePassword";
    // 重置密码
    public static final String AUTH_USER_RESET_PASSWORD = "auth:user:reset";
    // 分配角色
    public static final String AUTH_USER_ASSIGN_ROLE = "auth:user:assignRole";
    // 恢复
    public static final String AUTH_USER_RECOVERY = "auth:user:recovery";

    /**
     * tenant
     */
    public static final String AUTH_TENANT_ADD = "auth:tenant:add";

    public static final String AUTH_TENANT_EDIT = "auth:tenant:edit";

    public static final String AUTH_TENANT_DELETE = "auth:tenant:delete";
    // 分配用户
    public static final String AUTH_TENANT_ASSIGN_USER = "auth:tenant:assignUser";
    // 设置为租户管理员
    public static final String AUTH_TENANT_SET_USER_TO_TENANT_ADMIN = "auth:tenant:modifyTenantManager";
    // 查看用户
    public static final String AUTH_TENANT_VIEW_USER = "auth:tenant:viewUser";

    /**
     * system setting
     */
    public static final String SYSTEM_SETTING_INFO_LOG_LIST = "settings:systemlog:loglist";

    public static final String SYSTEM_SETTING_INFO_ROOT_LOG = "settings:systemlog:rootlog";

    /**
     * alert 策略
     */
    public static final String SYSTEM_ALERT_RULE_ADD = "settings:alertrule:add";

    public static final String SYSTEM_ALERT_RULE_EDIT = "settings:alertrule:edit";

    public static final String SYSTEM_ALERT_RULE_DELETE = "settings:alertrule:delete";
}
