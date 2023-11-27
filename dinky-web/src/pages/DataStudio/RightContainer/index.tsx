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

import Title from '@/components/Front/Title';
import MovableSidebar from '@/components/Sidebar/MovableSidebar';
import useThemeValue from '@/hooks/useThemeValue';
import { mapDispatchToProps } from '@/pages/DataStudio/function';
import { StateType, VIEW } from '@/pages/DataStudio/model';
import { RightSide } from '@/pages/DataStudio/route';
import { l } from '@/utils/intl';
import { connect } from '@@/exports';
import { Tabs } from 'antd';
import React from 'react';

export type RightContainerProps = {
  size: number;
  bottomHeight: number;
};
const RightContainer: React.FC<RightContainerProps> = (prop: any) => {
  const themeValue = useThemeValue();
  const {
    size,
    leftContainer,
    rightContainer,
    toolContentHeight,
    updateRightWidth,
    updateSelectRightKey,
    tabs
  } = prop;
  const maxWidth = size.width - 2 * VIEW.leftToolWidth - leftContainer.width - 600;
  return (
    <MovableSidebar
      contentHeight={toolContentHeight}
      onResize={(
        event: any,
        direction: any,
        elementRef: {
          offsetWidth: any;
        }
      ) => updateRightWidth(elementRef.offsetWidth)}
      title={<Title>{l(rightContainer.selectKey)}</Title>}
      handlerMinimize={() => updateSelectRightKey('')}
      handlerMaxsize={() => updateRightWidth(maxWidth)}
      visible={rightContainer.selectKey !== ''}
      defaultSize={{
        width: rightContainer.width,
        height: rightContainer.height
      }}
      minWidth={200}
      maxWidth={maxWidth}
      enable={{ left: true }}
      style={{ borderInlineStart: `1px solid ${themeValue.borderColor}` }}
    >
      {tabs.panes.length > 0 ? (
        <Tabs
          activeKey={rightContainer.selectKey}
          items={RightSide}
          tabBarStyle={{ display: 'none' }}
        />
      ) : (
        <> </>
      )}
    </MovableSidebar>
  );
};

export default connect(
  ({ Studio }: { Studio: StateType }) => ({
    leftContainer: Studio.leftContainer,
    rightContainer: Studio.rightContainer,
    bottomContainer: Studio.bottomContainer,
    activeBreadcrumbTitle: Studio.tabs.activeBreadcrumbTitle,
    tabs: Studio.tabs,
    toolContentHeight: Studio.toolContentHeight
  }),
  mapDispatchToProps
)(RightContainer);
