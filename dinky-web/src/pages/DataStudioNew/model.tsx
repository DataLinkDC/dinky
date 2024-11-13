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

import { DataStudioActionType, ToolbarSelect } from '@/pages/DataStudioNew/data.d';
import { Effect, Reducer } from '@@/plugin-dva/types';
import { createModelTypes } from '@/utils/modelUtils';
import { leftDefaultShowTab, ToolbarRoutes } from '@/pages/DataStudioNew/Toolbar/ToolbarRoute';
import { layout } from '@/pages/DataStudioNew/ContentLayout';
import {
  CenterTabDTO,
  FlinkCluster,
  HandleLayoutChangeDTO,
  ProjectDTO,
  ProjectState,
  SaveToolbarLayoutDTO,
  SetLayoutDTO,
  TaskState,
  TempData,
  TempDataDTO,
  UpdateActionDTO
} from '@/pages/DataStudioNew/type';
import { LayoutBase } from 'rc-dock/src/DockData';
import { getAllPanel } from '@/pages/DataStudioNew/function';
import { ToolbarPosition } from '@/pages/DataStudioNew/Toolbar/data.d';
import { findToolbarPositionByTabId } from '@/pages/DataStudioNew/DockLayoutFunction';
import { EnvType } from '@/pages/DataStudioNew/type';
import {
  getClusterConfigurationData,
  getEnvData,
  getFlinkConfigs,
  getFlinkUdfOptions,
  getSessionData,
  querySuggestionData
} from '@/pages/DataStudioNew/service';
import { Alert } from '@/types/RegCenter/data';
import { showAlertGroup } from '@/pages/RegCenter/Alert/AlertGroup/service';
import { DefaultOptionType } from 'antd/es/select';
import { getDataSourceList } from '@/pages/DataStudioNew/Toolbar/DataSource/service';
import { getUserData } from '@/pages/DataStudioNew/service';
import { UserBaseInfo } from '@/types/AuthCenter/data.d';

/**
 * @description:
 *  zh: 中间tab 类型
 *  en: Center tab type
 */
export type CenterTabType = 'web' | 'task' | 'dataSource';

/**
 * @description:
 *  zh: 中间 tab 信息
 *  en: Center tab informations
 */
export type CenterTab = {
  id: string;
  /**
   * zh: tab 类型
   * en: tab Type
   */
  tabType: CenterTabType;
  /**
   * zh: tab 标题
   * en: tab title
   */
  title: string;
  /**
   * zh: tab 是否存在更新
   * en: tab is updated
   */
  isUpdate: boolean;
  /**
   * zh: tab 参数
   * en: tab params
   */
  params: Record<string, any>;
};

/**
 * @description:
 *  zh: 布局状态
 *  en: Layout state
 */
export type DataStudioState = {
  /**
   * zh: 基础布局数据
   * en: Basic layout data
   */
  layoutData: LayoutBase;
  layoutSize: {
    leftTop: number;
    leftBottom: number;
    right: number;
    centerContent?: number | undefined;
  };
  /**
   * zh: 工具栏布局
   * en: Toolbar layouts
   */
  toolbar: {
    /**
     * zh: 是否显示描述
     * en: Whether to show descriptions
     */
    showDesc: boolean;
    /**
     * zh: 是否显示激活的tab
     * en: Whether to show the active tabs
     */
    showActiveTab: boolean;
    /**
     * zh: 左上角的tab
     * en: Left top tab
     */
    leftTop: ToolbarSelect;
    /**
     * zh: 左下角的tab
     * en: Left bottom tab
     */
    leftBottom: ToolbarSelect;
    /**
     * zh: 右边的tab
     * en: Right tabs
     */
    right: ToolbarSelect;
    /**
     * zh: 中间内容
     * en: Center contents
     */
    centerContent: ToolbarSelect;
    /**
     * zh: 项目数据状态
     * en: Project data status
     */
    project: ProjectState;
  };
  theme: {
    // 是否开启紧凑模式
    compact: boolean;
  };

  /**
   * zh: 中间内容 tab 列表
   * en: Center content tab list
   */
  centerContent: {
    /**
     * zh: tab 列表
     * en: Tab list
     */
    tabs: CenterTab[];
    /**
     * zh: 激活的tab
     * en: Active tab
     */
    activeTab?: string | undefined;
  };
  /**
   * zh: 记录按钮操作
   * en: Record button operations
   */
  action: {
    /**
     * zh: 操作类型
     * en: Action type
     */
    actionType?: DataStudioActionType;
    /**
     * zh: 参数
     * en: Params
     */
    params?: Record<string, any> | TaskState;
  };
  /**
   * zh: 临时数据
   * en: Temporary data
   */
  tempData: TempData;
  users: UserBaseInfo.User[];
};

