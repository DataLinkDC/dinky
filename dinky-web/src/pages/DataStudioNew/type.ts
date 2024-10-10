import {DockLayout} from "rc-dock";
import {ToolbarPosition, ToolbarRoute} from "@/pages/DataStudioNew/Toolbar/data.d";
import {AnyAction} from "@@/plugin-dva/types";
import {DropDirection, LayoutBase} from "rc-dock/src/DockData";
import {CenterTab} from "@/pages/DataStudioNew/model";

// dispatch DTO
export interface InitSaveLayoutDTO extends AnyAction {
  dockLayout: DockLayout;
}

export interface HandleLayoutChangeDTO extends AnyAction {
  dockLayout: DockLayout;
  newLayout: LayoutBase;
  currentTabId?: string;
  direction?: DropDirection;
}

export interface PayloadType extends AnyAction {
  dockLayout: DockLayout;
  route: ToolbarRoute;
}

export interface SaveToolbarLayoutDTO extends AnyAction {
  dockLayout: DockLayout;
  position: ToolbarPosition;
  list: string[]
}

export interface CenterTabDTO extends AnyAction, CenterTab {
}

export interface ProjectDTO extends AnyAction, ProjectState {
}



// state
export type ProjectState = {
  expandKeys: number[];
  selectedKeys?: number[]
}
