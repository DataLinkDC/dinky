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

import { CircleDataStudioButtonProps } from '@/components/CallBackButton/CircleBtn';
import Console from '@/pages/DataStudio/BottomContainer/Console';
import Lineage from '@/pages/DataStudio/BottomContainer/Lineage';
import Result from '@/pages/DataStudio/BottomContainer/Result';
import TableData from '@/pages/DataStudio/BottomContainer/TableData';
import JsonToSql from '@/pages/DataStudio/BottomContainer/Tools/JsonToSql';
import { isSql } from '@/pages/DataStudio/HeaderContainer/service';
import Catalog from '@/pages/DataStudio/LeftContainer/Catalog';
import DataSource from '@/pages/DataStudio/LeftContainer/DataSource';
import GlobalVariable from '@/pages/DataStudio/LeftContainer/GlobaleVar';
import Project from '@/pages/DataStudio/LeftContainer/Project';
import { TabsPageSubType, TabsPageType } from '@/pages/DataStudio/model';
import HistoryVersion from '@/pages/DataStudio/RightContainer/HistoryVersion';
import JobConfig from '@/pages/DataStudio/RightContainer/JobConfig';
import JobInfo from '@/pages/DataStudio/RightContainer/JobInfo';
import PreViewConfig from '@/pages/DataStudio/RightContainer/PreViewConfig';
import SavePoints from '@/pages/DataStudio/RightContainer/SavePoints';
import { convertCodeEditTheme } from '@/utils/function';
import { l } from '@/utils/intl';
import {
  ApartmentOutlined,
  ArrowsAltOutlined,
  ConsoleSqlOutlined,
  DatabaseOutlined,
  EnvironmentOutlined,
  FolderOutlined,
  FunctionOutlined,
  HistoryOutlined,
  InfoCircleOutlined,
  InsertRowRightOutlined,
  MonitorOutlined,
  PlusCircleOutlined,
  PlusOutlined,
  ReloadOutlined,
  RightSquareOutlined,
  RotateRightOutlined,
  SettingOutlined,
  ShrinkOutlined,
  TableOutlined,
  ToolOutlined
} from '@ant-design/icons';
import { DiffEditor } from '@monaco-editor/react';
import { ReactNode } from 'react';

export const LeftSide = [
  {
    auth: '/datastudio/left/project',
    key: 'menu.datastudio.project',
    icon: <ConsoleSqlOutlined />,
    label: l('menu.datastudio.project'),
    children: <Project />
  },
  {
    auth: '/datastudio/left/catalog',
    key: 'menu.datastudio.catalog',
    icon: <TableOutlined />,
    label: l('menu.datastudio.catalog'),
    children: <Catalog />
  },
  {
    auth: '/datastudio/left/datasource',
    key: 'menu.datastudio.datasource',
    icon: <DatabaseOutlined />,
    label: l('menu.datastudio.datasource'),
    children: <DataSource />
  },
  {
    auth: '/datastudio/left/globalVariable',
    key: 'menu.registration.fragment',
    icon: <FunctionOutlined />,
    label: l('menu.registration.fragment'),
    children: <GlobalVariable />
  }
];

export const RightSide: TabProp[] = [
  {
    auth: '/datastudio/right/jobConfig',
    key: 'menu.datastudio.jobConfig',
    icon: <SettingOutlined />,
    label: l('menu.datastudio.jobConfig'),
    children: <JobConfig />,
    isShow: (type, subType) =>
      type === TabsPageType.project &&
      (TabsPageSubType.flinkSql === subType || TabsPageSubType.flinkJar === subType)
  },
  {
    auth: '/datastudio/right/previewConfig',
    key: 'menu.datastudio.previewConfig',
    icon: <InsertRowRightOutlined />,
    label: l('menu.datastudio.previewConfig'),
    children: <PreViewConfig />,
    isShow: (type, subType) =>
      (type === TabsPageType.project && TabsPageSubType.flinkSql === subType) ||
      isSql(subType ?? '')
  },
  {
    auth: '/datastudio/right/savePoint',
    key: 'menu.datastudio.savePoint',
    icon: <FolderOutlined />,
    label: l('menu.datastudio.savePoint'),
    children: <SavePoints />,
    isShow: (type, subType) => type === TabsPageType.project && TabsPageSubType.flinkSql === subType
  },
  {
    auth: '/datastudio/right/historyVision',
    key: 'menu.datastudio.historyVision',
    icon: <HistoryOutlined />,
    label: l('menu.datastudio.historyVision'),
    children: <HistoryVersion />,
    isShow: (type, subType) => type === TabsPageType.project && TabsPageSubType.flinkSql === subType
  },
  {
    auth: '/datastudio/right/jobInfo',
    key: 'menu.datastudio.jobInfo',
    icon: <InfoCircleOutlined />,
    label: l('menu.datastudio.jobInfo'),
    children: <JobInfo />,
    isShow: (type) => type === TabsPageType.project
  }
];

