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

import { RightContextMenuState } from '@/pages/DataStudioNew/data.d';
import { Dispatch, SetStateAction } from 'react';
import { ContextMenuPosition } from '@/types/Public/state';
import { DropDirection } from 'rc-dock';
import { ToolbarPosition } from '@/pages/DataStudioNew/Toolbar/data.d';

export const handleRightClick = (
  e: any,
  stateAction: Dispatch<SetStateAction<RightContextMenuState>>
) => {
  let x = e.clientX;
  let y = e.clientY;
  // 判断右键的位置是否超出屏幕 , 如果超出屏幕则设置为屏幕的最大值
  if (x + 150 > window.innerWidth) {
    x = window.innerWidth - 160; // 160 是右键菜单的宽度
  }
  if (y + 200 > window.innerHeight) {
    y = window.innerHeight - 210; // 210 是右键菜单的高度
  }
  stateAction((prevState) => {
    return {
      ...prevState,
      show: true,
      position: {
        top: y + 5,
        left: x + 10
      }
    } as RightContextMenuState;
  });

  e.preventDefault(); // 阻止浏览器默认的右键行为
};

export const InitContextMenuPosition: ContextMenuPosition = {
  left: 0,
  top: 0,
  position: 'fixed',
  cursor: 'pointer',
  width: '12vw',
  zIndex: 1000
};

// 根据工具栏位置获取停靠位置
export const getDockPositionByToolbarPosition = (position: ToolbarPosition): DropDirection => {
  switch (position) {
    case 'leftTop':
      return 'left';
    case 'leftBottom':
      return 'bottom';
    case 'right':
      return 'right';
  }
};
