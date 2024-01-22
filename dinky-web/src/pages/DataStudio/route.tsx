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

import { CircleBottomButtonProps } from '@/components/CallBackButton/CircleBtn';
import Console from '@/pages/DataStudio/BottomContainer/Console';
import JobExecHistory from '@/pages/DataStudio/BottomContainer/JobExecHistory';
import Lineage from '@/pages/DataStudio/BottomContainer/Lineage';
import Result from '@/pages/DataStudio/BottomContainer/Result';
import JsonToSql from '@/pages/DataStudio/BottomContainer/Tools/JsonToSql';
import TextComparison from '@/pages/DataStudio/BottomContainer/Tools/TextComparison';
import { LeftBottomKey, LeftMenuKey, RightMenuKey } from '@/pages/DataStudio/data.d';
import { isSql } from '@/pages/DataStudio/HeaderContainer/function';
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
import { DIALECT } from '@/services/constants';
import { PermissionConstants } from '@/types/Public/constants';
import { l } from '@/utils/intl';
import {
  ApartmentOutlined,
  ConsoleSqlOutlined,
  DatabaseOutlined,
  FolderOutlined,
  FunctionOutlined,
  HistoryOutlined,
  InfoCircleOutlined,
  InsertRowRightOutlined,
  MonitorOutlined,
  RightSquareOutlined,
  RotateRightOutlined,
  SettingOutlined,
  TableOutlined,
  ToolOutlined
} from '@ant-design/icons';
import { TabPaneProps } from 'antd';
import React, { ReactNode } from 'react';

export const LeftSide: TabProp[] = [
  {
    auth: PermissionConstants.DATA_STUDIO_LEFT_PROJECT,
    key: LeftMenuKey.PROJECT_KEY,
    icon: <ConsoleSqlOutlined />,
    label: l(LeftMenuKey.PROJECT_KEY),
    children: <Project />
  },
  {
    auth: PermissionConstants.DATASTUDIO_LEFT_CATALOG,
    key: LeftMenuKey.CATALOG_KEY,
    icon: <TableOutlined />,
    label: l(LeftMenuKey.CATALOG_KEY),
    children: <Catalog />,
    isShow: (type, subType) =>
      (type === TabsPageType.project || type === TabsPageType.metadata) &&
      subType?.toLowerCase() !== DIALECT.FLINKJAR &&
      subType?.toLowerCase() !== DIALECT.FLINKSQLENV &&
      subType?.toLowerCase() !== DIALECT.SCALA &&
      subType?.toLowerCase() !== DIALECT.PYTHON_LONG &&
      subType?.toLowerCase() !== DIALECT.JAVA &&
      subType?.toLowerCase() !== DIALECT.FLINKSQLENV
  },
  {
    auth: PermissionConstants.DATASTUDIO_LEFT_DATASOURCE,
    key: LeftMenuKey.DATASOURCE_KEY,
    icon: <DatabaseOutlined />,
    label: l(LeftMenuKey.DATASOURCE_KEY),
    children: <DataSource />
  },
  {
    auth: PermissionConstants.DATASTUDIO_LEFT_GLOBAL_VARIABLE,
    key: LeftMenuKey.FRAGMENT_KEY,
    icon: <FunctionOutlined />,
    label: l(LeftMenuKey.FRAGMENT_KEY),
    children: <GlobalVariable />,
    isShow: (type, subType) =>
      type === TabsPageType.project &&
      !isSql(subType ?? '') &&
      subType?.toLowerCase() !== DIALECT.SCALA &&
      subType?.toLowerCase() !== DIALECT.PYTHON_LONG &&
      subType?.toLowerCase() !== DIALECT.JAVA
  }
];

export const RightSide: TabProp[] = [
  {
    auth: PermissionConstants.DATASTUDIO_RIGHT_JOB_CONFIG,
    key: RightMenuKey.JOB_CONFIG_KEY,
    icon: <SettingOutlined />,
    label: l(RightMenuKey.JOB_CONFIG_KEY),
    children: <JobConfig />,
    isShow: (type, subType) =>
      type === TabsPageType.project &&
      (TabsPageSubType.flinkSql === subType || TabsPageSubType.flinkJar === subType)
  },
  {
    auth: PermissionConstants.DATASTUDIO_RIGHT_PREVIEW_CONFIG,
    key: RightMenuKey.PREVIEW_CONFIG_KEY,
    icon: <InsertRowRightOutlined />,
    label: l(RightMenuKey.PREVIEW_CONFIG_KEY),
    children: <PreViewConfig />,
    isShow: (type, subType) =>
      (type === TabsPageType.project && TabsPageSubType.flinkSql === subType) ||
      isSql(subType ?? '')
  },
  {
    auth: PermissionConstants.DATASTUDIO_RIGHT_SAVE_POINT,
    key: RightMenuKey.SAVEPOINT_KEY,
    icon: <FolderOutlined />,
    label: l(RightMenuKey.SAVEPOINT_KEY),
    children: <SavePoints />,
    isShow: (type, subType) => type === TabsPageType.project && TabsPageSubType.flinkSql === subType
  },
  {
    auth: PermissionConstants.DATASTUDIO_RIGHT_HISTORY_VISION,
    key: RightMenuKey.HISTORY_VISION_KEY,
    icon: <HistoryOutlined />,
    label: l(RightMenuKey.HISTORY_VISION_KEY),
    children: <HistoryVersion />,
    isShow: (type, subType) => type === TabsPageType.project && TabsPageSubType.flinkSql === subType
  },
  {
    auth: PermissionConstants.DATASTUDIO_RIGHT_JOB_INFO,
    key: RightMenuKey.JOB_INFO_KEY,
    icon: <InfoCircleOutlined />,
    label: l(RightMenuKey.JOB_INFO_KEY),
    children: <JobInfo />,
    isShow: (type) => type === TabsPageType.project
  }
];

