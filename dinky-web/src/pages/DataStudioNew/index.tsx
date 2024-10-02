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

import { DockLayout, TabData } from 'rc-dock';
import React, { useRef, useState } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import 'rc-dock/dist/rc-dock.css';
import { Col, Row, theme } from 'antd';
import FooterContainer from '@/pages/DataStudio/FooterContainer';
import Toolbar from '@/pages/DataStudioNew/Toolbar';
import { LayoutState, RightContextMenuState } from '@/pages/DataStudioNew/data.d';
import {
  getDockPositionByToolbarPosition,
  getLayoutState,
  handleRightClick,
  InitContextMenuPosition
} from '@/pages/DataStudioNew/function';
import RightContextMenu, { useRightMenuItem } from '@/pages/DataStudioNew/RightContextMenu';
import { MenuInfo } from 'rc-menu/es/interface';
import { leftDefaultShowTab, toolbarRoutes } from '@/pages/DataStudioNew/Toolbar/toolbar-route';
import { ToolbarPosition, ToolbarRoute } from '@/pages/DataStudioNew/Toolbar/data.d';
import { groups, layout, useLayout } from '@/pages/DataStudioNew/ContentLayout';
import { PanelData } from 'rc-dock/lib/DockData';

const { useToken } = theme;

const DataStudioNew: React.FC = () => {
  const { token } = useToken();
  const dockLayoutRef = useRef<DockLayout>(null);

  // 页面布局状态
  const [layoutState, setLayoutState] = useState<LayoutState>(
    localStorage.getItem('datastudio-layout')
      ? getLayoutState(JSON.parse(localStorage.getItem('datastudio-layout')!!))
      : ({
          layoutData: layout,
          toolbar: {
            showDesc: false,
            leftTop: {
              currentSelect: leftDefaultShowTab.key,
              allOpenTabs: [leftDefaultShowTab.key],
              allTabs: toolbarRoutes.filter((x) => x.position === 'leftTop').map((x) => x.key)
            },
            leftBottom: {
              allTabs: toolbarRoutes.filter((x) => x.position === 'leftBottom').map((x) => x.key)
            },
            right: {
              allTabs: toolbarRoutes.filter((x) => x.position === 'right').map((x) => x.key)
            }
          }
        } as LayoutState)
  );
  const { onLayoutChange } = useLayout(layoutState, setLayoutState, dockLayoutRef);

  const menuItem = useRightMenuItem({ layoutState });
  // 右键弹出框状态
  const [rightContextMenuState, setRightContextMenuState] = useState<RightContextMenuState>({
    show: false,
    position: InitContextMenuPosition
  });

  // 工具栏宽度
  const toolbarWidth = layoutState.toolbar.showDesc ? 60 : 30;

  //  右键菜单handle
  const rightContextMenuHandle = (e: any) => handleRightClick(e, setRightContextMenuState);

  const handleMenuClick = (values: MenuInfo) => {
    setRightContextMenuState((prevState) => ({ ...prevState, show: false }));

    switch (values.key) {
      case 'showToolbarDesc':
        setLayoutState((prevState) => ({
          ...prevState,
          toolbar: { ...prevState.toolbar, showDesc: true }
        }));
        break;
      case 'hideToolbarDesc':
        setLayoutState((prevState) => ({
          ...prevState,
          toolbar: { ...prevState.toolbar, showDesc: false }
        }));
        break;
      case 'saveLayout':
        console.log(dockLayoutRef.current?.saveLayout());
        break;
    }
  };

  const toolbarOnClick = (route: ToolbarRoute) => {
    setLayoutState((prevState) => {
      const newTab = dockLayoutRef.current?.find(route.key) as TabData;
      let tab = dockLayoutRef.current?.find(prevState.toolbar[route.position].currentSelect!!);
      // 如果没有选中的tab，就遍历所有tab，找到第一个添加进去
      if (!tab) {
        const keys = prevState.toolbar[route.position].allOpenTabs;
        if (keys) {
          for (const key of keys) {
            if (tab) {
              break;
            }
            tab = dockLayoutRef.current?.find(key);
          }
        }
      }
      if (prevState.toolbar[route.position].currentSelect === route.key) {
        // 取消选中
        if (newTab) {
          if (layoutState.toolbar.showActiveTab) {
            dockLayoutRef.current?.dockMove(newTab, null, 'active');
          } else {
            dockLayoutRef.current?.dockMove(newTab.parent as PanelData, null, 'remove');
          }
        }
        prevState.toolbar[route.position] = {
          ...prevState.toolbar[route.position],
          currentSelect: undefined,
          allOpenTabs: Array.from(
            new Set(
              [...(prevState.toolbar[route.position]?.allOpenTabs ?? [])].filter(
                (t) => t !== route.key
              )
            )
          )
        };
      } else {
        // 新增tab
        if (tab && !newTab) {
          dockLayoutRef.current?.dockMove(
            {
              id: route.key,
              content: route.content,
              title: route.title,
              group: route.position
            },
            tab,
            'middle'
          );
        } else if (newTab) {
          dockLayoutRef.current?.dockMove(newTab, newTab.parent!!, 'middle');
        } else {
          // 创建窗口
          dockLayoutRef.current?.dockMove(
            {
              id: route.key,
              content: route.content,
              title: route.title,
              group: route.position
            },
            dockLayoutRef.current?.getLayout().dockbox,
            getDockPositionByToolbarPosition(route.position)
          );
        }

        prevState.toolbar[route.position] = {
          ...prevState.toolbar[route.position],
          currentSelect: route.key,
          allOpenTabs: Array.from(
            new Set([...(prevState.toolbar[route.position]?.allOpenTabs ?? []), route.key])
          )
        };
      }
      return { ...prevState };
    });
  };

  const saveTab = (tabData: TabData & any) => {
    let { id, inputValue } = tabData;
    return { id };
  };
  // 保存工具栏按钮位置布局
  const saveToolbarLayout = (position: ToolbarPosition, list: string[]) => {
    setLayoutState((prevState) => ({
      ...prevState,
      toolbar: {
        ...prevState.toolbar,
        [position]: {
          ...prevState.toolbar[position],
          allTabs: list
        }
      }
    }));
    // todo 保存到本地
    // localStorage.setItem('datastudio-layout', cacheLayoutData);
  };
  return (
    <PageContainer
      breadcrumb={undefined}
      title={false}
      childrenContentStyle={{ margin: 0, padding: 0 }}
    >
      <Row style={{ height: 'calc(100vh - 81px)' }}>
        {/*左边工具栏*/}
        <Col
          style={{ width: toolbarWidth, height: 'inherit' }}
          flex='none'
          onContextMenu={rightContextMenuHandle}
        >
          {/*左上工具栏*/}
          <Col style={{ width: 'inherit', height: '50%' }}>
            <Toolbar
              showDesc={layoutState.toolbar.showDesc}
              position={'leftTop'}
              onClick={toolbarOnClick}
              toolbarSelect={layoutState.toolbar.leftTop}
              saveToolbarLayout={saveToolbarLayout}
            />
          </Col>

          {/*左下工具栏*/}
          <Col
            style={{
              width: 'inherit',
              height: '50%'
            }}
          >
            <Toolbar
              showDesc={layoutState.toolbar.showDesc}
              position={'leftBottom'}
              onClick={toolbarOnClick}
              toolbarSelect={layoutState.toolbar.leftBottom}
              saveToolbarLayout={saveToolbarLayout}
            />
          </Col>
        </Col>

        {/* 中间内容栏*/}
        <Col style={{ height: 'inherit' }} flex='auto'>
          <DockLayout
            ref={dockLayoutRef}
            defaultLayout={layoutState.layoutData}
            groups={groups}
            style={{ position: 'absolute', left: 0, top: 0, right: 0, bottom: 0 }}
            onLayoutChange={onLayoutChange}
            saveTab={saveTab}
            loadTab={(savedTab) => {
              const id = savedTab.id;
              const route = toolbarRoutes.find((x) => x.key === id);
              return {
                ...savedTab,
                content: route?.content ?? <></>,
                title: route?.title ?? <></>
              };
            }}
          />
        </Col>

        {/*右边工具栏*/}
        <Col
          style={{ width: toolbarWidth, height: 'inherit' }}
          flex='none'
          onContextMenu={rightContextMenuHandle}
        >
          <Toolbar
            showDesc={layoutState.toolbar.showDesc}
            position={'right'}
            onClick={toolbarOnClick}
            toolbarSelect={layoutState.toolbar.right}
            saveToolbarLayout={saveToolbarLayout}
          />
        </Col>
      </Row>

      {/*@ts-ignore*/}
      <FooterContainer token={token} />

      {/*右键菜单*/}
      <RightContextMenu
        contextMenuPosition={rightContextMenuState.position}
        open={rightContextMenuState.show}
        openChange={() => setRightContextMenuState((prevState) => ({ ...prevState, show: false }))}
        items={menuItem}
        onClick={handleMenuClick}
      />
    </PageContainer>
  );
};

export default DataStudioNew;
