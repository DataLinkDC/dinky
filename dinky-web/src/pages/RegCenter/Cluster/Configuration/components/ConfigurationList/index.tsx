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

import {ActionType, ProList} from '@ant-design/pro-components';
import {API_CONSTANTS, PRO_LIST_CARD_OPTIONS, PROTABLE_OPTIONS_PUBLIC,} from '@/services/constants';
import {Cluster} from '@/types/RegCenter/data';
import {l} from '@/utils/intl';
import {queryList} from '@/services/api';
import {CreateBtn} from '@/components/CallBackButton/CreateBtn';
import React, {useEffect, useRef, useState} from 'react';
import {
    handleAddOrUpdate,
    handleOption,
    handlePutDataByParams,
    handleRemoveById,
    updateEnabled
} from '@/services/BusinessCrud';
import {EditBtn} from '@/components/CallBackButton/EditBtn';
import {EnableSwitchBtn} from '@/components/CallBackButton/EnableSwitchBtn';
import ConfigurationModal from "@/pages/RegCenter/Cluster/Configuration/components/ConfigurationModal";
import {DataAction} from "@/components/StyledComponents";
import {Button, Descriptions, Modal, Space, Tag, Tooltip} from "antd";
import DescriptionsItem from "antd/es/descriptions/Item";
import {NormalDeleteBtn} from "@/components/CallBackButton/NormalDeleteBtn";
import {CheckCircleOutlined, ExclamationCircleOutlined, HeartTwoTone} from "@ant-design/icons";
import {ClusterConfigIcon} from "@/components/Icons/HomeIcon";
import {imgStyle} from "@/pages/Home/constants";
import {RunningBtn} from "@/components/CallBackButton/RunningBtn";
import {CLUSTER_CONFIG_TYPE} from "@/pages/RegCenter/Cluster/Configuration/components/contants";
import {useRequest} from "@umijs/max";


