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

import {Button, Layout, Menu, Space, Tabs, Tooltip} from 'antd';
import {connect, getDvaApp} from "umi";
import {
  AndroidOutlined,
  AppleOutlined,
  AppstoreAddOutlined,
  CaretRightOutlined,
  CloseSquareOutlined,
  CopyOutlined,
  DatabaseOutlined,
  FolderOutlined,
  HistoryOutlined,
  InfoCircleOutlined,
  MinusOutlined,
  PlayCircleOutlined,
  QuestionOutlined,
  RightSquareOutlined,
  SettingOutlined,
  TableOutlined,
} from "@ant-design/icons";
import React, {Fragment, useCallback, useEffect, useState} from "react";
import {StateType} from "@/pages/DataStudio/model";
import CodeShow from "@/components/CustomEditor/CodeShow";
import {ProCard} from "@ant-design/pro-components";
import MovableSidebar from "@/components/Sidebar/MovableSidebar";
import {Dispatch} from "@@/plugin-dva/types";
import { PersistGate } from 'redux-persist/integration/react';

const {Header, Footer, Sider, Content} = Layout;

const headerStyle: React.CSSProperties = {
  display: "inline-flex",
  lineHeight: '32px',
  height: "32px",
  backgroundColor: '#fff',
};

const leftSide = [
  {
    key: 'project',
    icon: <AppstoreAddOutlined/>,
    label: '项目',
  },
  {
    key: 'structure',
    icon: <TableOutlined/>,
    label: '结构',
  },
  {
    key: 'metadata',
    icon: <DatabaseOutlined/>,
    label: '元数据',
  }
]
const rightSide = [
  {
    key: 'jobConfig',
    icon: <SettingOutlined/>,
    label: '作业配置',
  },
  {
    key: 'executeConfig',
    icon: <PlayCircleOutlined/>,
    label: '执行配置',
  },
  {
    key: 'savePoint',
    icon: <FolderOutlined/>,
    label: '保存点',
  },
  {
    key: 'versionHistory',
    icon: <HistoryOutlined/>,
    label: '版本历史',
  }, {
    key: 'jobInfo',
    icon: <InfoCircleOutlined/>,
    label: '作业信息',
  }
]
const leftBottomSide = [
  {
    key: 'console',
    icon: <RightSquareOutlined/>,
    label: '控制台',
  }
]
const DataStudio = (props: any) => {
  const {} = props;
  const app = getDvaApp(); // 获取dva的实例
  const persistor = app._store.persist;
  const bottomHeight = props.bottomContainer.selectKey === "" ? 0 : props.bottomContainer.height;
  const VIEW = {
    headerHeight: 32,
    headerNavHeight: 55,
    footerHeight: 25,
    sideWidth: 40,
    leftToolWidth: 180,
    marginTop: 84,
    topHeight: 35.6,
    bottomHeight: 100,
    rightMargin: 32,
    leftMargin: 36,
    midMargin: 46,
    otherHeight: 10,
    paddingInline: 50,
  };
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
  }, []);

  useEffect(() => {
    window.addEventListener('resize', onResize);
    onResize();
    return () => {
      window.removeEventListener('resize', onResize);
    };
  }, [onResize]);

  const renderLeftContainer = () => {
    return <MovableSidebar
      contentHeight={size.contentHeight - VIEW.midMargin - bottomHeight - 10}
      onResize={(event: any, direction: any, elementRef: { offsetWidth: any; }, delta: any) => {
        props.updateLeftWidth(elementRef.offsetWidth)
      }}
      title={props.leftContainer.selectKey}
      handlerMinimize={() => {
        props.updateSelectLeftKey("")
      }}
      visible={props.leftContainer.selectKey !== ""}
      defaultSize={{
        width: props.leftContainer.width,
        height: props.leftContainer.height
      }}
      minWidth={200}
      maxWidth={size.width - 2 * VIEW.sideWidth - props.rightContainer.width - 200}
      enable={{right: true}}
    >
      <><Space wrap>
        <Button icon={<QuestionOutlined/>} block type={"text"} shape={"circle"} size={"small"}/>
        <Button icon={<CloseSquareOutlined/>} block type={"text"} shape={"circle"}
                size={"small"}/>
        <Button icon={<CopyOutlined/>} block type={"text"} shape={"circle"} size={"small"}/>
      </Space>

        <ProCard
          split={'vertical'}
          style={{height: "100%", overflow: 'auto'}}
          bordered ghost
          headerBordered
        >

        </ProCard>
      </>
    </MovableSidebar>
  }
  const renderRightContainer = () => {
    return <MovableSidebar
      contentHeight={size.contentHeight - VIEW.midMargin - bottomHeight - 10}
      onResize={(event: any, direction: any, elementRef: { offsetWidth: any; }, delta: any) => {
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
      minWidth={200}
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
    <Fragment>
      <Layout style={{minHeight: "60vh"}}>
        <Header key={"h"} style={headerStyle}>
          <div style={{width: (size.width - 2 * VIEW.paddingInline) / 2}}>1</div>
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

        <Layout hasSider
                style={{minHeight: size.contentHeight}}>
          <Sider collapsed collapsedWidth={40}>
            <Menu
              // theme="dark"
              mode="inline"
              selectedKeys={[props.leftContainer.selectKey]}
              items={leftSide}
              style={{height: '50%', borderRight: 0}}
              onClick={(item) => {
                props.updateSelectLeftKey(item.key === props.leftContainer.selectKey ? '' : item.key)
              }}
            />
            <Menu
              // theme="dark"
              mode="inline"
              selectedKeys={[props.bottomContainer.selectKey]}
              items={leftBottomSide}
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
              minHeight={VIEW.midMargin}
              maxHeight={size.contentHeight - 40}
              onResize={(event: any, direction: any, elementRef: { offsetHeight: any; }, delta: any) => {
                props.updateBottomHeight(elementRef.offsetHeight)
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
                <Tabs
                  size={"small"}
                  tabBarGutter={10}
                  defaultActiveKey="2"
                  items={[AppleOutlined, AndroidOutlined].map((Icon, i) => {
                    const id = String(i + 1);

                    return {
                      label: (
                        <span>
                  <Icon/>
                  Tab {id}
                </span>
                      ),
                      key: id,
                    };
                  })}
                />
                <CodeShow code={"123\n1\n1\n1\n1\n1\n1\n1\n1\n1n\n1\n1\n1"}
                          height={size.contentHeight - bottomHeight - 60 + "px"}/>
              </Content>

              {renderRightContainer()}

            </div>
          </Content>
          <Sider collapsed collapsedWidth={40}>
            <Menu
              selectedKeys={[props.rightContainer.selectKey]}
              // theme="dark"
              mode="inline"
              style={{height: '100%', borderRight: 0}}
              items={rightSide}
              onClick={(item) => {
                props.updateSelectRightKey(item.key === props.rightContainer.selectKey ? '' : item.key)
              }}
            />
          </Sider>
        </Layout>
      </Layout>
    </Fragment>
    </PersistGate>
  );
};
const mapDispatchToProps = (dispatch: Dispatch) => ({
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
});

export default connect(({Studio}: { Studio: StateType }) => ({
  leftContainer: Studio.leftContainer,
  rightContainer: Studio.rightContainer,
  bottomContainer: Studio.bottomContainer,
}), mapDispatchToProps)(DataStudio);
