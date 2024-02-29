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

export enum API_CONSTANTS {
  BASE_URL = '.',
  GET_SERVICE_VERSION = '/api/version',

  /** ------------------------------------------------- auth center ------------------------------------ */
  // ------------------------------------ user ------------------------------------
  LOGIN_PATH = '/user/login',
  TOKEN_INFO = '/api/tokenInfo',
  LOGIN_RECORD = '/api/log/loginRecord',
  OPERATE_LOG = '/api/log/operateLog',
  LOGIN = '/api/login',
  CURRENT_USER = '/api/current',
  LOGOUT = '/api/outLogin',
  CHOOSE_TENANT = '/api/chooseTenant',
  USER = '/api/user',
  USER_RECOVERY = '/api/user/recovery',
  USER_RESET_PASSWORD = '/api/user/resetPassword',
  USER_ENABLE = '/api/user/enable',
  USER_DELETE = '/api/user/delete',
  USER_MODIFY_PASSWORD = '/api/user/modifyPassword',
  USER_ASSIGN_ROLE = '/api/user/assignRole',
  GET_ROLES_BY_USERID = '/api/role/getRolesAndIdsByUserId',

  // ------------------------------------ token ------------------------------------
  TOKEN = '/api/token/list',
  TOKEN_DELETE = '/api/token/delete',
  TOKEN_SAVE_OR_UPDATE = '/api/token/saveOrUpdateToken',
  TOKEN_BUILD = '/api/token/buildToken',

  // ------------------------------------ tenant ------------------------------------
  TENANT = '/api/tenant',
  ASSIGN_USER_TO_TENANT = '/api/tenant/assignUserToTenant',
  TENANT_DELETE = '/api/tenant/delete',
  GET_USER_LIST_BY_TENANTID = '/api/user/getUserListByTenantId',
  TENANT_USERS = '/api/tenant/getUsersByTenantId',
  USER_SET_TENANT_ADMIN = '/api/user/updateUserToTenantAdmin',
  TENANT_USER_LIST = '/api/tenant/getTenantListByUserId',

  // ------------------------------------ role ------------------------------------
  ROLE = '/api/role',
  ROLE_ADDED_OR_UPDATE = '/api/role/addedOrUpdateRole',
  ROLE_DELETE = '/api/role/delete',
  ROLE_ASSIGN_MENU = '/api/roleMenu/assignMenuToRole',
  ROLE_MENU_LIST = '/api/menu/roleMenus',
  ROLE_USER_LIST = '/api/role/getUserListByRoleId',

  // ------------------------------------ menu ------------------------------------
  MENU_ADD_OR_UPDATE = '/api/menu/addOrUpdate',
  MENU_DELETE = '/api/menu/delete',
  MENU_TREE = '/api/menu/tree',
  MENU_LIST = '/api/menu/listMenus',

  // ------------------------------------ row permissions ------------------------------------
  ROW_PERMISSIONS = '/api/rowPermissions',
  ROW_PERMISSIONS_DELETE = '/api/rowPermissions/delete',

  // ------------------------------------ global variable ------------------------------------
  GLOBAL_VARIABLE = '/api/fragment',
  GLOBAL_VARIABLE_DELETE = '/api/fragment/delete',
  GLOBAL_VARIABLE_ENABLE = '/api/fragment/enable',

  /** ------------------------------------ register center ------------------------------------ */

  // ------------------------------------ cluster instance ------------------------------------
  CLUSTER_INSTANCE = '/api/cluster',
  CLUSTER_INSTANCE_LIST = '/api/cluster/list',
  CLUSTER_INSTANCE_ENABLE = '/api/cluster/enable',
  CLUSTER_INSTANCE_DELETE = '/api/cluster/delete',
  CLUSTER_INSTANCE_KILL = '/api/cluster/killCluster',
  CLUSTER_INSTANCE_HEARTBEATS = '/api/cluster/heartbeats',
  CLUSTER_CONFIGURATION_START = '/api/cluster/deploySessionClusterInstance',
  CLUSTER_INSTANCE_SESSION = '/api/cluster/listSessionEnable',

