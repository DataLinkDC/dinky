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

import { DockLayout } from 'rc-dock';
import { ToolbarPosition, ToolbarRoute } from '@/pages/DataStudio/Toolbar/data.d';
import { AnyAction } from '@@/plugin-dva/types';
import { DropDirection, LayoutBase } from 'rc-dock/src/DockData';
import { CenterTab } from '@/pages/DataStudio/model';
import { DataStudioActionType } from '@/pages/DataStudio/data.d';
import { Alert, DataSources, ResourceInfo } from '@/types/RegCenter/data';
import { DefaultOptionType } from 'antd/es/select';
import { TaskExtConfig } from '@/types/Studio/data';
import { SuggestionInfo } from '@/types/Public/data';
import { JarSubmitParam } from '@/pages/DataStudio/CenterTabContent/SqlTask';

/**
 * @description:
 * zh: 设置布局 DTO
 *  en: Set layout DTO.
 */
export interface SetLayoutDTO extends AnyAction {
  layout: LayoutBase;
}

/**
 * @description:
 *  zh: 保存布局数据 DTO
 *  en: Save layout data DTO.
 */
export interface HandleLayoutChangeDTO extends AnyAction {
  /**
   * @description:
   *  zh: 旧布局数据
   *  en:  old Layout data.
   */
  dockLayout: DockLayout;
  /**
   * @description:
   * zh: 新布局数据
   * en: new Layout data.
   */
  newLayout: LayoutBase;
  /**
   * @description:
   * zh: 当前选中的 tabId
   * en: current selected tabId.
   */
  currentTabId?: string;
  /**
   * @description:
   * zh: 拖拽的方向
   * en: Drag direction.
   */
  direction?: DropDirection;
}

export interface PayloadType extends AnyAction {
  dockLayout: DockLayout;
  route: ToolbarRoute;
}

/**
 * @description:
 * zh: 保存工具栏布局 DTO
 * en: Save toolbar layout DTO.
 */
export interface SaveToolbarLayoutDTO extends AnyAction {
  /**
   * @description:
   * zh: 工具栏布局
   * en: Toolbar layout.
   */
  dockLayout: DockLayout;
  /**
   * @description:
   * zh: 工具栏位置
   * en: Toolbar position.
   */
  position: ToolbarPosition;
  /**
   * @description:
   * zh: 工具栏路由列表
   * en: Toolbar route list.
   */
  list: string[];
}

/**
 * @description:
 *  zh: 中心面板 DTO
 *  en: Center panel DTO.
 */
export interface CenterTabDTO extends AnyAction, CenterTab {}

/**
 * @description:
 * zh: 项目 DTO
 * en: Project DTO.
 */
export interface ProjectDTO extends AnyAction, ProjectState {}

/**
 * @description:
 *   zh: 更新操作 DTO`
 *   en: Update action DTO`
 */
export interface UpdateActionDTO extends AnyAction {
  /**
   * @description:
   * zh: 操作类型
   * en: Action type.
   */
  actionType: DataStudioActionType;
  /**
   * @description:
   * zh: 参数
   * en: Params.
   */
  params: Record<string, any>;
}

/**
 * @description:
 * zh: 临时数据 DTO
 * en: Temporary data DTO.
 */
export interface TempDataDTO extends AnyAction {}

/**
 * @description:
 *  zh: 运行类型
 *  en: Run type.
 */
export type FlinkTaskRunType =
  | 'local'
  | 'standalone'
  | 'yarn-session'
  | 'yarn-per-job'
  | 'yarn-application'
  | 'kubernetes-session'
  | 'kubernetes-application'
  | 'kubernetes-application-operator';

/**
 * @description:
 * zh: 项目状态
 * en: Project state.
 */
export type ProjectState = {
  /**
   * @description:
   *  zh: 展开的 key
   *  en: Expanded key.
   */
  expandKeys: number[];
  /**
   * @description:
   * zh: 选中的 key
   * en: Selected key.
   */
  selectedKeys?: number[];
};

/**
 * @description:
 * zh: 临时数据
 * en: Temporary data.
 */
export type TempData = {
  /**
   * @description:
   * zh: flink env 列表数据
   * en: flink env list data.
   */
  flinkEnv: EnvType[];
  /**
   * @description:
   * zh: flink 集群列表数据
   * en: flink cluster list data.
   */
  flinkCluster: FlinkCluster[];
  /**
   * @description:
   * zh: 告警列表
   * en: Alert list.
   */
  alertGroup: Alert.AlertGroup[];
  /**
   * @description:
   * zh: flink 配置选项
   * en: flink config options.
   */
  flinkConfigOptions: DefaultOptionType[];
  /**
   * @description:
   * zh: flink udf 配置选项
   * en: flink udf config options.
   */
  flinkUdfOptions: DefaultOptionType[];
  /**
   * @description:
   * zh: 数据源列表
   * en: Data source list.
   */
  dataSourceDataList: DataSources.DataSource[];
  suggestions: SuggestionInfo[];
  resourceDataList: ResourceInfo[];
};

/**
 * @description:
 * zh: flink 集群
 * en: flink cluster.
 */
export type FlinkCluster = {
  id: number;
  name: string;
  enabled: boolean;
  type: FlinkTaskRunType;
};

/**
 * @description:
 * zh: 环境类型
 * en: Env type.
 */
export type EnvType = {
  id?: number;
  name?: string;
  fragment?: boolean;
};

export type TaskState = {
  taskId: number;
  statement: string;
  name: string;
  type: FlinkTaskRunType;
  dialect: string;
  envId: number;
  versionId: number;
  savePointStrategy: number;
  savePointPath: string;
  parallelism: number;
  fragment: boolean;
  batchModel: boolean;
  clusterId?: number | null;
  clusterConfigurationId?: number | null;
  databaseId?: number;
  alertGroupId?: number;
  configJson: TaskExtConfig;
  note: string;
  step: number;
  firstLevelOwner: number;
  secondLevelOwners: number[];
  createTime: Date;
  updateTime: Date;
  status: string;
  mockSinkFunction: boolean;
};
/**
 * @description:
 * zh: 任务血缘参数
 * en: Task Lineage params.
 */
export interface StudioLineageParams {
  type: number;
  statementSet: boolean;
  dialect: string;
  databaseId: number;
  statement: string;
  envId: number;
  fragment: boolean;
  variables: any;
  taskId: number;
}
export type TreeVo = {
  name: string;
  value: string;
  children?: TreeVo[];
};

/**
 * job running type msg
 */
export type JobRunningMsgType = {
  taskId: number | null;
  jobName: string;
  jobState: string;
  runningLog: string;
};

export type SqlConvertForm = {
  enable: boolean;
  initSqlStatement?: string;
  jarSubmitParam?: JarSubmitParam;
};