export const LeftBottomSide: TabProp[] = [
  {
    auth: PermissionConstants.DATASTUDIO_LEFT_BOTTOM_CONSOLE,
    key: LeftBottomKey.CONSOLE_KEY,
    icon: <RightSquareOutlined />,
    label: l(LeftBottomKey.CONSOLE_KEY),
    children: <Console />,
    isShow: (type, subType) =>
      type === TabsPageType.project &&
      subType?.toLowerCase() !== DIALECT.FLINKSQLENV &&
      subType?.toLowerCase() !== DIALECT.SCALA &&
      subType?.toLowerCase() !== DIALECT.PYTHON_LONG &&
      subType?.toLowerCase() !== DIALECT.JAVA
  },
  {
    auth: PermissionConstants.DATASTUDIO_LEFT_BOTTOM_RESULT,
    key: LeftBottomKey.RESULT_KEY,
    icon: <MonitorOutlined />,
    label: l(LeftBottomKey.RESULT_KEY),
    children: <Result />,
    isShow: (type, subType) =>
      type === TabsPageType.project &&
      subType?.toLowerCase() !== DIALECT.FLINKSQLENV &&
      subType?.toLowerCase() !== DIALECT.FLINKJAR &&
      subType?.toLowerCase() !== DIALECT.SCALA &&
      subType?.toLowerCase() !== DIALECT.PYTHON_LONG &&
      subType?.toLowerCase() !== DIALECT.JAVA
  },
  {
    auth: PermissionConstants.DATASTUDIO_LEFT_BOTTOM_LINEAGE,
    key: LeftBottomKey.LINEAGE_KEY,
    icon: <ApartmentOutlined />,
    label: l(LeftBottomKey.LINEAGE_KEY),
    children: <Lineage />,
    isShow: (type, subType) =>
      type === TabsPageType.project &&
      subType?.toLowerCase() !== DIALECT.FLINKSQLENV &&
      subType?.toLowerCase() !== DIALECT.FLINKJAR &&
      subType?.toLowerCase() !== DIALECT.SCALA &&
      subType?.toLowerCase() !== DIALECT.PYTHON_LONG &&
      subType?.toLowerCase() !== DIALECT.JAVA
  },
  {
    auth: PermissionConstants.DATASTUDIO_LEFT_BOTTOM_HISTORY,
    key: LeftBottomKey.HISTORY_KEY,
    icon: <HistoryOutlined />,
    label: l(LeftBottomKey.HISTORY_KEY),
    children: <JobExecHistory />,
    isShow: (type, subType) =>
      type === TabsPageType.project &&
      !isSql(subType ?? '') &&
      subType?.toLowerCase() !== DIALECT.FLINKSQLENV &&
      subType?.toLowerCase() !== DIALECT.FLINKJAR &&
      subType?.toLowerCase() !== DIALECT.SCALA &&
      subType?.toLowerCase() !== DIALECT.PYTHON_LONG &&
      subType?.toLowerCase() !== DIALECT.JAVA
  },
  {
    auth: PermissionConstants.DATASTUDIO_LEFT_BOTTOM_TABLE_DATA,
    key: LeftBottomKey.TABLE_DATA_KEY,
    icon: <TableOutlined />,
    label: l(LeftBottomKey.TABLE_DATA_KEY),
    children: <></>,
    isShow: (type, subType) =>
      type === TabsPageType.project && subType?.toLowerCase() === DIALECT.FLINK_SQL
  },
  {
    auth: PermissionConstants.DATASTUDIO_LEFT_BOTTOM_TOOL,
    key: LeftBottomKey.TOOLS_KEY,
    icon: <ToolOutlined />,
    label: l(LeftBottomKey.TOOLS_KEY),
    children: <></>
  }
];

export const LeftBottomMoreTabs: { [c: string]: TabProp[] } = {
  'menu.datastudio.tool': [
    {
      key: 'menu.datastudio.tool.text-comparison',
      icon: <ToolOutlined />,
      label: l('menu.datastudio.tool.text-comparison'),
      children: <TextComparison />
    },
    {
      key: 'menu.datastudio.tool.jsonToSql',
      icon: <RotateRightOutlined />,
      label: l('menu.datastudio.tool.jsonToSql'),
      children: <JsonToSql />
    }
  ]
};

export const BottomBtnRoute: { [c: string]: CircleBottomButtonProps[] } = {
  // todo 右边按钮功能有待完善
  // [LeftBottomKey.TABLE_DATA_KEY]: [
  //     {
  //         icon: <PlusOutlined/>,
  //         title: l('test'),
  //         key: 'test',
  //         onClick: async (tabs, key, data, refresh) => {
  //         }
  //     }
  // ]
};

export type TabProp = {
  key: string;
  icon: ReactNode;
  label: string;
  children: ReactNode;
  isShow?: (type: TabsPageType, subType?: TabsPageSubType) => boolean;
  auth?: string;
};

export interface Tab extends Omit<TabPaneProps, 'tab'> {
  key: string;
  label: React.ReactNode;
}
