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
import React, { lazy, useEffect, useMemo, useRef, useState } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import { Col, ConfigProvider, Row, Spin, theme as antdTheme } from 'antd';
import Toolbar from '@/pages/DataStudio/Toolbar';
import { DataStudioActionType, RightContextMenuState } from '@/pages/DataStudio/data.d';
import {
  getAllPanel,
  getLayoutState,
  getTabIcon,
  handleRightClick,
  InitContextMenuPosition
} from '@/pages/DataStudio/function';
import RightContextMenu, { useRightMenuItem } from '@/pages/DataStudio/RightContextMenu';
import { MenuInfo } from 'rc-menu/es/interface';
import { lazyComponent, ToolbarRoutes } from '@/pages/DataStudio/Toolbar/ToolbarRoute';
import { ToolbarPosition, ToolbarRoute } from '@/pages/DataStudio/Toolbar/data.d';
import { groups } from '@/pages/DataStudio/ContentLayout';
import { connect } from 'umi';
import { CenterTab, DataStudioState } from '@/pages/DataStudio/model';
import { mapDispatchToProps } from '@/pages/DataStudio/DvaFunction';
import { AliveScope, KeepAlive, useAliveController } from 'react-activation';
import { activeTab, createNewPanel } from '@/pages/DataStudio/DockLayoutFunction';
import * as Algorithm from './Algorithm';
import { PanelData } from 'rc-dock/lib/DockData';
import { useAsyncEffect } from 'ahooks';
import { useTheme } from '@/hooks/useThemeValue';
import { DataStudioContext } from '@/pages/DataStudio/DataStudioContext';
import './css/index.less';
import { getTenantByLocalStorage } from '@/utils/function';
import FooterContainer from '@/pages/DataStudio/FooterContainer';
import { useToken } from 'antd/es/theme/internal';
import { TAG_RIGHT_CONTEXT_MENU } from '@/pages/DataStudio/constants';
import { ContextMenuSpace } from '@/pages/DataStudio/ContextMenuSpace';
import { sleep } from '@antfu/utils';

const SqlTask = lazy(() => import('@/pages/DataStudio/CenterTabContent/SqlTask'));
const DataSourceDetail = lazy(() => import('@/pages/DataStudio/CenterTabContent/DataSourceDetail'));

