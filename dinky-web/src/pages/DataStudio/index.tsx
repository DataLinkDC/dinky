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

import {Layout, Menu, theme} from 'antd';
import {connect, getDvaApp} from "umi";
import React, {Fragment, useCallback, useEffect, useState} from "react";
import {StateType, TabsItemType, TabsPageType, VIEW} from "@/pages/DataStudio/model";
import {PersistGate} from 'redux-persist/integration/react';
import {getDataBase} from "@/pages/DataStudio/LeftContainer/MetaData/service";
import MiddleContainer from "@/pages/DataStudio/MiddleContainer";
import LeftContainer from "@/pages/DataStudio/LeftContainer";
import {LeftBottomMoreTabs, LeftBottomSide, LeftSide, RightSide} from "@/pages/DataStudio/route";
import {mapDispatchToProps} from "@/pages/DataStudio/function";
import BottomContainer from "@/pages/DataStudio/BottomContainer";
import HeaderContainer from "@/pages/DataStudio/HeaderContainer";
import RightContainer from "@/pages/DataStudio/RightContainer";
import {getConsoleData} from "@/pages/DataStudio/BottomContainer/Console/service";
import useThemeValue from "@/hooks/useThemeValue";

const {Sider, Content} = Layout;

const { useToken } = theme;
const DataStudio = (props: any) => {
  const {
    bottomContainer, leftContainer, rightContainer, saveDataBase, updateToolContentHeight,updateBottomConsole
    , updateCenterContentHeight, updateSelectLeftKey, updateLeftWidth, updateSelectRightKey
    , updateRightWidth, updateSelectBottomKey, updateBottomHeight, activeBreadcrumbTitle, updateSelectBottomSubKey, tabs
  } = props
  const {token} = useToken();
  const themeValue = useThemeValue();

  const getActiveTabType: () => (TabsPageType) = () => {
    if (parseInt(tabs.activeKey) < 0) {
      return TabsPageType.None
    }
    return (tabs.panes as TabsItemType[]).find(item => item.key === tabs.activeKey)?.type || TabsPageType.None
  }

  const app = getDvaApp(); // 获取dva的实例
  const persistor = app._store.persist;
  const bottomHeight = bottomContainer.selectKey === "" ? 0 : bottomContainer.height;
  const [size, setSize] = useState({
    width: document.documentElement.clientWidth,
    height: document.documentElement.clientHeight,
    contentHeight: document.documentElement.clientHeight - VIEW.headerHeight - VIEW.headerNavHeight - VIEW.footerHeight - VIEW.otherHeight,
  });

  const onResize = useCallback(() => {
    setSize({
      width: document.documentElement.clientWidth,
      height: document.documentElement.clientHeight,
      contentHeight: document.documentElement.clientHeight - VIEW.headerHeight - VIEW.headerNavHeight - VIEW.footerHeight - VIEW.otherHeight,
    })
    const centerContentHeight = document.documentElement.clientHeight - VIEW.headerHeight - VIEW.headerNavHeight - VIEW.footerHeight - VIEW.otherHeight - bottomHeight;
    updateCenterContentHeight(centerContentHeight)
    updateToolContentHeight(centerContentHeight - VIEW.midMargin)
  }, []);

  useEffect(() => {
    window.addEventListener('resize', onResize);
    onResize();
    return () => {
      window.removeEventListener('resize', onResize);
    };
  }, [onResize]);


  useEffect(() => {
    getDataBase().then(res => saveDataBase(res))
    getConsoleData().then(res => updateBottomConsole(res))
    onResize();
  }, []);


  /**
   * 渲染头部
   * @returns {JSX.Element}
   */
  const renderHeaderContainer = () => {
    return <HeaderContainer size={size} activeBreadcrumbTitle={activeBreadcrumbTitle}/>
  }


  /**
   * 渲染左侧侧边栏
   * @returns {JSX.Element}
   */
  const renderLeftContainer = () => {
    return <LeftContainer size={size}/>
  }

  /**
   * 渲染右侧侧边栏
   * @returns {JSX.Element}
   */
  const renderRightContainer = () => {
    return <RightContainer size={size} bottomHeight={bottomHeight}/>
  }
  return (
    <PersistGate loading={null} persistor={persistor}>
      <Fragment>
        <div style={{marginInline: -10, marginBlock: -5}}>
          {/* 渲染 header */}
          {renderHeaderContainer()}
          <Layout hasSider style={{minHeight: size.contentHeight,paddingInline:0}}>
            {/*渲染左侧侧边栏*/}
            <Sider collapsed collapsedWidth={40}>
              <Menu
                mode="inline"
                selectedKeys={[leftContainer.selectKey]}
                items={LeftSide.map(x => {
                  return {key: x.key, label: x.label, icon: x.icon}
                })}
                style={{height: '50%',borderBlockStart:"1px solid "+themeValue.borderColor,borderInlineEnd:"1px solid "+themeValue.borderColor}}
                onClick={(item) => {
                  updateSelectLeftKey(item.key === leftContainer.selectKey ? '' : item.key)
                }}
              />


              {/*底部菜单*/}
              <Menu
                mode="inline"
                selectedKeys={[bottomContainer.selectKey]}
                items={LeftBottomSide.map(x => {
                  return {key: x.key, label: x.label, icon: x.icon}
                })}
                style={{
                  display: 'flex',
                  height: '50%',
                  flexDirection: "column-reverse",
                  borderInlineEnd:"1px solid "+themeValue.borderColor
                }}
                onClick={(item) => {
                  updateSelectBottomKey(item.key === bottomContainer.selectKey ? '' : item.key)
                  if (bottomContainer.selectKey !== '' && (!bottomContainer.selectSubKey[item.key] && LeftBottomMoreTabs[item.key])) {
                    updateSelectBottomSubKey(LeftBottomMoreTabs[item.key][0].key)
                  }
                }}
              />

            </Sider>

            <Content style={{
              flexDirection: "column-reverse",
              display: "flex",
              height: size.contentHeight,
            }}>
              {/*渲染底部内容*/}
              {<BottomContainer size={size}/>}


              <div style={{
                display: "flex",
                position: "absolute",
                top: VIEW.headerHeight,
                width: size.width - VIEW.sideWidth * 2
              }}>
                {renderLeftContainer()}

                <Content
                  style={{width: size.width - 2 * VIEW.sideWidth - leftContainer.width - rightContainer.width}}>
                  <MiddleContainer/>
                </Content>

                {renderRightContainer()}

              </div>
            </Content>

            {/* 渲染右侧侧边栏 */}
            <Sider collapsed collapsedWidth={40}>
              <Menu
                selectedKeys={[rightContainer.selectKey]}
                mode="inline"
                style={{height: '100%', borderInlineStart: "1px solid "+themeValue.borderColor,borderBlockStart:"1px solid "+themeValue.borderColor}}
                items={RightSide.filter(x => {
                  if (!x.isShow) {
                    return true
                  }
                  return x.isShow(getActiveTabType())
                }).map(x => {
                  return {key: x.key, label: x.label, icon: x.icon}
                })}
                onClick={(item) => {
                  updateSelectRightKey(item.key === rightContainer.selectKey ? '' : item.key)
                }}
              />
            </Sider>
          </Layout>
        </div>
      </Fragment>
    </PersistGate>
  );
};


export default connect(({Studio}: { Studio: StateType }) => ({
  leftContainer: Studio.leftContainer,
  rightContainer: Studio.rightContainer,
  bottomContainer: Studio.bottomContainer,
  activeBreadcrumbTitle: Studio.tabs.activeBreadcrumbTitle,
  tabs: Studio.tabs,
}), mapDispatchToProps)(DataStudio);
