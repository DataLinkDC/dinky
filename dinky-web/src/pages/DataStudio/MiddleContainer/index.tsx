/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {ConfigProvider, Divider, Dropdown, Menu, Space, Tabs} from "antd";
import React, {useState} from "react";
import {connect} from "@@/exports";
import {
  DataStudioParams,
  MetadataParams,
  STUDIO_MODEL,
  StateType,
  TabsItemType,
  TabsPageType
} from "@/pages/DataStudio/model";
import RightTagsRouter from "@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter";
import KeyBoard from "@/pages/DataStudio/MiddleContainer/KeyBoard";
import {MenuInfo} from "rc-menu/es/interface";
import {STUDIO_TAG_RIGHT_CONTEXT_MENU} from "@/pages/DataStudio/constants";
import QuickGuide from "@/pages/DataStudio/MiddleContainer/QuickGuide";
import ContentScroll from "@/components/Scroll/ContentScroll";
import useThemeValue from "@/hooks/useThemeValue";
import Editor from "@/pages/DataStudio/MiddleContainer/Editor";
import {getTabIcon} from "@/pages/DataStudio/MiddleContainer/function";
import {RightSide} from "@/pages/DataStudio/route";


type TargetKey = React.MouseEvent | React.KeyboardEvent | string;

const MiddleContainer = (props: any) => {
  const {tabs: {panes, activeKey},rightKey, dispatch} = props;
  const themeValue = useThemeValue();

  const [contextMenuPosition, setContextMenuPosition] = useState({});
  const [contextMenuVisible, setContextMenuVisible] = useState(false);
  const [includeTab, setIncludeTab] = useState({});


  const updateRightKey = (key:string) => {
    let oldPane:TabsItemType;
    let newPane:TabsItemType;

    panes.forEach((pane: TabsItemType) => {
      if (pane.key === key) {
        newPane = pane
      }

      if (pane.key === activeKey) {
        oldPane = pane
      }
    })

    let oldRightSideAvailableKey:string[]=[];
    let newRightSideAvailableKey:string[]=[];

    RightSide.forEach(x=>{
      if (x.isShow){
        if (x.isShow(oldPane.type, oldPane.subType)) {
          oldRightSideAvailableKey.push(x.key)
        }if (x.isShow(newPane.type, newPane.subType)) {
          newRightSideAvailableKey.push(x.key)
        }
      }else {
        oldRightSideAvailableKey.push(x.key)
        newRightSideAvailableKey.push(x.key)
      }
    });

    if (!rightKey || newRightSideAvailableKey.includes(rightKey)) {
      return;
    }

    if (newRightSideAvailableKey.length === 0) {
      dispatch({
        type: STUDIO_MODEL.updateSelectRightKey,
        payload: '',
      })
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
      payload: selectKey,
    })
  }
  /**
   * 更新当前激活的tab
   * @param {string} key
   * @param eInfo
   */
  const updateActiveKey = (key: string, eInfo: any) => {
    if (key === activeKey) {
      return
    }

    const {target: {innerText}} = eInfo;
    if (!innerText){
      return;
    }

    setContextMenuVisible(false)
    updateRightKey(key);

    dispatch({
      type: STUDIO_MODEL.updateTabsActiveKey,
      payload: key,
    })

    // 替换掉 . 为 /, 因为再 tree 里选中的 key 是 / 分割的
    const replaceLabel = innerText.toString().replace('.', '/')
    dispatch({
      type: STUDIO_MODEL.updateDatabaseSelectKey,
      payload: [replaceLabel],
    })
  };

  /**
   * 关闭所有标签
   */
  const handleCloseAllTabs = () => {
    dispatch({
      type: STUDIO_MODEL.closeAllTabs,
    });
    setContextMenuVisible(false)
  }

  /**
   * 关闭其他标签
   */
  const handleCloseOtherTabs = () => {
    dispatch({
      type: STUDIO_MODEL.closeOtherTabs,
      payload: includeTab,
    });
    setContextMenuVisible(false)
  }

  /**
   * the right click event
   * @param info
   * @param item
   */
  const handleRightClick = (info: any, item: any) => {
    // 阻止默认右键事件
    info.preventDefault()
    updateActiveKey(item.key, info);

    // 替换掉 . 为 /, 因为再 tree 里选中的 key 是 / 分割的
    const replaceLabel = item.label.toString().replace('.', '/')
    dispatch({
      type: STUDIO_MODEL.updateDatabaseSelectKey,
      payload: [replaceLabel],
    })

    // 设置选中的值
    setIncludeTab(item);
    setContextMenuVisible(true)
    setContextMenuPosition({
      position: 'fixed', cursor: 'context-menu', width: '10vw', zIndex: 9999,
      left: info.clientX + 10, // + 10 是为了让鼠标不至于在选中的节点上 && 不遮住当前鼠标位置
      top: info.clientY + 10, // + 10 是为了让鼠标不至于在选中的节点上 && 不遮住当前鼠标位置
    });
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
   * 右键菜单
   * @returns {JSX.Element}
   */
  const renderRightClickMenu = () => {
    return <Dropdown
      arrow
      trigger={['contextMenu']}
      overlayStyle={{...contextMenuPosition}}
      menu={{items: STUDIO_TAG_RIGHT_CONTEXT_MENU, onClick: handleMenuClick}}
      open={contextMenuVisible}
      onOpenChange={setContextMenuVisible}>
      {/*占位*/}
      <div style={{...contextMenuPosition}}/>
    </Dropdown>
  }

  /**
   * render tabs
   */
  const tabItems = panes.map((item: TabsItemType) => {
    const renderContent = () => {
      switch (item.type) {
        case TabsPageType.metadata:
          const params = item.params as MetadataParams;
          return <RightTagsRouter tableInfo={params.tableInfo} queryParams={params.queryParams}/>
        case TabsPageType.project:
          if (parseInt(activeKey) < 0) {
            return TabsPageType.None
          }
          const v =item.params as DataStudioParams;
          return <Editor statement={v.taskData.statement}/>
        default:
          return <></>
      }
    }

    return {
      key: item.key,
      label:
        <Space onClick={() => updateActiveKey(item.key, {target: {innerText: item.label}})}
               size={0}
               onContextMenu={(e) => handleRightClick(e, item)}
               key={item.key}>{getTabIcon(item.icon, 16)}{item.label}
        </Space>,
      children:
        <ContentScroll height={activeKey === item.key ? props.centerContentHeight - 35 : 0}>
          {renderContent()}
        </ContentScroll>
    }
  })

  /**
   * 关闭tab
   * @param {TargetKey} targetKey
   */
  const closeTab = (targetKey: TargetKey) => {
    if (panes.length === 1) {
      dispatch({
        type: STUDIO_MODEL.updateSelectRightKey,
        payload: "",
      })
    }

    dispatch({
      type: STUDIO_MODEL.closeTab,
      payload: targetKey,
    })
  };

  /**
   * render middle content
   * @returns {JSX.Element}
   */
  const renderMiddleContent = () => {
    if (tabItems?.length === 0) {
      return <>
        <KeyBoard/>
        <Divider/><br/><br/><br/>
        <QuickGuide/>
      </>
    }

    return <>
      <ConfigProvider theme={{
        components: {
          Tabs: {
            margin: 0,
            borderRadiusLG: 0
          }
        }
      }}>
        <Tabs
          className={"data-studio-tabs"}
          tabBarStyle={{borderBlock: `1px solid ${themeValue.borderColor}`}}
          hideAdd
          onTabClick={(active, e) => updateActiveKey(active, e)}
          activeKey={activeKey}
          type="editable-card"
          onEdit={closeTab}
          items={tabItems}
        />
        {renderRightClickMenu()}
      </ConfigProvider>
    </>
  }

  return <>
    {renderMiddleContent()}
  </>
}

export default connect(({Studio}: { Studio: StateType }) => ({
  tabs: Studio.tabs,
  centerContentHeight: Studio.centerContentHeight,
  rightKey: Studio.rightContainer.selectKey
}))(MiddleContainer);
