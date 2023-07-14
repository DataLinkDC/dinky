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

import {Breadcrumb, Button, Layout, Menu, Space, Tabs, Tooltip} from 'antd';
import {connect, getDvaApp} from "umi";
import {
  CaretRightOutlined,
  CloseSquareOutlined,
  CopyOutlined, HomeOutlined,
  MinusOutlined,
  QuestionOutlined,
} from "@ant-design/icons";
import React, {useCallback, useEffect, useState} from "react";
import { StateType, VIEW} from "@/pages/DataStudio/model";
import MovableSidebar from "@/components/Sidebar/MovableSidebar";
import {Dispatch} from "@@/plugin-dva/types";
import {PersistGate} from 'redux-persist/integration/react';
import {getDataBase} from "@/pages/DataStudio/LeftContainer/MetaData/service";
import MiddleContainer from "@/pages/DataStudio/MiddleContainer";
import {TestDiv} from "@/components/StyledComponents";
import LeftContainer from "@/pages/DataStudio/LeftContainer";
import {LeftBottomSide, LeftSide, RightSide} from "@/pages/DataStudio/route";
import { l } from '@/utils/intl';
import {DataSources} from "@/types/RegCenter/data";
import {getLocalTheme} from "@/utils/function";

const {Header, Sider, Content} = Layout;

const headerStyle: React.CSSProperties = {
  display: "inline-flex",
  lineHeight: '32px',
  height: "32px",
  backgroundColor: '#fff',
};


