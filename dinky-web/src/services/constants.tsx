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


import {l} from "@/utils/intl";
import {ModalFormProps} from "@ant-design/pro-form/es/layouts/ModalForm";

/**
 * the  interface api constants
 */
export const API_CONSTANTS = {
  // --- user ---
  // login path
  LOGIN_PATH: "/user/login",
  // user login
  LOGIN: "/api/login",
  // current user info
  CURRENT_USER: "/api/current",
  // logout
  LOGOUT: "/api/outLogin",
  // choose tenant
  CHOOSE_TENANT: "/api/chooseTenant",
  // user list
  USER: "/api/user",
  // enable user
  USER_ENABLE: "/api/user/enable",
  // delete user
  USER_DELETE: "/api/user/delete",
  // user change password
  USER_MODIFY_PASSWORD: "/api/user/modifyPassword",
  // GRANT USER TO role
  USER_ASSIGN_ROLE: "/api/user/assignRole",
  // QUERY roles by userid
  GET_ROLES_BY_USERID: "/api/role/getRolesAndIdsByUserId",

  // --- tenant ---
  // tenant list
  TENANT: "/api/tenant",
  // assign user to tenant
  ASSIGN_USER_TO_TENANT: "/api/tenant/assignUserToTenant",
  // delete tenant
  TENANT_DELETE: "/api/tenant/delete",
  // get user list by tenantId
  GET_USER_LIST_BY_TENANTID: "/api/user/getUserListByTenantId",

  // --- role ---
  // role list
  ROLE: "/api/role",
  ROLE_ADDED_OR_UPDATE: "/api/role/addedOrUpdateRole",
  ROLE_DELETE: "/api/role/delete",

  // --- row Permissions ---
     // row permissions list
    ROW_PERMISSIONS: "/api/rowPermissions",
    // row permissions delete
    ROW_PERMISSIONS_DELETE: "/api/rowPermissions/delete",

  // --- global variable ---
  // global variable list
  GLOBAL_VARIABLE: "/api/fragment",
  // delete global variable  by id
  GLOBAL_VARIABLE_DELETE: "/api/fragment/delete",
  // global variable enable or disable
  GLOBAL_VARIABLE_ENABLE: "/api/fragment/enable",


  // --- registries  center ---

  // ----cluster instance
    // cluster instance list
  CLUSTER_INSTANCE: "/api/cluster",
  CLUSTER_INSTANCE_ENABLE: "/api/cluster/enable",
  CLUSTER_INSTANCE_DELETE: "/api/cluster/delete",
  CLUSTER_INSTANCE_HEARTBEATS: "/api/cluster/heartbeats",
  CLUSTER_INSTANCE_RECYCLE: "/api/cluster/recycle",
  CLUSTER_CONFIGURATION_START: "/api/cluster/deploySessionClusterInstance",
  // cluster configuration list
  CLUSTER_CONFIGURATION: "/api/clusterConfiguration",
  CLUSTER_CONFIGURATION_DELETE: "/api/clusterConfiguration/delete",
  CLUSTER_CONFIGURATION_ENABLE: "/api/clusterConfiguration/enable",
  CLUSTER_CONFIGURATION_TEST: "/api/clusterConfiguration/testConnect",


  // datasource registries list
  DATASOURCE: "/api/database",
  // datasource registries delete
  DATASOURCE_DELETE: "/api/database/delete",
  // datasource registries enable or disable
  DATASOURCE_ENABLE: "/api/database/enable",
  // datasource registries test
  DATASOURCE_TEST: "/api/database/testConnect",
  // datasource  checkHeartBeat By Id
  DATASOURCE_CHECK_HEARTBEAT_BY_ID: "/api/database/checkHeartBeatByDataSourceId",
  // copy datasource
  DATASOURCE_COPY: "/api/database/copyDatabase",
  // get schema by datasource id
  DATASOURCE_GET_SCHEMA_TABLES: "/api/database/getSchemasAndTables",
  DATASOURCE_GET_COLUMNS_BY_TABLE: "/api/database/listColumns",
  DATASOURCE_GET_GEN_SQL: "/api/database/getSqlGeneration",
  DATASOURCE_QUERY_DATA: "/api/database/queryData",


  // document list
  DOCUMENT: "/api/document",
  // delete document by id
  DOCUMENT_DELETE: "/api/document/delete",
  // document enable or disable
  DOCUMENT_ENABLE: "/api/document/enable",

  // ---- alert instance ----
  // alert instance list
  ALERT_INSTANCE: "/api/alertInstance",
  // delete alert instance by id
  ALERT_INSTANCE_DELETE: "/api/alertInstance/delete",
  // alert instance enable or disable
  ALERT_INSTANCE_ENABLE: "/api/alertInstance/enable",
  // alert instance list all
  ALERT_INSTANCE_LIST_ENABLE_ALL: "/api/alertInstance/listEnabledAll",

  // ---- alert group ----
  ALERT_GROUP: "/api/alertGroup",
  // delete alert group by id
  ALERT_GROUP_DELETE: "/api/alertGroup/delete",
  // alert group enable or disable
  ALERT_GROUP_ENABLE: "/api/alertGroup/enable",


  // ---- get git project list----
  GIT_PROJECT: "/api/git/getProjectList",
  // ---- saveOrUpdate ----
  GIT_SAVE_UPDATE: "/api/git/saveOrUpdate",
  // dragendSortProject
  GIT_DRAGEND_SORT_PROJECT: "/api/git/dragendSortProject",
  // dragendSort jar
  GIT_DRAGEND_SORT_JAR: "/api/git/dragendSortJar",
  // ---- get git branch ----
  GIT_BRANCH: "/api/git/getBranchList",
  // ---- DELETE project ----
  GIT_PROJECT_DELETE: "/api/git/deleteProject",
  // ---- update project State  ----
  GIT_PROJECT_ENABLE: "/api/git/updateEnable",
  // ---- get project details by id ----
  GIT_PROJECT_CODE_TREE: "/api/git/getProjectCode",
  // ---- get project build by id ----
  GIT_PROJECT_BUILD: "/api/git/build",
  // ---- get project build logs by id----
  GIT_PROJECT_BUILD_STEP_LOGS: "/api/git/build-step-logs",

  // UDF template
  UDF_TEMPLATE: "/api/udf/template/list",
  // UDF template add or update
  UDF_TEMPLATE_ADD_UPDATE: "/api/udf/template",
  // UDF template delete
  UDF_TEMPLATE_DELETE: "/api/udf/template/delete",
  // UDF template enable or disable
  UDF_TEMPLATE_ENABLE: "/api/udf/template/enable",



  // system config center
  // global config list
  SYSTEM_GET_ALL_CONFIG: "/api/sysConfig/getAll",
  // update global config by key
  SYSTEM_MODIFY_CONFIG: "/api/sysConfig/modifyConfig",
  //-- system root logs
  SYSTEM_ROOT_LOG: "/api/system/getRootLog",
  // -- get logs list
  SYSTEM_ROOT_LOG_LIST: "/api/system/listLogDir",
  // -- READ LOG file
  SYSTEM_ROOT_LOG_READ: "/api/system/readFile",

  // process list
  PROCESS_LIST: "/api/process/listAllProcess",

  // ---- devops
  GET_JOB_LIST: "/api/jobInstance",
  GET_JOB_DETAIL: "/api/jobInstance/getJobInfoDetail",

  // -- LDAP
  GET_LDAP_ENABLE: "/api/ldap/ldapEnableStatus",
  LDAP_TEST_CONNECT: "/api/ldap/testConnection",
  LDAP_TEST_LOGIN: "/api/ldap/testLogin",
  LDAP_LIST_USER: "/api/ldap/listUser",
  LDAP_IMPORT_USERS: "/api/ldap/importUsers",

  // -- home
  GET_STATUS_COUNT: "api/jobInstance/getStatusCount",
  GET_RESOURCE_OVERVIEW: "/api/home/getResourceOverview",
  GET_JOB_STATUS_OVERVIEW: "/api/home/getJobStatusOverview",
  GET_JOB_TYPE_OVERVIEW: "/api/home/getJobTypeOverview",
  GET_JOB_MODEL_OVERVIEW: "/api/home/getJobModelOverview",

  // monitor
  MONITOR_GET_SYSTEM_DATA:"/api/monitor/getSysData",
  MONITOR_GET_LAST_DATA:"/api/monitor/getLastUpdateData",
  JOB_METRICS: "/api/monitor/jobMetrics",

  SAVE_FLINK_METRICS: "/api/monitor/saveFlinkMetrics",
  GET_METRICS_LAYOUT: "/api/monitor/getMetricsLayout",

  // flink
  FLINK_PROXY: "/api/flink",

  // resource
  RESOURCE_SHOW_TREE:'/api/resource/showByTree',
  RESOURCE_GET_CONTENT_BY_ID:'/api/resource/getContentByResourceId',
  RESOURCE_REMOVE:'/api/resource/remove',
  RESOURCE_CREATE_FOLDER:'/api/resource/createFolder',
  RESOURCE_RENAME:'/api/resource/rename',
  RESOURCE_UPLOAD:'/api/resource/uploadFile',
};


