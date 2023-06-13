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

import React, {useEffect, useState} from "react";
import {useModel} from "@@/exports";
import {Descriptions, Divider, Empty, Form, Space, Tag} from "antd";
import {PageContainer, PageLoading, ProCard} from "@ant-design/pro-components";
import {UserBaseInfo} from "@/types/User/data";
import {l} from "@/utils/intl";
import {LogSvg} from "@/components/Icons/CodeLanguageIcon";
import PasswordForm from "@/pages/AuthCenter/User/components/PasswordModal/PasswordForm";
import {SecurityScanTwoTone} from "@ant-design/icons";
import {handleOption} from "@/services/BusinessCrud";
import {API_CONSTANTS} from "@/services/constants";
import BaseInfo from "@/pages/Other/PersonCenter/BaseInfo";
import Pop from "@/components/Animation/Pop";
import {flushSync} from "react-dom";

const PersonCenter = () => {

    const [form] = Form.useForm();

    const { initialState, setInitialState } = useModel('@@initialState');
    const [activeKey, setActiveKey] = useState('operation');
    const loading = <PageLoading/>;

    const fetchUserInfo = async () => {
        const userInfo = await initialState?.fetchUserInfo?.();
        if (userInfo) {
            flushSync(() => {
                setInitialState((s) => ({
                    ...s,
                    currentUser: userInfo,
                }));
            });
        }
    };

    useEffect(() => {
        fetchUserInfo();
    },[initialState?.currentUser]);



    if (!initialState) {
        return loading;
    }
    const { currentUser } = initialState;
    if (!currentUser || !currentUser.user.username) {
        return loading;
    }

    const { roleList, tenantList,currentTenant,user } = currentUser;


    /**
     * renderTenantTagList
     * @param {UserBaseInfo.Tenant[]} items
     * @returns {JSX.Element[] | undefined}
     */
    const renderTenantTagList = (items: UserBaseInfo.Tenant[]) => {
        return items?.map((item: UserBaseInfo.Tenant) => {
            return <Descriptions.Item key={item.id}>
                    <Tag color={'success'} key={item.id}>{item.tenantCode}</Tag>
                </Descriptions.Item>
        })
    };

    /**
     * renderRoleTagList
     * @param {UserBaseInfo.Role[]} items
     * @returns {JSX.Element[] | undefined}
     */
    const renderRoleTagList = (items: UserBaseInfo.Role[]) => {
        return items?.map((item: UserBaseInfo.Role) => {
            return <Descriptions.Item key={item.id}>
                   <Tag color={'success'} key={item.id}>{item.roleCode}</Tag>
               </Descriptions.Item>
        })
    };

    /**
     * handleSubmitPassWord
     * @param value
     * @returns {Promise<void>}
     */
    const handleSubmitPassWord = async (value: UserBaseInfo.ChangePasswordParams) => {
        await handleOption(API_CONSTANTS.USER_MODIFY_PASSWORD, l("button.changePassword"), value);
        form.resetFields();
    }


    /**
     * tabList
     * @type {({children: JSX.Element, label: JSX.Element, key: string} | {children: JSX.Element, label: JSX.Element, key: string})[]}
     */
    const tabList = [
        {
            key: 'operation',
            label: <><LogSvg/>{l('user.op')}</>,
            children: <><Empty description={l('global.stay.tuned')} image={Empty.PRESENTED_IMAGE_DEFAULT}/></>,
        },
        {
            key: 'changePassword',
            label: <><SecurityScanTwoTone/>{l('button.changePassword')}</>,
            children: <PasswordForm form={form} renderSubmit values={user} onSubmit={handleSubmitPassWord} />,
        },
    ];


    /**
     * render
     */
    return <Pop>
        <PageContainer title={false}>
            <ProCard ghost gutter={[16, 16]} hoverable loading={!loading && currentUser}>
                <ProCard style={{height: '91vh', textAlign:'center', overflowY: 'auto',}} colSpan="30%" hoverable bordered>
                    <BaseInfo user={user} tenant={currentTenant} />
                    <Divider orientation={'left'} plain >{l('user.tenant')}</Divider>
                    <Descriptions size={'small'} column={4}>{renderTenantTagList(tenantList || [])}</Descriptions>
                    <Divider plain ></Divider>
                    <Divider orientation={'left'} plain >{l('user.role')}</Divider>
                    <Descriptions size={'small'} column={4}>{renderRoleTagList(roleList || [])}</Descriptions>
                </ProCard>

                <ProCard
                    hoverable bordered
                    style={{height: '91vh',textAlign:'center',overflowY: 'auto',}}
                    tabs={{
                        activeKey: activeKey,
                        type: 'card',
                        animated: true,
                        onChange: (key: string) => setActiveKey(key),
                        items: tabList,
                    }}
                />
            </ProCard>
        </PageContainer>
    </Pop>;
}

export default PersonCenter;