const DataStudio = (props: any) => {
  const app = getDvaApp(); // 获取dva的实例
  const persistor = app._store.persist;
  const bottomHeight = props.bottomContainer.selectKey === "" ? 0 : props.bottomContainer.height;
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
    props.updateCenterContentHeight(centerContentHeight)
    props.updateToolContentHeight(centerContentHeight-VIEW.midMargin)
  }, []);

  useEffect(() => {
    window.addEventListener('resize', onResize);
    onResize();
    return () => {
      window.removeEventListener('resize', onResize);
    };
  }, [onResize]);
  useEffect(() => {
    getDataBase().then(res => props.saveDataBase(res))
    onResize();
  }, []);
  const renderLeftContainer = () => {
    return <LeftContainer size={size}/>
  }
  const renderRightContainer = () => {
    return <MovableSidebar
              contentHeight={size.contentHeight - VIEW.midMargin - bottomHeight}
              onResize={(event: any, direction: any, elementRef: { offsetWidth: any; }) => {
                props.updateRightWidth(elementRef.offsetWidth)
              }}
              title={props.rightContainer.selectKey}
              handlerMinimize={() => {
                props.updateSelectRightKey("")
              }}
              visible={props.rightContainer.selectKey !== ""}
              defaultSize={{
                width: props.rightContainer.width,
                height: props.rightContainer.height
              }}
              minWidth={300}
              maxWidth={size.width - 2 * VIEW.sideWidth - props.leftContainer.width - 200}
              enable={{left: true}}
    >
      <Space wrap>
        <Button icon={<QuestionOutlined/>} block type={"text"} shape={"circle"} size={"small"}/>
        <Button icon={<CloseSquareOutlined/>} block type={"text"} shape={"circle"}
                size={"small"}/>
        <Button icon={<CopyOutlined/>} block type={"text"} shape={"circle"} size={"small"}/>
      </Space>
    </MovableSidebar>
  }
  return (
    <PersistGate loading={null} persistor={persistor}>
            <TestDiv>
              <Layout style={{minHeight: "60vh"}}>
                <Header key={"h"} style={headerStyle}>
                  <div style={{width: (size.width - 2 * VIEW.paddingInline) / 2}}>
                    <Breadcrumb
                      items={
                      [
                        {
                          title: <HomeOutlined />,
                        },...(props.activeBreadcrumbTitle.split("/") as string[]).map(x=>{return {title:x}})
                      ]}
                    />

                  </div>
                  <div
                      style={{
                        width: (size.width - 2 * VIEW.paddingInline) / 2,
                        display: "flex",
                        flexDirection: "row-reverse"
                      }}>
                    <Space align={"end"} direction={"horizontal"} wrap>
                      <Button icon={<MinusOutlined/>} block type={"text"} shape={"circle"}/>
                      <Button icon={<MinusOutlined/>} block type={"text"} shape={"circle"}/>
                      <Tooltip title="search" placement="bottom">
                        <Button icon={<CaretRightOutlined/>} block type={"text"} shape={"circle"}/>
                      </Tooltip>
                    </Space>
                  </div>

                </Header>

                <Layout hasSider style={{minHeight: size.contentHeight}}>
                  <Sider collapsed collapsedWidth={40}>
                    <Menu
                        mode="inline"
                        selectedKeys={[props.leftContainer.selectKey]}
                        items={LeftSide.map(x=>{return {key:x.key,label:l(x.label),icon:x.icon}})}
                        style={{height: '50%', borderRight: 0}}
                        onClick={(item) => {
                          props.updateSelectLeftKey(item.key === props.leftContainer.selectKey ? '' : item.key)
                        }}
                    />
                    <Menu
                        mode="inline"
                        selectedKeys={[props.bottomContainer.selectKey]}
                        items={LeftBottomSide}
                        style={{display: 'flex', height: '50%', borderRight: 0, flexDirection: "column-reverse"}}
                        onClick={(item) => {
                          props.updateSelectBottomKey(item.key === props.bottomContainer.selectKey ? '' : item.key)
                        }}
                    />

                  </Sider>
                  <Content style={{
                    flexDirection: "column-reverse",
                    display: "flex",
                    height: size.contentHeight,
                  }}>

                    <MovableSidebar
                        title={<Tabs
                            items={[{
                              key: "test",
                              label: "123"
                            }]}
                            defaultActiveKey="StudioMsg" size="small" tabPosition="top" style={{
                          border: "1px solid #f0f0f0", height: "40px", margin: "0 32px"
                        }}>

                        </Tabs>}
                        visible={props.bottomContainer.selectKey !== ""}
                        style={{
                          border: "solid 1px #ddd",
                          background: "#f0f0f0",
                          zIndex: 999
                        }}
                        defaultSize={{
                          width: "100%",
                          height: props.bottomContainer.height
                        }}
                        minHeight={VIEW.midMargin+10}
                        maxHeight={size.contentHeight - 40}
                        onResize={(event: any, direction: any, elementRef: { offsetHeight: any; }, delta: any) => {
                          props.updateBottomHeight(elementRef.offsetHeight)
                          const centerContentHeight = document.documentElement.clientHeight - VIEW.headerHeight - VIEW.headerNavHeight - VIEW.footerHeight - VIEW.otherHeight - props.bottomContainer.height;
                          props.updateCenterContentHeight(centerContentHeight)
                          props.updateToolContentHeight(centerContentHeight-VIEW.midMargin)
                        }}
                        enable={{top: true}}
                        handlerMinimize={() => {
                          props.updateSelectBottomKey("")
                        }}
                    >


                    </MovableSidebar>
                    {/*<div>001</div>*/}
                    <div style={{
                      display: "flex",
                      position: "absolute",
                      top: VIEW.headerHeight,
                      width: size.width - VIEW.sideWidth * 2
                    }}>
                      {renderLeftContainer()}
                      <Content
                          style={{width: size.width - 2 * VIEW.sideWidth - props.leftContainer.width - props.rightContainer.width}}>
                        <MiddleContainer/>
                        {/*<CodeShow code={"123\n1\n1\n1\n1\n1\n1\n1\n1\n1n\n1\n1\n1"}*/}
                        {/*          height={size.contentHeight - bottomHeight - 60 + "px"}/>*/}
                      </Content>

                      {renderRightContainer()}

                    </div>
                  </Content>
                  <Sider collapsed collapsedWidth={40}>
                    <Menu
                        selectedKeys={[props.rightContainer.selectKey]}
                        theme={getLocalTheme() === "dark" ? "dark" : "light"}
                        mode="inline"
                        style={{height: '100%', borderRight: 0}}
                        items={RightSide}
                        onClick={(item) => {
                          props.updateSelectRightKey(item.key === props.rightContainer.selectKey ? '' : item.key)
                        }}
                    />
                  </Sider>
                </Layout>
              </Layout>
            </TestDiv>
    </PersistGate>
  );
};
const mapDispatchToProps = (dispatch: Dispatch) => ({
  updateToolContentHeight: (key: number) => dispatch({
    type: "Studio/updateToolContentHeight",
    payload: key,
  }),
  updateCenterContentHeight: (key: number) => dispatch({
    type: "Studio/updateCenterContentHeight",
    payload: key,
  }),
  updateSelectLeftKey: (key: string) => dispatch({
    type: "Studio/updateSelectLeftKey",
    payload: key,
  }),
  updateLeftWidth: (width: number) => dispatch({
    type: "Studio/updateLeftWidth",
    payload: width,
  }), updateSelectRightKey: (key: string) => dispatch({
    type: "Studio/updateSelectRightKey",
    payload: key,
  }),
  updateRightWidth: (width: number) => dispatch({
    type: "Studio/updateRightWidth",
    payload: width,
  }), updateSelectBottomKey: (key: string) => dispatch({
    type: "Studio/updateSelectBottomKey",
    payload: key,
  }),
  updateBottomHeight: (height: number) => dispatch({
    type: "Studio/updateBottomHeight",
    payload: height,
  }),
  saveDataBase: (data: DataSources.DataSource[]) => dispatch({
    type: "Studio/saveDataBase",
    payload: data,
  }),

});

export default connect(({Studio}: { Studio: StateType }) => ({
  leftContainer: Studio.leftContainer,
  rightContainer: Studio.rightContainer,
  bottomContainer: Studio.bottomContainer,
  activeBreadcrumbTitle: Studio.tabs.activeBreadcrumbTitle,
}), mapDispatchToProps)(DataStudio);
