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

import React, { Dispatch, RefObject, SetStateAction } from 'react';
import { DockLayout, DropDirection, LayoutBase, LayoutData, TabGroup } from 'rc-dock';
import { DockContext, PanelData, TabData } from 'rc-dock/lib/DockData';
import 'rc-dock/style/index-light.less';
import './index.less';
import {
  BorderOutlined,
  CloseOutlined,
  ImportOutlined,
  SelectOutlined,
  SwitcherOutlined
} from '@ant-design/icons';
import KeyBoard from '@/pages/DataStudio/MiddleContainer/KeyBoard';
import QuickGuide from '@/pages/DataStudio/MiddleContainer/QuickGuide';
import { Divider } from 'antd';
import { sleep } from 'ahooks/es/utils/testingHelpers';
import { LayoutState } from '@/pages/DataStudioNew/data.d';
import { leftDefaultShowTab } from '@/pages/DataStudioNew/Toolbar/toolbar-route';
import { getDockPositionByToolbarPosition } from '@/pages/DataStudioNew/function';

const quickGuideTab: TabData = {
  closable: false,
  id: 'quick-start',
  title: '快速开始',
  content: (
    <div style={{ height: 0 }}>
      <KeyBoard />
      <Divider />
      <br />
      <br />
      <br />
      <QuickGuide />
    </div>
  ),
  group: 'centerContent'
};

export const useLayout = (
  layoutState: LayoutState,
  setLayoutState: Dispatch<SetStateAction<LayoutState>>,
  dockLayoutRef: RefObject<DockLayout>
) => {
  const onLayoutChange = (
    newLayout: LayoutBase,
    currentTabId: string,
    direction?: DropDirection
  ) => {
    // 遍历layout，获取所有激活的tab
    if (direction === 'remove') {
      // 删除工具栏选中
      const currentTab = dockLayoutRef.current?.find(currentTabId)!!;
      const toolbarPosition = (currentTab as TabData).group;
      if (toolbarPosition) {
        //@ts-ignore
        const tabIds: string[] = currentTab?.parent?.tabs?.map((x) => x.id);
        setLayoutState((prevState) => {
          tabIds.forEach((tabId) => {
            //@ts-ignore
            prevState.toolbar[toolbarPosition].allTabs = prevState.toolbar[
              toolbarPosition
            ].allTabs?.filter((t) => t !== tabId);
          });
          //@ts-ignore
          prevState.toolbar[toolbarPosition].currentSelect = undefined;
          return {
            ...prevState
          };
        });
      }
    }
    if (direction !== 'move') {
      // 这里需要睡眠0.01s秒，为了防止点击事件触发太快，导致获取不到panel
      // todo 0.01s秒是一个经验值，后续可以根据实际情况调整，如果设备性能低，需加大时间，后续需要有测试
      sleep(10).then(activePanelOnClick);
    }
    sleep(10).then(saveLayout);
  };
  const saveLayout = () => {
    // todo 获取所有激活的tab
    const map = layoutState.toolbar.leftTop.allTabs
      .map((x) => dockLayoutRef.current?.find(x) as TabData)
      .filter((x) => x && x?.parent?.activeId === x?.id)
      .map((x) => ({ id: x.id, group: x.parent?.group }));
    console.log(map);

    const cacheLayoutData = JSON.stringify({
      ...layoutState,
      layoutData: dockLayoutRef.current?.saveLayout()
    });
    localStorage.setItem('datastudio-layout', cacheLayoutData);
  };

  // 激活panel点击事件
  const activePanelOnClick = () => {
    const panels = Array.from(
      // @ts-ignore
      dockLayoutRef.current?._ref.querySelectorAll('.dock-box>.dock-panel')
    );
    for (const panel of panels) {
      const classNameList = panel.classList;
      // classNameList 挨个匹配dock-style-(.*)，获取位置
      const reg = /dock-style-(.*)/;
      const position = classNameList.toString().match(reg);
      if (
        position?.length == 2 &&
        (position[1] === 'leftTop' || position[1] === 'right' || position[1] === 'leftBottom')
      ) {
        // 这里为panel添加点击事件
        (panel as HTMLElement).onclick = (e) => {
          // 如果是toolbar容器，获取激活的tab，然后聚焦toolbar select
          const selector = panel.querySelector('.dock-tab-active>.dock-tab-btn');
          const activeId = selector?.id;
          // 正则：rc-tabs-\d+-tab-(.*) ，我只需要获取.*的内容,匹配 activeKey
          const reg = /rc-tabs-\d+-tab-(.*)/;
          const activeKey = activeId?.match(reg);
          if (activeKey?.length == 2) {
            setLayoutState((prevState) => {
              return {
                ...prevState,
                toolbar: {
                  ...prevState.toolbar,
                  [position[1]]: {
                    // @ts-ignore
                    ...prevState.toolbar[position[1]],
                    currentSelect: activeKey[1]
                  }
                }
              };
            });
          }
        };
      }
    }
  };

  return { onLayoutChange };
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
                    content: leftDefaultShowTab.content,
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
  if (panelData.parent?.mode !== 'window') {
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
          // @ts-ignore
          context.dockMove(
            panelData,
            context.state.layout.dockbox,
            getDockPositionByToolbarPosition(panelData.group)
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
