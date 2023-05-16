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
  // ---- get git branch ----
  GIT_BRANCH: "/api/git/getBranchList",
  // ---- DELETE project ----
  GIT_PROJECT_DELETE: "/api/git/deleteProject",
  // ---- update project State  ----
  GIT_PROJECT_ENABLE: "/api/git/updateEnable",
  // ---- get project details by id ----
  GIT_GET_ONE_DETAILS: "/api/git/getOneDetails",
  // ---- get project details by id ----
  GIT_PROJECT_CODE_TREE: "/api/git/getProjectCode",
  // ---- get project build by id ----
  GIT_PROJECT_BUILD: "/api/git/build",
  // ---- get project build logs by id----
  GIT_PROJECT_BUILD_STEP_LOGS: "/api/git/build-step-logs",
  // ---- get project build steps ----
  GIT_PROJECT_BUILD_STEPS: "/api/git/build-steps",
  // ---- get project all build logs ----
  GIT_PROJECT_BUILD_ALL_LOGS: "/api/git/getAllBuildLog",

  // UDF template
  UDF_TEMPLATE: "/api/udf/template/list",
  // UDF template add or update
  UDF_TEMPLATE_ADD_UPDATE: "/api/udf/template",
  // UDF template delete
  UDF_TEMPLATE_DELETE: "/api/udf/template/delete",
  // UDF template enable or disable
  UDF_TEMPLATE_ENABLE: "/api/udf/template/enable",



  // system config center
  //-- system root logs
  SYSTEM_ROOT_LOG: "/api/system/getRootLog",
  // -- get logs list
  SYSTEM_ROOT_LOG_LIST: "/api/system/listLogDir",
  // -- READ LOG file
  SYSTEM_ROOT_LOG_READ: "/api/system/readFile",

  // process list
  PROCESS_LIST: "/api/process/listAllProcess",

};


/**
 * user tenant id
 */
export const TENANT_ID = "tenantId";

/**
 * the platform version
 */
export const VERSION = "0.8.0";

/**
 * REQUEST METHOD CONSTANTS
 */
export const METHOD_CONSTANTS = {
  GET: "GET",
  POST: "POST",
  PUT: "PUT",
  DELETE: "DELETE",
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
  width: "40%",
  style: {
    maxHeight: "70vh",
    overflowY: "auto",
  },
};


/**
 * the protable layout of public
 */
export const PROTABLE_OPTIONS_PUBLIC = {
  pagination: {
    defaultPageSize: 8,
    hideOnSinglePage: true,
    showQuickJumper: false,
    showSizeChanger: false,
  },
  rowKey: "id",
  search: {
    labelWidth: 120, // must be number
    span: 4,
  },
};

/**
 * the modal layout of public
 */
export const NORMAL_MODAL_OPTIONS = {
  width: "40%",
  bodyStyle: {padding: "20px 10px 10px"},
  destroyOnClose: true,
  maskClosable: false,
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
