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

import { ContextMenuPosition } from '@/types/Public/state';

import {LayoutState} from "@/pages/DataStudioNew/model";



export type ToolbarSelect = {
  // 当前选中的tab
  currentSelect?: string;
  // 所有打开的tab
  allOpenTabs: string[];
  allTabs: string[];
};

// 没必要持久化
// 右键状态
export type RightContextMenuState = {
  show: boolean;
  position: ContextMenuPosition;
};

export type RightMenuItemProps = {
  layoutState: LayoutState;
};


export enum DataStudioActionType {
  // project
  PROJECT_COLLAPSE_ALL = 'project-collapse-all',
  PROJECT_EXPAND_ALL = 'project-expand-all',
  PROJECT_RIGHT_CLICK = 'project-right-click',

}
