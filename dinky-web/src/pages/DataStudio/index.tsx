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

import {Affix, Button, Card, Col, Form, Layout, Menu, Row, Space, Tabs} from 'antd';
import {connect} from "umi";
import {
  CloseSquareOutlined,
  CopyOutlined,
  MinusOutlined, QuestionOutlined,
  SearchOutlined,
  UploadOutlined,
  UserOutlined,
  VideoCameraOutlined
} from "@ant-design/icons";
import {Resizable} from 're-resizable';
import {PageContainer, ProBreadcrumb, ProLayout} from '@ant-design/pro-layout';
import React, {useCallback, useEffect, useRef, useState} from "react";
import DraggleVerticalLayout from '@/components/DraggleLayout/DraggleVerticalLayout';
import styles from './index.less';
import DraggleLayout from "@/components/DraggleLayout";
import {StateType} from "@/pages/DataStudio/model";
import {AndroidOutlined, AppleOutlined} from '@ant-design/icons';
import CodeShow from "@/components/CustomEditor/CodeShow";
import {ProCard} from "@ant-design/pro-components";

const {Header, Footer, Sider, Content} = Layout;

const headerStyle: React.CSSProperties = {
  textAlign: 'center',
  color: '#fff',
  lineHeight: '64px',
  backgroundColor: '#7dbcea',
};

const contentStyle: React.CSSProperties = {
  textAlign: 'center',
  minHeight: 120,
  lineHeight: '120px',
  color: '#fff',
  backgroundColor: '#108ee9',
  overflow: 'auto'
};

const siderStyle: React.CSSProperties = {
  textAlign: 'center',
  lineHeight: '120px',
  color: '#fff',
  backgroundColor: '#3ba0e9',
  overflow: 'auto'
};

