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

import React from 'react';
import { LayoutData, TabGroup } from 'rc-dock';
import { DockContext, PanelData, TabData } from 'rc-dock/lib/DockData';
import 'rc-dock/style/index-light.less';
import './index.less';
import {
  ArrowsAltOutlined,
  BorderOutlined,
  CloseOutlined,
  ImportOutlined,
  PlusCircleOutlined,
  ReloadOutlined,
  SelectOutlined,
  ShrinkOutlined,
  SwitcherOutlined,
  SyncOutlined
} from '@ant-design/icons';
import { leftDefaultShowTab } from '@/pages/DataStudio/Toolbar/ToolbarRoute';
import { l } from '@/utils/intl';
import * as Algorithm from 'rc-dock/src/Algorithm';
import { createNewPanel } from '@/pages/DataStudio/DockLayoutFunction';
import { ToolbarPosition, ToolbarRoute } from '@/pages/DataStudio/Toolbar/data.d';
import { DataStudioActionType } from '@/pages/DataStudio/data.d';

const quickGuideTab: TabData = {
  closable: false,
  id: 'quick-start',
  title: '快速开始',
  content: <></>,
  group: 'centerContent'
};

export const layout: LayoutData = {
  dockbox: {
    mode: 'vertical',
    size: 1000,
    children: [
      {
        mode: 'horizontal',
        size: 600,
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
                    title: '项目',
                    minHeight: 30,
                    group: leftDefaultShowTab.position
                  }
                ]
              }
            ]
          },
          {
            size: 800,
            tabs: [quickGuideTab],
            panelLock: { panelStyle: 'main' }
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
        key='float'
        title={l('global.float')}
        onClick={() => context.dockMove(panelData, null, 'float')}
      />
    );
    const MaximizeIcon = panelData.parent?.mode === 'maximize' ? SwitcherOutlined : BorderOutlined;
    buttons.push(
      <MaximizeIcon
        className='my-panel-extra-btn'
        key='maximize'
        title={panelData.parent?.mode === 'maximize' ? l('button.recovery') : l('global.max')}
        onClick={() => context.dockMove(panelData, null, 'maximize')}
      />
    );
  } else {
    if (panelData.parent?.mode == 'float') {
      buttons.push(
        <SelectOutlined
          rotate={90}
          className='my-panel-extra-btn'
          key='new-window'
          title={l('global.blankOpen')}
          onClick={() => context.dockMove(panelData, null, 'new-window')}
        />
      );
    }
    buttons.push(
      <ImportOutlined
        className='my-panel-extra-btn'
        key='move to dock'
        title='Dock'
        onClick={() => {
          // @ts-ignore
          const route: ToolbarRoute = {
            key: panelData.activeId as string,
            // 标题
            title: () => panelData.activeId as string,
            // 图标
            icon: <> </>,
            position: panelData.group as ToolbarPosition
          };
          const layout = Algorithm.fixLayoutData(
            // @ts-ignore
            createNewPanel(context.state.layout, route),
            // @ts-ignore
            context.props.groups
          );
          // @ts-ignore
          context.changeLayout(layout, route.key, 'update', false);
          context.dockMove(panelData, null, 'remove');
        }}
      />
    );
  }
  return buttons;
};

const toolbarPanelExtraButtons = (
  panelData: PanelData,
  context: DockContext,
  updateAction: any
) => {
  const buttons = [];
  if (panelData.activeId === 'project') {
    buttons.push(
      <SyncOutlined
        className='my-panel-extra-btn'
        key='button.refresh'
        title={l('button.refresh')}
        onClick={() => {
          updateAction({ actionType: DataStudioActionType.PROJECT_REFRESH, params: {} });
        }}
      />
    );
    buttons.push(
      <PlusCircleOutlined
        className='my-panel-extra-btn'
        key='right.menu.createRoot'
        title={l('right.menu.createRoot')}
        onClick={() => {
          updateAction({ actionType: DataStudioActionType.PROJECT_CREATE_ROOT_DIR, params: {} });
        }}
      />
    );
    buttons.push(
      <ArrowsAltOutlined
        className='my-panel-extra-btn'
        key='button.expand-all'
        title={l('button.expand-all')}
        onClick={() => {
          updateAction({ actionType: DataStudioActionType.PROJECT_EXPAND_ALL, params: {} });
        }}
      />
    );
    buttons.push(
      <ShrinkOutlined
        className='my-panel-extra-btn'
        key='button.collapse-all'
        title={l('button.collapse-all')}
        onClick={() => {
          updateAction({ actionType: DataStudioActionType.PROJECT_COLLAPSE_ALL, params: {} });
        }}
      />
    );
  } else if (panelData.activeId === 'catalog') {
    buttons.push(
      <ReloadOutlined
        className='my-panel-extra-btn'
        key='button.refresh'
        title={l('button.refresh')}
        onClick={() => {
          updateAction({ actionType: DataStudioActionType.CATALOG_REFRESH, params: {} });
        }}
      />
    );
  } else if (panelData.activeId === 'datasource') {
    buttons.push(
      <SyncOutlined
        className='my-panel-extra-btn'
        key='button.refresh'
        title={l('button.refresh')}
        onClick={() => {
          updateAction({ actionType: DataStudioActionType.DATASOURCE_REFRESH, params: {} });
        }}
      />
    );
    buttons.push(
      <PlusCircleOutlined
        className='my-panel-extra-btn'
        key='button.create'
        title={l('button.create')}
        onClick={() => {
          updateAction({ actionType: DataStudioActionType.DATASOURCE_CREATE, params: {} });
        }}
      />
    );
  }
  const close = (
    <CloseOutlined
      className='my-panel-extra-btn'
      key='close'
      title={l('button.close')}
      onClick={() => context.dockMove(panelData, null, 'remove')}
    />
  );
  return [...buttons, ...centerPanelExtraButtons(panelData, context), close];
};
const toolbarPanelExtra = (panelData: PanelData, context: DockContext, updateAction: any) => {
  return <>{toolbarPanelExtraButtons(panelData, context, updateAction).map((button) => button)}</>;
};

export const groups = (
  updateAction: (params: { actionType: string; params: Record<string, any> }) => void
): { [key: string]: TabGroup } => {
  const panelExtra = (panelData: PanelData, context: DockContext) => {
    return toolbarPanelExtra(panelData, context, updateAction);
  };
  return {
    leftTop: {
      floatable: true,
      panelExtra: panelExtra,
      newWindow: true
    },
    leftBottom: {
      floatable: true,
      panelExtra: panelExtra,
      newWindow: true
    },
    right: {
      floatable: true,
      panelExtra: panelExtra,
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
};
