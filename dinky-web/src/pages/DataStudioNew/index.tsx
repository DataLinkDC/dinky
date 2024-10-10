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

import {DockLayout, TabData} from 'rc-dock';
import React, {useEffect, useRef, useState} from 'react';
import {PageContainer} from '@ant-design/pro-layout';
import 'rc-dock/dist/rc-dock.css';
import {Col, Input, Row, theme} from 'antd';
import FooterContainer from '@/pages/DataStudio/FooterContainer';
import Toolbar from '@/pages/DataStudioNew/Toolbar';
import {RightContextMenuState} from '@/pages/DataStudioNew/data.d';
import {
  getAllPanel,
  getDockPositionByToolbarPosition,
  handleRightClick,
  InitContextMenuPosition
} from '@/pages/DataStudioNew/function';
import RightContextMenu, {useRightMenuItem} from '@/pages/DataStudioNew/RightContextMenu';
import {MenuInfo} from 'rc-menu/es/interface';
import {TestRoutes, ToolbarRoutes} from '@/pages/DataStudioNew/Toolbar/ToolbarRoute';
import {ToolbarPosition, ToolbarRoute} from '@/pages/DataStudioNew/Toolbar/data.d';
import {groups} from '@/pages/DataStudioNew/ContentLayout';
import {connect} from "umi";
import {CenterTab, LayoutState} from "@/pages/DataStudioNew/model";
import {mapDispatchToProps} from "@/pages/DataStudioNew/DvaFunction";
import {getUUID} from "rc-select/es/hooks/useId";
import {PanelData} from "rc-dock/lib/DockData";
import * as Algorithm from "rc-dock/src/Algorithm";

const {useToken} = theme;

