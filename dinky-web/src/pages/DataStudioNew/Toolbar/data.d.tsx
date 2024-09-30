import { JSX } from 'react';
import { ToolbarSelect } from '@/pages/DataStudioNew/data.d';
import * as React from 'react';
import { TabData } from 'rc-dock';

export type ToolbarProp = {
  showDesc: boolean;
  showActiveTab: boolean;
  route: ToolbarRoute[];
  onClick: (route: ToolbarRoute) => void;
  toolbarSelect?: ToolbarSelect;
};

// 位置总共分为 左上 左下 右
export type ToolbarPosition = 'leftTop' | 'leftBottom' | 'right';

export type ToolbarRoute = {
  key: string;
  // 标题
  title: string;
  // 图标
  icon: JSX.Element;
  position: ToolbarPosition;
  content: React.ReactElement | ((tab: TabData) => React.ReactElement);
};