export type StudioModelType = {
  namespace: string;
  state: DataStudioState;
  effects: {
    queryFlinkEnv: Effect;
    queryFlinkCluster: Effect;
    queryAlertGroup: Effect;
    queryFlinkConfigOptions: Effect;
    queryFlinkUdfOptions: Effect;
    queryDataSourceDataList: Effect;
    querySuggestions: Effect;
    queryUserData: Effect;
  };
  reducers: {
    // 保存布局
    setLayout: Reducer<DataStudioState, SetLayoutDTO>;
    // 监听布局变化
    handleLayoutChange: Reducer<DataStudioState, HandleLayoutChangeDTO>;
    // 操作工具栏显示描述
    handleToolbarShowDesc: Reducer<DataStudioState>;
    // 切换紧凑模式
    handleThemeCompact: Reducer<DataStudioState>;
    // 保存工具栏布局
    saveToolbarLayout: Reducer<DataStudioState, SaveToolbarLayoutDTO>;
    // 更新中间tab
    updateCenterTab: Reducer<DataStudioState, CenterTabDTO>;
    // 添加中间tab
    addCenterTab: Reducer<DataStudioState, CenterTabDTO>;
    // 删除中间tab
    removeCenterTab: Reducer<DataStudioState>;
    //更新 project
    updateProject: Reducer<DataStudioState, ProjectDTO>;
    // 更新操作
    updateAction: Reducer<DataStudioState, UpdateActionDTO>;
    saveTempData: Reducer<DataStudioState, TempDataDTO>;
    saveUserData: Reducer<DataStudioState>;
  };
};

