import {ToolbarSelect} from "@/pages/DataStudioNew/data.d";
import {Reducer} from "@@/plugin-dva/types";
import {createModelTypes} from "@/utils/modelUtils";
import {leftDefaultShowTab, ToolbarRoutes} from "@/pages/DataStudioNew/Toolbar/ToolbarRoute";
import {layout} from "@/pages/DataStudioNew/ContentLayout";
import {
  CenterTabDTO,
  HandleLayoutChangeDTO,
  InitSaveLayoutDTO,
  ProjectDTO,
  ProjectState,
  SaveToolbarLayoutDTO
} from "@/pages/DataStudioNew/type";
import {LayoutBase} from "rc-dock/src/DockData";
import {getAllPanel} from "@/pages/DataStudioNew/function";
import {ToolbarPosition} from "@/pages/DataStudioNew/Toolbar/data.d";
import {findToolbarPositionByTabId} from "@/pages/DataStudioNew/DockLayoutFunction";

export type CenterTabType = "web" | "task"
export type CenterTab = {
  id: string;
  tabType: CenterTabType;
  title: string;
  icon?: any;
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
  }
};


export type StudioModelType = {
  namespace: string;
  state: LayoutState;
  effects: {},
  reducers: {
    // 初始化保存布局
    initSaveLayout: Reducer<LayoutState, InitSaveLayoutDTO>;
    // 监听布局变化
    handleLayoutChange: Reducer<LayoutState, HandleLayoutChangeDTO>;
    // 操作工具栏显示描述
    handleToolbarShowDesc: Reducer<LayoutState>;
    // 保存工具栏布局
    saveToolbarLayout: Reducer<LayoutState, SaveToolbarLayoutDTO>;
    // 添加中间tab
    addCenterTab: Reducer<LayoutState, CenterTabDTO>;
    //更新 project
    updateProject: Reducer<LayoutState, ProjectDTO>;
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
    }
  },
  effects: {},
  reducers: {
    initSaveLayout(state, {dockLayout}) {
      return {
        ...state,
        layoutData: dockLayout.saveLayout()
      }
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
                }, true)
              }
            }
          }


        }
      } else if (direction === 'active') {
        if (state.centerContent.tabs.map(x => x.id).includes(currentTabId!!)) {
          state.centerContent.activeTab = currentTabId;
        }
      }

      state.toolbar.leftBottom.currentSelect=undefined;
      state.toolbar.right.currentSelect=undefined;
      state.toolbar.leftTop.currentSelect=undefined;
      // 获取所有panel,并更正工具栏的显示
      getAllPanel(newLayout).forEach((panel) => {
        const toolbarPosition = panel.group as ToolbarPosition;
        if (toolbarPosition && (toolbarPosition === 'leftTop' || toolbarPosition === 'leftBottom' || toolbarPosition === 'right')) {
          state.toolbar[toolbarPosition].allOpenTabs = panel.activeId ? [panel.activeId] : [];
          state.toolbar[toolbarPosition].currentSelect = panel.activeId;
        }
      })
      state.layoutData = newLayout;
      return {...state}
    },
    // 操作工具栏显示描述
    handleToolbarShowDesc(state, {}) {
      return {
        ...state,
        toolbar: {
          ...state.toolbar,
          showDesc: !state.toolbar.showDesc
        }
      }
    },
    // 保存工具栏布局
    saveToolbarLayout(state, {dockLayout, position, list}) {
      return {
        ...state,
        toolbar: {
          ...state.toolbar,
          [position]: {
            ...state.toolbar[position],
            allTabs: list
          }
        }
      }
    },
    addCenterTab(state, {id, tabType, title, icon}) {
      const newTab = {
        id,
        tabType,
        title,
        icon
      };
      let tabs = state.centerContent.tabs
      if (!state.centerContent.tabs.map(x => x.id).includes(id)) {
        tabs = [newTab, ...state.centerContent.tabs]
      }

      return {
        ...state,
        centerContent: {
          ...state.centerContent,
          tabs: tabs,
          activeTab: id
        }
      }
    },
    updateProject(state, {expandKeys, selectKey}) {
      return {
        ...state,
        toolbar: {
          ...state.toolbar,
          project: {
            expandKeys,
            selectKey
          }
        }
      }
    },
  }
}

export const [STUDIO_MODEL, STUDIO_MODEL_ASYNC] = createModelTypes(StudioModel);

export default StudioModel;
