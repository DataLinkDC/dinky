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
    DataStudioTabsItemType,
    EnvType,
    JobRunningMsgType, MetadataTabsItemType,
    STUDIO_MODEL,
    TabsItemType, TabsPageType
} from '@/pages/DataStudio/model';
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
    })
});

export function isDataStudioTabsItemType(item: DataStudioTabsItemType | MetadataTabsItemType | TabsItemType | undefined): item is DataStudioTabsItemType {
    return item?.type ===  TabsPageType.project;
}

export function isMetadataTabsItemType(item: DataStudioTabsItemType | MetadataTabsItemType | TabsItemType | undefined): item is MetadataTabsItemType {
    return item?.type === TabsPageType.metadata;
}

export function getCurrentTab(panes: TabsItemType[], activeKey: string):  DataStudioTabsItemType | MetadataTabsItemType  | undefined {
    const item = panes.find((item) => item.key === activeKey);
    if (item?.type === 'project') {
        return item as DataStudioTabsItemType;
    }

    if (item?.type === 'metadata') {
        return item as MetadataTabsItemType;
    }

    return undefined;
}

export const getCurrentData = (panes: DataStudioTabsItemType[], activeKey: string): Record<string, any> | undefined => {
  const item = getCurrentTab(panes, activeKey);
  if (isDataStudioTabsItemType(item)) {
      return item.params.taskData;
  }
  return undefined;
};

export const getFooterValue = (panes: any, activeKey: string) => {
    const currentTab = getCurrentTab(panes, activeKey);
    if (isDataStudioTabsItemType(currentTab)) {
        return {
            codePosition: [1, 1],
            codeType: currentTab.params.taskData.dialect
        };
    }
    return {};
};
