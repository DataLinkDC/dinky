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

import {
  AlertGroupState,
  AlertInstanceState,
  AlertTemplateState,
  BuildStepsState,
  ClusterConfigState,
  ClusterInstanceState,
  DocumentState,
  GitProjectState,
  GlobalVarState,
  ResourceState,
  TemplateState
} from '@/types/RegCenter/state';

/**
 * alert group state init
 * @type {{addedAlertGroupOpen: boolean, editAlertGroupOpen: boolean, loading: boolean, value: {}, alertGroupList: any[]}}
 */
export const InitAlertGroupState: AlertGroupState = {
  alertGroupList: [],
  loading: false,
  addedOpen: false,
  editOpen: false,
  value: {}
};

/**
 * alert instance state init
 * @type {{addedAlertInstanceOpen: boolean, alertInstanceList: any[], loading: boolean, editAlertInstanceOpen: boolean, value: {}}}
 */
export const InitAlertInstanceState: AlertInstanceState = {
  alertInstanceList: [],
  loading: false,
  addedOpen: false,
  editOpen: false,
  value: {}
};

/**
 * alert template state init
 */
export const InitAlertTemplateState: AlertTemplateState = {
  alertTemplateList: [],
  loading: false,
  addedOpen: false,
  editOpen: false,
  value: {}
};

/**
 * cluster configuration state init
 * @type {{addedClusterConfigOpen: boolean, loading: boolean, editClusterConfigOpen: boolean, value: {}}}
 */
export const InitClusterConfigState: ClusterConfigState = {
  loading: false,
  addedOpen: false,
  editOpen: false,
  value: {},
  configList: []
};

/**
 * cluster instance state init
 * @type {{loading: boolean, editClusterInstanceOpen: boolean, value: {}, addedClusterInstanceOpen: boolean}}
 */
export const InitClusterInstanceState: ClusterInstanceState = {
  loading: false,
  addedOpen: false,
  editOpen: false,
  value: {},
  instanceList: []
};

/**
 * document state init
 * @type {{addedDocumentOpen: boolean, loading: boolean, editDocumentOpen: boolean, value: {}, drawerOpen: boolean}}
 */
export const InitDocumentState: DocumentState = {
  loading: false,
  addedOpen: false,
  editOpen: false,
  value: {},
  drawerOpen: false
};

/**
 * git project state init
 * @type {{buildOpen: boolean, logOpen: boolean, codeTreeOpen: boolean, addedOpen: boolean, loading: boolean, value: {}, editOpen: boolean}}
 */
export const InitGitProjectState: GitProjectState = {
  loading: false,
  addedOpen: false,
  editOpen: false,
  value: {},
  logOpen: false,
  codeTreeOpen: false,
  buildOpen: false
};

/**
 * git build steps state init
 * @type {{currentStep: number, logRecord: string, percent: number, steps: any[], showListOpen: any}}
 */
export const InitGitBuildStepsState: BuildStepsState = {
  logRecord: '',
  currentStep: 0,
  showListOpen: false,
  percent: 0,
  steps: []
};

/**
 * global variable state init
 * @type {{addedOpen: boolean, loading: boolean, value: {}, drawerOpen: boolean, editOpen: boolean}}
 */
export const InitGlobalVarState: GlobalVarState = {
  loading: false,
  addedOpen: false,
  editOpen: false,
  value: {},
  drawerOpen: false
};

/**
 * resource state init
 * @type {{clickedNode: {}, selectedKeys: any[], contextMenuPosition: {top: number, left: number}, editContent: string, uploadOpen: boolean, rightClickedNode: {}, contextMenuOpen: boolean, value: {}, content: string, editOpen: boolean, treeData: any[]}}
 */
export const InitResourceState: ResourceState = {
  treeData: [],
  content: '',
  clickedNode: {},
  rightClickedNode: {},
  contextMenuOpen: false,
  contextMenuPosition: { top: 0, left: 0 },
  selectedKeys: [],
  editOpen: false,
  editContent: '',
  uploadOpen: false,
  value: {}
};

/**
 * template state init
 * @type {{addedOpen: boolean, loading: boolean, value: {}, drawerOpen: boolean, editOpen: boolean}}
 */
export const InitTemplateState: TemplateState = {
  loading: false,
  addedOpen: false,
  editOpen: false,
  value: {},
  drawerOpen: false
};
