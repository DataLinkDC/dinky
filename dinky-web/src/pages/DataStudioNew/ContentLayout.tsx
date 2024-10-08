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

import React, {Dispatch, RefObject, SetStateAction} from 'react';
import {DockLayout, DropDirection, LayoutBase, LayoutData, TabGroup} from 'rc-dock';
import {DockContext, PanelData, TabData} from 'rc-dock/lib/DockData';
import 'rc-dock/style/index-light.less';
import './index.less';
import {BorderOutlined, CloseOutlined, ImportOutlined, SelectOutlined, SwitcherOutlined} from '@ant-design/icons';
import {leftDefaultShowTab} from '@/pages/DataStudioNew/Toolbar/ToolbarRoute';
import {getDockPositionByToolbarPosition} from '@/pages/DataStudioNew/function';
import {ToolbarPosition} from "@/pages/DataStudioNew/Toolbar/data.d";

const quickGuideTab: TabData = {
  closable: false,
  id: 'quick-start',
  title: '快速开始',
  content: (
    <></>
  ),
  group: 'centerContent'
};


export const layout: LayoutData = {
  dockbox: {
    mode: 'vertical',
    children: [
      {
        mode: 'horizontal',
        size: 200,
        children: [
          {
            mode: 'vertical',
            size: 200,
            children: [
              {
                tabs: [
                  {
                    content: <></>,
                    id: leftDefaultShowTab.key,
                    title: leftDefaultShowTab.title,
                    minHeight: 50,
                    group: leftDefaultShowTab.position
                  }
                ]
              }
            ]
          },
          {
            size: 1000,
            tabs: [quickGuideTab],
            panelLock: {panelStyle: 'main'}
          }
        ]
      },
      {
        mode: 'horizontal',
        tabs: []
      }
    ]
  }
};

const centerPanelExtraButtons = (panelData: PanelData, context: DockContext) => {
  const buttons = [];
  if (panelData.parent?.mode !== 'window' && panelData.parent?.mode !== 'float') {
    buttons.push(
      <SelectOutlined
        rotate={90}
        className='my-panel-extra-btn'
        key='new-window'
        title='在新窗口中打开'
        onClick={() => context.dockMove(panelData, null, 'new-window')}
      />
    );
    const MaximizeIcon = panelData.parent?.mode === 'maximize' ? SwitcherOutlined : BorderOutlined;
    buttons.push(
      <MaximizeIcon
        className='my-panel-extra-btn'
        key='maximize'
        title={panelData.parent?.mode === 'maximize' ? '恢复' : '最大化'}
        onClick={() => context.dockMove(panelData, null, 'maximize')}
      />
    );
  } else {
    buttons.push(
      <ImportOutlined
        className='my-panel-extra-btn'
        key='move to dock'
        title='Dock'
        onClick={() =>
          context.dockMove(
            panelData,
            // @ts-ignore
            context.state.layout.dockbox,
            getDockPositionByToolbarPosition(panelData.group as ToolbarPosition)
          )
        }
      />
    );
  }
  return buttons;
};

const toolbarPanelExtraButtons = (panelData: PanelData, context: DockContext) => {
  const buttons = centerPanelExtraButtons(panelData, context);
  buttons.push(
    <CloseOutlined
      className='my-panel-extra-btn'
      key='close'
      title='关闭'
      onClick={() => context.dockMove(panelData, null, 'remove')}
    />
  );
  return buttons;
};
const toolbarPanelExtra = (panelData: PanelData, context: DockContext) => {
  return <>{toolbarPanelExtraButtons(panelData, context).map((button) => button)}</>;
};

export const groups: {
  [key: string]: TabGroup;
} = {
  leftTop: {
    floatable: true,
    panelExtra: toolbarPanelExtra,
    newWindow: true
  },
  leftBottom: {
    floatable: true,
    panelExtra: toolbarPanelExtra,
    newWindow: true
  },
  right: {
    floatable: true,
    panelExtra: toolbarPanelExtra,
    newWindow: true
  },
  //  中间内容group
  centerContent: {
    newWindow: true,
    tabLocked: true,
    panelExtra: (panelData: PanelData, context: DockContext) => {
      return <div>{centerPanelExtraButtons(panelData, context).map((button) => button)}</div>;
    }
  }
};