export const LeftBottomSide = [
  {
    auth: '/datastudio/bottom/console',
    key: 'menu.datastudio.console',
    icon: <RightSquareOutlined />,
    label: l('menu.datastudio.console'),
    children: <Console />
  },
  {
    auth: '/datastudio/bottom/result',
    key: 'menu.datastudio.result',
    icon: <MonitorOutlined />,
    label: l('menu.datastudio.result'),
    children: <Result />
  },
  {
    auth: '/datastudio/bottom/lineage',
    key: 'menu.datastudio.lineage',
    icon: <ApartmentOutlined />,
    label: l('menu.datastudio.lineage'),
    children: <Lineage />
  },
  {
    auth: '/datastudio/bottom/history',
    key: 'menu.datastudio.history',
    icon: <HistoryOutlined />,
    label: l('menu.datastudio.history')
    // children: <JobExecHistory />
  },
  {
    auth: '/datastudio/bottom/table-data',
    key: 'menu.datastudio.table-data',
    icon: <TableOutlined />,
    label: l('menu.datastudio.table-data'),
    children: <TableData />
  },
  {
    auth: '/datastudio/bottom/tool',
    key: 'menu.datastudio.tool',
    icon: <ToolOutlined />,
    label: l('menu.datastudio.tool')
  }
];

export const LeftBottomMoreTabs: { [c: string]: TabProp[] } = {
  'menu.datastudio.tool': [
    {
      key: 'menu.datastudio.tool.text-comparison',
      icon: <ToolOutlined />,
      label: l('menu.datastudio.tool.text-comparison'),
      children: (
        <DiffEditor
          height={'100%'}
          options={{
            readOnly: false,
            originalEditable: true,
            selectOnLineNumbers: true,
            lineDecorationsWidth: 20,
            mouseWheelZoom: true,
            automaticLayout: true,
            scrollBeyondLastLine: false,
            scrollbar: {
              useShadows: false,
              verticalScrollbarSize: 8,
              horizontalScrollbarSize: 8,
              arrowSize: 30
            }
          }}
          language={'text'}
          theme={convertCodeEditTheme()}
        />
      )
    },
    {
      key: 'menu.datastudio.tool.jsonToSql',
      icon: <RotateRightOutlined />,
      label: l('menu.datastudio.tool.jsonToSql'),
      children: <JsonToSql />
    }
  ]
};

// btn route
export const BtnRoute: { [c: string]: CircleDataStudioButtonProps[] } = {
  'menu.datastudio.datasource': [
    {
      icon: <PlusOutlined />,
      title: l('button.create'),
      onClick: () => {}
    },
    {
      icon: <ReloadOutlined />,
      title: l('button.refresh'),
      onClick: () => {}
    }
  ],
  'menu.datastudio.catalog': [
    {
      icon: <ReloadOutlined />,
      title: l('button.refresh'),
      onClick: () => {}
    }
  ],
  'menu.datastudio.project': [
    {
      icon: <PlusCircleOutlined />,
      title: l('right.menu.createRoot'),
      key: 'right.menu.createRoot',
      onClick: () => {}
    },
    {
      icon: <ArrowsAltOutlined />,
      title: l('button.expand-all'),
      key: 'button.expand-all',
      onClick: () => {}
    },
    {
      icon: <ShrinkOutlined />,
      title: l('button.collapse-all'),
      key: 'button.collapse-all',
      onClick: () => {}
    },
    {
      icon: <EnvironmentOutlined />,
      title: l('button.position'),
      key: 'button.position',
      onClick: () => {}
    }
  ]
};

export type TabProp = {
  key: string;
  icon: ReactNode;
  label: string;
  children: ReactNode;
  isShow?: (type: TabsPageType, subType?: TabsPageSubType) => boolean;
  auth?: string;
};
