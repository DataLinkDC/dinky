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

import { CircleBtn, CircleDataStudioButtonProps } from '@/components/CallBackButton/CircleBtn';
import MovableSidebar, { MovableSidebarProps } from '@/components/Sidebar/MovableSidebar';
import useThemeValue from '@/hooks/useThemeValue';
import { BtnContext } from '@/pages/DataStudio/LeftContainer/BtnContext';
import ProjectTitle from '@/pages/DataStudio/LeftContainer/Project/ProjectTitle';
import { StateType, STUDIO_MODEL, VIEW } from '@/pages/DataStudio/model';
import { LeftSide } from '@/pages/DataStudio/route';
import { connect } from '@@/exports';
import { Tabs } from 'antd';
import React, { useContext } from 'react';

export type LeftContainerProps = {
  size: number;
  leftContainer: StateType['leftContainer'];
  rightContainer: StateType['rightContainer'];
};
const LeftContainer: React.FC<LeftContainerProps> = (props: any) => {
  const {
    dispatch,
    size,
    toolContentHeight,
    leftContainer,
    rightContainer,
    tabs: { panes, activeKey }
  } = props;
  const btn = useContext(BtnContext);
  const themeValue = useThemeValue();
  const MAX_WIDTH = size.width - 2 * VIEW.leftToolWidth - rightContainer.width - 700;
  /**
   * 侧边栏大小变化
   * @param width
   */
  const handleReSizeChange = (width: any) => {
    dispatch({
      type: STUDIO_MODEL.updateLeftWidth,
      payload: width
    });
  };

  /**
   * 侧边栏最小化
   */
  const handleMinimize = () => {
    dispatch({
      type: STUDIO_MODEL.updateSelectLeftKey,
      payload: ''
    });
  };

  /**
   * 侧边栏最大化
   */
  const handleMaxsize = () => {
    handleReSizeChange(MAX_WIDTH);
  };

  /**
   * 侧边栏属性
   * @type {{onResize: (event: any, direction: any, elementRef: {offsetWidth: any}) => void, visible: boolean, defaultSize: {width: any, height: any}, enable: {right: boolean}, minWidth: number, title: string, handlerMinimize: () => void, contentHeight: any, maxWidth: number}}
   */
  const restMovableSidebarProps: MovableSidebarProps = {
    contentHeight: toolContentHeight,
    onResize: (event: any, direction: any, elementRef: { offsetWidth: any }) =>
      handleReSizeChange(elementRef.offsetWidth),
    title: <ProjectTitle />,
    handlerMinimize: () => handleMinimize(),
    handlerMaxsize: handleMaxsize,
    visible: leftContainer.selectKey !== '',
    defaultSize: { width: leftContainer.width, height: leftContainer.height },
    minWidth: 160,
    maxWidth: MAX_WIDTH,
    enable: { right: true },
    btnGroup: btn[leftContainer.selectKey]
      ? btn[leftContainer.selectKey].map((item: CircleDataStudioButtonProps) => (
          <CircleBtn
            title={item.title}
            icon={item.icon}
            onClick={() => item.onClick?.(panes, activeKey)}
            key={item.title}
          />
        ))
      : [],
    style: { borderInlineEnd: `1px solid ${themeValue.borderColor}` }
  };

  const content = (
    <Tabs activeKey={leftContainer.selectKey} items={LeftSide} tabBarStyle={{ display: 'none' }} />
  );

  return (
    <MovableSidebar {...restMovableSidebarProps}>
      {/*<Tabs activeKey={leftContainer.selectKey} items={LeftSide} tabBarStyle={{display: "none"}}/>*/}
      {content}
    </MovableSidebar>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  // leftContainer: Studio.leftContainer,
  // rightContainer: Studio.rightContainer,
  toolContentHeight: Studio.toolContentHeight,
  tabs: Studio.tabs
}))(LeftContainer);
