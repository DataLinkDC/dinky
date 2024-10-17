import {DataStudioActionType, ToolbarSelect} from "@/pages/DataStudioNew/data.d";
import {Effect, Reducer} from "@@/plugin-dva/types";
import {createModelTypes} from "@/utils/modelUtils";
import {leftDefaultShowTab, ToolbarRoutes} from "@/pages/DataStudioNew/Toolbar/ToolbarRoute";
import {layout} from "@/pages/DataStudioNew/ContentLayout";
import {
  CenterTabDTO,
  FlinkCluster,
  HandleLayoutChangeDTO,
  ProjectDTO,
  ProjectState,
  SaveToolbarLayoutDTO,
  SetLayoutDTO,
  TempData,
  TempDataDTO,
  UpdateActionDTO
} from "@/pages/DataStudioNew/type";
import {LayoutBase} from "rc-dock/src/DockData";
import {getAllPanel} from "@/pages/DataStudioNew/function";
import {ToolbarPosition} from "@/pages/DataStudioNew/Toolbar/data.d";
import {findToolbarPositionByTabId} from "@/pages/DataStudioNew/DockLayoutFunction";
import {EnvType} from "@/pages/DataStudio/model";
import {
  getClusterConfigurationData,
  getEnvData,
  getFlinkConfigs,
  getFlinkUdfOptions,
  getSessionData
} from "@/pages/DataStudio/RightContainer/JobConfig/service";
import {Alert} from "@/types/RegCenter/data";
import {showAlertGroup} from "@/pages/RegCenter/Alert/AlertGroup/service";
import {DefaultOptionType} from "antd/es/select";

export type CenterTabType = "web" | "task"
export type CenterTab = {
  id: string;
  tabType: CenterTabType;
  title: string;
  params: Record<string, any>;
}
export type LayoutState = {
  layoutData: LayoutBase;
  toolbar: {
    showDesc: boolean;
    showActiveTab: boolean;
    leftTop: ToolbarSelect;
    leftBottom: ToolbarSelect;
    right: ToolbarSelect;
    centerContent: ToolbarSelect;
    project: ProjectState
  };
  // 中间内容的tab
  centerContent: {
    tabs: CenterTab[],
    activeTab?: string | undefined
  },
  // 记录按钮操作
  action: {
    // 操作类型
    actionType?: DataStudioActionType,
    // 参数
    params?: Record<string, any>
  },
  tempData: TempData
};


