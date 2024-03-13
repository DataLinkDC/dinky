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

import { SMS_TYPE } from '@/pages/RegCenter/Alert/AlertInstance/constans';
import { Alert, ALERT_TYPE } from '@/types/RegCenter/data.d';
import {
  AlertGroupState,
  AlertInstanceState,
  AlertTemplateState,
  BuildStepsState,
  ClusterConfigState,
  ClusterInstanceState,
  DataSourceState,
  DocumentState,
  GitProjectState,
  GlobalVarState,
  ResourceState,
  TemplateState
} from '@/types/RegCenter/state';
import { randomStr } from '@antfu/utils';

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

export const InitAlertInstanceParamsDingTalk: Alert.AlertInstanceParamsDingTalk = {
  webhook: undefined,
  keyword: undefined,
  secret: undefined,
  isEnableProxy: false,
  isAtAll: false,
  atMobiles: [''],
  proxy: undefined,
  port: undefined,
  user: undefined,
  password: undefined
};
export const InitAlertInstanceParamsFeiShu: Alert.AlertInstanceParamsFeiShu = {
  webhook: undefined,
  keyword: undefined,
  secret: undefined,
  isEnableProxy: false,
  isAtAll: false,
  users: [''],
  proxy: undefined,
  port: undefined,
  user: undefined,
  password: undefined
};

export const InitAlertInstanceParamsEmail: Alert.AlertInstanceParamsEmail = {
  serverHost: undefined,
  serverPort: undefined,
  sender: undefined,
  receivers: [''],
  receiverCcs: [''],
  enableSmtpAuth: false,
  starttlsEnable: false,
  sslEnable: false,
  smtpSslTrust: undefined,
  user: undefined,
  password: undefined
};

export const InitAlertInstanceParamsWeChat: Alert.AlertInstanceParamsWeChat = {
  sendType: 'wechat',
  isAtAll: false,
  webhook: undefined,
  keyword: undefined,
  users: undefined,
  corpId: undefined,
  secret: undefined,
  agentId: undefined
};

export const InitAlertInstanceParamsSms: Alert.AlertInstanceParamsSms = {
  suppliers: SMS_TYPE.ALIBABA,
  accessKeyId: undefined,
  sdkAppId: undefined,
  accessKeySecret: undefined,
  signature: undefined,
  templateId: undefined,
  configId: randomStr(32),
  weight: 1,
  retryInterval: 5,
  maxRetries: 3,
  phoneNumbers: ['']
};

export const InitAlertInstance: Alert.AlertInstance = {
  id: undefined,
  name: undefined,
  type: ALERT_TYPE.DINGTALK,
  params:
    InitAlertInstanceParamsDingTalk ||
    InitAlertInstanceParamsFeiShu ||
    InitAlertInstanceParamsEmail ||
    InitAlertInstanceParamsWeChat ||
    InitAlertInstanceParamsSms,
  enabled: true,
  createTime: undefined,
  updateTime: undefined
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
  value: InitAlertInstance
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

export const InitDataSourceState: DataSourceState = {
  loading: false,
  addedOpen: false,
  editOpen: false,
  value: {},
  isDetailPage: false
};
