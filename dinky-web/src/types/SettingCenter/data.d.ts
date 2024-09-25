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

export type ProcessSteps = {
  stepStatus: string;
  info: string;
  error: string;
  startTime: Date;
  endTime: Date;
  time: string;
};

export type Process = {
  pid: string;
  name: string;
  taskId: number;
  taskName: string;
  type: string;
  status: string;
  startTime: Date;
  endTime: Date;
  time: string;
  nullProcess: boolean;
  stepIndex: number;
  steps: ProcessSteps[];
  userId: number;
};

export type Rule = {
  ruleKey: string;
  ruleOperator: string;
  ruleValue: number;
};

export type AlertRule = {
  id: number;
  name: string;
  templateId: number;
  description: string;
  ruleType: string;
  triggerConditions: string;
  rule: AlertRuleCondition[];
  enabled: boolean;
};

// [{"ruleKey":"jobStatus","ruleOperator":"EQ","ruleValue":"'FAILED'","rulePriority":"1"}]
export type AlertRuleCondition = {
  ruleKey: string;
  ruleOperator: string;
  ruleValue: string;
  rulePriority?: number;
};

// ============================  System Settings ============================
export type Settings = {
  dolphinscheduler: BaseConfigProperties[];
  env: BaseConfigProperties[];
  flink: BaseConfigProperties[];
  maven: BaseConfigProperties[];
  ldap: BaseConfigProperties[];
  metrics: BaseConfigProperties[];
  resource: BaseConfigProperties[];
};

export type BaseConfigProperties = {
  key: string;
  name: string;
  value: any;
  note: string;
  frontType: string;
  example: string[];
};

export interface LogInfo {
  id: number;
  name: string;
  path: string;
  content: string;
  parentId: number;
  size: number;
  desc: string;
  children: LogInfo[];
  leaf: boolean;
}

/**
 * 任务责任人锁定策略
 */
export enum TaskOwnerLockingStrategy {
  OWNER = 'OWNER', // 只有责任人可以操作
  OWNER_AND_MAINTAINER = 'OWNER_AND_MAINTAINER', // 责任人和维护人都可以操作
  ALL = 'ALL' // 责任人和维护人都可以操作
}

/**
 * 全局配置所有的 key
 */