export type StudioModelType = {
  namespace: string;
  state: LayoutState;
  effects: {
    queryFlinkEnv: Effect;
    queryFlinkCluster: Effect;
    queryAlertGroup: Effect;
    queryFlinkConfigOptions: Effect;
    queryFlinkUdfOptions: Effect;
  },
  reducers: {
    // 保存布局
    setLayout: Reducer<LayoutState, SetLayoutDTO>;
    // 监听布局变化
    handleLayoutChange: Reducer<LayoutState, HandleLayoutChangeDTO>;
    // 操作工具栏显示描述
    handleToolbarShowDesc: Reducer<LayoutState>;
    // 保存工具栏布局
    saveToolbarLayout: Reducer<LayoutState, SaveToolbarLayoutDTO>;
    // 添加中间tab
    addCenterTab: Reducer<LayoutState, CenterTabDTO>;
    // 删除中间tab
    removeCenterTab: Reducer<LayoutState>;
    //更新 project
    updateProject: Reducer<LayoutState, ProjectDTO>;
    // 更新操作
    updateAction: Reducer<LayoutState, UpdateActionDTO>;
    saveTempData: Reducer<LayoutState, TempDataDTO>;
  };
}

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
      flinkUdfOptions: []
    }
  },
  effects: {
    * queryFlinkEnv({payload}, {call, put, select}) {
      const tempData: TempData = yield select((state: any) => state.DataStudio.tempData);
      const response: EnvType[] = yield call(getEnvData, payload);
      // 移除数据，并保留当前类别的属性
      yield put({
        type: 'saveTempData',
        payload: {
          ...tempData,
          flinkEnv: response.map(item => ({id: item.id, name: item.name, fragment: item.fragment}))
        }
      });
    },
    * queryFlinkCluster({payload}, {call, put, select}) {
      const tempData: TempData = yield select((state: any) => state.DataStudio.tempData);
      const sessionData: FlinkCluster[] = yield call(getSessionData, payload);
      const clusterConfigurationData: FlinkCluster[] = yield call(getClusterConfigurationData, payload);
      const flinkClusterData = [...sessionData, ...clusterConfigurationData].map(x => ({
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
    * queryAlertGroup({}, {call, put, select}) {
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
    * queryFlinkConfigOptions({}, {call, put, select}) {
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
    * queryFlinkUdfOptions({}, {call, put, select}) {
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
  },
  reducers: {
    setLayout(state, {layout}) {
      return {
        ...state,
        layoutData: layout
      };
    },
    handleLayoutChange(state, {dockLayout, newLayout, currentTabId, direction}) {

      if (direction === 'remove') {
        // 删除工具栏选中
        if (currentTabId) {
          const toolbarPosition = findToolbarPositionByTabId(state.toolbar, currentTabId);
          if (toolbarPosition) {
            state.toolbar[toolbarPosition].allOpenTabs = state.toolbar[toolbarPosition].allOpenTabs?.filter((t) => t !== currentTabId);
            if (state.toolbar[toolbarPosition].currentSelect === currentTabId) {
              state.toolbar[toolbarPosition].currentSelect = undefined;
            }
          } else {
            if (state.centerContent.tabs.map(x => x.id).includes(currentTabId)) {
              // 中间内容
              state.centerContent.tabs = state.centerContent.tabs.filter((x) => x.id !== currentTabId);
              if (state.centerContent.activeTab === currentTabId) {
                state.centerContent.activeTab = state.centerContent.tabs[0]?.id;
              }
              if (state.centerContent.tabs.length === 0) {
                // 进入快速开始界面
                dockLayout.updateTab(currentTabId, {
                  closable: false,
                  id: 'quick-start',
                  title: '快速开始',
                  content: (
                    <></>
                  ),
                  group: 'centerContent'
                }, true);
              }
            }
          }


        }
      } else if (direction === 'active') {
        if (state.centerContent.tabs.map(x => x.id).includes(currentTabId!!)) {
          state.centerContent.activeTab = currentTabId;
        }
      }

      state.toolbar.leftBottom.currentSelect = undefined;
      state.toolbar.right.currentSelect = undefined;
      state.toolbar.leftTop.currentSelect = undefined;
      // 获取所有panel,并更正工具栏的显示
      getAllPanel(newLayout).forEach((panel) => {
        const toolbarPosition = panel.group as ToolbarPosition;
        if (toolbarPosition && (toolbarPosition === 'leftTop' || toolbarPosition === 'leftBottom' || toolbarPosition === 'right')) {
          state.toolbar[toolbarPosition].allOpenTabs = panel.activeId ? [panel.activeId] : [];
          state.toolbar[toolbarPosition].currentSelect = panel.activeId;
        }
      });
      state.layoutData = newLayout;
      return {...state};
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
    // 保存工具栏布局
    saveToolbarLayout(state, {position, list}) {
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
    addCenterTab(state, {id, tabType, title, params}) {
      const newTab = {
        id,
        tabType,
        title,
        params
      };
      let tabs = state.centerContent.tabs;
      if (!state.centerContent.tabs.map(x => x.id).includes(id)) {
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
    removeCenterTab: function (prevState: LayoutState, {id}): LayoutState {
      const tabs = prevState.centerContent.tabs.filter((x) => x.id !== id);

      return {
        ...prevState,
        centerContent: {
          ...prevState.centerContent,
          tabs: tabs,
          activeTab: prevState.centerContent.activeTab === id ? tabs[0]?.id : prevState.centerContent.activeTab
        }
      };
    },
    updateProject(state, {expandKeys, selectedKeys}) {
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
    updateAction(state, {actionType, params}) {
      return {
        ...state,
        action: {
          actionType,
          params
        }
      };
    },
    saveTempData: function (prevState: LayoutState, action: TempDataDTO): LayoutState {
      return {
        ...prevState,
        tempData: action.payload
      }
    }
  }
}

export const [STUDIO_MODEL, STUDIO_MODEL_ASYNC] = createModelTypes(StudioModel);

export default StudioModel;