export default () => {

    /**
     * state
     */
    const actionRef = useRef<ActionType>();
    const [loading, setLoading] = useState<boolean>(false);
    const [createOpen, setCreateOpen] = useState<boolean>(false);
    const [modifyOpen, setModifyOpen] = useState<boolean>(false);
    const [formValue, setFormValue] = useState<Partial<Cluster.Config>>({});

    const {data, run} = useRequest({url: API_CONSTANTS.CLUSTER_CONFIGURATION, method: 'POST', data: {}})

    /**
     * execute and callback function
     * @param {() => void} callback
     * @returns {Promise<void>}
     */
    const executeAndCallbackRefresh = async (callback: () => void) => {
        setLoading(true);
        await callback();
        await run();
        setLoading(false);
        actionRef.current?.reload?.();
    };


    /**
     * handle delete
     * @param id
     */
    const handleDeleteSubmit = async (id: number) => {
        Modal.confirm({
            title: l('rc.cc.delete'),
            content: l('rc.cc.deleteConfirm'),
            okText: l('button.confirm'),
            cancelText: l('button.cancel'),
            onOk: async () => {
                await executeAndCallbackRefresh(async () => {
                    await handleRemoveById(API_CONSTANTS.CLUSTER_CONFIGURATION_DELETE, id);
                });
            }
        });
    };

    /**
     * handle enable
     * @param item
     */
    const handleEnable = async (item: Cluster.Config) => {
        await executeAndCallbackRefresh(async () => {
            await updateEnabled(API_CONSTANTS.CLUSTER_CONFIGURATION_ENABLE, {id: item.id});
        });
    };


    /**
     * START CLUSTER
     * @param item
     */
    const handleStartCluster = async (item: Cluster.Config) => {
        await executeAndCallbackRefresh(async () => {
            await handlePutDataByParams(API_CONSTANTS.CLUSTER_CONFIGURATION_START, l('rc.cc.start'), {id: item.id});
        });
    };

    /**
     * cancel
     */
    const handleCancel = async () => {
        setCreateOpen(false);
        setModifyOpen(false);
        setFormValue({});
    };

    /**
     * submit add or update
     * @param value
     */
    const handleSubmit = async (value: Partial<Cluster.Config>) => {
        await executeAndCallbackRefresh(async () => {
            value.configJson = JSON.stringify(value.configJson)
            await handleAddOrUpdate(API_CONSTANTS.CLUSTER_CONFIGURATION, value);
            await handleCancel();
        });
    };


    /**
     * render sub title
     * @param item
     */
    const renderDataSubTitle = (item: Cluster.Config) => {
        return (
            <Descriptions size={'small'} layout={'vertical'} column={1}>
                <DescriptionsItem
                    className={'hidden-overflow'}
                    key={item.id}>
                    <Tooltip key={item.name} title={item.name}>{item.name}</Tooltip>
                </DescriptionsItem>
            </Descriptions>
        );
    };

    /**
     * edit click callback
     * @param item
     */
    const editClick = (item: Cluster.Config) => {
        item.configJson = JSON.parse(item.configJson)
        setFormValue(item);
        setModifyOpen(!modifyOpen);
    };

    /**
     * handle check heart
     * @param item
     */
    const handleCheckHeartBeat = async (item: Cluster.Config) => {
        await executeAndCallbackRefresh(async () => {
            await handleOption(API_CONSTANTS.CLUSTER_CONFIGURATION_TEST, l('button.heartbeat'), item);
        });
    };

    /**
     * render action button
     * @param item
     */
    const renderDataActionButton = (item: Cluster.Config) => {
        return [
            <EditBtn key={`${item.id}_edit`} onClick={() => editClick(item)}/>,
            <NormalDeleteBtn key={`${item.id}_delete`} onClick={() => handleDeleteSubmit(item.id)}/>,
            <RunningBtn key={`${item.id}_running`} title={l('rc.cc.start')} onClick={() => handleStartCluster(item)}/>,
            <Button
                className={'options-button'}
                key={`${item.id}_heart`}
                onClick={() => handleCheckHeartBeat(item)}
                title={l('button.heartbeat')}
                icon={<HeartTwoTone twoToneColor={item.isAvailable ? '#1ac431' : '#e10d0d'}/>}
            />,
        ];
    };
    /**
     * render content
     * @param item
     */
    const renderDataContent = (item: Cluster.Config) => {
        return (
            <Space size={4} align={'baseline'} className={'hidden-overflow'}>
                <EnableSwitchBtn record={item} onChange={() => handleEnable(item)}/>
                <Tag color="cyan">{CLUSTER_CONFIG_TYPE.find(record => item.type === record.value)?.label}</Tag>
                <Tag
                    icon={item.isAvailable ? <CheckCircleOutlined/> : <ExclamationCircleOutlined/>}
                    color={item.isAvailable ? 'success' : 'warning'}
                >
                    {item.isAvailable ? l('global.table.status.normal') : l('global.table.status.abnormal')}
                </Tag>
            </Space>
        );
    };


    /**
     * render data list
     */
    const renderData = data?.map((item: Cluster.Config) => ({
        subTitle: renderDataSubTitle(item),
        actions: <DataAction>{renderDataActionButton(item)}</DataAction>,
        avatar: <ClusterConfigIcon style={imgStyle}/>,
        content: renderDataContent(item),
        key: item.id,
    }));


    /**
     * tool bar render
     */
    const toolBarRender = () => [<CreateBtn key={'configcreate'} onClick={() => setCreateOpen(true)}/>,];

    /**
     * render
     */
    return <>
        <ProList<Cluster.Config>
            {...PROTABLE_OPTIONS_PUBLIC}
            {...PRO_LIST_CARD_OPTIONS as any}
            loading={loading}
            actionRef={actionRef}
            headerTitle={l('rc.cc.management')}
            toolBarRender={toolBarRender}
            dataSource={renderData}
        />

        {/*added*/}
        <ConfigurationModal visible={createOpen} onClose={handleCancel} value={{}} onSubmit={handleSubmit}/>
        {/*modify*/}
        {modifyOpen &&
            <ConfigurationModal visible={modifyOpen} onClose={handleCancel} value={formValue} onSubmit={handleSubmit}/>}

    </>;
};
