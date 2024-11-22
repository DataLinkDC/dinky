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

import * as React from 'react';
import { JSX } from 'react';
import { ToolbarSelect } from '@/pages/DataStudio/data.d';
import { TabData } from 'rc-dock';

export type ToolbarProp = {
  showDesc: boolean;
  onClick: (route: ToolbarRoute) => void;
  toolbarSelect: ToolbarSelect;
  position: ToolbarPosition;
  saveToolbarLayout: (position: ToolbarPosition, list: string[]) => void;
  height: number;
};

// 位置总共分为 左上 左下 右
export type ToolbarPosition = 'leftTop' | 'leftBottom' | 'right' | 'centerContent';

export type ToolbarRoute = {
  key: string;
  // 标题
  title: () => string;
  // 图标
  icon: JSX.Element;
  position: ToolbarPosition;
  content: () => React.ReactElement;
};