  // ------------------------------------ cluster configuration ------------------------------------
  CLUSTER_CONFIGURATION = '/api/clusterConfiguration/list',
  CLUSTER_CONFIGURATION_LIST_ENABLE_ALL = '/api/clusterConfiguration/listEnabledAll',
  CLUSTER_CONFIGURATION_ADD_OR_UPDATE = '/api/clusterConfiguration/saveOrUpdate',
  CLUSTER_CONFIGURATION_DELETE = '/api/clusterConfiguration/delete',
  CLUSTER_CONFIGURATION_ENABLE = '/api/clusterConfiguration/enable',
  CLUSTER_CONFIGURATION_TEST = '/api/clusterConfiguration/testConnect',

  // ------------------------------------ datasource registries ------------------------------------
  DATASOURCE = '/api/database/list',
  DATASOURCE_ADD_OR_UPDATE = '/api/database/saveOrUpdate',
  DATASOURCE_DELETE = '/api/database/delete',
  DATASOURCE_ENABLE = '/api/database/enable',
  DATASOURCE_TEST = '/api/database/testConnect',
  DATASOURCE_CHECK_HEARTBEAT_BY_ID = '/api/database/checkHeartBeatByDataSourceId',
  DATASOURCE_COPY = '/api/database/copyDatabase',
  DATASOURCE_GET_SCHEMA_TABLES = '/api/database/getSchemasAndTables',
  DATASOURCE_GET_COLUMNS_BY_TABLE = '/api/database/listColumns',
  DATASOURCE_GET_GEN_SQL = '/api/database/getSqlGeneration',
  DATASOURCE_QUERY_DATA = '/api/database/queryData',

  // ------------------------------------ document ------------------------------------
  DOCUMENT = '/api/document',
  DOCUMENT_DELETE = '/api/document/delete',
  DOCUMENT_ENABLE = '/api/document/enable',

  // ------------------------------------ alert instance ------------------------------------
  ALERT_INSTANCE = '/api/alertInstance/list',
  ALERT_INSTANCE_ADD_OR_UPDATE = '/api/alertInstance/saveOrUpdate',
  ALERT_INSTANCE_DELETE = '/api/alertInstance/delete',
  ALERT_INSTANCE_ENABLE = '/api/alertInstance/enable',
  ALERT_INSTANCE_LIST_ENABLE_ALL = '/api/alertInstance/listEnabledAll',
  ALERT_INSTANCE_SEND_TEST = '/api/alertInstance/sendTest',

  // ------------------------------------ alert group ------------------------------------
  ALERT_GROUP = '/api/alertGroup/list',
  ALERT_GROUP_LIST_ENABLE_ALL = '/api/alertGroup/listEnabledAll',
  ALERT_GROUP_ADD_OR_UPDATE = '/api/alertGroup/addOrUpdate',
  ALERT_GROUP_DELETE = '/api/alertGroup/delete',
  ALERT_GROUP_ENABLE = '/api/alertGroup/enable',

  // ------------------------------------ alert rule ------------------------------------
  ALERT_RULE_LIST = '/api/alertRule/list',
  ALERT_RULE = '/api/alertRule',
  ALERT_TEMPLATE = '/api/alertTemplate',

  // ------------------------------------ git ------------------------------------
  GIT_PROJECT = '/api/git/getProjectList',
  GIT_SAVE_UPDATE = '/api/git/saveOrUpdate',
  GIT_DRAGEND_SORT_PROJECT = '/api/git/dragendSortProject',
  GIT_DRAGEND_SORT_JAR = '/api/git/dragendSortJar',
  GIT_BRANCH = '/api/git/getBranchList',
  GIT_PROJECT_DELETE = '/api/git/deleteProject',
  GIT_PROJECT_ENABLE = '/api/git/updateEnable',
  GIT_PROJECT_CODE_TREE = '/api/git/getProjectCode',
  GIT_PROJECT_BUILD = '/api/git/build',
  GIT_PROJECT_BUILD_STEP_LOGS = '/api/git/build-step-logs',

  // ------------------------------------ resource ------------------------------------
  RESOURCE_SHOW_TREE = '/api/resource/getResourcesTreeData',
  RESOURCE_GET_CONTENT_BY_ID = '/api/resource/getContentByResourceId',
  RESOURCE_REMOVE = '/api/resource/remove',
  RESOURCE_CREATE_FOLDER = '/api/resource/createFolder',
  RESOURCE_RENAME = '/api/resource/rename',
  RESOURCE_UPLOAD = '/api/resource/uploadFile',
  RESOURCE_SYNC_DATA = '/api/resource/syncRemoteDirectory',