export enum GLOBAL_SETTING_KEYS {
  SYS_FLINK_SETTINGS_USE_REST_API = 'sys.flink.settings.useRestAPI',
  SYS_FLINK_SETTINGS_JOB_ID_WAIT = 'sys.flink.settings.jobIdWait',
  SYS_MAVEN_SETTINGS_SETTINGS_FILE_PATH = 'sys.maven.settings.settingsFilePath',
  SYS_MAVEN_SETTINGS_REPOSITORY = 'sys.maven.settings.repository',
  SYS_MAVEN_SETTINGS_REPOSITORY_USER = 'sys.maven.settings.repositoryUser',
  SYS_MAVEN_SETTINGS_REPOSITORY_PASSWORD = 'sys.maven.settings.repositoryPassword',
  SYS_ENV_SETTINGS_PYTHON_HOME = 'sys.env.settings.pythonHome',
  SYS_ENV_SETTINGS_DINKY_ADDR = 'sys.env.settings.dinkyAddr',
  SYS_ENV_SETTINGS_JOB_RESEND_DIFF_SECOND = 'sys.env.settings.jobResendDiffSecond',
  SYS_ENV_SETTINGS_DIFF_MINUTE_MAX_SEND_COUNT = 'sys.env.settings.diffMinuteMaxSendCount',
  SYS_ENV_SETTINGS_MAX_RETAIN_DAYS = 'sys.env.settings.maxRetainDays',
  SYS_ENV_SETTINGS_MAX_RETAIN_COUNT = 'sys.env.settings.maxRetainCount',
  SYS_ENV_SETTINGS_EXPRESSION_VARIABLE = 'sys.env.settings.expressionVariable',
  SYS_ENV_SETTINGS_TASK_OWNER_LOCK_STRATEGY = 'sys.env.settings.taskOwnerLockStrategy',
  SYS_DOLPHINSETTINGS_ENABLE = 'sys.dolphinscheduler.settings.enable',
  SYS_DOLPHINSETTINGS_URL = 'sys.dolphinscheduler.settings.url',
  SYS_DOLPHINSETTINGS_TOKEN = 'sys.dolphinscheduler.settings.token',
  SYS_DOLPHINSETTINGS_PROJECTNAME = 'sys.dolphinscheduler.settings.projectName',
  SYS_LDAP_SETTINGS_URL = 'sys.ldap.settings.url',
  SYS_LDAP_SETTINGS_USER_DN = 'sys.ldap.settings.userDn',
  SYS_LDAP_SETTINGS_USER_PASSWORD = 'sys.ldap.settings.userPassword',
  SYS_LDAP_SETTINGS_TIME_LIMIT = 'sys.ldap.settings.timeLimit',
  SYS_LDAP_SETTINGS_BASE_DN = 'sys.ldap.settings.baseDn',
  SYS_LDAP_SETTINGS_FILTER = 'sys.ldap.settings.filter',
  SYS_LDAP_SETTINGS_AUTOLOAD = 'sys.ldap.settings.autoload',
  SYS_LDAP_SETTINGS_DEFAULT_TENANT = 'sys.ldap.settings.defaultTeant',
  SYS_LDAP_SETTINGS_CAST_USERNAME = 'sys.ldap.settings.castUsername',
  SYS_LDAP_SETTINGS_CAST_NICKNAME = 'sys.ldap.settings.castNickname',
  SYS_LDAP_SETTINGS_ENABLE = 'sys.ldap.settings.enable',
  SYS_METRICS_SETTINGS_SYS_ENABLE = 'sys.metrics.settings.sys.enable',
  SYS_METRICS_SETTINGS_SYS_GATHER_TIMING = 'sys.metrics.settings.sys.gatherTiming',
  SYS_METRICS_SETTINGS_FLINK_GATHER_TIMING = 'sys.metrics.settings.flink.gatherTiming',
  SYS_METRICS_SETTINGS_FLINK_GATHER_TIMEOUT = 'sys.metrics.settings.flink.gatherTimeout',
  SYS_RESOURCE_SETTINGS_BASE_ENABLE = 'sys.resource.settings.base.enable',
  SYS_RESOURCE_SETTINGS_BASE_PHYSICAL_DELETION = 'sys.resource.settings.base.physicalDeletion',
  SYS_RESOURCE_SETTINGS_BASE_UPLOAD_BASE_PATH = 'sys.resource.settings.base.upload.base.path',
  SYS_RESOURCE_SETTINGS_BASE_MODEL = 'sys.resource.settings.base.model',
  SYS_RESOURCE_SETTINGS_OSS_ENDPOINT = 'sys.resource.settings.oss.endpoint',
  SYS_RESOURCE_SETTINGS_OSS_ACCESS_KEY = 'sys.resource.settings.oss.accessKey',
  SYS_RESOURCE_SETTINGS_OSS_SECRET_KEY = 'sys.resource.settings.oss.secretKey',
  SYS_RESOURCE_SETTINGS_OSS_BUCKET_NAME = 'sys.resource.settings.oss.bucketName',
  SYS_RESOURCE_SETTINGS_OSS_REGION = 'sys.resource.settings.oss.region',
  SYS_RESOURCE_SETTINGS_OSS_PATH_STYLE_ACCESS = 'sys.resource.settings.oss.pathStyleAccess',
  SYS_RESOURCE_SETTINGS_HDFS_ROOT_USER = 'sys.resource.settings.hdfs.root.user',
  SYS_RESOURCE_SETTINGS_HDFS_FS_DEFAULT_FS = 'sys.resource.settings.hdfs.fs.defaultFS',
  SYS_RESOURCE_SETTINGS_HDFS_CORE_SITE = 'sys.resource.settings.hdfs.core.site',
  SYS_RESOURCE_SETTINGS_HDFS_HDFS_SITE = 'sys.resource.settings.hdfs.hdfs.site'
}
