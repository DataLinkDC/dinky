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

import { LeftBottomKey, LeftMenuKey } from '@/pages/DataStudio/data.d';
import { getFooterValue, isDataStudioTabsItemType } from '@/pages/DataStudio/function';
import { getDataSourceList } from '@/pages/DataStudio/LeftContainer/DataSource/service';
import { getTaskData } from '@/pages/DataStudio/LeftContainer/Project/service';
import {
  getClusterConfigurationData,
  getEnvData,
  getFlinkConfigs,
  getSessionData,
  querySuggessionData
} from '@/pages/DataStudio/RightContainer/JobConfig/service';
import { QueryParams } from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/data';
import { SuggestionInfo } from '@/types/Public/data';
import { Cluster, DataSources } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import { createModelTypes } from '@/utils/modelUtils';
import { Effect, Reducer } from '@@/plugin-dva/types';
import { Monaco } from '@monaco-editor/react';
import { DefaultOptionType } from 'antd/es/select';
import { editor } from 'monaco-editor';
import React from 'react';
import ICodeEditor = editor.ICodeEditor;

/**
 * 初始化布局宽高度
 */
export const VIEW = {
  headerHeight: 32,
  headerNavHeight: 56,
  footerHeight: 25,
  sideWidth: 40,
  leftToolWidth: 180,
  marginTop: 84,
  topHeight: 35.6,
  bottomHeight: 100,
  rightMargin: 42,
  leftMargin: 42,
  midMargin: 44,
  otherHeight: 0,
  paddingInline: 50
};

export type SqlMetaData = {
  statement?: string;
  metaData?: MetaData[];
};
export type MetaData = {
  table: string;
  connector: string;
  columns: Column[];
};
export type Column = {
  name: string;
  type: string;
};
type TargetKey = React.MouseEvent | React.KeyboardEvent | number;

export type EnvType = {
  id?: number;
  name?: string;
  fragment?: boolean;
};

export type TaskType = {
  id?: number;
  catalogueId?: number;
  name?: string;
  dialect?: string;
  type?: string;
  checkPoint?: number;
  savePointStrategy?: number;
  savePointPath?: string;
  parallelism?: number;
  fragment?: boolean;
  statementSet?: boolean;
  batchModel?: boolean;
  config?: [];
  clusterId?: any;
  clusterName?: string;
  clusterConfigurationId?: number;
  clusterConfigurationName?: string;
  databaseId?: number;
  databaseName?: string;
  envId?: number;
  jobInstanceId?: number;
  note?: string;
  enabled?: boolean;
  createTime?: Date;
  updateTime?: Date;
  statement?: string;
  session: string;
  maxRowNum: number;
  jobName: string;
  useChangeLog: boolean;
  useAutoCancel: boolean;
};

export type ConsoleType = {
  results: {}[];
  // eslint-disable-next-line @typescript-eslint/ban-types
  result: {};
  // eslint-disable-next-line @typescript-eslint/ban-types
  chart: {};
};
export type MetadataParams = {
  queryParams: QueryParams;
  // eslint-disable-next-line @typescript-eslint/ban-types
  tableInfo: {};
};

export type TaskDataBaseType = {
  id: number;
  name: string;
  statement: string;
  dialect: string;
  step: number;
  // Only common sql has(只有普通sql才有)
  databaseId?: number;
  envId?: number;
};

export type TaskDataType = TaskDataBaseType & Record<string, any>;

export type DataStudioParams = {
  taskId: number;
  taskData: TaskDataType;
};

export enum TabsPageType {
  None = '',
  metadata = 'metadata',
  project = 'project'
}

export enum TabsPageSubType {
  flinkSql = 'FlinkSql',
  flinkJar = 'FlinkJar',
  None = ''
}

export interface TabsItemType {
  id: string;
  label: string;
  breadcrumbLabel: string;
  type: TabsPageType;
  subType?: TabsPageSubType;
  key: string;
  treeKey: number;
  value: string;
  icon: any;
  closable: boolean;
  path: string[];
  monacoInstance: React.RefObject<Monaco | undefined>;
  console: ConsoleType;
  isModified: boolean;
}

export interface MetadataTabsItemType extends TabsItemType {
  params: MetadataParams;
  sqlMetaData?: SqlMetaData;
  metaStore?: MetaStoreCatalogType[];
}

export interface DataStudioTabsItemType extends TabsItemType {
  task?: TaskType;
  monaco?: ICodeEditor;
  params: DataStudioParams;
}