  // ------------------------------------ udf manage ------------------------------------
  UDF_LIST = '/api/udf/list',
  UDF_RESOURCES_LIST = '/api/udf/udfResourcesList',
  UDF_ADD = '/api/udf/addOrUpdateByResourceId',
  UDF_UPDATE = '/api/udf/update',

  // ------------------------------------ udf template ------------------------------------
  UDF_TEMPLATE = '/api/udf/template/list',
  UDF_TEMPLATE_ADD_UPDATE = '/api/udf/template',
  UDF_TEMPLATE_DELETE = '/api/udf/template/delete',
  UDF_TEMPLATE_ENABLE = '/api/udf/template/enable',
  UDF_TEMPLATE_TREE = '/api/udf/template/tree',

  /** --------------------------------------------  setting center ------------------------------------------------ */
  // ------------------------------------ system settings ------------------------------------
  SYSTEM_GET_ALL_CONFIG = '/api/sysConfig/getAll',
  SYSTEM_GET_ONE_TYPE_CONFIG = '/api/sysConfig/getConfigByType',
  SYSTEM_MODIFY_CONFIG = '/api/sysConfig/modifyConfig',

  // ------------------------------------ system log ------------------------------------
  SYSTEM_ROOT_LOG = '/api/system/getRootLog',
  SYSTEM_ROOT_LOG_LIST = '/api/system/listLogDir',
  SYSTEM_ROOT_LOG_READ = '/api/system/readFile',

  // ------------------------------------ system process  ------------------------------------
  PROCESS_LIST = '/api/process/listAllProcess',
  PROCESS_LOG = '/api/process/getProcess',

  /** ------------------------------------------------ Devops center  ------------------------------------ */
  // ------------------------------------ devops job  ------------------------------------
  JOB_INSTANCE = '/api/jobInstance',
  GET_JOB_INSTANCE_BY_TASK_ID = '/api/jobInstance/getJobInstanceByTaskId',
  GET_JOB_BY_ID = '/api/jobInstance/getOneById',
  GET_LATEST_HISTORY_BY_ID = '/api/history/getLatestHistoryById',
  GET_JOB_DETAIL = '/api/jobInstance/getJobInfoDetail',
  REFRESH_JOB_DETAIL = '/api/jobInstance/refreshJobInfoDetail',
  GET_JOBMANAGER_LOG = '/api/jobInstance/getJobManagerLog',
  GET_JOBMANAGER_STDOUT = '/api/jobInstance/getJobManagerStdOut',
  GET_JOBMANAGER_THREAD_DUMP = '/api/jobInstance/getJobManagerThreadDump',
  GET_TASKMANAGER_LIST = '/api/jobInstance/getTaskManagerList',
  GET_TASKMANAGER_LOG = '/api/jobInstance/getTaskManagerLog',
  GET_JOB_METRICS_ITEMS = '/api/jobInstance/getJobMetricsItems',
  JOB_INSTANCE_GET_LINEAGE = '/api/jobInstance/getLineage',
  GET_STATUS_COUNT = '/api/jobInstance/getStatusCount',

  // ------------------------------------ devops studio  ------------------------------------
  STUDIO_GET_LINEAGE = '/api/studio/getLineage',
  STUDIO_GET_MSSCHEMA_INFO = '/api/studio/getMSSchemaInfo',
  STUDIO_GET_MSCATALOGS = '/api/studio/getMSCatalogs',
  STUDIO_GET_MSCOLUMNS = '/api/studio/getMSColumns',

  // ------------------------------------ savepoints  ------------------------------------
  GET_SAVEPOINT_LIST_BY_TASK_ID = '/api/savepoints/listSavepointsByTaskId',
  GET_SAVEPOINT_LIST = '/api/savepoints',

  // ------------------------------------ alert history ------------------------------------
  ALERT_HISTORY_LIST = '/api/alertHistory/list',
  ALERT_HISTORY_DELETE = '/api/alertHistory/delete',

  // ----------------------------------------- ldap ------------------------------------
  GET_LDAP_ENABLE = '/api/ldap/ldapEnableStatus',
  LDAP_TEST_CONNECT = '/api/ldap/testConnection',
  LDAP_TEST_LOGIN = '/api/ldap/testLogin',
  LDAP_LIST_USER = '/api/ldap/listUser',
  LDAP_IMPORT_USERS = '/api/ldap/importUsers',

