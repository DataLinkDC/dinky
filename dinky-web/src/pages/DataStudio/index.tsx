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
import {connect} from "umi";
import {
  AppstoreAddOutlined,
  AppstoreOutlined,
  CaretRightOutlined,
  CloseSquareOutlined,
  CopyOutlined,
  DatabaseOutlined,
  ExclamationCircleOutlined,
  FolderOutlined,
  HistoryOutlined,
  InfoCircleOutlined,
  InsertRowAboveOutlined,
  MinusOutlined, PlayCircleOutlined,
  QuestionOutlined,
  RightSquareOutlined,
  SettingOutlined,
  TableOutlined,
} from "@ant-design/icons";
import {Resizable} from 're-resizable';
import React, {Fragment, useCallback, useEffect, useState} from "react";
import {StateType} from "@/pages/DataStudio/model";
import {AndroidOutlined, AppleOutlined} from '@ant-design/icons';
import CodeShow from "@/components/CustomEditor/CodeShow";
import {ProCard} from "@ant-design/pro-components";
import MovableSidebar from "@/components/Sidebar/MovableSidebar";
import {Dispatch} from "@@/plugin-dva/types";
import {PageContainer} from "@ant-design/pro-layout";
import {convertCodeEditTheme} from "@/utils/function";
import MonacoEditor from "react-monaco-editor";
import {ClusterConfigIcon} from "@/components/Icons/HomeIcon";
import {RunningBtn} from "@/components/CallBackButton/RunningBtn";

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
    icon: <PlayCircleOutlined />,
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
  const VIEW = {
    headerHeight: 32,
    headerNavHeight: 55,
    footerHeight: 25,
    sideWidth: 40,
    leftToolWidth: 300,
    marginTop: 84,
    topHeight: 35.6,
    bottomHeight: 127,
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
      contentHeight={size.contentHeight - VIEW.midMargin - props.bottomContainer.height - 10}
      onResize={(event, direction, elementRef, delta) => {
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
      maxWidth={size.width-2*VIEW.sideWidth-props.rightContainer.width-200}
      enable={{right: true}}
      content={<><Space wrap>
        <Button icon={<QuestionOutlined/>} block type={"text"} shape={"circle"} size={"small"}/>
        <Button icon={<CloseSquareOutlined/>} block type={"text"} shape={"circle"}
                size={"small"}/>
        <Button icon={<CopyOutlined/>} block type={"text"} shape={"circle"} size={"small"}/>
      </Space>
        <div style={{
          height: 100,
          overflow: 'auto'
        }}>

          <ProCard
            title="左右分栏带标题"
            extra="2019年9月28日"
            split={'vertical'}
            bordered
            headerBordered
          >
            <ProCard title="左侧详情" colSpan="50%">
              <div style={{height: 700}}>左侧内容</div>
            </ProCard>
            <ProCard title="流量占用情况">
              <div style={{height: 700}}>右侧内容</div>
            </ProCard>
          </ProCard>
        </div>
      </>}>
    </MovableSidebar>
  }
  const renderRightContainer = () => {
    return <MovableSidebar
      contentHeight={size.contentHeight - VIEW.midMargin - props.bottomContainer.height - 10}
      onResize={(event, direction, elementRef, delta) => {
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
      maxWidth={size.width-2*VIEW.sideWidth-props.leftContainer.width-200}
      enable={{left: true}}
      content={<><Space wrap>
        <Button icon={<QuestionOutlined/>} block type={"text"} shape={"circle"} size={"small"}/>
        <Button icon={<CloseSquareOutlined/>} block type={"text"} shape={"circle"}
                size={"small"}/>
        <Button icon={<CopyOutlined/>} block type={"text"} shape={"circle"} size={"small"}/>
      </Space>
      </>}>
    </MovableSidebar>
  }
  return (
    <Fragment>
      <Layout style={{minHeight: "60vh"}}>
        <Header key={"h"} style={headerStyle}>
          <div style={{width: (size.width - 2 * VIEW.paddingInline) / 2}}>1</div>
          <div
            style={{width: (size.width - 2 * VIEW.paddingInline) / 2, display: "flex", flexDirection: "row-reverse"}}>
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
                props.updateBottomHeight(item.key === props.bottomContainer.selectKey ? 0 : props.bottomContainer.height)
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
              onResize={(event, direction, elementRef, delta) => {
                props.updateBottomHeight(elementRef.offsetHeight)
              }}
              enable={{top: true}}
              handlerMinimize={() => {
                props.updateSelectBottomKey("")
                props.updateBottomHeight(0)
              }}

            ></MovableSidebar>
            {/*<div>001</div>*/}
            <div style={{
              display: "flex",
              position: "absolute",
              top: VIEW.headerHeight,
              width: size.width - VIEW.sideWidth * 2
            }}>

              {renderLeftContainer()}

              <Content style={{width:size.width-2*VIEW.sideWidth-props.leftContainer.width-props.rightContainer.width}}>
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
                          height={size.contentHeight - props.bottomContainer.height - 60 + "px"}/>
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
