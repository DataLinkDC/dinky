import {DockLayout} from "rc-dock";
import {ToolbarPosition, ToolbarRoute} from "@/pages/DataStudioNew/Toolbar/data.d";
import {AnyAction} from "@@/plugin-dva/types";
import {DropDirection, LayoutBase} from "rc-dock/src/DockData";
import {CenterTab} from "@/pages/DataStudioNew/model";
import {DataStudioActionType} from "@/pages/DataStudioNew/data.d";
import {Alert} from "@/types/RegCenter/data";
import {DefaultOptionType} from "antd/es/select";

// dispatch DTO
export interface SetLayoutDTO extends AnyAction {
  layout: LayoutBase;
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

export interface UpdateActionDTO extends AnyAction {
  actionType: DataStudioActionType;
  params: Record<string, any>;
}


export interface TempDataDTO extends AnyAction {
}

export type FlinkTaskRunType =
  'local'
  | 'standalone'
  | 'yarn-session'
  | 'yarn-per-job'
  | 'yarn-application'
  | 'kubernetes-session'
  | 'kubernetes-application'
  | 'kubernetes-application-operator'

// state
export type ProjectState = {
  expandKeys: number[];
  selectedKeys?: number[]
}

export type TempData = {
  flinkEnv: EnvType[];
  flinkCluster: FlinkCluster[];
  alertGroup: Alert.AlertGroup[];
  flinkConfigOptions: DefaultOptionType[];
  flinkUdfOptions: DefaultOptionType[];
}
export type FlinkCluster = {
  id: number;
  name: string;
  enabled: boolean;
  type: FlinkTaskRunType;
}
export type EnvType = {
  id?: number;
  name?: string;
  fragment?: boolean;
};



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
