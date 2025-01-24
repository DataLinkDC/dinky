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

import { ContextMenuPosition } from '@/types/Public/state.d';
import { MenuItemType } from 'antd/es/menu/hooks/useItems';

export interface ProjectState {
  rightActiveKey: string;
  cutId: number | undefined;
  contextMenuPosition: ContextMenuPosition;
  contextMenuOpen: boolean;
  menuItems: MenuItemType[];
  isLeaf: boolean;
  rightClickedNode?: any;
  isCreateSub: boolean;
  isEdit: boolean;
  isRename: boolean;
  isCreateTask: boolean;
  isCut: boolean;
  value: any;
}

export interface CatalogTreeState {
  rightActiveKey: string;
  contextMenuPosition: ContextMenuPosition;
  contextMenuOpen: boolean;
  menuItems: MenuItemType[];
  isLeaf: boolean;
  rightClickedNode?: any;
  value: any;
}