let didMount = false;
const DataStudio: React.FC = (props: any) => {
  const {
    dataStudioState,
    handleToolbarShowDesc,
    handleThemeCompact,
    saveToolbarLayout,
    handleLayoutChange,
    updateAction,
    removeCenterTab,
    setLayout,
    queryFlinkEnv,
    queryFlinkCluster,
    queryAlertGroup,
    queryFlinkConfigOptions,
    queryFlinkUdfOptions,
    queryDataSourceDataList,
    querySuggestions,
    queryUserData
  } = props;
  const [_, token] = useToken();

  const dockLayoutRef = useRef<DockLayout>(null);
  const { drop } = useAliveController();
  const menuItem = useRightMenuItem({ dataStudioState });

  // 作业树右键弹出框状态
  const [edgeAreaRightMenuState, setEdgeAreaRightMenuState] = useState<RightContextMenuState>({
    show: false,
    position: InitContextMenuPosition
  });

  // 标签右键弹出框状态
  const [tagRightMenuState, setTagRightMenuState] = useState<
    RightContextMenuState & { id?: string }
  >({
    show: false,
    position: InitContextMenuPosition,
    id: undefined
  });

  const [loading, setLoading] = useState<boolean>(true);
  const theme = useTheme() as 'realDark' | 'light';
  const themeAlgorithm = useMemo(() => {
    const algorithms = [theme === 'light' ? antdTheme.defaultAlgorithm : antdTheme.darkAlgorithm];
    if (dataStudioState.theme.compact) {
      algorithms.push(antdTheme.compactAlgorithm);
    }
    return algorithms;
  }, [dataStudioState.theme.compact, theme]);

  const layout = useMemo(() => {
    const layoutData = getLayoutState(dataStudioState.layoutData, didMount);
    if (!didMount) {
      setLayout({
        layout: layoutData
      });
    }
    didMount = true;
    return layoutData;
  }, [dataStudioState.layoutData, setLayout]);

  useAsyncEffect(async () => {
    updateAction({
      actionType: null,
      params: null
    });
    await queryFlinkEnv();
    await queryFlinkCluster();
    setLoading(false);
    // 剩下不重要的可以后续慢加载
    await queryAlertGroup();
    await queryFlinkConfigOptions();
    await queryFlinkUdfOptions();
    await queryDataSourceDataList();
    await querySuggestions();
    await queryUserData({ id: getTenantByLocalStorage() });
  }, []);
  useEffect(() => {
    const { actionType, params } = dataStudioState.action;
    if (actionType?.includes('task-run-')) {
      const dockLayout = dockLayoutRef.current!!;
      let position: ToolbarPosition = 'leftBottom';
      const key = 'service';
      if (dataStudioState.toolbar.leftBottom.allTabs.find((x: string) => x === key)) {
        position = 'leftBottom';
      } else if (dataStudioState.toolbar.leftTop.allTabs.find((x: string) => x === key)) {
        position = 'leftTop';
      } else if (dataStudioState.toolbar.right.allTabs.find((x: string) => x === key)) {
        position = 'right';
      }
      const serviceRoute: ToolbarRoute = {
        ...ToolbarRoutes.find((item) => item.key === key)!!,
        position: position
      };
      const currentSelect = dataStudioState.toolbar[serviceRoute.position].currentSelect;
      if (!currentSelect) {
        // 添加panel
        const layout = Algorithm.fixLayoutData(
          createNewPanel(dataStudioState.layoutData, serviceRoute),
          dockLayout.props.groups
        );
        dockLayout.changeLayout(layout, serviceRoute.key, 'update', false);
      } else {
        //  切换tab
        dockLayout.updateTab(
          currentSelect,
          {
            id: serviceRoute.key,
            content: <></>,
            title: serviceRoute.title(),
            group: serviceRoute.position
          },
          true
        );
      }
    } else if (actionType === DataStudioActionType.TASK_DELETE) {
      const current = dockLayoutRef.current;
      if (current) {
        const currentLayoutData = current.getLayout();
        const source = Algorithm.find(currentLayoutData, params.id) as TabData;
        const layoutData = Algorithm.removeFromLayout(currentLayoutData, source);
        current.changeLayout(layoutData, params.id, 'remove', false);
      }
    }
  }, [dataStudioState.action]);

  useEffect(() => {
    if (dockLayoutRef.current) {
      if (dataStudioState.centerContent.activeTab) {
        // 中间tab变化
        const tab = (dataStudioState.centerContent.tabs as CenterTab[]).find(
          (x) => x.id === dataStudioState.centerContent.activeTab
        )!!;
        const centerContent = getAllPanel(dockLayoutRef.current.getLayout()).find(
          (x) => x.group === 'centerContent'
        )!!;
        const tabData: TabData = {
          closable: true,
          id: tab.id,
          content: <></>,
          title: tab.title,
          group: 'centerContent'
        };
        if (dataStudioState.centerContent.tabs.length === 1) {
          dockLayoutRef.current.updateTab(centerContent.activeId!!, tabData, true);
        } else {
          activeTab(
            dockLayoutRef.current,
            dataStudioState.layoutData,
            tabData,
            centerContent.activeId!!
          );
        }
      }
    }
  }, [dataStudioState.centerContent]);

  // 工具栏宽度
  const toolbarSize = dataStudioState.toolbar.showDesc ? 60 : 40;

  /**
   * 边缘区域调整布局右键点击事件 | edge area adjustment layout right-click events
   * @param e
   */
  const edgeAreaRightMenuHandle = (e: any) => handleRightClick(e, setEdgeAreaRightMenuState);
  /**
   * 右键菜单的点击事件 | right-click menu click event of the right-click menu
   * @param values
   */
  const handleEdgeAreaRightMenuClick = (values: MenuInfo) => {
    setEdgeAreaRightMenuState((prevState) => ({ ...prevState, show: false }));

    switch (values.key) {
      case 'showToolbarDesc':
      case 'hideToolbarDesc':
        handleToolbarShowDesc();
        break;
      case 'closeCompact':
      case 'openCompact':
        handleThemeCompact();
        break;
    }
  };

  const toolbarOnClick = (route: ToolbarRoute) => {
    const dockLayout = dockLayoutRef.current!!;
    const currentSelect = dataStudioState.toolbar[route.position].currentSelect;
    if (!currentSelect) {
      // 添加panel
      const layout = Algorithm.fixLayoutData(
        createNewPanel(
          dataStudioState.layoutData,
          route,
          dataStudioState.layoutSize[route.position]
        ),
        dockLayout.props.groups
      );
      dockLayout.changeLayout(layout, route.key, 'update', false);
    } else if (currentSelect === route.key) {
      // 取消选中
      dockLayout.dockMove(dockLayout.find(route.key) as TabData, null, 'remove');
    } else {
      //  切换tab
      dockLayout.updateTab(
        currentSelect,
        {
          id: route.key,
          content: <></>,
          title: route.title(),
          group: route.position
        },
        true
      );
    }
  };

  /**
   * 标签右键菜单handle | the right-click menu handle of the tag
   * @param e
   */
  const tagRightMenuHandle = (e: any) => handleRightClick(e, setTagRightMenuState);
  /**
   * 右键菜单的点击事件 | right-click menu click event of the right-click menu
   * @param {MenuInfo} node
   */
  const handleTagRightMenuClick = (node: MenuInfo) => {
    setTagRightMenuState((prevState) => ({ ...prevState, show: false }));
    const { key } = node;
    const current = dockLayoutRef.current;
    const handleCloseOther = () => {
      if (current) {
        dataStudioState.centerContent.tabs.forEach((tab: CenterTab) => {
          if (tab.id === tagRightMenuState.id) return;
          const currentLayoutData = current.getLayout();
          const source = Algorithm.find(currentLayoutData, tab.id) as TabData;
          const layoutData = Algorithm.removeFromLayout(currentLayoutData, source);
          current.changeLayout(layoutData, tab.id, 'remove', false);
        });
      }
    };
    switch (key) {
      case 'closeAll':
        if (current) {
          // 先关闭其他，再睡眠50ms 关闭当前页，否则会导致布局混乱
          handleCloseOther();
          sleep(50).then(() => {
            const currentLayoutData = current.getLayout();

            const source = Algorithm.find(currentLayoutData, tagRightMenuState.id!!) as TabData;
            const layoutData = Algorithm.removeFromLayout(currentLayoutData, source);
            current.changeLayout(layoutData, tagRightMenuState.id!!, 'remove', false);
          });
        }
        break;
      case 'closeOther':
        handleCloseOther();
        break;
      default:
        break;
    }
  };

  const saveTab = (tabData: TabData & any) => {
    let { id, group, title } = tabData;
    return { id, group, title };
  };
  const loadTab = (tab: TabData) => {
    const { id, group } = tab;
    if (group !== 'centerContent') {
      const route = ToolbarRoutes.find((x) => x.key === id) as ToolbarRoute;
      const content = ToolbarRoutes.find((item) => item.key === route.key)!!.content();
      const autoFreeze = route.key !== 'service';
      return {
        ...tab,
        content: (
          <KeepAlive cacheKey={route.key} autoFreeze={autoFreeze}>
            {content}
          </KeepAlive>
        ),
        title: route.title(),
        minHeight: 30,
        minWidth: 200
      };
    } else {
      if (id === 'quick-start') {
        const route = ToolbarRoutes.find((x) => x.key === id) as ToolbarRoute;
        return {
          ...tab,
          content: route.content(),
          title: route.title(),
          minHeight: 30,
          minWidth: 200
        };
      }
      const tabData = (dataStudioState.centerContent.tabs as CenterTab[]).find((x) => x.id === id);
      if (!tabData) {
        dockLayoutRef.current?.dockMove(tab, id!!, 'remove');
        return tab;
      }

      const getTitle = () => {
        const rightMenuHandle = (e: React.MouseEvent<HTMLElement>) => {
          setTagRightMenuState((prevState) => ({ ...prevState, id: id }));
          tagRightMenuHandle(e);
        };
        switch (tabData.tabType) {
          case 'task':
            const titleContent = (
              <ContextMenuSpace onContextMenu={rightMenuHandle}>
                {getTabIcon(tabData.params.dialect, 22)} {tabData.title}
              </ContextMenuSpace>
            );
            if (tabData.isUpdate) {
              return (
                <ContextMenuSpace onContextMenu={rightMenuHandle}>
                  <span style={{ color: '#52c41a' }}>
                    {titleContent}
                    {'  *'}
                  </span>
                </ContextMenuSpace>
              );
            }
            return (
              <ContextMenuSpace onContextMenu={rightMenuHandle}>{titleContent}</ContextMenuSpace>
            );
          case 'dataSource':
            const dialect = tabData.params.type;
            return (
              <ContextMenuSpace onContextMenu={rightMenuHandle}>
                {getTabIcon(dialect, 22)} {tabData.title}
              </ContextMenuSpace>
            );
          default:
            return (
              <ContextMenuSpace onContextMenu={rightMenuHandle}>{tabData.title}</ContextMenuSpace>
            );
        }
      };

      let content = <></>;
      const currentData = (dataStudioState.centerContent.tabs as CenterTab[]).find(
        (tab) => id == tab.id
      )!!;

      // todo 添加中间tab内容
      switch (tabData.tabType) {
        case 'task':
          content = <SqlTask tabData={tabData} />;
          break;
        case 'dataSource':
          content = <DataSourceDetail {...currentData} />;
          break;
      }
      return {
        ...tab,
        title: getTitle(),
        closable: true,
        content: (
          <KeepAlive
            name={tabData.id}
            cacheKey={tabData.id}
            autoFreeze={true}
            // when={() =>
            //   !(dataStudioState.centerContent.tabs as CenterTab[]).some((x) => x.id === id)
            // }
          >
            {lazyComponent(content)}
          </KeepAlive>
        ),
        minHeight: 30,
        minWidth: 200
      };
    }
  };
  // 保存工具栏按钮位置布局
  const saveToolbarLayoutHandle = (position: ToolbarPosition, list: string[]) => {
    const dockLayout = dockLayoutRef.current!!;
    //todo 思考：当工具栏布局更新时，选择的tab是否需要更新到对应的位置
    const currentSelect: string = dataStudioState.toolbar[position].currentSelect;
    // 如果新的布局中有tab,说明toolbar被移动了
    const addSelect = list.find((x) => !dataStudioState.toolbar[position].allTabs.includes(x));
    if (addSelect) {
      const tabData = {
        id: addSelect,
        title: ToolbarRoutes.find((x) => x.key === addSelect)!!.title(),
        content: <></>,
        group: position
      };
      // 查找被移动的toolbar位置，先删除，再添加
      const getMoveToolbarPosition = (): ToolbarPosition | undefined => {
        if (dataStudioState.toolbar.leftTop.allTabs.includes(addSelect)) {
          return 'leftTop';
        }
        if (dataStudioState.toolbar.leftBottom.allTabs.includes(addSelect)) {
          return 'leftBottom';
        }
        if (dataStudioState.toolbar.right.allTabs.includes(addSelect)) {
          return 'right';
        }
      };
      const moveToolbarPosition = getMoveToolbarPosition();
      if (moveToolbarPosition) {
        if (dataStudioState.toolbar[moveToolbarPosition].currentSelect === addSelect) {
          if (currentSelect) {
            dockLayout.updateTab(currentSelect, tabData, true);
            dockLayout.dockMove(dockLayout.find(addSelect) as TabData, null, 'remove');
          } else {
            const route = {
              ...ToolbarRoutes.find((x) => x.key === addSelect)!!,
              position: position
            };
            let layout = Algorithm.removeFromLayout(
              dockLayout.getLayout(),
              dockLayout.find(addSelect) as TabData
            );
            layout = Algorithm.fixLayoutData(
              createNewPanel(layout, route),
              dockLayout.props.groups,
              dataStudioState.layoutData[route.position]
            );
            dockLayout.changeLayout(layout, route.key, 'update', false);
          }
        }
      }
    }

    saveToolbarLayout({
      dockLayout: dockLayoutRef.current!!,
      position,
      list
    });
  };
  return (
    <DataStudioContext.Provider value={{ theme: theme }}>
      <ConfigProvider
        theme={{
          token: {
            colorBgContainer: 'var(--primary-color)'
          },
          components: {
            Table: {
              headerBg: 'var(--second-color)',
              rowHoverBg: 'var(--second-color)',
              rowSelectedBg: 'var(--second-color)',
              rowSelectedHoverBg: 'var(--second-color)',
              headerFilterHoverBg: 'var(--primary-color)',
              headerSortActiveBg: 'var(--primary-color)',
              headerSortHoverBg: 'var(--primary-color)',
              fixedHeaderSortActiveBg: 'var(--primary-color)',
              bodySortBg: 'var(--second-color)'
            }
          },
          algorithm: themeAlgorithm
        }}
      >
        <PageContainer
          breadcrumb={{}}
          title={false}
          childrenContentStyle={{ margin: 0, padding: 0 }}
          className={(theme === 'light' ? 'light-theme' : 'dark-theme') + ' page-container'}
        >
          <Spin spinning={loading} size={'large'} tip={'loading'}>
            <Row style={{ height: 'calc(100vh - 81px)' }}>
              {/*左边工具栏*/}
              <Col
                style={{
                  width: toolbarSize,
                  height: 'inherit'
                }}
                flex='none'
                onContextMenu={edgeAreaRightMenuHandle}
              >
                {/*左上工具栏*/}
                <Col style={{ width: 'inherit', height: '50%' }}>
                  <Toolbar
                    height={toolbarSize}
                    showDesc={dataStudioState.toolbar.showDesc}
                    position={'leftTop'}
                    onClick={toolbarOnClick}
                    toolbarSelect={dataStudioState.toolbar.leftTop}
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
                    height={toolbarSize}
                    showDesc={dataStudioState.toolbar.showDesc}
                    position={'leftBottom'}
                    onClick={toolbarOnClick}
                    toolbarSelect={dataStudioState.toolbar.leftBottom}
                    saveToolbarLayout={saveToolbarLayoutHandle}
                  />
                </Col>
              </Col>

              {/* 中间内容栏*/}
              <Col style={{ height: 'inherit' }} flex='auto' className={'content-container'}>
                <AliveScope>
                  <DockLayout
                    ref={dockLayoutRef}
                    layout={layout}
                    groups={groups(updateAction)}
                    dropMode={'edge'}
                    style={{ position: 'absolute', left: 0, top: 0, right: 0, bottom: 0 }}
                    onLayoutChange={async (newLayout, currentTabId, direction) => {
                      // todo 这里移到方向会导致布局和算法异常，先暂时规避掉
                      if (
                        direction === 'left' ||
                        direction === 'right' ||
                        direction === 'top' ||
                        direction === 'bottom' ||
                        direction === 'middle'
                      ) {
                        return;
                      }
                      // 移除centerContent中的tab
                      if (
                        currentTabId &&
                        direction === 'remove' &&
                        (dockLayoutRef.current?.find(currentTabId) as PanelData)?.group ===
                          'centerContent'
                      ) {
                        drop(currentTabId).then();

                        if (dataStudioState.centerContent.tabs.length === 1) {
                          dockLayoutRef.current?.updateTab(
                            currentTabId,
                            {
                              closable: false,
                              id: 'quick-start',
                              title: '快速开始',
                              content: <></>,
                              group: 'centerContent'
                            },
                            false
                          );
                        } else {
                          setLayout({
                            layout: newLayout
                          });
                        }
                        removeCenterTab(currentTabId);
                        return;
                      }
                      // 这里必需使用定时器，解决reducer 调用dispatch抛出的Reducers may not dispatch actions 异常
                      handleLayoutChange({
                        dockLayout: dockLayoutRef.current!!,
                        newLayout,
                        currentTabId,
                        direction
                      });
                    }}
                    saveTab={saveTab}
                    loadTab={loadTab}
                  />
                </AliveScope>
              </Col>

              {/*右边工具栏*/}
              <Col
                style={{ width: toolbarSize, height: 'inherit' }}
                flex='none'
                onContextMenu={edgeAreaRightMenuHandle}
              >
                <Toolbar
                  height={toolbarSize}
                  showDesc={dataStudioState.toolbar.showDesc}
                  position={'right'}
                  onClick={toolbarOnClick}
                  toolbarSelect={dataStudioState.toolbar.right}
                  saveToolbarLayout={saveToolbarLayoutHandle}
                />
              </Col>
            </Row>

            <FooterContainer token={token} centerContent={dataStudioState.centerContent} />

            {/* 边缘区域布局右键菜单*/}
            <RightContextMenu
              contextMenuPosition={edgeAreaRightMenuState.position}
              open={edgeAreaRightMenuState.show}
              openChange={() =>
                setEdgeAreaRightMenuState((prevState) => ({ ...prevState, show: false }))
              }
              items={menuItem}
              onClick={handleEdgeAreaRightMenuClick}
            />
            {/*标签的右键菜单*/}
            <RightContextMenu
              onClick={handleTagRightMenuClick}
              items={TAG_RIGHT_CONTEXT_MENU}
              contextMenuPosition={tagRightMenuState.position}
              open={tagRightMenuState.show}
              openChange={() =>
                setTagRightMenuState((prevState) => ({ ...prevState, show: false }))
              }
            />
          </Spin>
        </PageContainer>
      </ConfigProvider>
    </DataStudioContext.Provider>
  );
};

export default connect(
  ({ DataStudio }: { DataStudio: DataStudioState }) => ({
    dataStudioState: DataStudio
  }),
  mapDispatchToProps
)(DataStudio);