export type TabsType = {
  activeKey: string;
  activeBreadcrumbTitle: string;
  selectedStatement?: string;
  panes: TabsItemType[];
};

export type ConnectorType = {
  tableName: string;
};

export type MetaStoreCatalogType = {
  name: string;
  databases: MetaStoreDataBaseType[];
};

export type MetaStoreDataBaseType = {
  name: string;
  tables: MetaStoreTableType[];
  views: string[];
  functions: string[];
  userFunctions: string[];
  modules: string[];
};

export type MetaStoreTableType = {
  name: string;
  columns: MetaStoreColumnType[];
};

export type MetaStoreColumnType = {
  name: string;
  type: string;
};

export type Container = {
  selectKey: string;
  selectSubKey: { [c: string]: string };
  height: number | string;
  width: number | string;
  maxWidth?: number | string;
};

export type BottomContainerContent = {
  console: string;
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

/**
 * footer
 */
export type FooterType = {
  codePosition: [number, number];
  space: number;
  codeEncoding: string;
  lineSeparator: string;
  codeType: string;
  memDetails: string;
  jobRunningMsg: JobRunningMsgType;
};

/**
 * state type overview
 */
export type StateType = {
  isFullScreen: boolean;
  toolContentHeight: number;
  centerContentHeight: number;
  leftContainer: Container;
  rightContainer: Container;
  bottomContainer: Container;
  database: {
    dbData: DataSources.DataSource[];
    selectDatabaseId: number | null;
    expandKeys: [];
    selectKey: [];
  };
  project: {
    data: any[];
    // selectId: number | null;
    expandKeys: [];
    selectKey: [];
  };
  sessionCluster: Cluster.Instance[];
  clusterConfiguration: Cluster.Config[];
  flinkConfigOptions: DefaultOptionType[];
  env: EnvType[];
  tabs: TabsType;
  bottomContainerContent: BottomContainerContent;
  footContainer: FooterType;
  suggestions: SuggestionInfo[];
};

export type ModelType = {
  namespace: string;
  state: StateType;
  effects: {
    queryProject: Effect;
    queryFlinkConfigOptions: Effect;
    querySuggestions: Effect;
    queryEnv: Effect;
    queryDatabaseList: Effect;
    queryTaskData: Effect;
    querySessionData: Effect;
    queryClusterConfigurationData: Effect;
  };
  reducers: {
    updateToolContentHeight: Reducer<StateType>;
    updateCenterContentHeight: Reducer<StateType>;
    updateSelectLeftKey: Reducer<StateType>;
    updateLeftWidth: Reducer<StateType>;
    updateSelectRightKey: Reducer<StateType>;
    updateRightWidth: Reducer<StateType>;
    updateSelectBottomKey: Reducer<StateType>;
    updateSelectBottomSubKey: Reducer<StateType>;
    updateBottomHeight: Reducer<StateType>;
    saveDataBase: Reducer<StateType>;
    saveProject: Reducer<StateType>;
    updateProjectExpandKey: Reducer<StateType>;
    updateProjectSelectKey: Reducer<StateType>;
    updateTabsActiveKey: Reducer<StateType>;
    updateActiveBreadcrumbTitle: Reducer<StateType>;
    closeTab: Reducer<StateType>;
    removeTag: Reducer<StateType>;
    addTab: Reducer<StateType>;
    saveTabs: Reducer<StateType>;
    closeAllTabs: Reducer<StateType>;
    closeOtherTabs: Reducer<StateType>;
    updateSelectDatabaseId: Reducer<StateType>;
    updateDatabaseExpandKey: Reducer<StateType>;
    updateDatabaseSelectKey: Reducer<StateType>;
    updateBottomConsole: Reducer<StateType>;
    saveSession: Reducer<StateType>;
    saveClusterConfiguration: Reducer<StateType>;
    saveEnv: Reducer<StateType>;
    saveFooterValue: Reducer<StateType>;
    updateJobRunningMsg: Reducer<StateType>;
    saveFlinkConfigOptions: Reducer<StateType>;
    updateSuggestions: Reducer<StateType>;
  };
};

const Model: ModelType = {
  namespace: 'Studio',
  state: {
    isFullScreen: false,
    toolContentHeight: 0,
    centerContentHeight: 0,
    leftContainer: {
      selectKey: LeftMenuKey.PROJECT_KEY,
      selectSubKey: {},
      height: '100%',
      width: 260
    },
    rightContainer: {
      selectKey: '',
      selectSubKey: {},
      height: '100%',
      width: 260
    },
    bottomContainer: {
      selectKey: LeftBottomKey.CONSOLE_KEY,
      selectSubKey: {},
      height: 180,
      width: '100%'
    },
    database: {
      dbData: [],
      selectDatabaseId: null,
      expandKeys: [],
      selectKey: []
    },
    project: {
      data: [],
      expandKeys: [],
      selectKey: []
    },
    tabs: {
      activeBreadcrumbTitle: '',
      activeKey: '0',
      panes: []
    },
    bottomContainerContent: {
      console: ''
    },
    sessionCluster: [],
    clusterConfiguration: [],
    flinkConfigOptions: [],
    env: [],
    footContainer: {
      codePosition: [1, 1],
      space: 2,
      codeEncoding: 'UTF-8',
      lineSeparator: 'LF',
      codeType: '',
      memDetails: '100/500M',
      jobRunningMsg: {
        taskId: null,
        jobName: '',
        jobState: '',
        runningLog: ''
      }
    },
    suggestions: []
  },
  effects: {
    *queryProject({ payload }, { call, put }) {
      const response: [] = yield call(getTaskData, payload);
      yield put({
        type: 'saveProject',
        payload: response
      });
    },
    *queryEnv({ payload }, { call, put }) {
      const response: EnvType[] = yield call(getEnvData, payload);
      yield put({
        type: 'saveEnv',
        payload: response
      });
    },
    *queryFlinkConfigOptions({ payload }, { call, put }) {
      const response: [] = yield call(getFlinkConfigs, payload);
      yield put({
        type: 'saveFlinkConfigOptions',
        payload: response
      });
    },
    *querySuggestions({ payload }, { call, put }) {
      const response: SuggestionInfo[] = yield call(querySuggessionData, payload);
      yield put({
        type: 'updateSuggestions',
        payload: response
      });
    },
    *queryDatabaseList({ payload }, { call, put }) {
      const response: DataSources.DataSource[] = yield call(getDataSourceList, payload);
      yield put({
        type: 'saveDataBase',
        payload: response
      });
    },
    *queryTaskData({ payload }, { call, put }) {
      const response: TaskType = yield call(getTaskData, payload);
      yield put({
        type: 'saveProject',
        payload: response
      });
    },
    *querySessionData({ payload }, { call, put }) {
      const response: Cluster.Instance[] = yield call(getSessionData, payload);
      yield put({
        type: 'saveSession',
        payload: response
      });
    },
    *queryClusterConfigurationData({ payload }, { call, put }) {
      const response: Cluster.Config[] = yield call(getClusterConfigurationData, payload);
      yield put({
        type: 'saveClusterConfiguration',
        payload: response
      });
    }
  },
  reducers: {
    /**
     * 更新工具栏高度
     */
    updateToolContentHeight(state, { payload }) {
      if (payload != state.toolContentHeight) {
        return {
          ...state,
          toolContentHeight: payload
        };
      }
      return state;
    },
    /**
     * 更新中间内容高度
     */
    updateCenterContentHeight(state, { payload }) {
      if (payload != state.centerContentHeight) {
        return {
          ...state,
          centerContentHeight: payload
        };
      }
      return state;
    },
    /**
     * 更新左侧选中key
     */
    updateSelectLeftKey(state, { payload }) {
      return {
        ...state,
        leftContainer: {
          ...state.leftContainer,
          selectKey: payload,
          label: payload.trim() === '' ? '' : l(payload)
        }
      };
    },
    /**
     * 更新左侧宽度
     */
    updateLeftWidth(state, { payload }) {
      return {
        ...state,
        leftContainer: {
          ...state.leftContainer,
          width: payload
        }
      };
    },
    /**
     * 更新右侧选中key
     */
    updateSelectRightKey(state, { payload }) {
      return {
        ...state,
        rightContainer: {
          ...state.rightContainer,
          selectKey: payload
        }
      };
    },
    /**
     * 更新右侧选中key
     */
    updateRightWidth(state, { payload }) {
      return {
        ...state,
        rightContainer: {
          ...state.rightContainer,
          width: payload
        }
      };
    },
    /**
     * 更新底部选中key
     */
    updateSelectBottomKey(state, { payload }) {
      let centerContentHeight = 0;
      let toolContentHeight = 0;
      if (payload === '') {
        centerContentHeight = state.centerContentHeight + (state.bottomContainer.height as number);
        toolContentHeight = state.toolContentHeight + (state.bottomContainer.height as number);
      } else if (state.bottomContainer.selectKey !== '') {
        centerContentHeight = state.centerContentHeight;
        toolContentHeight = state.toolContentHeight;
      } else {
        centerContentHeight = state.centerContentHeight - (state.bottomContainer.height as number);
        toolContentHeight = state.toolContentHeight - (state.bottomContainer.height as number);
      }

      return {
        ...state,
        centerContentHeight: centerContentHeight,
        toolContentHeight: toolContentHeight,
        bottomContainer: {
          ...state.bottomContainer,
          selectKey: payload
        }
      };
    },
    updateSelectBottomSubKey(state, { payload }) {
      return {
        ...state,
        bottomContainer: {
          ...state.bottomContainer,
          selectSubKey: {
            ...state.bottomContainer.selectSubKey,
            [state.bottomContainer.selectKey]: payload
          }
        }
      };
    },
    /**
     * 更新底部高度
     */
    updateBottomHeight(state, { payload }) {
      if (payload != state.bottomContainer.height) {
        return {
          ...state,
          bottomContainer: {
            ...state.bottomContainer,
            height: payload
          }
        };
      }
      return state;
    },
    /**
     * 更新数据库列表
     */
    saveDataBase(state, { payload }) {
      return {
        ...state,
        database: { ...state.database, dbData: payload }
      };
    },
    saveProject(state, { payload }) {
      return {
        ...state,
        project: { ...state.project, data: payload }
      };
    },

    updateProjectExpandKey(state, { payload }) {
      return {
        ...state,
        project: { ...state.project, expandKeys: payload }
      };
    },
    updateProjectSelectKey(state, { payload }) {
      return {
        ...state,
        project: { ...state.project, selectKey: payload }
      };
    },

    /**
     * flink config options
     */
    saveFlinkConfigOptions(state, { payload }) {
      return {
        ...state,
        flinkConfigOptions: payload
      };
    },
    /**
     * 更新tabs activeKey
     */
    updateTabsActiveKey(state, { payload }) {
      const itemTypes = state.tabs.panes.filter((x) => x.key === payload);
      if (itemTypes.length === 1) {
        let footerValue: object = getFooterValue(state.tabs.panes, payload);
        const itemType = itemTypes[0];
        return {
          ...state,
          tabs: {
            ...state.tabs,
            activeKey: payload,
            activeBreadcrumbTitle: [itemType.type, itemType.breadcrumbLabel, itemType.label].join(
              '/'
            )
          },
          footContainer: {
            ...state.footContainer,
            ...footerValue
          }
        };
      }
      return {
        ...state,
        tabs: {
          ...state.tabs,
          activeKey: payload
        },
        rightContainer: {
          ...state.rightContainer,
          selectKey: ''
        }
      };
    },
    /**
     * 移除标签
     */
    removeTag(state, { payload }) {
      const needRemoveKey = payload;
      const {
        tabs: { panes, activeKey }
      } = state;

      const index = panes.findIndex((item, index) => {
        if (isDataStudioTabsItemType(item)) {
          return item.params.taskId === needRemoveKey;
        }
        return false;
      });

      // 关闭 传过来的key
      if (index !== -1) {
        panes.splice(index, 1);
      }
      const newActiveKey = activeKey === needRemoveKey ? panes[panes.length - 1].key : activeKey;
      return {
        ...state,
        tabs: {
          ...state.tabs,
          panes: panes,
          activeKey: newActiveKey
        }
      };
    },

    /**
     *  关闭tab
     */
    closeTab(state, { payload }) {
      const needCloseKey = (payload as TargetKey).toString();
      const {
        tabs: { panes, activeKey }
      } = state;
      // close self
      if (needCloseKey === activeKey) {
        for (const [index, pane] of panes.entries()) {
          if (pane.key === needCloseKey) {
            const nextPane = panes[(index + 1) % panes.length];
            const height =
              document.documentElement.clientHeight -
              VIEW.headerHeight -
              VIEW.headerNavHeight -
              VIEW.footerHeight -
              VIEW.otherHeight;
            return {
              ...state,
              tabs: {
                panes: panes.filter((pane) => pane.key !== needCloseKey),
                activeKey: nextPane.key,
                activeBreadcrumbTitle:
                  panes.length < 2
                    ? ''
                    : [nextPane.type, nextPane.breadcrumbLabel, nextPane.label].join('/')
              },
              footContainer: {
                ...state.footContainer,
                ...getFooterValue(panes, nextPane.key)
              },
              toolContentHeight:
                panes.length < 2 ? height - VIEW.leftMargin : state.toolContentHeight,
              centerContentHeight: panes.length < 2 ? height : state.toolContentHeight
            };
          }
        }
      }

      const newPanes = panes.filter((pane) => pane.key !== needCloseKey);
      return {
        ...state,
        tabs: {
          panes: newPanes,
          activeKey: activeKey,
          activeBreadcrumbTitle: state.tabs.activeBreadcrumbTitle
        },
        footContainer: {
          ...state.footContainer,
          ...getFooterValue(newPanes, activeKey)
        }
      };
    },
    updateActiveBreadcrumbTitle(state, { payload }) {
      return {
        ...state,
        tabs: {
          ...state.tabs,
          activeBreadcrumbTitle: payload
        }
      };
    },

    /**
     * 添加tab 如果存在则不添加
     */
    addTab(state, { payload }) {
      const node = payload as TabsItemType;
      for (const item of state.tabs.panes) {
        if (item.id === node.id) {
          let footerValue: object = getFooterValue(state.tabs.panes, item.key);
          return {
            ...state,
            tabs: {
              ...state.tabs,
              activeKey: item.key,
              activeBreadcrumbTitle: [item.type, item.breadcrumbLabel, item.label].join('/')
            },
            footContainer: {
              ...state.footContainer,
              ...footerValue
            }
          };
        }
      }

      node.key =
        state.tabs.panes.length === 0
          ? '0'
          : (parseInt(state.tabs.panes[state.tabs.panes.length - 1].key) + 1).toString();
      const panes = [...state.tabs.panes, node];
      let footerValue: object = getFooterValue(panes, node.key);
      return {
        ...state,
        tabs: {
          panes: panes,
          activeBreadcrumbTitle: [node.type, node.breadcrumbLabel, node.label].join('/'),
          activeKey: node.key
        },
        footContainer: {
          ...state.footContainer,
          ...footerValue
        }
      };
    },

    /**
     * 关闭所有tab
     */
    closeAllTabs(state) {
      return {
        ...state,
        tabs: {
          panes: [],
          activeKey: '',
          activeBreadcrumbTitle: ''
        }
      };
    },
    /**
     * 关闭其他tab
     */
    closeOtherTabs(state, { payload }) {
      // 从 pans 中找到需要关闭的 tab
      const tabsItem = state.tabs.panes.find((pane) => pane.key === payload.key);
      const breadcrumbLabel = tabsItem?.breadcrumbLabel?.split('/') ?? [];
      return {
        ...state,
        tabs: {
          panes: tabsItem ? [tabsItem] : [],
          activeKey: tabsItem?.key ?? '',
          activeBreadcrumbTitle:
            breadcrumbLabel.length > 0
              ? [tabsItem?.type, ...breadcrumbLabel, tabsItem?.label].join('/')
              : ''
        }
      };
    },

    /**
     * 更新选中数据库id
     */
    updateSelectDatabaseId(state, { payload }) {
      return {
        ...state,
        database: { ...state.database, selectDatabaseId: payload }
      };
    },

    /**
     * 更新数据库展开key
     * @param state
     * @param payload
     * @returns {any}
     */
    updateDatabaseExpandKey(state, { payload }) {
      return {
        ...state,
        database: { ...state.database, expandKeys: payload }
      };
    },
    /**
     * 更新数据库选中key
     */
    updateDatabaseSelectKey(state, { payload }) {
      return {
        ...state,
        database: { ...state.database, selectKeys: payload }
      };
    },
    updateBottomConsole(state, { payload }) {
      return {
        ...state,
        bottomContainerContent: {
          ...state.bottomContainerContent,
          console: payload
        }
      };
    },
    saveSession(state, { payload }) {
      return {
        ...state,
        sessionCluster: payload
      };
    },
    saveEnv(state, { payload }) {
      return {
        ...state,
        env: payload
      };
    },
    saveTabs(state, { payload }) {
      return {
        ...state,
        tabs: payload
      };
    },
    saveClusterConfiguration(state, { payload }) {
      return {
        ...state,
        clusterConfiguration: payload
      };
    },
    saveFooterValue(state, { payload }) {
      return {
        ...state,
        footContainer: payload
      };
    },
    updateJobRunningMsg(state, { payload }) {
      return {
        ...state,
        footContainer: {
          ...state.footContainer,
          jobRunningMsg: payload
        }
      };
    },
    updateSuggestions(state, { payload }) {
      return {
        ...state,
        suggestions: payload
      };
    }
  }
};

export const [STUDIO_MODEL, STUDIO_MODEL_ASYNC] = createModelTypes(Model);

export default Model;