const DataStudioNew: React.FC = (props: any) => {
  const {
    layoutState,
    handleToolbarShowDesc,
    saveToolbarLayout,
    handleLayoutChange,
    addCenterTab
  } = props
  const {token} = useToken();
  const dockLayoutRef = useRef<DockLayout>(null);

  const menuItem = useRightMenuItem({layoutState});
  // 右键弹出框状态
  const [rightContextMenuState, setRightContextMenuState] = useState<RightContextMenuState>({
    show: false,
    position: InitContextMenuPosition
  });
  useEffect(() => {
    if (dockLayoutRef.current) {
      if (layoutState.centerContent.activeTab) {
        // 中间tab变化
        const tab = (layoutState.centerContent.tabs as CenterTab[]).find(x => x.id === layoutState.centerContent.activeTab)!!;
        const centerContent = getAllPanel(dockLayoutRef.current.getLayout()).find((x) => x.group === "centerContent")!!;
        const tabData: TabData = {
          closable: true,
          id: tab.id,
          content: <></>,
          title: tab.title,
          group: "centerContent"
        }
        if (layoutState.centerContent.tabs.length === 1) {
          dockLayoutRef.current.updateTab(centerContent.activeId!!, tabData, true)
        } else if (layoutState.centerContent.tabs.length === 0) {
          // 进入快速开始界面
          dockLayoutRef.current.updateTab(centerContent.activeId!!, {
            closable: false,
            id: 'quick-start',
            title: '快速开始',
            content: (
              <></>
            ),
            group: 'centerContent'
          }, true)
        } else {
          dockLayoutRef.current.dockMove(tabData, centerContent.activeId!!, 'active')
        }
      }
    }
  }, [layoutState.centerContent]);

  // 工具栏宽度
  const toolbarWidth = layoutState.toolbar.showDesc ? 60 : 30;

  //  右键菜单handle
  const rightContextMenuHandle = (e: any) => handleRightClick(e, setRightContextMenuState);

  const handleMenuClick = (values: MenuInfo) => {
    setRightContextMenuState((prevState) => ({...prevState, show: false}));

    switch (values.key) {
      case 'showToolbarDesc':
      case 'hideToolbarDesc':
        handleToolbarShowDesc()
        break;
      case "saveLayout":
        addCenterTab({
          id: "123" + getUUID(),
          title: "123" + getUUID(),
          tabType: 'code'
        })
        break;
    }
  };

  const toolbarOnClick = (route: ToolbarRoute) => {
    const dockLayout = dockLayoutRef.current!!;
    const newTab = dockLayout.find(route.key) as TabData;
    let tab = dockLayout.find(layoutState.toolbar[route.position].currentSelect!!);
    // 如果没有选中的tab，就遍历所有tab，找到第一个添加进去
    if (!tab) {
      const keys = layoutState.toolbar[route.position].allOpenTabs;
      if (keys) {
        for (const key of keys) {
          if (tab) {
            break;
          }
          tab = dockLayout.find(key);
        }
      }
    }
    if (layoutState.toolbar[route.position].currentSelect === route.key) {
      // 取消选中
      if (newTab) {
        if (layoutState.toolbar.showActiveTab) {
          dockLayout.dockMove(newTab, null, 'active');
        } else {
          // 删除panel
          dockLayout.dockMove(newTab.parent as PanelData, null, 'remove');
        }
      }
    } else {
      // todo 切换tab
      if (tab && !newTab) {
        dockLayout.updateTab(tab.id!!, {
          id: route.key,
          content: TestRoutes[route?.key],
          title: route.title,
          group: route.position
        }, true)
      } else if (newTab) {
        dockLayout.dockMove(newTab, newTab.parent!!, 'middle');
      } else {
        // 创建窗口
        // todo 这里创建窗口可以优化
        dockLayout.dockMove(
          {
            id: route.key,
            content: TestRoutes[route?.key],
            title: route.title,
            group: route.position
          },
          dockLayout.getLayout().dockbox,
          getDockPositionByToolbarPosition(route.position)
        );

      }
    }
  };

  const saveTab = (tabData: TabData & any) => {
    let {id, group, title} = tabData;
    return {id, group, title};
  };
  const loadTab = (tab: TabData) => {
    const {id, title, group} = tab;
    if (group !== "centerContent") {
      const route = ToolbarRoutes.find((x) => x.key === id);
      return {
        ...tab,
        // content: route?.content() ?? <></>,
        content:TestRoutes[route?.key],
        // content:<Input />,
        title
      };
    } else {
      if (id === "quick-start") {
        const route = ToolbarRoutes.find((x) => x.key === id);
        return {
          ...tab,
          content: TestRoutes[route?.key],
          title
        };
      }
      // todo 添加中间tab内容
      return {
        ...tab,
        title,
        closable: true,
        content: <Input/>,
      };
    }

  }
  // 保存工具栏按钮位置布局
  const saveToolbarLayoutHandle = (position: ToolbarPosition, list: string[]) => {
    const dockLayout = dockLayoutRef.current!!;
    //todo 思考：当工具栏布局更新时，选择的tab是否需要更新到对应的位置
    const currentSelect: string = layoutState.toolbar[position].currentSelect;
    // 如果新的布局中有tab,说明toolbar被移动了
    const addSelect = list.find((x) => !layoutState.toolbar[position].allTabs.includes(x));
    console.log(addSelect, position, list)
    if (addSelect) {
      const tabData = {
        id: addSelect,
        title: ToolbarRoutes.find((x) => x.key === addSelect)!!.title,
        content: <></>,
        group: position
      }
      // 查找被移动的toolbar位置，先删除，再添加
      const getMoveToolbarPosition = (): ToolbarPosition | undefined => {
        if (layoutState.toolbar.leftTop.allTabs.includes(addSelect)) {
          return 'leftTop'
        }
        if (layoutState.toolbar.leftBottom.allTabs.includes(addSelect)) {
          return 'leftBottom'
        }
        if (layoutState.toolbar.right.allTabs.includes(addSelect)) {
          return 'right'
        }
      }
      const moveToolbarPosition = getMoveToolbarPosition()
      if (moveToolbarPosition) {
        if (layoutState.toolbar[moveToolbarPosition].currentSelect === addSelect) {
          if (currentSelect) {
            dockLayout.updateTab(currentSelect, tabData, true)
          } else {
            setTimeout(() => {
              dockLayout.dockMove(tabData, dockLayoutRef.current!!.getLayout().dockbox, getDockPositionByToolbarPosition(position))
            }, 0)
          }
        }
        dockLayout.dockMove((dockLayout.find(addSelect) as TabData), null, 'remove')
      }
    }


    saveToolbarLayout({
      dockLayout: dockLayoutRef.current!!,
      position,
      list
    })
  };
  return (
    <PageContainer
      breadcrumb={undefined}
      title={false}
      childrenContentStyle={{margin: 0, padding: 0}}
    >
      <Row style={{height: 'calc(100vh - 81px)'}}>
        {/*左边工具栏*/}
        <Col
          style={{width: toolbarWidth, height: 'inherit'}}
          flex='none'
          onContextMenu={rightContextMenuHandle}
        >
          {/*左上工具栏*/}
          <Col style={{width: 'inherit', height: '50%'}}>
            <Toolbar
              showDesc={layoutState.toolbar.showDesc}
              position={'leftTop'}
              onClick={toolbarOnClick}
              toolbarSelect={layoutState.toolbar.leftTop}
              saveToolbarLayout={saveToolbarLayoutHandle}
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
              saveToolbarLayout={saveToolbarLayoutHandle}
            />
          </Col>
        </Col>

        {/* 中间内容栏*/}
        <Col style={{height: 'inherit'}} flex='auto'>
          <DockLayout
            ref={dockLayoutRef}
            layout={layoutState.layoutData}
            groups={groups(layoutState)}
            style={{position: 'absolute', left: 0, top: 0, right: 0, bottom: 0}}
            onLayoutChange={(newLayout, currentTabId, direction) => {
              // 这里必需使用定时器，解决reducer 调用dispatch抛出的Reducers may not dispatch actions 异常
              handleLayoutChange({
                dockLayout: dockLayoutRef.current!!,
                newLayout,
                currentTabId,
                direction
              })
            }
            }
            saveTab={saveTab}
            loadTab={loadTab}
          />
        </Col>

        {/*右边工具栏*/}
        <Col
          style={{width: toolbarWidth, height: 'inherit'}}
          flex='none'
          onContextMenu={rightContextMenuHandle}
        >
          <Toolbar
            showDesc={layoutState.toolbar.showDesc}
            position={'right'}
            onClick={toolbarOnClick}
            toolbarSelect={layoutState.toolbar.right}
            saveToolbarLayout={saveToolbarLayoutHandle}
          />
        </Col>
      </Row>

      {/*@ts-ignore*/}
      <FooterContainer token={token}/>

      {/*右键菜单*/}
      <RightContextMenu
        contextMenuPosition={rightContextMenuState.position}
        open={rightContextMenuState.show}
        openChange={() => setRightContextMenuState((prevState) => ({...prevState, show: false}))}
        items={menuItem}
        onClick={handleMenuClick}
      />
    </PageContainer>
  );
};

export default connect(
  ({DataStudio}: { DataStudio: LayoutState }) => ({
    layoutState: DataStudio
  }), mapDispatchToProps)(DataStudioNew);

// export default DataStudioNew
