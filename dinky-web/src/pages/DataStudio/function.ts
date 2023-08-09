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


import {Dispatch} from "@@/plugin-dva/types";
import {Cluster, DataSources} from "@/types/RegCenter/data";
import {DataStudioParams, EnvType, JobRunningMsgType, TabsItemType} from "@/pages/DataStudio/model";

export const mapDispatchToProps = (dispatch: Dispatch) => ({
  updateToolContentHeight: (key: number) => dispatch({
    type: "Studio/updateToolContentHeight",
    payload: key,
  }),
  updateCenterContentHeight: (key: number) => dispatch({
    type: "Studio/updateCenterContentHeight",
    payload: key,
  }),
  updateSelectLeftKey: (key: string) => dispatch({
    type: "Studio/updateSelectLeftKey",
    payload: key,
  }),
  updateLeftWidth: (width: number) => dispatch({
    type: "Studio/updateLeftWidth",
    payload: width,
  }), updateSelectRightKey: (key: string) => dispatch({
    type: "Studio/updateSelectRightKey",
    payload: key,
  }),
  updateRightWidth: (width: number) => dispatch({
    type: "Studio/updateRightWidth",
    payload: width,
  }), updateSelectBottomKey: (key: string) => dispatch({
    type: "Studio/updateSelectBottomKey",
    payload: key,
  }), updateSelectBottomSubKey: (key: string) => dispatch({
    type: "Studio/updateSelectBottomSubKey",
    payload: key,
  }),
  updateBottomHeight: (height: number) => dispatch({
    type: "Studio/updateBottomHeight",
    payload: height,
  }),
  saveDataBase: (data: DataSources.DataSource[]) => dispatch({
    type: "Studio/saveDataBase",
    payload: data,
  }),
  saveProject: (data: any[]) => dispatch({
    type: "Studio/saveProject",
    payload: data,
  }),
  updateBottomConsole: (data: string) => dispatch({
    type: "Studio/updateBottomConsole",
    payload: data,
  }),
  saveSession: (data: Cluster.Instance[]) => dispatch({
    type: "Studio/saveSession",
    payload: data,
  }),
  saveEnv: (data: EnvType[]) => dispatch({
    type: "Studio/saveEnv",
    payload: data,
  }),
  saveTabs: (data: TabsItemType[]) => dispatch({
    type: "Studio/saveTabs",
    payload: data,
  }),
  saveClusterConfiguration: (data: Cluster.Config[]) => dispatch({
    type: "Studio/saveClusterConfiguration",
    payload: data,
  }),
  updateJobRunningMsg: (data: JobRunningMsgType) => dispatch({
    type: "Studio/updateJobRunningMsg",
    payload: data,
  }),

});

export const getCurrentTab = (panes: any, activeKey: string) => {
  return (panes as TabsItemType[]).find(item => item.key === activeKey)
}

export const getCurrentData = (panes: any, activeKey: string) => {
  return (getCurrentTab(panes,activeKey)?.params as DataStudioParams)?.taskData
}



export const getFooterValue = (panes: any, activeKey: string)=>{
  const currentTab = getCurrentTab(panes,activeKey);
  let footerValue:object={};
  if (currentTab&&currentTab.type === "project") {
    footerValue ={
      codePosition: [1, 1],
      codeType: (currentTab.params as DataStudioParams).taskData.dialect,
    }
  }
  return footerValue
}