const footerStyle: React.CSSProperties = {
  textAlign: 'center',
  color: '#fff',
  backgroundColor: '#7dbcea',
  height: "25px",
  paddingInline: "0px",
  paddingBlock: "0px"
};
const leftSide = [
  {
    key: '1',
    icon: <UserOutlined/>,
    label: '项目',
  },
  {
    key: '2',
    icon: <VideoCameraOutlined/>,
    label: 'nav 2',
  },
  {
    key: '3',
    icon: <UploadOutlined/>,
    label: 'nav 3',
  }
]
const DataStudio = (props: any) => {
  const {isFullScreen, rightClickMenu, toolHeight, toolLeftWidth, toolRightWidth, dispatch} = props;
  const [contentFooterHeight,setContentFooterHeight] = useState(200)

  const [contentBottomMoveHeight,setContentBottomMoveHeight] = useState(0)

  const reRef=useRef(null)

  const [form] = Form.useForm();
  const VIEW = {
    headerHeight:64,
    headerNavHeight:55,
    footerHeight:25,
    leftToolWidth: 300,
    marginTop: 84,
    topHeight: 35.6,
    bottomHeight: 127,
    rightMargin: 32,
    leftMargin: 36,
    midMargin: 46,
    otherHeight:10
  };
  const [size, setSize] = useState({
    width: document.documentElement.clientWidth,
    height: document.documentElement.clientHeight,
  });

  const onResize = useCallback(() => {
    console.log(document.documentElement.clientWidth, document.documentElement.clientHeight, VIEW)
    setSize({
      width: document.documentElement.clientWidth,
      height: document.documentElement.clientHeight,
    })
    console.log(size.height - VIEW.marginTop)
    console.log(reRef.current)
  }, []);

  useEffect(() => {
    window.addEventListener('resize', onResize);
    onResize();
    return () => {
      window.removeEventListener('resize', onResize);
    };
  }, [onResize]);
  // return (
  //   <div >
  //     <Card bordered={false} className={styles.card} size="small" id="studio_card" style={{marginBottom: 0}}>
  //       <DraggleVerticalLayout
  //         containerWidth={size.width}
  //         containerHeight={(size.height - VIEW.marginTop)}
  //         min={(VIEW.topHeight)}
  //         max={(size.height - VIEW.bottomHeight)}
  //         initTopHeight={VIEW.topHeight}
  //         handler={
  //           <div
  //             style={{
  //               height: 4,
  //               width: '100%',
  //               background: 'rgb(240, 240, 240)',
  //             }}
  //           />
  //         }
  //       >
  //         <Row>
  //           <DraggleLayout
  //             containerWidth={size.width}
  //             containerHeight={toolHeight}
  //             min={VIEW.leftMargin + VIEW.midMargin}
  //             max={size.width - VIEW.rightMargin}
  //             initLeftWidth={size.width - toolRightWidth}
  //             isLeft={false}
  //             handler={
  //               <div
  //                 style={{
  //                   width: 4,
  //                   height: '100%',
  //                   background: 'rgb(240, 240, 240)',
  //                 }}
  //               />
  //             }
  //           >
  //             <DraggleLayout
  //               containerWidth={size.width - toolRightWidth}
  //               containerHeight={toolHeight}
  //               min={VIEW.leftMargin}
  //               max={size.width - VIEW.rightMargin - VIEW.midMargin}
  //               initLeftWidth={toolLeftWidth}
  //               isLeft={true}
  //               handler={
  //                 <div
  //                   style={{
  //                     width: 4,
  //                     height: '100%',
  //                     background: 'rgb(240, 240, 240)',
  //                   }}
  //                 />
  //               }
  //             >
  //               <Col className={styles["vertical-tabs"]}>
  //                 {/*<StudioLeftTool style={{*/}
  //                 {/*  display: 'flex',*/}
  //                 {/*  alignItems: 'center',*/}
  //                 {/*  justifyContent: 'center',*/}
  //                 {/*}}/>*/}
  //               </Col>
  //               <Col>
  //                 <CodeShow code={""}/>
  //                 {/*{!isFullScreen ? <StudioTabs width={size.width - toolRightWidth - toolLeftWidth}/> : undefined}*/}
  //               </Col>
  //             </DraggleLayout>
  //             <Col id='StudioRightTool' className={styles["vertical-tabs"]}>
  //               {/*<StudioRightTool form={form}/>*/}
  //             </Col>
  //           </DraggleLayout>
  //         </Row>
  //         <Row>
  //           <Col span={24}>
  //             {/*<StudioConsole height={size.height - toolHeight - VIEW.marginTop}/>*/}
  //           </Col>
  //         </Row>
  //       </DraggleVerticalLayout>
  //     </Card>
  //   </div>
  // )

  return (
    <Layout style={{minHeight: "60vh"}}>
      <Header style={headerStyle}>Header</Header>
      <Layout hasSider style={{minHeight: size.height-VIEW.headerHeight-VIEW.headerNavHeight-VIEW.footerHeight-VIEW.otherHeight}}>
        <Sider collapsed collapsedWidth={40}>
          <Menu
            // theme="dark"
            mode="inline"
            defaultSelectedKeys={['1']}
            items={leftSide}
            style={{height: '50%', borderRight: 0}}
          />
          <Menu
            // theme="dark"
            mode="inline"
            defaultSelectedKeys={['1']}
            items={leftSide}
            style={{display: 'flex', height: '50%', borderRight: 0, flexDirection: "column-reverse"}}
          />

        </Sider>
        <Content style={{flexDirection:"column-reverse",display:"flex",height:size.height-VIEW.headerHeight-VIEW.headerNavHeight-VIEW.footerHeight-VIEW.otherHeight}}>
          <Resizable ref={reRef}
                     style={{
                       display: "flex",
                       alignItems: "center",
                       justifyContent: "center",
                       border: "solid 1px #ddd",
                       flexDirection: "column-reverse",
                       background: "#f0f0f0"
                     }}
                     defaultSize={{
                       width: "100%",
                       height: contentFooterHeight
                     }}
                     onResize={(event, direction, elementRef, delta)=>{
                       console.log(event)
                       setContentBottomMoveHeight((origin)=>{
                         return delta.height ;
                       })
                     }}
          >
            <div>001</div>
          </Resizable>
          <div style={{display: "flex"}}>
            <Resizable
              defaultSize={{
                width: 500,
                height: '100%'
              }}
              minWidth={200}
              maxWidth={1200}
              enable={{right: true}}
            >

              <PageContainer
                fixedHeader
                header={{
                  title: "dd",
                  extra: [
                    <Button icon={<MinusOutlined/>} block type={"text"} shape={"circle"}/>
                  ],
                  style: {borderBottom: '1px solid black'}
                }}
              >
                <Space wrap>
                  <Button icon={<QuestionOutlined/>} block type={"text"} shape={"circle"} size={"small"}/>
                  <Button icon={<CloseSquareOutlined/>} block type={"text"} shape={"circle"}
                          size={"small"}/>
                  <Button icon={<CopyOutlined/>} block type={"text"} shape={"circle"} size={"small"}/>
                </Space>
                <div style={{height: size.height-VIEW.headerHeight-VIEW.headerNavHeight-VIEW.footerHeight-contentFooterHeight-46-24-contentBottomMoveHeight-10-VIEW.otherHeight, overflow: 'auto'}}>

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
              </PageContainer>

            </Resizable>
            <Content>
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
              <CodeShow code={"123"}/>

              {/*   <div >*/}
              {/*     <Card bordered={false} className={styles.card} size="small" id="studio_card" style={{marginBottom: 0}}>*/}
              {/*       <DraggleVerticalLayout*/}
              {/*         containerWidth={size.width}*/}
              {/*         containerHeight={(size.height - VIEW.marginTop)}*/}
              {/*         min={(VIEW.topHeight)}*/}
              {/*         max={(size.height - VIEW.bottomHeight)}*/}
              {/*         initTopHeight={VIEW.topHeight}*/}
              {/*         handler={*/}
              {/*           <div*/}
              {/*             style={{*/}
              {/*               height: 4,*/}
              {/*               width: '100%',*/}
              {/*               background: 'rgb(240, 240, 240)',*/}
              {/*             }}*/}
              {/*           />*/}
              {/*         }*/}
              {/*       >*/}
              {/*         <Row>*/}
              {/*           <DraggleLayout*/}
              {/*             containerWidth={size.width}*/}
              {/*             containerHeight={toolHeight}*/}
              {/*             min={VIEW.leftMargin + VIEW.midMargin}*/}
              {/*             max={size.width - VIEW.rightMargin}*/}
              {/*             initLeftWidth={size.width - toolRightWidth}*/}
              {/*             isLeft={false}*/}
              {/*             handler={*/}
              {/*               <div*/}
              {/*                 style={{*/}
              {/*                   width: 4,*/}
              {/*                   height: '100%',*/}
              {/*                   background: 'rgb(240, 240, 240)',*/}
              {/*                 }}*/}
              {/*               />*/}
              {/*             }*/}
              {/*           >*/}
              {/*             <DraggleLayout*/}
              {/*               containerWidth={size.width - toolRightWidth}*/}
              {/*               containerHeight={toolHeight}*/}
              {/*               min={VIEW.leftMargin}*/}
              {/*               max={size.width - VIEW.rightMargin - VIEW.midMargin}*/}
              {/*               initLeftWidth={toolLeftWidth}*/}
              {/*               isLeft={true}*/}
              {/*               handler={*/}
              {/*                 <div*/}
              {/*                   style={{*/}
              {/*                     width: 4,*/}
              {/*                     height: '100%',*/}
              {/*                     background: 'rgb(240, 240, 240)',*/}
              {/*                   }}*/}
              {/*                 />*/}
              {/*               }*/}
              {/*             >*/}
              {/*               <Col className={styles["vertical-tabs"]}>*/}

              {/*                 /!*<StudioLeftTool style={{*!/*/}
              {/*                 /!*  display: 'flex',*!/*/}
              {/*                 /!*  alignItems: 'center',*!/*/}
              {/*                 /!*  justifyContent: 'center',*!/*/}
              {/*                 /!*}}/>*!/*/}
              {/*               </Col>*/}
              {/*               <Col>*/}
              {/*                 <CodeShow code={""}/>*/}
              {/*                 /!*{!isFullScreen ? <StudioTabs width={size.width - toolRightWidth - toolLeftWidth}/> : undefined}*!/*/}
              {/*               </Col>*/}
              {/*             </DraggleLayout>*/}
              {/*             <Col id='StudioRightTool' className={styles["vertical-tabs"]}>*/}
              {/*               /!*<StudioRightTool form={form}/>*!/*/}
              {/*             </Col>*/}
              {/*           </DraggleLayout>*/}
              {/*         </Row>*/}
              {/*         <Row>*/}
              {/*           <Col span={24}>*/}
              {/*             <Card title="Default size card" extra={<a href="#">More</a>} style={{ width: "100%" }}>*/}
              {/*               <p>Card content</p>*/}
              {/*               <p>Card content</p>*/}
              {/*               <p>Card content</p>*/}
              {/*             </Card>*/}
              {/*             /!*<StudioConsole height={size.height - toolHeight - VIEW.marginTop}/>*!/*/}
              {/*           </Col>*/}
              {/*         </Row>*/}
              {/*       </DraggleVerticalLayout>*/}
              {/*     </Card>*/}
              {/*   </div>*/}
            </Content>


          </div>


        </Content>
        <Sider style={siderStyle} collapsed collapsedWidth={40}>
          <Menu
            // theme="dark"
            mode="inline"
            defaultSelectedKeys={['1']}
            items={[
              {
                key: '1',
                icon: <UserOutlined/>,
                label: 'nav 1',
              },
              {
                key: '2',
                icon: <VideoCameraOutlined/>,
                label: 'nav 2',
              },
              {
                key: '3',
                icon: <UploadOutlined/>,
                label: 'nav 3',
              },
            ]}
          />
        </Sider>
      </Layout>


      <Footer style={footerStyle}>Footer</Footer>
    </Layout>

  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  isFullScreen: Studio.isFullScreen,
  rightClickMenu: Studio.rightClickMenu,
  toolHeight: Studio.toolHeight,
  toolLeftWidth: Studio.toolLeftWidth,
  toolRightWidth: Studio.toolRightWidth,
}))(DataStudio);
