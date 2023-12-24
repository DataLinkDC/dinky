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

import {
  DataStudioTabsItemType,
  EnvType,
  FooterType,
  JobRunningMsgType,
  MetadataTabsItemType,
  STUDIO_MODEL,
  STUDIO_MODEL_ASYNC,
  TabsItemType,
  TabsPageType,
  TaskDataType
} from '@/pages/DataStudio/model';
import { CONFIG_MODEL_ASYNC } from '@/pages/SettingCenter/GlobalSetting/model';
import { DIALECT } from '@/services/constants';
import { Cluster, DataSources } from '@/types/RegCenter/data';
import { Dispatch } from '@@/plugin-dva/types';

export const mapDispatchToProps = (dispatch: Dispatch) => ({
  updateToolContentHeight: (key: number) =>
    dispatch({
      type: STUDIO_MODEL.updateToolContentHeight,
      payload: key
    }),
  updateCenterContentHeight: (key: number) =>
    dispatch({
      type: STUDIO_MODEL.updateCenterContentHeight,
      payload: key
    }),
  updateSelectLeftKey: (key: string) =>
    dispatch({
      type: STUDIO_MODEL.updateSelectLeftKey,
      payload: key
    }),
  updateLeftWidth: (width: number) =>
    dispatch({
      type: STUDIO_MODEL.updateLeftWidth,
      payload: width
    }),
  updateSelectRightKey: (key: string) =>
    dispatch({
      type: STUDIO_MODEL.updateSelectRightKey,
      payload: key
    }),
  updateRightWidth: (width: number) =>
    dispatch({
      type: STUDIO_MODEL.updateRightWidth,
      payload: width
    }),
  updateSelectBottomKey: (key: string) =>
    dispatch({
      type: STUDIO_MODEL.updateSelectBottomKey,
      payload: key
    }),
  updateSelectBottomSubKey: (key: string) =>
    dispatch({
      type: STUDIO_MODEL.updateSelectBottomSubKey,
      payload: key
    }),
  updateBottomHeight: (height: number) =>
    dispatch({
      type: STUDIO_MODEL.updateBottomHeight,
      payload: height
    }),
  saveDataBase: (data: DataSources.DataSource[]) =>
    dispatch({
      type: STUDIO_MODEL.saveDataBase,
      payload: data
    }),
  queryDatabaseList: () =>
    dispatch({
      type: STUDIO_MODEL_ASYNC.queryDatabaseList
    }),
  queryTaskData: () => {
    dispatch({
      type: STUDIO_MODEL_ASYNC.queryTaskData
    });
  },
  querySessionData: () => {
    dispatch({
      type: STUDIO_MODEL_ASYNC.querySessionData
    });
  },
  queryEnv: () => {
    dispatch({
      type: STUDIO_MODEL_ASYNC.queryEnv
    });
  },
  queryClusterConfigurationData: () => {
    dispatch({
      type: STUDIO_MODEL_ASYNC.queryClusterConfigurationData
    });
  },

  saveProject: (data: any[]) =>
    dispatch({
      type: STUDIO_MODEL.saveProject,
      payload: data
    }),
  updateBottomConsole: (data: string) =>
    dispatch({
      type: STUDIO_MODEL.updateBottomConsole,
      payload: data
    }),
  saveSession: (data: Cluster.Instance[]) =>
    dispatch({
      type: STUDIO_MODEL.saveSession,
      payload: data
    }),
  saveEnv: (data: EnvType[]) =>
    dispatch({
      type: STUDIO_MODEL.saveEnv,
      payload: data
    }),
  saveTabs: (data: TabsItemType[]) =>
    dispatch({
      type: STUDIO_MODEL.saveTabs,
      payload: data
    }),
  saveClusterConfiguration: (data: Cluster.Config[]) =>
    dispatch({
      type: STUDIO_MODEL.saveClusterConfiguration,
      payload: data
    }),
  updateJobRunningMsg: (data: JobRunningMsgType) =>
    dispatch({
      type: STUDIO_MODEL.updateJobRunningMsg,
      payload: data
    }),
  queryDsConfig: (data: string) =>
    dispatch({
      type: CONFIG_MODEL_ASYNC.queryDsConfig,
      payload: data
    })
});

export function isDataStudioTabsItemType(
  item: DataStudioTabsItemType | MetadataTabsItemType | TabsItemType | undefined
): item is DataStudioTabsItemType {
  return item?.type === TabsPageType.project;
}

export function isMetadataTabsItemType(
  item: DataStudioTabsItemType | MetadataTabsItemType | TabsItemType | undefined
): item is MetadataTabsItemType {
  return item?.type === TabsPageType.metadata;
}

export function getCurrentTab(
  panes: TabsItemType[],
  activeKey: string
): DataStudioTabsItemType | MetadataTabsItemType | undefined {
  const item = panes.find((item) => item.key === activeKey);
  switch (item?.type) {
    case 'project':
      return item as DataStudioTabsItemType;
    case 'metadata':
      return item as MetadataTabsItemType;
    default:
      return undefined;
  }
}

export function isProjectTabs(panes: TabsItemType[], activeKey: string): boolean {
  const item = panes.find((item) => item.key === activeKey);
  switch (item?.type) {
    case 'project':
      return true;
    default:
      return false;
  }
}

export function isShowRightTabsJobConfig(dialect: string): boolean {
  return (
    dialect.toLowerCase() === DIALECT.JAVA ||
    dialect.toLowerCase() === DIALECT.PYTHON_LONG ||
    dialect.toLowerCase() === DIALECT.SCALA ||
    dialect.toLowerCase() === DIALECT.FLINKSQLENV
  );
}

export function getTabByTaskId(
  panes: TabsItemType[],
  id: number
): DataStudioTabsItemType | MetadataTabsItemType | undefined {
  const item = panes.find((item) => item.treeKey === id);
  switch (item?.type) {
    case 'project':
      return item as DataStudioTabsItemType;
    case 'metadata':
      return item as MetadataTabsItemType;
    default:
      return undefined;
  }
}

export const getCurrentData = (
  panes: TabsItemType[],
  activeKey: string
): TaskDataType | undefined => {
  const item = getCurrentTab(panes, activeKey);
  return isDataStudioTabsItemType(item) ? item.params.taskData : undefined;
};

export const getFooterValue = (panes: any, activeKey: string): Partial<FooterType> => {
  const currentTab = getCurrentTab(panes, activeKey);
  return isDataStudioTabsItemType(currentTab)
    ? {
        codePosition: [1, 1],
        codeType: currentTab.subType
      }
    : {};
};
