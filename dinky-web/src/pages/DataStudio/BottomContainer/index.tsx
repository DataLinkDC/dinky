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

import { CircleBtn } from '@/components/CallBackButton/CircleBtn';
import Title from '@/components/Front/Title';
import ContentScroll from '@/components/Scroll/ContentScroll';
import MovableSidebar from '@/components/Sidebar/MovableSidebar';
import { getCurrentData } from '@/pages/DataStudio/function';
import {
  StateType,
  STUDIO_MODEL,
  TabsItemType,
  TabsPageType,
  VIEW
} from '@/pages/DataStudio/model';
import { BottomBtnRoute, LeftBottomMoreTabs, LeftBottomSide, Tab } from '@/pages/DataStudio/route';
import { BottomTabRoute } from '@/pages/DataStudio/TabRoute';
import { l } from '@/utils/intl';
import { connect } from '@@/exports';
import { ConfigProvider, Space, Tabs } from 'antd';
import React, { useEffect, useState } from 'react';

export type BottomContainerProps = {
  size: number;
  height: number | string;
};

const BottomContainer: React.FC<BottomContainerProps> = (props: any) => {
  const {
    dispatch,
    size,
    bottomContainer,
    height,
    tabs: { activeKey, panes }
  } = props;
  const currentData = getCurrentData(panes, activeKey);
  const taskId = currentData?.id ?? 0;
  const [tabState, setTabState] = useState<Record<string, Tab[]>>(
    Object.keys(BottomTabRoute)
      .map((x) => ({ [x]: [] }))
      .reduce((a, b) => ({ ...a, ...b }), {})
  );
  const tabItems = (LeftBottomMoreTabs[bottomContainer.selectKey] ?? []).map((item: any) => {
    return {
      key: bottomContainer.selectKey + '/' + item.key,
      label: (
        <span>
          {item.icon}
          {item.label}
        </span>
      )
    };
  });
  useEffect(() => {
    setTabState({ ...tabState, [bottomContainer.selectKey]: [] });
  }, [taskId, bottomContainer.selectKey]);
  const refresh = () => {
    const tabs = tabState?.[bottomContainer.selectKey] ?? [];
    setTabState({ ...tabState, [bottomContainer.selectKey]: [...tabs] });
  };

  const width = document.documentElement.clientWidth - VIEW.sideWidth * 2;

  /**
   * 侧边栏最小化
   */
  const handleMinimize = () => {
    dispatch({
      type: STUDIO_MODEL.updateSelectBottomKey,
      payload: ''
    });
  };

  /**
   * 更新底部高度
   * @param {number} height
   */
  const updateBottomHeight = (height: number) => {
    dispatch({
      type: STUDIO_MODEL.updateBottomHeight,
      payload: height
    });
  };

  /**
   * 更新中间内容高度
   * @param {number} height
   */
  const updateCenterContentHeight = (height: number) => {
    dispatch({
      type: STUDIO_MODEL.updateCenterContentHeight,
      payload: height
    });
  };

  const updateSelectBottomSubKey = (key: string) => {
    dispatch({
      type: STUDIO_MODEL.updateSelectBottomSubKey,
      payload: key
    });
  };

  const updateSelectBottomKey = (key: string) => {
    dispatch({
      type: STUDIO_MODEL.updateSelectBottomKey,
      payload: key
    });
  };

  /**
   * 更新工具栏内容高度
   * @param {number} height
   */
  const updateToolContentHeight = (height: number) => {
    dispatch({
      type: STUDIO_MODEL.updateToolContentHeight,
      payload: height
    });
  };
  const getSubTabs = () => {
    // @ts-ignore
    return Object.values(
      Object.keys(LeftBottomMoreTabs).map((x) =>
        LeftBottomMoreTabs[x].map((y) => {
          return { ...y, key: x + '/' + y.key };
        })
      )
    ).flatMap((x) => x);
  };

  /**
   * 拖动回调
   * @param event
   * @param direction
   * @param {{offsetHeight: any}} elementRef
   */
  const resizeCallback = (
    event: any,
    direction: any,
    elementRef: {
      offsetHeight: any;
    }
  ) => {
    const centerContentHeight =
      document.documentElement.clientHeight -
      VIEW.headerHeight -
      VIEW.headerNavHeight -
      VIEW.footerHeight -
      VIEW.otherHeight -
      elementRef.offsetHeight;
    updateBottomHeight(elementRef.offsetHeight);
    updateCenterContentHeight(centerContentHeight);
    updateToolContentHeight(centerContentHeight - VIEW.leftMargin);
  };

  const renderTabPane = () => {
    const onChange = (activeKey: string) => {
      updateSelectBottomSubKey(activeKey.split('/')[1]);
    };
    const onEdit = async (
      targetKey: React.MouseEvent | React.KeyboardEvent | string,
      action: 'add' | 'remove'
    ) => {
      switch (action) {
        case 'add':
          await BottomTabRoute[bottomContainer.selectKey].onAdd(
            tabState?.[bottomContainer.selectKey] ?? [],
            bottomContainer.selectKey,
            currentData,
            refresh
          );
          break;
        case 'remove':
          const newPanes = tabState?.[bottomContainer.selectKey].filter(
            (pane) => pane.key !== targetKey
          );
          setTabState({ ...tabState, [bottomContainer.selectKey]: newPanes });
          break;
      }
    };
    return (
      <>
        <Tabs
          style={{ height: '32px', display: '-webkit-box' }}
          items={tabItems}
          type='card'
          onChange={(key: string) => {
            updateSelectBottomSubKey(key.split('/')[1]);
          }}
          activeKey={
            bottomContainer.selectKey +
            '/' +
            bottomContainer.selectSubKey[bottomContainer.selectKey]
          }
          size='small'
          tabPosition='top'
        />
        {BottomTabRoute[bottomContainer.selectKey] && (
          <Tabs
            style={{ height: '32px', display: '-webkit-box' }}
            items={tabState?.[bottomContainer.selectKey].map((x) => ({
              key: x.key,
              label: x.label
            }))}
            onEdit={onEdit}
            activeKey={
              bottomContainer.selectKey +
              '/' +
              bottomContainer.selectSubKey[bottomContainer.selectKey]
            }
            onChange={onChange}
            type='editable-card'
            size='small'
            tabPosition='top'
          />
        )}
      </>
    );
  };
  const renderItems = () => {
    return [
      ...LeftBottomSide.filter((tab) => {
        if (!tab.isShow) {
          return true;
        }
        if (parseInt(activeKey) < 0) {
          return TabsPageType.None;
        }
        const currentTab = (panes as TabsItemType[]).find((item) => item.key === activeKey);
        const show = tab.isShow(currentTab?.type ?? TabsPageType.None, currentTab?.subType);
        // 如果当前打开的菜单等于 状态存的菜单 且 菜单不显示状态下，先切换到项目key(因为项目key 不可能不显示) 在关闭这个
        // if current open menu equal status menu and menu is not show status, first switch to project key(because project key is not show) and close this
        // if (tab.key === bottomContainer.selectKey && !show) {
        //   updateSelectBottomKey(
        //     currentTab?.subType?.toLowerCase() === DIALECT.FLINKSQLENV
        //     || currentTab?.subType?.toLowerCase() === DIALECT.SCALA
        //     || currentTab?.subType?.toLowerCase() === DIALECT.JAVA
        //     || currentTab?.subType?.toLowerCase() === DIALECT.PYTHON_LONG
        //       ? LeftBottomKey.TOOLS_KEY // 如果当前打开的是flinksql环境，scala，java，python，切换到工具栏
        //       : currentTab?.subType?.toLowerCase() === DIALECT.FLINK_SQL
        //       || currentTab?.subType?.toLowerCase() === DIALECT.FLINKJAR
        //       || isSql(currentTab?.subType  ?? '')
        //     ? LeftBottomKey.CONSOLE_KEY : LeftBottomKey.TOOLS_KEY // 如果当前打开的是flinksql，flinkjar，切换到控制台
        //   );
        // }
        return show;
      }).map((x) => {
        return { ...x, key: x.key + '/' };
      }),
      ...getSubTabs(),
      ...(tabState?.[bottomContainer.selectKey] ?? [])
    ].map((item) => {
      return {
        ...item,
        children: (
          <ContentScroll height={props.height - VIEW.leftMargin}>{item.children}</ContentScroll>
        )
      };
    });
  };

  return (
    <MovableSidebar
      title={
        <ConfigProvider
          theme={{
            components: {
              Tabs: {
                horizontalMargin: '0',
                cardPaddingSM: '6px',
                horizontalItemPadding: '0'
              }
            }
          }}
        >
          <Space>
            <Title>{l(bottomContainer.selectKey)}</Title>
            {renderTabPane()}
          </Space>
        </ConfigProvider>
      }
      visible={bottomContainer.selectKey}
      style={{
        zIndex: 999,
        height: height,
        marginTop: 0,
        position: 'fixed',
        bottom: VIEW.footerHeight
      }}
      defaultSize={{ width: '100%', height: height }}
      minHeight={VIEW.midMargin}
      maxHeight={size.contentHeight - 40}
      onResize={(
        event: any,
        direction: any,
        elementRef: {
          offsetHeight: any;
        }
      ) => resizeCallback(event, direction, elementRef)}
      btnGroup={BottomBtnRoute[bottomContainer.selectKey]?.map((x) => (
        <CircleBtn
          key={x.key}
          icon={x.icon}
          onClick={async () => {
            const tabs = tabState?.[bottomContainer.selectKey] ?? [];
            await x.onClick?.(tabs, bottomContainer.selectKey, currentData, refresh);
            refresh();
          }}
        />
      ))}
      enable={{ top: true }}
      handlerMinimize={handleMinimize}
      maxWidth={width}
    >
      <Tabs
        activeKey={
          bottomContainer.selectKey +
          '/' +
          (bottomContainer.selectSubKey[bottomContainer.selectKey]
            ? bottomContainer.selectSubKey[bottomContainer.selectKey]
            : '')
        }
        items={renderItems()}
        tabBarStyle={{ display: 'none' }}
      />
    </MovableSidebar>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  bottomContainer: Studio.bottomContainer,
  tabs: Studio.tabs
}))(BottomContainer);
