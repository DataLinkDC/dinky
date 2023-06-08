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

import React from "react";
import {useModel} from "@@/exports";
import {Avatar, Divider, Drawer, Spin, Tag, Typography} from "antd";
import {PageContainer, PageLoading, ProCard} from "@ant-design/pro-components";
import {UserBaseInfo} from "@/types/User/data";

const { Paragraph } = Typography;

const PersonCenter = () => {

    const { initialState, setInitialState } = useModel('@@initialState');
    const loading = <PageLoading/>;

    if (!initialState) {
        return loading;
    }
    const { currentUser } = initialState;
    if (!currentUser || !currentUser.user.username) {
        return loading;
    }

    // TODO: SUPPORT USER INFO , the left card is render user base info , the right card is render change password form etc..
    return <PageContainer title={false}>
        <ProCard ghost gutter={[16, 16]} hoverable loading={!loading && currentUser}>
            <ProCard style={{height: '91vh',alignItems:'center'}} colSpan="30%" hoverable bordered>
                <Avatar draggable size={128} src={currentUser.user.avatar} />
                <Divider dashed plain />
                {/*<Typography.Title level={1} >{currentUser.user.username}</Typography.Title >*/}
                {/*<Typography.Title level={3} >{currentUser.user.nickname}</Typography.Title >*/}
                {/*<Typography.Title level={4} >{currentUser.user.mobile}</Typography.Title >*/}
                {/*<Typography.Title level={4} >{currentUser.user.worknum}</Typography.Title >*/}
                {/*<Divider orientation={'left'} dashed plain >租户</Divider>*/}
                {/*<Paragraph>*/}
                {/*    {currentUser.roleList}*/}
                {/*    {currentUser.tenantList?.map((item: UserBaseInfo.Tenant) => {*/}
                {/*        return <Tag color={'default'} key={item.id}>{item.tenantCode} </Tag>*/}
                {/*    })}*/}
                {/*</Paragraph>*/}
            </ProCard>
            <ProCard hoverable bordered>Auto</ProCard>
        </ProCard>
    </PageContainer>;
}

export default PersonCenter;