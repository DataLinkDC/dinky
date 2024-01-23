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

import RightContextMenu from '@/components/RightContextMenu';
import ContentScroll from '@/components/Scroll/ContentScroll';
import { useEditor } from '@/hooks/useEditor';
import useThemeValue from '@/hooks/useThemeValue';
import { STUDIO_TAG_RIGHT_CONTEXT_MENU } from '@/pages/DataStudio/constants';
import {
  getCurrentTab,
  isDataStudioTabsItemType,
  isMetadataTabsItemType
} from '@/pages/DataStudio/function';
import {
  getBottomSelectKeyFromNodeClickJobType,
  getRightSelectKeyFromNodeClickJobType
} from '@/pages/DataStudio/LeftContainer/Project/function';
import { getTabIcon } from '@/pages/DataStudio/MiddleContainer/function';
import KeyBoard from '@/pages/DataStudio/MiddleContainer/KeyBoard';
import QuickGuide from '@/pages/DataStudio/MiddleContainer/QuickGuide';
import StudioEditor from '@/pages/DataStudio/MiddleContainer/StudioEditor';
import { StateType, STUDIO_MODEL, TabsItemType, TabsPageType } from '@/pages/DataStudio/model';
import { RightSide } from '@/pages/DataStudio/route';
import RightTagsRouter from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter';
import { ContextMenuPosition, InitContextMenuPosition } from '@/types/Public/state.d';
import { l } from '@/utils/intl';
import { connect } from '@@/exports';
import { ExclamationCircleFilled } from '@ant-design/icons';
import { ConfigProvider, Divider, Modal, Space, Tabs, Typography } from 'antd';
import { MenuInfo } from 'rc-menu/es/interface';
import React, { memo, useState } from 'react';

const { Text } = Typography;
const { confirm } = Modal;

type TargetKey = React.MouseEvent | React.KeyboardEvent | string;