/**
 * user tenant id
 */
export const TENANT_ID = "tenantId";

/**
 * the platform version
 */
export const VERSION = "1.0.0-SNAPSHOT";

/**
 * the platform language
 */
export const STORY_LANGUAGE = "language";
export const LANGUAGE_KEY = 'umi_locale';
export const LANGUAGE_ZH = 'zh-CN';
export const LANGUAGE_EN = 'en-US';

/**
 * REQUEST METHOD CONSTANTS
 */
export enum METHOD_CONSTANTS {
  GET = "GET",
  POST = "POST",
  PUT = "PUT",
  DELETE = "DELETE",
};


/**
 * ALL TABLE COLUMN of status
 * @constructor
 */
export const STATUS_MAPPING = () => {
  return [
    {
      text: l("status.enabled"),
      value: 1,
    },
    {
      text: l("status.disabled"),
      value: 0,
    },
  ];

};

/**
 * ALL TABLE COLUMN of status enum
 * @constructor
 */
export const STATUS_ENUM = () => {
  return {
    true: {text: l("status.enabled"), status: "Success"},
    false: {text: l("status.disabled"), status: "Error"},
  };
};


export const RESPONSE_CODE = {
  SUCCESS: 0,
  ERROR: 1,
};


/**
 * the form layout of public
 */