const StudioModel: StudioModelType = {
  namespace: 'DataStudio',
  state: {
    layoutData: layout,
    // 工具栏
    toolbar: {
      showDesc: false,
      leftTop: {
        currentSelect: leftDefaultShowTab.key,
        allOpenTabs: [leftDefaultShowTab.key],
        allTabs: ToolbarRoutes.filter((x) => x.position === 'leftTop').map((x) => x.key)
      },
      leftBottom: {
        allTabs: ToolbarRoutes.filter((x) => x.position === 'leftBottom').map((x) => x.key),
        allOpenTabs: []
      },
      right: {
        allTabs: ToolbarRoutes.filter((x) => x.position === 'right').map((x) => x.key),
        allOpenTabs: []
      },
      showActiveTab: false,
      // 这个没有用到，只用到下方
      centerContent: {
        currentSelect: undefined,
        allOpenTabs: [],
        allTabs: []
      },
      project: {
        expandKeys: [],
        selectedKeys: []
      }
    },
    theme: {
      compact: false
    },
    centerContent: {
      tabs: [],
      activeTab: undefined
    },
    action: {
      actionType: undefined,
      params: undefined
    },
    tempData: {
      flinkEnv: [],
      flinkCluster: [],
      alertGroup: [],
      flinkConfigOptions: [],
      flinkUdfOptions: [],
      dataSourceDataList: [],
      suggestions: []
    },
    layoutSize: {
      leftTop: 200,
      leftBottom: 400,
      right: 200
    },
    users: []
  },
  effects: {
    *queryFlinkEnv({ payload }, { call, put, select }) {
      const tempData: TempData = yield select((state: any) => state.DataStudio.tempData);
      const response: EnvType[] = yield call(getEnvData, payload);
      // 移除数据，并保留当前类别的属性
      yield put({
        type: 'saveTempData',
        payload: {
          ...tempData,
          flinkEnv: response.map((item) => ({
            id: item.id,
            name: item.name,
            fragment: item.fragment
          }))
        }
      });
    },
    *queryFlinkCluster({ payload }, { call, put, select }) {
      const tempData: TempData = yield select((state: any) => state.DataStudio.tempData);
      const sessionData: FlinkCluster[] = yield call(getSessionData, payload);
      const clusterConfigurationData: FlinkCluster[] = yield call(
        getClusterConfigurationData,
        payload
      );
      const flinkClusterData = [...sessionData, ...clusterConfigurationData].map((x) => ({
        id: x.id,
        name: x.name,
        enabled: x.enabled,
        type: x.type
      }));
      // 移除数据，并保留当前类别的属性
      yield put({
        type: 'saveTempData',
        payload: {
          ...tempData,
          flinkCluster: flinkClusterData
        }
      });
    },
    *queryAlertGroup({}, { call, put, select }) {
      const tempData: TempData = yield select((state: any) => state.DataStudio.tempData);
      const data: Alert.AlertGroup[] = yield call(showAlertGroup);
      // 移除数据，并保留当前类别的属性
      yield put({
        type: 'saveTempData',
        payload: {
          ...tempData,
          alertGroup: data
        }
      });
    },
    *queryFlinkConfigOptions({}, { call, put, select }) {
      const tempData: TempData = yield select((state: any) => state.DataStudio.tempData);
      const data: DefaultOptionType[] = yield call(getFlinkConfigs);
      // 移除数据，并保留当前类别的属性
      yield put({
        type: 'saveTempData',
        payload: {
          ...tempData,
          flinkConfigOptions: data
        }
      });
    },
    *queryFlinkUdfOptions({}, { call, put, select }) {
      const tempData: TempData = yield select((state: any) => state.DataStudio.tempData);
      const data: [] = yield call(getFlinkUdfOptions);

      // 移除数据，并保留当前类别的属性
      yield put({
        type: 'saveTempData',
        payload: {
          ...tempData,
          flinkUdfOptions: data
        }
      });
    },
    *queryDataSourceDataList({}, { call, put, select }) {
      const tempData: TempData = yield select((state: any) => state.DataStudio.tempData);
      const data: [] = yield call(getDataSourceList);

      // 移除数据，并保留当前类别的属性
      yield put({
        type: 'saveTempData',
        payload: {
          ...tempData,
          dataSourceDataList: data
        }
      });
    },
    *querySuggestions({}, { call, put, select }) {
      const tempData: TempData = yield select((state: any) => state.DataStudio.tempData);
      const data: [] = yield call(querySuggestionData, { enableSchemaSuggestions: false });

      // 移除数据，并保留当前类别的属性
      yield put({
        type: 'saveTempData',
        payload: {
          ...tempData,
          suggestions: data
        }
      });
    },
    *queryUserData({ payload }, { call, put }) {
      const response: [] = yield call(getUserData, payload);
      yield put({
        type: 'saveUserData',
        payload: response
      });
    }
  },
  reducers: {
    setLayout(state, { layout }) {
      return {
        ...state,
        layoutData: layout
      };
    },
    handleLayoutChange(state, { dockLayout, newLayout, currentTabId, direction }) {
      if (direction === 'remove') {
        // 删除工具栏选中
        if (currentTabId) {
          const toolbarPosition = findToolbarPositionByTabId(state.toolbar, currentTabId);
          if (toolbarPosition) {
            state.toolbar[toolbarPosition].allOpenTabs = state.toolbar[
              toolbarPosition
            ].allOpenTabs?.filter((t) => t !== currentTabId);
            if (state.toolbar[toolbarPosition].currentSelect === currentTabId) {
              state.toolbar[toolbarPosition].currentSelect = undefined;
            }
            state.layoutSize[toolbarPosition] = dockLayout.find(currentTabId)?.parent?.size ?? 200;
          } else {
            if (state.centerContent.tabs.map((x) => x.id).includes(currentTabId)) {
              // 中间内容
              state.centerContent.tabs = state.centerContent.tabs.filter(
                (x) => x.id !== currentTabId
              );
              if (state.centerContent.activeTab === currentTabId) {
                state.centerContent.activeTab = state.centerContent.tabs[0]?.id;
              }
              if (state.centerContent.tabs.length === 0) {
                // 进入快速开始界面
                dockLayout.updateTab(
                  currentTabId,
                  {
                    closable: false,
                    id: 'quick-start',
                    title: '快速开始',
                    content: <></>,
                    group: 'centerContent'
                  },
                  true
                );
              }
            }
          }
        }
      } else if (direction === 'active') {
        if (state.centerContent.tabs.map((x) => x.id).includes(currentTabId!!)) {
          state.centerContent.activeTab = currentTabId;
        }
      }

      state.toolbar.leftBottom.currentSelect = undefined;
      state.toolbar.right.currentSelect = undefined;
      state.toolbar.leftTop.currentSelect = undefined;
      // 获取所有panel,并更正工具栏的显示
      getAllPanel(newLayout).forEach((panel) => {
        const toolbarPosition = panel.group as ToolbarPosition;
        if (
          toolbarPosition &&
          (toolbarPosition === 'leftTop' ||
            toolbarPosition === 'leftBottom' ||
            toolbarPosition === 'right')
        ) {
          state.toolbar[toolbarPosition].allOpenTabs = panel.activeId ? [panel.activeId] : [];
          state.toolbar[toolbarPosition].currentSelect = panel.activeId;
        }
      });
      state.layoutData = newLayout;
      return { ...state };
    },
    // 操作工具栏显示描述
    handleToolbarShowDesc(state, {}) {
      return {
        ...state,
        toolbar: {
          ...state.toolbar,
          showDesc: !state.toolbar.showDesc
        }
      };
    },
    handleThemeCompact(state, {}) {
      return {
        ...state,
        theme: {
          ...state.theme,
          compact: !state.theme.compact
        }
      };
    },
    // 保存工具栏布局
    saveToolbarLayout(state, { position, list }) {
      return {
        ...state,
        toolbar: {
          ...state.toolbar,
          [position]: {
            ...state.toolbar[position],
            allTabs: list
          }
        }
      };
    },
    updateCenterTab(state, { id, tabType, title, params, isUpdate }) {
      return {
        ...state,
        centerContent: {
          ...state.centerContent,
          tabs: state.centerContent.tabs.map((x) => {
            if (x.id === id) {
              return {
                id,
                tabType,
                title,
                params,
                isUpdate
              };
            } else {
              return x;
            }
          })
        }
      };
    },
    addCenterTab(state, { id, tabType, title, params }) {
      const newTab = {
        id,
        tabType,
        title,
        params,
        isUpdate: false
      };
      let tabs = state.centerContent.tabs;
      if (!state.centerContent.tabs.map((x) => x.id).includes(id)) {
        tabs = [newTab, ...state.centerContent.tabs];
      }
      return {
        ...state,
        centerContent: {
          ...state.centerContent,
          tabs: tabs,
          activeTab: id
        }
      };
    },
    removeCenterTab: function (prevState: DataStudioState, { id }): DataStudioState {
      const tabs = prevState.centerContent.tabs.filter((x) => x.id !== id);

      return {
        ...prevState,
        centerContent: {
          ...prevState.centerContent,
          tabs: tabs,
          activeTab:
            prevState.centerContent.activeTab === id
              ? tabs[0]?.id
              : prevState.centerContent.activeTab
        }
      };
    },
    updateProject(state, { expandKeys, selectedKeys }) {
      return {
        ...state,
        toolbar: {
          ...state.toolbar,
          project: {
            expandKeys: expandKeys ?? state.toolbar.project.expandKeys,
            selectedKeys: selectedKeys ?? state.toolbar.project.selectedKeys
          }
        }
      };
    },
    updateAction(state, { actionType, params }) {
      return {
        ...state,
        action: {
          actionType,
          params
        }
      };
    },
    saveTempData: function (prevState: DataStudioState, action: TempDataDTO): DataStudioState {
      return {
        ...prevState,
        tempData: action.payload
      };
    },
    saveUserData(state, { payload }) {
      const users = payload.users.filter((user: UserBaseInfo.User) => {
        return payload.userIds.includes(user.id);
      });
      return {
        ...state,
        users: users
      };
    }
  }
};

export const [STUDIO_MODEL, STUDIO_MODEL_ASYNC] = createModelTypes(StudioModel);

export default StudioModel;
