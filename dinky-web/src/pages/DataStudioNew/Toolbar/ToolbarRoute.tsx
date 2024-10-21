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

import {ToolbarRoute} from '@/pages/DataStudioNew/Toolbar/data.d';
import {
  ConsoleSqlOutlined,
  DatabaseOutlined,
  FunctionOutlined,
  SettingOutlined,
  TableOutlined,
  ToolOutlined
} from '@ant-design/icons';
import React, {lazy, ReactElement, ReactNode, Suspense} from 'react';


export const lazyComponent = (element: ReactNode): ReactElement => {
  return <Suspense fallback={<div>loading...</div>}>{element}</Suspense>
}
const Project = lazy(() => import('@/pages/DataStudioNew/Toolbar/Project'));
const Catalog = lazy(() => import('@/pages/DataStudio/LeftContainer/Catalog'));
const StartIntro = lazy(() => import('@/pages/DataStudioNew/StartIntroPage'));
const DataSource = lazy(() => import('@/pages/DataStudio/LeftContainer/DataSource'));
const GlobalVariable = lazy(() => import('@/pages/DataStudio/LeftContainer/GlobaleVar'));
const Service = lazy(() => import('@/pages/DataStudioNew/Toolbar/Service'));
const Tool = lazy(() => import('@/pages/DataStudioNew/Toolbar/Tool'));
export const ToolbarRoutes: ToolbarRoute[] = [
  {
    key: 'quick-start',
    title: '快速开始',
    icon: <></>,
    position: 'centerContent',
    content: () => lazyComponent(<StartIntro/>)
  },
  {
    key: 'project',
    title: '项目',
    icon: <ConsoleSqlOutlined/>,
    position: 'leftTop',
    content: () =>
      lazyComponent(<Project/>)
  },
  {
    key: 'catalog',
    title: 'Catalog',
    icon: <TableOutlined/>,
    position: 'leftTop',
    content: () => lazyComponent(<Catalog/>)
  },
  {
    key: 'datasource',
    title: '数据源',
    icon: <DatabaseOutlined/>,
    position: 'leftTop',
    content: () => lazyComponent(<DataSource/>)
  },
  {
    key: 'function',
    title: '函数',
    icon: <FunctionOutlined/>,
    position: 'leftTop',
    content: () => lazyComponent(<GlobalVariable/>)
  },
  {
    key: '服务',
    title: '服务',
    icon: <SettingOutlined/>,
    position: 'leftBottom',
    content: () => lazyComponent(<Service/>)
  },
  {
    key: 'tool',
    title: '工具',
    icon: <ToolOutlined/>,
    position: 'leftBottom',
    content: () => lazyComponent(<Tool/>)
  },
];

export const leftDefaultShowTab: ToolbarRoute = ToolbarRoutes[1];