export const FORM_LAYOUT_PUBLIC = {
  labelCol: {span: 5},
  wrapperCol: {span: 15},
};


/**
 * the modal form layout of public
 */
export const MODAL_FORM_STYLE: any = {
  width: "50%",
  style: {
    maxHeight: "70vh",
    overflowY: "auto",
  },
};



export const PRO_LIST_CARD_META = {
  title: {},
  subTitle: {},
  type: {},
  avatar: {},
  content: {},
  actions: {
    cardActionProps: "actions"
  },
};

export const PRO_LIST_CARD_OPTIONS = {
  search: false,
  metas: PRO_LIST_CARD_META,
  size: "small",
  pagination: {
    defaultPageSize: 15,
    hideOnSinglePage: true,
  },
  grid: {gutter: 24, column: 5}
};


/**
 * the protable layout of public
 */
export const PROTABLE_OPTIONS_PUBLIC : any = {
  pagination: {
    defaultPageSize: 12,
    hideOnSinglePage: true,
    showQuickJumper: false,
    showSizeChanger: false,
    position: ["bottomCenter"],
  },
  ghost: false,
  rowKey: "id",
  size: "small",
  scroll: {
    y: "auto",
  },
  search: {
    labelWidth: 100, // must be number
    span: 4,
  },
};

/**
 * the modal layout of public
 */
export const NORMAL_MODAL_OPTIONS = {
  width: "50%",
  bodyStyle: {padding: "20px 10px 10px"},
  destroyOnClose: true,
  maskClosable: false,
};


/**
 * the modal layout of public
 */
export const MODAL_FORM_OPTIONS:ModalFormProps = {
  width: "50%",
};

/**
 * the modal layout of public
 */
export const NORMAL_TABLE_OPTIONS = {
  pagination: {
    defaultPageSize: 6,
    hideOnSinglePage: true,
  },
  rowKey: "id",
  style: {
    scrollY: "auto",
    scrollX: "auto",
  }
};


export const SWITCH_OPTIONS =() => {
  return {
    checkedChildren: l("status.enabled"),
    unCheckedChildren: l("status.disabled"),
  };
}




export const DIALECT = {
  JAVA: "java",
  LOG: "log",
  XML: "xml",
  MD: "md",
  MDX: "mdx",
  MARKDOWN: "markdown",
  SCALA: "scala",
  PYTHON: "py",
  PYTHON_LONG: "python",
  YML: "yml",
  YAML: "yaml",
  SH: "sh",
  BASH: "bash",
  CMD: "cmd",
  SHELL: "shell",
  JSON: "json",
  SQL: "sql",
  JAVASCRIPT: "javascript",
};


export const RUN_MODE = {
  LOCAL: 'local',
  STANDALONE: 'standalone',
  YARN_SESSION: 'yarn-session',
  YARN_PER_JOB: 'yarn-per-job',
  YARN_APPLICATION: 'yarn-application',
  KUBERNETES_SESSION: 'kubernetes-session',
  KUBERNETES_APPLICATION: 'kubernetes-application',
  KUBERNETES_APPLICATION_OPERATOR: 'kubernetes-application-operator',
};
