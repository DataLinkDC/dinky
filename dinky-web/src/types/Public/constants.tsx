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

import { l } from '@/utils/intl';
import { Tag } from 'antd';

/**
 * the table filter enum
 * @type {{true: {text: JSX.Element, status: string}, false: {text: JSX.Element, status: string}}}
 */
export const YES_OR_NO_ENUM = {
  true: {
    text: <Tag color={'success'}>{l('global.yes')}</Tag>,
    status: 'Success'
  },
  false: { text: <Tag color={'error'}>{l('global.no')}</Tag>, status: 'Error' }
};

/**
 * the table filter mapping
 * @type {({text: string, value: boolean} | {text: string, value: boolean})[]}
 */
export const YES_OR_NO_FILTERS_MAPPING = [
  {
    value: 1,
    text: l('global.yes')
  },
  {
    value: 0,
    text: l('global.no')
  }
];

export enum PermissionConstants {
  /**
   * datastudio
   */
  // left
  DATA_STUDIO_LEFT_PROJECT = '/datastudio/left/project',
  DATASTUDIO_LEFT_CATALOG = '/datastudio/left/catalog',
  DATASTUDIO_LEFT_DATASOURCE = '/datastudio/left/datasource',
  DATASTUDIO_LEFT_GLOBAL_VARIABLE = '/datastudio/left/globalVariable',

  // right
  DATASTUDIO_RIGHT_JOB_CONFIG = '/datastudio/right/jobConfig',
  DATASTUDIO_RIGHT_PREVIEW_CONFIG = '/datastudio/right/previewConfig',
  DATASTUDIO_RIGHT_SAVE_POINT = '/datastudio/right/savePoint',
  DATASTUDIO_RIGHT_HISTORY_VISION = '/datastudio/right/historyVision',
  DATASTUDIO_RIGHT_JOB_INFO = '/datastudio/right/jobInfo',

  // LEFT BOTTOM
  DATASTUDIO_LEFT_BOTTOM_CONSOLE = '/datastudio/bottom/console',
  DATASTUDIO_LEFT_BOTTOM_RESULT = '/datastudio/bottom/result',
  DATASTUDIO_LEFT_BOTTOM_LINEAGE = '/datastudio/bottom/lineage',
  DATASTUDIO_LEFT_BOTTOM_HISTORY = '/datastudio/bottom/history',
  DATASTUDIO_LEFT_BOTTOM_TABLE_DATA = '/datastudio/bottom/table-data',
  DATASTUDIO_LEFT_BOTTOM_TOOL = '/datastudio/bottom/tool',

  /**
   * registration alert group
   */
  REGISTRATION_ALERT_GROUP_ADD = '/registration/alert/group/add',
  REGISTRATION_ALERT_GROUP_EDIT = '/registration/alert/group/edit',
  REGISTRATION_ALERT_GROUP_DELETE = '/registration/alert/group/delete',

  /**
   * registration alert instance
   */
  REGISTRATION_ALERT_INSTANCE_ADD = '/registration/alert/instance/add',
  REGISTRATION_ALERT_INSTANCE_EDIT = '/registration/alert/instance/edit',
  REGISTRATION_ALERT_INSTANCE_DELETE = '/registration/alert/instance/delete',

  /**
   * registration alert template
   */
  REGISTRATION_ALERT_TEMPLATE_ADD = '/registration/alert/template/add',
  REGISTRATION_ALERT_TEMPLATE_EDIT = '/registration/alert/template/edit',
  REGISTRATION_ALERT_TEMPLATE_DELETE = '/registration/alert/template/delete',

  /**
   * cluster config
   */
  REGISTRATION_CLUSTER_CONFIG_ADD = '/registration/cluster/config/add',
  REGISTRATION_CLUSTER_CONFIG_EDIT = '/registration/cluster/config/edit',
  REGISTRATION_CLUSTER_CONFIG_DELETE = '/registration/cluster/config/delete',
  REGISTRATION_CLUSTER_CONFIG_HEARTBEATS = '/registration/cluster/config/heartbeat',
  REGISTRATION_CLUSTER_CONFIG_DEPLOY = '/registration/cluster/config/deploy',

