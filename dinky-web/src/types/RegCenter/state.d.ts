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

import { BuildStepsState } from '@/pages/RegCenter/GitProject/data.d';
import { BaseState, ContextMenuPosition } from '@/types/Public/state.d';
import {
  Alert,
  Cluster,
  DataSources,
  Document,
  GitProject,
  GlobalVar,
  ResourceInfo,
  UDFTemplate
} from '@/types/RegCenter/data.d';

/**
 * alert group state
 */
export interface AlertGroupState extends BaseState {
  alertGroupList: Alert.AlertGroup[];
  value: Partial<Alert.AlertGroup>;
}

/**
 * alert instance state
 */
export interface AlertInstanceState extends BaseState {
  alertInstanceList: Alert.AlertInstance[];
  value: Partial<Alert.AlertInstance>;
}

/**
 * alert template state
 */
export interface AlertTemplateState extends BaseState {
  value: Partial<Alert.AlertTemplate>;
}

/**
 * cluster configuration state
 */
export interface ClusterConfigState extends BaseState {
  value: Partial<Cluster.Config>;
  configList: Cluster.Config[];
}

/**
 * cluster instance state
 */
export interface ClusterInstanceState extends BaseState {
  value: Partial<Cluster.Instance>;
  instanceList: Cluster.Instance[];
}

/**
 * document state
 */
export interface DocumentState extends BaseState {
  value: Partial<Document>;
  loading: boolean;
  drawerOpen: boolean;
}

/**
 * git project state
 */
export interface GitProjectState extends BaseState {
  buildOpen: boolean;
  logOpen: boolean;
  codeTreeOpen: boolean;
  value: Partial<GitProject>;
}

/**
 * git build steps state
 */
export interface GitBuildStepsState {
  logRecord: string;
  currentStep: number;
  showListOpen: boolean;
  percent: number;
  steps: BuildStepsState[];
}

/**
 * global variable state
 */
export interface GlobalVarState extends BaseState {
  value: Partial<GlobalVar>;
  drawerOpen: boolean;
}

/**
 * resource state
 */
export interface ResourceState {
  treeData: ResourceInfo[];
  content: string;
  clickedNode: any;
  rightClickedNode: any;
  contextMenuOpen: boolean;
  contextMenuPosition: ContextMenuPosition;
  selectedKeys: string[];
  editOpen: boolean;
  uploadOpen: boolean;
  value: Partial<ResourceInfo>;
}

/**
 * template state
 */
export interface TemplateState extends BaseState {
  value: Partial<UDFTemplate>;
  drawerOpen: boolean;
}

export interface DataSourceState extends BaseState {
  value: Partial<DataSources.DataSource>;
  isDetailPage: boolean;
}