  // ------------------------------------ home ------------------------------------
  GET_RESOURCE_OVERVIEW = '/api/home/getResourceOverview',
  GET_JOB_STATUS_OVERVIEW = '/api/home/getJobStatusOverview',
  GET_JOB_TYPE_OVERVIEW = '/api/home/getJobTypeOverview',
  GET_JOB_MODEL_OVERVIEW = '/api/home/getJobModelOverview',

  // ------------------------------------ monitor ------------------------------------
  MONITOR_GET_SYSTEM_DATA = '/api/monitor/getSysData',
  MONITOR_GET_FLINK_DATA = '/api/monitor/getFlinkData',
  MONITOR_GET_LAST_DATA = '/api/monitor/getLastUpdateData',
  MONITOR_GET_JVM_INFO = '/api/monitor/getJvmInfo',
  METRICS_LAYOUT_GET_BY_NAME = '/api/monitor/getMetricsLayoutByName',
  METRICS_LAYOUT_DELETE = '/api/monitor/deleteMetricsLayout',
  JOB_METRICS = '/api/monitor/jobMetrics',
  SAVE_FLINK_METRICS = '/api/monitor/saveFlinkMetrics/',
  GET_METRICS_LAYOUT = '/api/monitor/getMetricsLayout',
  GET_JVM_INFO = '/api/monitor/getJvmInfo',

  // ------------------------------------ flink ------------------------------------
  FLINK_PROXY = '/api/flink',
  FLINK_TABLE_DATA = '/api/subscribe/print',

  // ------------------------------------ catalogue ------------------------------------
  DELETE_CATALOGUE_BY_ID_URL = '/api/catalogue/deleteCatalogueById',
  SAVE_OR_UPDATE_TASK_URL = '/api/catalogue/saveOrUpdateCatalogueAndTask',
  SAVE_OR_UPDATE_CATALOGUE_URL = '/api/catalogue/saveOrUpdateCatalogue',
  COPY_TASK_URL = '/api/catalogue/copyTask',
  MOVE_CATALOGUE_URL = '/api/catalogue/moveCatalogue',

  // ------------------------------------ task ------------------------------------
  TASK = '/api/task',
  CANCEL_JOB = '/api/task/cancel',
  JSON_TO_FLINK_SQL = '/api/tools/jsonToFlinkSql',
  EXPLAIN_SQL = '/api/task/explainSql',
  GET_JOB_PLAN = '/api/task/getJobPlan',
  DEBUG_TASK = '/api/task/debugTask',
  SUBMIT_TASK = '/api/task/submitTask',
  CHANGE_TASK_LIFE = '/api/task/changeTaskLife',
  CATALOGUE_GET_CATALOGUE_TREE_DATA = '/api/catalogue/getCatalogueTreeData',
  GET_JOB_VERSION = '/api/task/version',
  RESTART_TASK = '/api/task/restartTask',
  SAVEPOINT = '/api/task/savepoint',
  RESTART_TASK_FROM_CHECKPOINT = '/api/task/selectSavePointRestartTask',
  LIST_FLINK_SQL_ENV = '/api/task/listFlinkSQLEnv',

  // ------------------------------------ task record ------------------------------------
  HISTORY_LIST = '/api/history/list',

  // ------------------------------------ scheduler ------------------------------------
  SCHEDULER_QUERY_UPSTREAM_TASKS = '/api/scheduler/queryUpstreamTasks',
  SCHEDULER_QUERY_TASK_DEFINITION = '/api/scheduler/queryTaskDefinition',
  SCHEDULER_CREATE_OR_UPDATE_TASK_DEFINITION = '/api/scheduler/createOrUpdateTaskDefinition',

  // ------------------------------------ flink conf about ------------------------------------
  READ_CHECKPOINT = '/api/flinkConf/readCheckPoint',
  FLINK_CONF_CONFIG_OPTIONS = '/api/flinkConf/configOptions',

  // ------------------------------------ suggestion ------------------------------------
  SUGGESTION_QUERY_ALL_SUGGESTIONS = '/api/suggestion/queryAllSuggestions'
}