const MiddleContainer = (props: any) => {
  const {
    tabs: { panes, activeKey },
    rightKey,
    dispatch
  } = props;
  const themeValue = useThemeValue();

  const { fullscreen } = useEditor();

  const [contextMenuPosition, setContextMenuPosition] =
    useState<ContextMenuPosition>(InitContextMenuPosition);
  const [contextMenuVisible, setContextMenuVisible] = useState(false);
  const [includeTab, setIncludeTab] = useState({});

  const updateRightKey = (key: string) => {
    const oldPane = panes.find((pane: TabsItemType) => pane.key === activeKey);
    const newPane = panes.find((pane: TabsItemType) => pane.key === key);

    let oldRightSideAvailableKey: string[] = [];
    let newRightSideAvailableKey: string[] = [];

    RightSide.forEach((x) => {
      if (!x.isShow) {
        oldRightSideAvailableKey.push(x.key);
        newRightSideAvailableKey.push(x.key);
        return;
      }

      if (x.isShow(oldPane.type, oldPane.subType)) {
        oldRightSideAvailableKey.push(x.key);
      }

      if (x.isShow(newPane.type, newPane.subType)) {
        newRightSideAvailableKey.push(x.key);
      }
    });

    if (!rightKey || newRightSideAvailableKey.includes(rightKey)) {
      return;
    }

    if (newRightSideAvailableKey.length === 0) {
      dispatch({
        type: STUDIO_MODEL.updateSelectRightKey,
        payload: ''
      });
      return;
    }

    const oldIndex = oldRightSideAvailableKey.findIndex((value) => value === rightKey);
    let selectKey: string;
    if (oldIndex >= newRightSideAvailableKey.length) {
      selectKey = newRightSideAvailableKey.pop() as string;
    } else {
      selectKey = newRightSideAvailableKey[oldIndex];
    }

    dispatch({
      type: STUDIO_MODEL.updateSelectRightKey,
      payload: selectKey
    });
  };

  const updateActiveKey = (item: TabsItemType) => {
    const { key, label, subType } = item;
    if (key === activeKey) {
      return;
    }

    setContextMenuVisible(false);
    updateRightKey(key);

    // 更新当前选中的 tab key
    dispatch({
      type: STUDIO_MODEL.updateTabsActiveKey,
      payload: key
    });

    // 根据 作业类型渲染 右侧选中菜单 key
    dispatch({
      type: STUDIO_MODEL.updateSelectRightKey,
      payload: getRightSelectKeyFromNodeClickJobType(subType ?? '')
    });

    // 根据 作业类型渲染 左下角选中菜单 key
    dispatch({
      type: STUDIO_MODEL.updateSelectBottomKey,
      payload: getBottomSelectKeyFromNodeClickJobType(subType ?? '')
    });

    // 这里如果加此项功能和定位功能重复 , 暂时注释
    // if (item.type === TabsPageType.project) {
    // 更新左侧树选中的 key
    // dispatch({
    //   type: STUDIO_MODEL.updateProjectSelectKey,
    //   payload: [treeKey]
    // });
    // }

    if (item.type === TabsPageType.metadata) {
      // 替换掉 . 为 /, 因为再 tree 里选中的 key 是 / 分割的
      const name = label.replace('.', '/');
      dispatch({
        type: STUDIO_MODEL.updateDatabaseSelectKey,
        payload: [name]
      });
    }
  };

  /**
   * 关闭所有标签
   */
  const handleCloseAllTabs = () => {
    dispatch({
      type: STUDIO_MODEL.closeAllTabs
    });
    setContextMenuVisible(false);
    dispatch({
      type: STUDIO_MODEL.updateSelectRightKey,
      payload: ''
    });
  };

  /**
   * 关闭其他标签
   */
  const handleCloseOtherTabs = () => {
    dispatch({
      type: STUDIO_MODEL.closeOtherTabs,
      payload: includeTab
    });
    setContextMenuVisible(false);
  };

  /**
   * the right click event
   */
  const handleRightClick = (info: React.MouseEvent<HTMLDivElement>, item: TabsItemType) => {
    // 阻止默认右键事件
    info.preventDefault();
    updateActiveKey(item);

    // 设置选中的值
    setIncludeTab(item);
    setContextMenuVisible(true);
    setContextMenuPosition((prevState) => ({
      ...prevState,
      width: '10vw',
      left: info.clientX + 10,
      top: info.clientY + 10
    }));
  };

  /**
   * 右键菜单的点击事件
   * @param {MenuInfo} node
   */
  const handleMenuClick = (node: MenuInfo) => {
    switch (node.key) {
      case 'closeAll':
        handleCloseAllTabs();
        break;
      case 'closeOther':
        handleCloseOtherTabs();
        break;
      default:
        break;
    }
  };

  /**
   * render tabs
   */
  const tabItems = panes.map((item: TabsItemType) => {
    const renderContent = () => {
      if (isDataStudioTabsItemType(item)) {
        if (parseInt(activeKey) < 0) {
          return TabsPageType.None;
        }

        return (
          <StudioEditor
            tabsItem={item}
            monacoInstance={item.monacoInstance}
            height={
              activeKey === item.key
                ? fullscreen
                  ? document.body.clientHeight
                  : props.centerContentHeight - 48
                : 0
            }
          />
        );
      }

      if (isMetadataTabsItemType(item)) {
        const params = item.params;
        return <RightTagsRouter tableInfo={params.tableInfo} queryParams={params.queryParams} />;
      }

      return <></>;
    };

    return {
      key: item.key,
      label: (
        <Space
          onClick={() => updateActiveKey(item)}
          size={0}
          onContextMenu={(e) => handleRightClick(e, item)}
          key={item.key}
        >
          {getTabIcon(item.icon, 16)}
          <Text type={item.isModified ? 'success' : undefined}>
            {item.label}
            {item.isModified ? ' *' : ''}
          </Text>
        </Space>
      ),
      children: (
        <ContentScroll
          height={
            activeKey === item.key
              ? fullscreen
                ? document.body.clientHeight
                : props.centerContentHeight - 40
              : 0
          }
        >
          {renderContent()}
        </ContentScroll>
      )
    };
  });

  /**
   * 关闭tab
   * @param {TargetKey} targetKey
   */
  const handleCloseTab = (targetKey: string) => {
    if (panes.length === 1) {
      dispatch({
        type: STUDIO_MODEL.updateSelectRightKey,
        payload: ''
      });
    }

    dispatch({
      type: STUDIO_MODEL.closeTab,
      payload: targetKey
    });
  };
  const closeTab = (targetKey: TargetKey) => {
    if (typeof targetKey == 'string') {
      const tab = getCurrentTab(panes, targetKey);
      if (tab?.isModified) {
        confirm({
          title: l('pages.datastudio.editor.notsave'),
          icon: <ExclamationCircleFilled />,
          content: l('pages.datastudio.editor.notsave.note'),
          onOk() {
            handleCloseTab(targetKey);
          }
        });
      } else {
        handleCloseTab(targetKey);
      }
    }
  };

  /**
   * render middle content
   */
  const renderMiddleContent = () => {
    if (tabItems?.length === 0) {
      return (
        // 这里必需设置高度，否则会导致下册内容无法正常拉动
        <div style={{ height: 0 }}>
          <KeyBoard />
          <Divider />
          <br />
          <br />
          <br />
          <QuickGuide />
        </div>
      );
    }

    return (
      <ConfigProvider
        theme={{
          components: {
            Tabs: {
              margin: 0,
              borderRadiusLG: 0
            }
          }
        }}
      >
        <Tabs
          className={'data-studio-tabs'}
          tabBarStyle={{ borderBlock: `1px solid ${themeValue.borderColor}` }}
          hideAdd
          // onTabClick={(active, e) => {updateActiveKey(active, tabItems[active].label)}}
          activeKey={activeKey}
          type='editable-card'
          onEdit={closeTab}
          items={tabItems}
        />
        <RightContextMenu
          onClick={handleMenuClick}
          items={STUDIO_TAG_RIGHT_CONTEXT_MENU}
          contextMenuPosition={contextMenuPosition}
          open={contextMenuVisible}
          openChange={() => setContextMenuVisible(false)}
        />
      </ConfigProvider>
    );
  };

  return <>{renderMiddleContent()}</>;
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  tabs: Studio.tabs,
  centerContentHeight: Studio.centerContentHeight,
  rightKey: Studio.rightContainer.selectKey
}))(memo(MiddleContainer));
