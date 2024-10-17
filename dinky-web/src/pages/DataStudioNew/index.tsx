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
import React, {lazy, useEffect, useRef, useState} from 'react';
import {PageContainer} from '@ant-design/pro-layout';
import 'rc-dock/dist/rc-dock.css';
import {Col, Row, theme} from 'antd';
import FooterContainer from '@/pages/DataStudio/FooterContainer';
import Toolbar from '@/pages/DataStudioNew/Toolbar';
import {RightContextMenuState} from '@/pages/DataStudioNew/data.d';
import {getAllPanel, handleRightClick, InitContextMenuPosition} from '@/pages/DataStudioNew/function';
import RightContextMenu, {useRightMenuItem} from '@/pages/DataStudioNew/RightContextMenu';
import {MenuInfo} from 'rc-menu/es/interface';
import {lazyComponent, ToolbarRoutes} from '@/pages/DataStudioNew/Toolbar/ToolbarRoute';
import {ToolbarPosition, ToolbarRoute} from '@/pages/DataStudioNew/Toolbar/data.d';
import {groups} from '@/pages/DataStudioNew/ContentLayout';
import {connect} from "umi";
import {CenterTab, LayoutState} from "@/pages/DataStudioNew/model";
import {mapDispatchToProps} from "@/pages/DataStudioNew/DvaFunction";
import {getUUID} from "rc-select/es/hooks/useId";
import {AliveScope, KeepAlive} from "react-activation";
import {activeTab, createNewPanel} from "@/pages/DataStudioNew/DockLayoutFunction";
import * as Algorithm from "./Algorithm";
import {PanelData} from "rc-dock/lib/DockData";
import {useAsyncEffect} from "ahooks";

const {useToken} = theme;
const FlinkSQL = lazy(() => import('@/pages/DataStudioNew/CenterTabContent/FlinkSQL'));

const DataStudioNew: React.FC = (props: any) => {
  const {
    layoutState,
    handleToolbarShowDesc,
    saveToolbarLayout,
    handleLayoutChange,
    addCenterTab,
    updateAction,
    removeCenterTab,
    setLayout,
    queryFlinkEnv,
    queryFlinkCluster,
    queryAlertGroup,
    queryFlinkConfigOptions,
    queryFlinkUdfOptions,
  } = props
  const {token} = useToken();
  const dockLayoutRef = useRef<DockLayout>(null);

  const menuItem = useRightMenuItem({layoutState});
  // 右键弹出框状态
  const [rightContextMenuState, setRightContextMenuState] = useState<RightContextMenuState>({
    show: false,
    position: InitContextMenuPosition
  });

  useAsyncEffect(async ()=>{
    await queryFlinkEnv()
    await queryFlinkCluster()
    await queryAlertGroup()
    await queryFlinkConfigOptions()
    await queryFlinkUdfOptions()
  },[])
  useEffect(() => {
    updateAction({
      actionType: undefined,
      params: undefined
    })
  }, []);
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
        } else {
          activeTab(dockLayoutRef.current, layoutState.layoutData, tabData, centerContent.activeId!!)
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
    const currentSelect = layoutState.toolbar[route.position].currentSelect;
    if (!currentSelect) {
      // 添加panel
      const layout = Algorithm.fixLayoutData(createNewPanel(layoutState.layoutData, route), dockLayout.props.groups);
      dockLayout.changeLayout(layout, route.key, "update", false)

    } else if (currentSelect === route.key) {
      // 取消选中
      dockLayout.dockMove(dockLayout.find(route.key) as TabData, null, 'remove');
    } else {
      //  切换tab
      dockLayout.updateTab(currentSelect, {
        id: route.key,
        content: <></>,
        title: route.title,
        group: route.position
      }, true)
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
        content: <KeepAlive
          cacheKey={route?.key}>{ToolbarRoutes.find(item => item.key === route?.key)?.content()}</KeepAlive>,
        title
      };
    } else {
      if (id === "quick-start") {
        const route = ToolbarRoutes.find((x) => x.key === id);
        return {
          ...tab,
          content: ToolbarRoutes.find(item => item.key === route?.key)?.content(),
          title
        };
      }
      const tabData = (layoutState.centerContent.tabs as CenterTab[]).find((x) => x.id === id)!!;

      // todo 添加中间tab内容
      return {
        ...tab,
        title,
        closable: true,
        content: <KeepAlive cacheKey={tabData.id}>{lazyComponent(<FlinkSQL  {...tabData}/>)}</KeepAlive>,
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
            dockLayout.dockMove((dockLayout.find(addSelect) as TabData), null, 'remove')
          } else {
            const route = {...ToolbarRoutes.find((x) => x.key === addSelect)!!, position: position};
            let layout = Algorithm.removeFromLayout(dockLayout.getLayout(), dockLayout.find(addSelect) as TabData);
            layout = Algorithm.fixLayoutData(createNewPanel(layout, route), dockLayout.props.groups);
            dockLayout.changeLayout(layout, route.key, "update", false)
          }
        }
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
          <AliveScope>
            <DockLayout
              ref={dockLayoutRef}
              layout={layoutState.layoutData}
              groups={groups(layoutState, updateAction)}
              dropMode={'edge'}
              style={{position: 'absolute', left: 0, top: 0, right: 0, bottom: 0}}
              onLayoutChange={(newLayout, currentTabId, direction) => {
                // todo 这里移到方向会导致布局和算法异常，先暂时规避掉
                if (direction === 'left' || direction === 'right' || direction === 'top' || direction === 'bottom' || direction === 'middle') {
                  return
                }
                // 移除centerContent中的tab
                if (currentTabId && direction === "remove" && (dockLayoutRef.current?.find(currentTabId) as PanelData)?.group === "centerContent") {
                  if (layoutState.centerContent.tabs.length === 1) {
                    dockLayoutRef.current?.updateTab(currentTabId, {
                      closable: false,
                      id: 'quick-start',
                      title: '快速开始',
                      content: (
                        <></>
                      ),
                      group: 'centerContent'
                    }, false)
                  } else {
                    setLayout({
                      layout: newLayout
                    })
                  }
                  removeCenterTab(currentTabId)
                  return;
                }
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
          </AliveScope>
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