  /**
   * cluster instance
   */
  REGISTRATION_CLUSTER_INSTANCE_ADD = '/registration/cluster/instance/add',
  REGISTRATION_CLUSTER_INSTANCE_EDIT = '/registration/cluster/instance/edit',
  REGISTRATION_CLUSTER_INSTANCE_DELETE = '/registration/cluster/instance/delete',
  REGISTRATION_CLUSTER_INSTANCE_HEARTBEATS = '/registration/cluster/instance/heartbeat',
  REGISTRATION_CLUSTER_INSTANCE_KILL = '/registration/cluster/instance/kill',

  /**
   * data source
   */
  REGISTRATION_DATA_SOURCE_LIST = '/registration/datasource/list',
  REGISTRATION_DATA_SOURCE_ADD = '/registration/datasource/list/add',
  REGISTRATION_DATA_SOURCE_EDIT = '/registration/datasource/list/edit',
  REGISTRATION_DATA_SOURCE_DELETE = '/registration/datasource/list/delete',
  REGISTRATION_DATA_SOURCE_COPY = '/registration/datasource/list/copy',
  REGISTRATION_DATA_SOURCE_CHECK_HEARTBEAT = '/registration/datasource/list/heartbeat',
  REGISTRATION_DATA_SOURCE_DETAIL_TREE = '/registration/datasource/detail/tree',
  REGISTRATION_DATA_SOURCE_DETAIL_DESC = '/registration/datasource/detail/desc',
  REGISTRATION_DATA_SOURCE_DETAIL_QUERY = '/registration/datasource/detail/query',
  REGISTRATION_DATA_SOURCE_DETAIL_GENSQL = '/registration/datasource/detail/gensql',
  REGISTRATION_DATA_SOURCE_DETAIL_CONSOLE = '/registration/datasource/detail/console',
  REGISTRATION_DATA_SOURCE_DETAIL_REFRESH = '/registration/datasource/detail/refresh',

  /**
   * document
   */
  REGISTRATION_DOCUMENT_ADD = '/registration/document/add',
  REGISTRATION_DOCUMENT_EDIT = '/registration/document/edit',
  REGISTRATION_DOCUMENT_DELETE = '/registration/document/delete',

  /**
   * fragment
   */
  REGISTRATION_FRAGMENT_ADD = '/registration/fragment/add',
  REGISTRATION_FRAGMENT_EDIT = '/registration/fragment/edit',
  REGISTRATION_FRAGMENT_DELETE = '/registration/fragment/delete',

  /**
   * git project
   */
  REGISTRATION_GIT_PROJECT_ADD = '/registration/gitproject/add',
  REGISTRATION_GIT_PROJECT_EDIT = '/registration/gitproject/edit',
  REGISTRATION_GIT_PROJECT_DELETE = '/registration/gitproject/delete',
  REGISTRATION_GIT_PROJECT_BUILD = '/registration/gitproject/build',
  REGISTRATION_GIT_PROJECT_SHOW_LOG = '/registration/gitproject/showLog',

  /**
   * udf
   */
  REGISTRATION_UDF_TEMPLATE_ADD = '/registration/udf/template/add',
  REGISTRATION_UDF_TEMPLATE_EDIT = '/registration/udf/template/edit',
  REGISTRATION_UDF_TEMPLATE_DELETE = '/registration/udf/template/delete',

  /**
   * RESOURCE
   *
   */
  REGISTRATION_RESOURCE_ADD_FOLDER = '/registration/resource/addFolder',
  REGISTRATION_RESOURCE_RENAME = '/registration/resource/rename',
  REGISTRATION_RESOURCE_DELETE = '/registration/resource/delete',
  REGISTRATION_RESOURCE_UPLOAD = '/registration/resource/upload',

  /**
   * auth menu
   */
  AUTH_MENU_ADD_ROOT = '/auth/menu/createRoot',
  AUTH_MENU_ADD_SUB = '/auth/menu/addSub',
  AUTH_MENU_EDIT = '/auth/menu/edit',
  AUTH_MENU_DELETE = '/auth/menu/delete',
  AUTH_MENU_REFRESH = '/auth/menu/refresh',

