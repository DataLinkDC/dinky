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

import { InitContextMenuPosition } from '@/types/Public/state.d';
import { PushDolphinParams } from '@/types/Studio/data';
import { CateLogState, ProjectState } from '@/types/Studio/state.d';

export const InitProjectState: ProjectState = {
  rightActiveKey: '',
  cutId: 0,
  contextMenuPosition: InitContextMenuPosition,
  contextMenuOpen: false,
  menuItems: [],
  selectedKeys: [],
  isLeaf: false,
  rightClickedNode: {},
  isCreateSub: false,
  isEdit: false,
  isRename: false,
  isCreateTask: false,
  isCut: false,
  value: {}
};

export const InitPushDolphinParams: PushDolphinParams = {
  taskId: '',
  upstreamCodes: [],
  taskPriority: 'MEDIUM',
  failRetryTimes: 0,
  failRetryInterval: 0,
  delayTime: 0,
  timeout: 30,
  timeoutFlag: false,
  flag: false,
  isCache: false,
  timeoutNotifyStrategy: ['WARN'],
  description: ''
};

export const InitCateLogState: CateLogState = {
  catalog: '',
  catalogSelect: [],
  databaseName: '',
  databaseId: -1,
  tableName: '',
  dialect: '',
  fragment: true,
  envId: -1,
  engine: 'Flink',
  treeData: [],
  modalVisit: false,
  rowData: {},
  loading: false,
  columnData: []
};
