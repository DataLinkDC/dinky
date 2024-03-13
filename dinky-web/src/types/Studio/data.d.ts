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

import { List } from 'antd';
import { ButtonProps } from 'antd/es/button/button';
import React from 'react';

export type Catalogue = {
  id: number;
  name: string;
  tenantId: number;
  taskId: number;
  type: string;
  parentId: number;
  isLeaf: boolean;
  createTime: Date;
  updateTime: Date;
  children: Catalogue[];
  configJson: TaskExtConfig;
  task: TaskInfo;
};

export type TaskUdfConfig = {
  templateId: number;
  selectKeys: List<string | number>;
  className: string;
};

export type ConfigItem = {
  key: string;
  value: string;
};

export type TaskExtConfig = {
  udfConfig: TaskUdfConfig;
  customConfig: List<Map<string, object>>;
};

export type TaskInfo = {
  id: number;
  name: string;
  dialect: string;
  tenantId: number;
  type: string;
  checkPoint: number;
  savePointStrategy: number;
  savePointPath: string;
  parallelism: number;
  fragment: boolean;
  statementSet: boolean;
  batchModel: boolean;
  clusterId: number;
  clusterConfigurationId: number;
  databaseId: number;
  envId: number;
  enabled: boolean;
  alertGroupId: number;
  note: string;
  step: number;
  jobInstanceId: number;
  versionId: number;
  statement: string;
  clusterName: string;
  savePoints: SavePoint[];
  configJson: TaskExtConfig;
  path: string;
  clusterConfigurationName: string;
  databaseName: string;
  envName: string;
  alertGroupName: string;
  createTime: Date;
  updateTime: Date;
};

export type SavePoint = {
  id: number;
  taskId: number;
  name: string;
  type: string;
  path: string;
  createTime: Date;
};

export type TaskVersionListItem = {
  id: number;
  taskId?: number;
  name?: string;
  dialect?: string;
  type?: string;
  statement: string;
  versionId?: string;
  createTime?: string;
  isLatest?: boolean;
};

export type ClusterConfig = {
  flinkConfigPath: string;
  flinkLibPath: string;
  hadoopConfigPath: string;
  appId: string;
};

export type AppConfig = {
  userJarPath: string;
  userJarParas: string[];
  userJarMainAppClass: string;
};

export type K8sConfig = {
  configuration: Map<string, string>;
  dockerConfig: Map<string, string>;
  kubeConfig: string;
  podTemplate: string;
  jmPodTemplate: string;
  tmPodTemplate: string;
};

export type FlinkConfig = {
  jobName: string;
  jobId: string;
  flinkVersion: string;
  action: ActionType;
  savePointType: SavePointType;
  savePoint: string;
  configuration: AppConfig;
  flinkConfigList: Map<string, string>[];
};

export type GatewayConfig = {
  taskId: number;
  jarPaths: string[];
  type: string;
  clusterConfig: ClusterConfig;
  flinkConfig: FlinkConfig;
  appConfig: AppConfig;
  kubernetesConfig: K8sConfig;
};

export enum ActionType {
  SAVEPOINT = 'savepoint',
  CANCEL = 'cancel'
}
export enum SavePointType {
  TRIGGER = 'trigger',
  DISPOSE = 'dispose',
  STOP = 'stop',
  CANCEL = 'cancel'
}
export enum SavePointStrategy {
  NONE = 0,
  LATEST = 1,
  EARLIEST = 2,
  CUSTOM = 3
}

export type JobConfig = {
  type: string;
  checkpoint: number;
  savepointStrategy: SavePointStrategy;
  savePointPath: string;
  parallelism: number;
  clusterId: number;
  clusterConfigurationId: number;
  step: number;
  configJson: Map<string, string>;
  useChangeLog: boolean;
  useAutoCancel: boolean;
  useRemote: boolean;
  address: string;
  taskId: number;
  jarFiles: string[];
  pyFiles: string[];
  jobName: string;
  fragment: boolean;
  statementSet: boolean;
  batchModel: boolean;
  maxRowNum: number;
  gatewayConfig: GatewayConfig;
  variables: Map<string, string>;
};

export type JobExecutionHistory = {
  id: number;
  clusterId: number;
  clusterConfigurationId: number;
  clusterName: string; // extend
  jobId: string;
  jobName: string;
  jobManagerAddress: string;
  status: number;
  type: string;
  statement: string;
  error: string;
  result: any;
  configJson: JobConfig;
  startTime: Date;
  endTime: Date;
  taskId: number;
};

export enum JobStatus {
  INITIALIZING = 'INITIALIZING',
  CREATED = 'CREATED',
  RUNNING = 'RUNNING',
  FAILING = 'FAILING',
  FAILED = 'FAILED',
  CANCELLING = 'CANCELLING',
  CANCELED = 'CANCELED',
  FINISHED = 'FINISHED',
  RESTARTING = 'RESTARTING',
  SUSPENDED = 'SUSPENDED',
  RECONCILING = 'RECONCILING',
  RECONNECTING = 'RECONNECTING',
  UNKNOWN = 'UNKNOWN'
}

/**
 * DolphinTaskMinInfo
 */
export interface DolphinTaskMinInfo {
  id: number;
  taskName: string;
  taskCode: number;
  taskVersion: number;
  taskType: string;
  taskCreateTime: Date;
  taskUpdateTime: Date;
  processDefinitionCode: number;
  processDefinitionVersion: number;
  processDefinitionName: string;
  processReleaseState: string;
  upstreamTaskMap: Map<number, string>;
  upstreamTaskCode: number;
  upstreamTaskName: string;
}

export interface TaskParamProperty {
  prop: string;
  direct: string;
  type: string;
  value: string;
}

export interface DolphinTaskDefinition {
  id: number;
  code: number;
  name: string;
  version: number;
  description: string;
  projectCode: number;
  userId: number;
  taskType: string;
  taskParams: Map<string, string>;
  taskParamList: TaskParamProperty[];
  taskParamMap: Map<string, string>;
  flag: string; // 0 no 1 yes
  isCache: string; // 0 no 1 yes
  taskPriority: string; // 0 highest 1 high 2 medium 3 low 4 lowest
  userName: string;
  projectName: string;
  workerGroup: string;
  environmentCode: number;
  failRetryTimes: number;
  failRetryInterval: number;
  timeoutFlag: string; // 0 close 1 open
  timeoutNotifyStrategy: string; // 0 warning 1 failure 2 warning and failure
  timeout: number;
  delayTime: number;
  resourceIds: string;
  createTime: Date;
  updateTime: Date;
  modifyBy: string;
  taskGroupId: number;
  taskGroupPriority: number;
  cpuQuota: number;
  memoryMax: number;
  taskExecuteType: number; // 0 batch 1 stream
  processDefinitionCode: number;
  processDefinitionVersion: number;
  processDefinitionName: string;
  upstreamTaskMap: Map<number, string>;
}

export interface PushDolphinParams {
  taskId: number | string;
  upstreamCodes: string[];
  taskPriority: string;
  failRetryTimes: number;
  failRetryInterval: number;
  delayTime: number;
  timeout: number;
  timeoutFlag: boolean | string;
  flag: boolean | string;
  isCache: boolean | string;
  timeoutNotifyStrategy: string[] | string;
  description: string;
}

export type ButtonRoute = {
  icon?: React.ReactNode;
  title?: string;
  click?: () => any;
  hotKey?: (e: KeyboardEvent) => boolean;
  hotKeyDesc?: string;
  isShow?: boolean;
  props?: ButtonProps;
};
