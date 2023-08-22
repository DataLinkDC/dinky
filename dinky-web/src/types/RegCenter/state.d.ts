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

import { BuildStepsState } from '@/pages/RegCenter/GitProject/data.d';
import { Resource } from '@/pages/RegCenter/Resource/components/ResourceOverView';
import { ContextMenuPosition } from '@/types/Public/state';
import {
  Alert,
  Cluster,
  Document,
  GitProject,
  GlobalVar,
  UDFTemplate
} from '@/types/RegCenter/data.d';

/**
 * alert group state
 */
export interface AlertGroupState {
  alertGroupList: Alert.AlertGroup[];
  loading: boolean;
  addedAlertGroupOpen: boolean;
  editAlertGroupOpen: boolean;
  value: Partial<Alert.AlertGroup>;
}

/**
 * alert instance state
 */
export interface AlertInstanceState {
  alertInstanceList: Alert.AlertInstance[];
  loading: boolean;
  addedAlertInstanceOpen: boolean;
  editAlertInstanceOpen: boolean;
  value: Partial<Alert.AlertInstance>;
}

/**
 * cluster configuration state
 */
export interface ClusterConfigState {
  loading: boolean;
  addedClusterConfigOpen: boolean;
  editClusterConfigOpen: boolean;
  value: Partial<Cluster.Config>;
}

/**
 * cluster instance state
 */
export interface ClusterInstanceState {
  loading: boolean;
  addedClusterInstanceOpen: boolean;
  editClusterInstanceOpen: boolean;
  value: Partial<Cluster.Instance>;
}

/**
 * document state
 */
export interface DocumentState {
  addedDocumentOpen: boolean;
  editDocumentOpen: boolean;
  value: Partial<Document>;
  loading: boolean;
  drawerOpen: boolean;
}

/**
 * git project state
 */
export interface GitProjectState {
  loading: boolean;
  addedOpen: boolean;
  editOpen: boolean;
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
export interface GlobalVarState {
  loading: boolean;
  addedOpen: boolean;
  editOpen: boolean;
  value: Partial<GlobalVar>;
  drawerOpen: boolean;
}

/**
 * resource state
 */
export interface ResourceState {
  treeData: any[];
  content: string;
  clickedNode: any;
  rightClickedNode: any;
  contextMenuOpen: boolean;
  contextMenuPosition: ContextMenuPosition;
  selectedKeys: string[];
  editOpen: boolean;
  uploadOpen: boolean;
  value: Partial<Resource>;
}

/**
 * template state
 */
export interface TemplateState {
  loading: boolean;
  addedOpen: boolean;
  editOpen: boolean;
  value: Partial<UDFTemplate>;
  drawerOpen: boolean;
}
