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
import { createContext, Dispatch, SetStateAction } from 'react';
import { ContextMenuPosition } from '@/types/Public/state';
import { DockLayout, DropDirection, LayoutBase } from 'rc-dock';
import { ToolbarPosition } from '@/pages/DataStudioNew/Toolbar/data.d';
import { BoxBase, PanelBase } from 'rc-dock/es';
import { DataStudioState } from '@/pages/DataStudioNew/model';
import { LayoutData } from 'rc-dock/src/DockData';
import { JOB_STATUS, JOB_SUBMIT_STATUS } from '@/pages/DevOps/constants';
import {
  FileIcon,
  FlinkJarSvg,
  FlinkSQLEnvSvg,
  FlinkSQLSvg,
  JavaSvg,
  LogSvg,
  MarkDownSvg,
  PythonSvg,
  ScalaSvg,
  ShellSvg,
  XMLSvg,
  YAMLSvg
} from '@/components/Icons/CodeLanguageIcon';
import { DIALECT } from '@/services/constants';
import {
  ClickHouseIcons,
  DorisIcons,
  HiveIcons,
  MysqlIcons,
  OracleIcons,
  PhoenixIcons,
  PostgresqlIcons,
  PrestoIcons,
  SQLIcons,
  SqlServerIcons,
  StarRocksIcons
} from '@/components/Icons/DBIcons';
import { CodeTwoTone } from '@ant-design/icons';
import { UserBaseInfo } from '@/types/AuthCenter/data.d';
import { TaskOwnerLockingStrategy } from '@/types/SettingCenter/data.d';

// 遍历layout，获取所有激活和打开的tab
export const getAllPanel = (newLayout: LayoutBase) => {
  return [
    ...getBoxPanels(newLayout.dockbox),
    ...getBoxPanels(newLayout.floatbox),
    ...getBoxPanels(newLayout.maxbox),
    ...getBoxPanels(newLayout.windowbox)
  ];
};

const getBoxPanels = (layout: BoxBase | undefined) => {
  const tabs: PanelBase[] = [];
  if (!layout) {
    return tabs;
  }
  layout.children?.forEach((child) => {
    const panel = child as PanelBase;
    if (panel.tabs) {
      tabs.push(panel);
    } else {
      tabs.push(...getBoxPanels(child as BoxBase));
    }
  });
  return tabs;
};

export const handleRightClick = (
  e: any,
  stateAction: Dispatch<SetStateAction<RightContextMenuState>>
) => {
  let x = e.clientX;
  let y = e.clientY;
  // 判断右键的位置是否超出屏幕 , 如果超出屏幕则设置为屏幕的最大值
  if (x + 180 > window.innerWidth) {
    x = window.innerWidth - 190; // 190 是右键菜单的宽度
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
    case 'centerContent':
      return 'right';
  }
};

export const getLayoutState = (layout: LayoutData, didInit: boolean): LayoutData => {
  if (didInit) {
    return layout;
  }
  let floatbox = layout?.floatbox;
  if (layout?.windowbox?.children) {
    if (floatbox) {
      layout.windowbox.children.forEach((item) => {
        layout.floatbox!!.children.push({ ...item });
      });
    } else {
      floatbox = layout.windowbox;
    }
  }
  return {
    ...layout,
    floatbox,
    windowbox: undefined
  };
};

export function isStatusDone(type: string) {
  if (!type) {
    return true;
  }
  switch (type) {
    case JOB_STATUS.FAILED:
    case JOB_STATUS.CANCELED:
    case JOB_STATUS.FINISHED:
    case JOB_STATUS.UNKNOWN:
    case JOB_SUBMIT_STATUS.SUCCESS:
    case JOB_SUBMIT_STATUS.FAILED:
    case JOB_SUBMIT_STATUS.CANCEL:
      return true;
    default:
      return false;
  }
}
export const getTabIcon = (type: string, size?: number) => {
  if (!type) {
    console.log('type is null');
    return <FileIcon />;
  }

  switch (type.toLowerCase()) {
    case DIALECT.JAVA:
      return <JavaSvg />;
    case DIALECT.SCALA:
      return <ScalaSvg />;
    case DIALECT.PYTHON:
    case DIALECT.PYTHON_LONG:
      return <PythonSvg />;
    case DIALECT.MD:
    case DIALECT.MDX:
      return <MarkDownSvg />;
    case DIALECT.XML:
      return <XMLSvg />;
    case DIALECT.YAML:
    case DIALECT.YML:
      return <YAMLSvg />;
    case DIALECT.SH:
    case DIALECT.BASH:
    case DIALECT.CMD:
      return <ShellSvg />;
    case DIALECT.LOG:
      return <LogSvg />;
    case DIALECT.FLINKJAR:
      return <FlinkJarSvg />;
    case DIALECT.FLINK_SQL:
      return <FlinkSQLSvg />;
    case DIALECT.FLINKSQLENV:
      return <FlinkSQLEnvSvg />;
    case DIALECT.SQL:
      return <SQLIcons size={size} />;
    case DIALECT.MYSQL:
      return <MysqlIcons size={size} />;
    case DIALECT.ORACLE:
      return <OracleIcons size={size} />;
    case DIALECT.POSTGRESQL:
      return <PostgresqlIcons size={size} />;
    case DIALECT.CLICKHOUSE:
      return <ClickHouseIcons size={size} />;
    case DIALECT.SQLSERVER:
      return <SqlServerIcons size={size} />;
    case DIALECT.DORIS:
      return <DorisIcons size={size} />;
    case DIALECT.PHOENIX:
      return <PhoenixIcons size={size} />;
    case DIALECT.HIVE:
      return <HiveIcons size={size} />;
    case DIALECT.STARROCKS:
      return <StarRocksIcons size={size} />;
    case DIALECT.PRESTO:
      return <PrestoIcons size={size} />;
    case DIALECT.TERMINAL:
      return <CodeTwoTone size={size} />;
    default:
      return <FileIcon />;
  }
};

export const getUserName = (id: Number, users: UserBaseInfo.User[] = []) => {
  let name = '';
  const user = users.find((user: UserBaseInfo.User) => user.id === id);
  if (user && user.username) {
    name = user.username;
  }
  return name;
};

/**
 * 构建责任人
 * @param id
 * @param users
 */
export const showFirstLevelOwner = (id: number, users: UserBaseInfo.User[] = []) => {
  return getUserName(id, users);
};

/**
 * 构建维护人
 * @param ids
 * @param users
 */
export const showSecondLevelOwners = (ids: number[], users: UserBaseInfo.User[] = []) => {
  return ids
    ?.map((id: Number) => {
      return getUserName(id, users);
    })
    ?.join();
};

export const lockTask = (
  firstLevelOwner: number,
  secondLevelOwners: number[] = [],
  currentUser: UserBaseInfo.User,
  taskOwnerLockingStrategy: TaskOwnerLockingStrategy
) => {
  if (currentUser?.superAdminFlag) {
    return false;
  }
  const isOwner = currentUser?.id == firstLevelOwner;
  switch (taskOwnerLockingStrategy) {
    case TaskOwnerLockingStrategy.OWNER:
      return !isOwner;
    case TaskOwnerLockingStrategy.OWNER_AND_MAINTAINER:
      return !isOwner && !secondLevelOwners?.includes(currentUser?.id);
    case TaskOwnerLockingStrategy.ALL:
      return false;
    default:
      return false;
  }
};
