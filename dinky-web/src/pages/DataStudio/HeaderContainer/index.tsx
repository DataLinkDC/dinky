/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
import {FlexCenterDiv} from "@/components/StyledComponents";
import {VIEW} from "@/pages/DataStudio/model";
import {Breadcrumb, Descriptions, Space} from "antd";
import {
    ApiTwoTone,
    EnvironmentOutlined,
    FlagTwoTone,
    PauseCircleTwoTone,
    PlayCircleTwoTone,
    RocketTwoTone,
    SafetyCertificateTwoTone,
    SaveTwoTone,
    SendOutlined
} from "@ant-design/icons";
import React from "react";
import {l} from "@/utils/intl";
import {buildBreadcrumbItems} from "@/pages/DataStudio/HeaderContainer/function";


const headerStyle: React.CSSProperties = {
    display: "inline-flex",
    lineHeight: '32px',
    height: "32px",
    fontStyle: "normal",
    fontWeight: "bold",
    fontSize: "16px",
    padding: "4px 10px",
};


const HeaderContainer = (props: any) => {

    const {size, activeBreadcrumbTitle} = props;

    /**
     * @description: 生成面包屑
     * @type {({title: JSX.Element} | {title: string})[]}
     */
    const renderBreadcrumbItems = () => {
        if (!activeBreadcrumbTitle) {
            return <Space><EnvironmentOutlined/><span>Guide Page</span></Space>
        }

        return <>
            <FlexCenterDiv style={{width: (size.width - 2 * VIEW.paddingInline) / 2}}>
                <Breadcrumb separator={">"} items={buildBreadcrumbItems(activeBreadcrumbTitle)}/>
            </FlexCenterDiv>
        </>
    };


    /**
     * @description: 渲染右侧按钮
     * @returns {JSX.Element}
     */
    const renderRightButtons = () => {
        return <>
            <Space size={'middle'} align={"center"} direction={"horizontal"} wrap>
                {/*保存按钮 icon*/}
                <SaveTwoTone title={l('button.save')} onClick={() => {
                }}/>
                {/*检查 sql按钮*/}
                <SafetyCertificateTwoTone title={l('button.check')} onClick={() => {
                }}/>
                {/*    执行图按钮*/}
                <FlagTwoTone title={l('button.graph')} onClick={() => {
                }}/>
                {/*    执行按钮*/}
                <PlayCircleTwoTone title={l('button.execute')} onClick={() => {
                }}/>
                {/*    异步提交按钮*/}
                <RocketTwoTone title={l('button.async')} onClick={() => {
                }}/>
                {/*  推送海豚, 此处需要将系统设置中的 ds 的配置拿出来做判断 启用才展示  */}
                <SendOutlined title={l('button.push')} onClick={() => {
                }}/>
                {/*    发布按钮*/}
                <PauseCircleTwoTone title={l('button.publish')} onClick={() => {
                }}/>

                {/*    api 按钮*/}
                <ApiTwoTone title={l('button.api')} onClick={() => {
                }}/>
            </Space>
        </>
    };


    /**
     * render
     */
    return <>
        <Descriptions column={2} size={'middle'} layout={'horizontal'} key={"h"} style={headerStyle}>
            <Descriptions.Item>
                {renderBreadcrumbItems()}
            </Descriptions.Item>
            <Descriptions.Item>
                {/*{renderRightButtons()}*/}
            </Descriptions.Item>
        </Descriptions>
    </>
}

export default HeaderContainer
