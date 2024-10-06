import {LayoutData} from "rc-dock";
import {ToolbarSelect} from "@/pages/DataStudioNew/data.d";
import {Reducer} from "@@/plugin-dva/types";
import {layout} from "@/pages/DataStudioNew/ContentLayout";
import {leftDefaultShowTab, toolbarRoutes} from "@/pages/DataStudioNew/Toolbar/ToolbarRoute";
import {createModelTypes} from "@/utils/modelUtils";

export type LayoutState = {
  layoutData: LayoutData;
  toolbar: {
    showDesc: boolean;
    showActiveTab: boolean;
    leftTop: ToolbarSelect;
    leftBottom: ToolbarSelect;
    right: ToolbarSelect;
    centerContent: ToolbarSelect;
  };
};


export type StudioModelType = {
  namespace: string;
  state: LayoutState;
  effects: {},
  reducers: {
    setLayoutData: Reducer<LayoutState>;
    handleToolbarShowDesc: Reducer<LayoutState>;
  };
}

const StudioModel: StudioModelType = {
  namespace: 'data-studio',
  state: {
    layoutData: layout,
    toolbar: {
      showDesc: false,
      leftTop: {
        currentSelect: leftDefaultShowTab.key,
        allOpenTabs: [leftDefaultShowTab.key],
        allTabs: toolbarRoutes.filter((x) => x.position === 'leftTop').map((x) => x.key)
      },
      leftBottom: {
        allTabs: toolbarRoutes.filter((x) => x.position === 'leftBottom').map((x) => x.key)
      },
      right: {
        allTabs: toolbarRoutes.filter((x) => x.position === 'right').map((x) => x.key)
      },
      showActiveTab: false,
      centerContent: {
        currentSelect: undefined,
        allOpenTabs: undefined,
        allTabs: []
      }
    }

  },
  effects: {},
  reducers: {
    setLayoutData(state, {payload}) {
      return {
        ...payload
      }
    },
    handleToolbarShowDesc(state, {}) {
      return {
        ...state,
        toolbar: {
          ...state.toolbar,
          showDesc: !state.toolbar.showDesc
        }
      }
    }
  }
}

export const [STUDIO_MODEL, STUDIO_MODEL_ASYNC] = createModelTypes(StudioModel);

export default StudioModel;
