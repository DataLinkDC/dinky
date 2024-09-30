import {ContextMenuPosition} from "@/types/Public/state";
import {ToolbarRoute} from "@/pages/DataStudioNew/Toolbar/data.d";
import {MenuItemType} from "antd/es/menu/interface";
import {useRightMenuItem} from "@/pages/DataStudioNew/RightContextMenu";

export  type  LayoutState={
  toolbar:{
    showDesc:boolean;
    showActiveTab:boolean;
    route:ToolbarRoute[],
    leftTop:ToolbarSelect;
    leftBottom:ToolbarSelect;
    right:ToolbarSelect;
  };
}


export type  ToolbarSelect = {
  // 当前选中的tab
  currentSelect?:string;
  // 所有打开的tab
  allTabs?:Set<string>;
}


// 没必要持久化
// 右键状态
export type RightContextMenuState = {
    show:boolean;
    position:ContextMenuPosition;
}


export type RightMenuItemProps={
  layoutState:LayoutState;
}