  /**
   * AUTH ROLE
   */
  AUTH_ROLE_ADD = '/auth/role/add',
  AUTH_ROLE_EDIT = '/auth/role/edit',
  AUTH_ROLE_DELETE = '/auth/role/delete',
  AUTH_ROLE_ASSIGN_MENU = '/auth/role/assignMenu',
  AUTH_ROLE_VIEW_USER_LIST = '/auth/role/viewUser',

  /**
   * Row Permissions
   */
  AUTH_ROW_PERMISSIONS_ADD = '/auth/rowPermissions/add',
  AUTH_ROW_PERMISSIONS_EDIT = '/auth/rowPermissions/edit',
  AUTH_ROW_PERMISSIONS_DELETE = '/auth/rowPermissions/delete',

  /**
   * token
   */
  AUTH_TOKEN_ADD = '/auth/token/add',
  AUTH_TOKEN_EDIT = '/auth/token/edit',
  AUTH_TOKEN_DELETE = '/auth/token/delete',

  /**
   * user
   */
  AUTH_USER_ADD = '/auth/user/add',
  AUTH_USER_EDIT = '/auth/user/edit',
  AUTH_USER_DELETE = '/auth/user/delete',
  AUTH_USER_CHANGE_PASSWORD = '/auth/user/changePassword',
  AUTH_USER_RESET_PASSWORD = '/auth/user/reset',
  AUTH_USER_ASSIGN_ROLE = '/auth/user/assignRole',
  AUTH_USER_RECOVERY = '/auth/user/recovery',

  /**
   * tenant
   */
  AUTH_TENANT_ADD = '/auth/tenant/add',
  AUTH_TENANT_EDIT = '/auth/tenant/edit',
  AUTH_TENANT_DELETE = '/auth/tenant/delete',
  AUTH_TENANT_ASSIGN_USER = '/auth/tenant/assignUser',
  AUTH_TENANT_SET_USER_TO_TENANT_ADMIN = '/auth/tenant/modifyTenantManager',
  AUTH_TENANT_VIEW_USER = '/auth/tenant/viewUser',

  /**
   * global setting
   */
  SETTING_GLOBAL_DINKY = '/settings/globalsetting/dinky',
  SETTING_GLOBAL_DINKY_EDIT = '/settings/globalsetting/dinky/edit',
  SETTING_GLOBAL_FLINK = '/settings/globalsetting/flink',
  SETTING_GLOBAL_FLINK_EDIT = '/settings/globalsetting/flink/edit',
  SETTING_GLOBAL_MAVEN = '/settings/globalsetting/maven',
  SETTING_GLOBAL_MAVEN_EDIT = '/settings/globalsetting/maven/edit',
  SETTING_GLOBAL_DS = '/settings/globalsetting/ds',
  SETTING_GLOBAL_DS_EDIT = '/settings/globalsetting/ds/edit',
  SETTING_GLOBAL_LDAP = '/settings/globalsetting/ldap',
  SETTING_GLOBAL_LDAP_EDIT = '/settings/globalsetting/ldap/edit',
  SETTING_GLOBAL_METRICS = '/settings/globalsetting/metrics',
  SETTING_GLOBAL_METRICS_EDIT = '/settings/globalsetting/metrics/edit',
  SETTING_GLOBAL_RESOURCE = '/settings/globalsetting/resource',
  SETTING_GLOBAL_RESOURCE_EDIT = '/settings/globalsetting/resource/edit',

  /**
   * system log
   */
  SYSTEM_SETTING_INFO_LOG_LIST = '/settings/systemlog/loglist',
  SYSTEM_SETTING_INFO_ROOT_LOG = '/settings/systemlog/rootlog',

  /**
   * alert 策略
   */
  SYSTEM_ALERT_RULE_ADD = '/settings/alertrule/add',
  SYSTEM_ALERT_RULE_EDIT = '/settings/alertrule/edit',
  SYSTEM_ALERT_RULE_DELETE = '/settings/alertrule/delete'
}
