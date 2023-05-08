/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import React, {useRef, useState} from 'react';
import type {ActionType, ProColumns} from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import {l} from "@/utils/intl";
import {Document, GlobalVar} from "@/types/RegCenter/data";
import {
    API_CONSTANTS,
    PROTABLE_OPTIONS_PUBLIC,
    STATUS_ENUM,
    STATUS_MAPPING,
} from "@/services/constants";
import {handleAddOrUpdate, handleRemoveById, updateEnabled} from "@/services/BusinessCrud";
import {queryList} from "@/services/api";
import CodeShow from "@/components/CustomEditor/CodeShow";
import GlobalVarModal from "@/pages/RegCenter/GlobalVar/components/GlobalVarModal";
import GlobalVarDrawer from "@/pages/RegCenter/GlobalVar/components/GlobalVarDrawer";
import {EnableSwitchBtn} from '@/components/CallBackButton/EnableSwitchBtn';
import {EditBtn} from "@/components/CallBackButton/EditBtn";
import {PopconfirmDeleteBtn} from "@/components/CallBackButton/PopconfirmDeleteBtn";
import {CreateBtn} from "@/components/CallBackButton/CreateBtn";

const GlobalVarProTable = () => {
    /**
     * state
     */
    const [modalVisible, handleModalVisible] = useState<boolean>(false);
    const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
    const [formValues, setFormValues] = useState({});
    const actionRef = useRef<ActionType>();
    const [loading, setLoading] = useState<boolean>(false);
    const [drawerOpen, setDrawerOpen] = useState<boolean>(false);

    /**
     * handle cancel all
     */
    const handleCancel = () => {
        handleModalVisible(false);
        handleUpdateModalVisible(false);
        setDrawerOpen(false);
        setFormValues({});
    }

    const executeAndCallbackRefresh = async (callback: () => void) => {
        setLoading(true);
        await callback();
        setLoading(false);
        handleCancel();
        actionRef.current?.reload?.();
    }

    /**
     * var enable or disable
     * @param value
     */
    const handleChangeEnable = async (value: Partial<GlobalVar>) => {
      await executeAndCallbackRefresh(async () => {
            await updateEnabled(API_CONSTANTS.GLOBAL_VARIABLE_ENABLE, {id: value.id});
        })
    };

    /**
     * delete role by id
     * @param id role id
     */
    const handleDeleteSubmit = async (id: number) => {
        await executeAndCallbackRefresh(async () => {
            await handleRemoveById(API_CONSTANTS.GLOBAL_VARIABLE_DELETE, id);
        })
    }



    /**
     * handle edit
     * @param {Partial<Document>} record
     */
    const handleClickEdit = (record: Partial<Document>) => {
        setFormValues(record);
        handleUpdateModalVisible(true);
    }

    /**
     * handle open drawer
     * @param {Partial<Document>} record
     */
    const handleOpenDrawer = (record: Partial<Document>) => {
        setFormValues(record)
        setDrawerOpen(true)
    }


    /**
     * handle add or update submit
     * @param {Partial<GlobalVar>} value
     * @returns {Promise<void>}
     */
    const handleAddOrUpdateSubmit = async (value: Partial<GlobalVar>) => {
        await executeAndCallbackRefresh(async () => {
            await handleAddOrUpdate(API_CONSTANTS.GLOBAL_VARIABLE, value);
        })
    }


    /**
     * columns
     */
    const columns: ProColumns<GlobalVar>[] = [
        {
            title: l('rc.gv.name'),
            dataIndex: 'name',
            sorter: true,
            render: (dom, record) => {
                return <a onClick={() => handleOpenDrawer(record)}>{dom}</a>;
            },
        },
        {
            title: l('rc.gv.value'),
            dataIndex: 'fragmentValue',
            hideInTable: true,
            render: (_, record) => {
                return <CodeShow width={"75vh"} code={record.fragmentValue}/>
            },
        },
        {
            title: l('global.table.note'),
            dataIndex: 'note',
            valueType: 'textarea',
        },
        {
            title: l("global.table.isEnable"),
            dataIndex: "enabled",
            hideInSearch: true,
            width: '15vh',
            render: (_, record) => {
                return <EnableSwitchBtn key={`${record.id}_enable`} disabled={drawerOpen} record={record}
                                        onChange={() => handleChangeEnable(record)}/>;
            },
            filters: STATUS_MAPPING(),
            filterMultiple: false,
            valueEnum: STATUS_ENUM(),
        },
        {
            title: l('global.table.createTime'),
            dataIndex: 'createTime',
            hideInSearch: true,
            width: '20vh',
            sorter: true,
            valueType: 'dateTime',
        },
        {
            title: l('global.table.lastUpdateTime'),
            dataIndex: 'updateTime',
            width: '20vh',
            hideInSearch: true,
            sorter: true,
            valueType: 'dateTime',
        },
        {
            title: l('global.table.operate'),
            width: '10vh',
            valueType: 'option',
            render: (_, record) => [
                <EditBtn key={`${record.id}_edit`} onClick={() => handleClickEdit(record)}/>,
                <PopconfirmDeleteBtn key={`${record.id}_delete`} onClick={() => handleDeleteSubmit(record.id)}
                                     description={l("rc.gv.deleteConfirm")}/>,
            ],
        },
    ];


    /**
     * render
     */
    return <>
        {/*table*/}
        <ProTable<GlobalVar>
            headerTitle={l('rc.gv.Management')}
            actionRef={actionRef}
            loading={loading}
            {...PROTABLE_OPTIONS_PUBLIC}
            toolBarRender={() => [<CreateBtn key={"vartable"} onClick={() => handleModalVisible(true)}/>,]}
            request={(params, sorter, filter: any) => queryList(API_CONSTANTS.GLOBAL_VARIABLE, {
                ...params,
                sorter,
                filter
            })}
            columns={columns}
        />

        {/*add*/}
        <GlobalVarModal
            onSubmit={(value) => handleAddOrUpdateSubmit(value)}
            onCancel={() => handleCancel()}
            modalVisible={modalVisible}
            values={{}}
        />
        {/*update*/}
        <GlobalVarModal
            onSubmit={(value) => handleAddOrUpdateSubmit(value)}
            onCancel={() => handleCancel()}
            modalVisible={updateModalVisible}
            values={formValues}
        />

        {/*drawer render*/}
        <GlobalVarDrawer
            onCancel={() => handleCancel()}
            values={formValues}
            modalVisible={drawerOpen}
            columns={columns}
        />
    </>
        ;
};
export default GlobalVarProTable;
