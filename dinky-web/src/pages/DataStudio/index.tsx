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

import {Breadcrumb, Button, Layout, Menu, Space} from 'antd';
import {connect, getDvaApp} from "umi";
import {
    CheckSquareTwoTone,
    CloseSquareOutlined,
    CopyOutlined,
    HomeOutlined,
    QuestionOutlined,
    SaveTwoTone,
} from "@ant-design/icons";
import React, {Fragment, useCallback, useEffect, useState} from "react";
import {StateType, VIEW} from "@/pages/DataStudio/model";
import MovableSidebar from "@/components/Sidebar/MovableSidebar";
import {PersistGate} from 'redux-persist/integration/react';
import {getDataBase} from "@/pages/DataStudio/LeftContainer/MetaData/service";
import MiddleContainer from "@/pages/DataStudio/MiddleContainer";
import {FlexCenterDiv} from "@/components/StyledComponents";
import LeftContainer from "@/pages/DataStudio/LeftContainer";
import {LeftBottomSide, LeftSide, RightSide} from "@/pages/DataStudio/route";
import {l} from '@/utils/intl';
import {getLocalTheme} from "@/utils/function";
import {mapDispatchToProps} from "@/pages/DataStudio/function";
import BottomContainer from "@/pages/DataStudio/BottomContainer";
import HeaderContainer from "@/pages/DataStudio/HeaderContainer";

const {Header, Sider, Content} = Layout;




const DataStudio = (props: any) => {

    const {
        bottomContainer, leftContainer, rightContainer, saveDataBase, updateToolContentHeight
        , updateCenterContentHeight, updateSelectLeftKey, updateLeftWidth, updateSelectRightKey
        , updateRightWidth, updateSelectBottomKey, updateBottomHeight, activeBreadcrumbTitle
    } = props


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
        return <>
            <MovableSidebar
                contentHeight={size.contentHeight - VIEW.midMargin - bottomHeight}
                onResize={(event: any, direction: any, elementRef: {
                    offsetWidth: any;
                }) => updateRightWidth(elementRef.offsetWidth)}
                title={rightContainer.selectKey}
                handlerMinimize={() => updateSelectRightKey("")}
                visible={rightContainer.selectKey !== ""}
                defaultSize={{width: rightContainer.width, height: rightContainer.height}}
                minWidth={300}
                maxWidth={size.width - 2 * VIEW.leftToolWidth - leftContainer.width - 600}
                enable={{left: true}}
            >
                <Space wrap>
                    <Button icon={<QuestionOutlined/>} block type={"text"} shape={"circle"} size={"small"}/>
                    <Button icon={<CloseSquareOutlined/>} block type={"text"} shape={"circle"}
                            size={"small"}/>
                    <Button icon={<CopyOutlined/>} block type={"text"} shape={"circle"} size={"small"}/>
                </Space>
            </MovableSidebar>
        </>
    }
    return (
        <PersistGate loading={null} persistor={persistor}>
            <Fragment>
                <Layout style={{minHeight: "60vh"}}>
                    {renderHeaderContainer()}

                    <Layout hasSider style={{minHeight: size.contentHeight}}>
                        <Sider collapsed collapsedWidth={40}>
                            <Menu
                                theme={getLocalTheme() === "dark" ? "dark" : "light"}
                                mode="inline"
                                selectedKeys={[leftContainer.selectKey]}
                                items={LeftSide.map(x => {
                                    return {key: x.key, label: l(x.label), icon: x.icon}
                                })}
                                style={{height: '50%', borderRight: 0}}
                                onClick={(item) => {
                                    updateSelectLeftKey(item.key === leftContainer.selectKey ? '' : item.key)
                                }}
                            />
                            <Menu
                                theme={getLocalTheme() === "dark" ? "dark" : "light"}
                                mode="inline"
                                selectedKeys={[bottomContainer.selectKey]}
                                items={LeftBottomSide.map(x => {
                                    return {key: x.key, label: l(x.label), icon: x.icon}
                                })}
                                style={{
                                    display: 'flex',
                                    height: '50%',
                                    borderRight: 0,
                                    flexDirection: "column-reverse"
                                }}
                                onClick={(item) => {
                                    updateSelectBottomKey(item.key === bottomContainer.selectKey ? '' : item.key)
                                }}
                            />

                        </Sider>

                        <Content style={{
                            flexDirection: "column-reverse",
                            display: "flex",
                            height: size.contentHeight,
                        }}>

                            {<BottomContainer size={size}/>}
                            {/*<div>001</div>*/}
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
                                    {/*<CodeShow code={"123\n1\n1\n1\n1\n1\n1\n1\n1\n1n\n1\n1\n1"}*/}
                                    {/*          height={size.contentHeight - bottomHeight - 60 + "px"}/>*/}
                                </Content>

                                {renderRightContainer()}

                            </div>
                        </Content>
                        <Sider collapsed collapsedWidth={40}>
                            <Menu
                                selectedKeys={[rightContainer.selectKey]}
                                theme={getLocalTheme() === "dark" ? "dark" : "light"}
                                mode="inline"
                                style={{height: '100%', borderRight: 0}}
                                items={RightSide}
                                onClick={(item) => {
                                    updateSelectRightKey(item.key === rightContainer.selectKey ? '' : item.key)
                                }}
                            />
                        </Sider>
                    </Layout>
                </Layout>
            </Fragment>
        </PersistGate>
    );
};


export default connect(({Studio}: { Studio: StateType }) => ({
    leftContainer: Studio.leftContainer,
    rightContainer: Studio.rightContainer,
    bottomContainer: Studio.bottomContainer,
    activeBreadcrumbTitle: Studio.tabs.activeBreadcrumbTitle,
}), mapDispatchToProps)(DataStudio);
