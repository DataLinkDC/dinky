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

import React, {useRef, useState} from "react";
import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {RowPermissions} from "@/types/User/data";
import {l} from "@/utils/intl";
import {EditBtn} from "@/components/CallBackButton/EditBtn";
import {PopconfirmDeleteBtn} from "@/components/CallBackButton/PopconfirmDeleteBtn";
import {handleAddOrUpdate, handleRemoveById} from "@/services/BusinessCrud";
import {API_CONSTANTS, PROTABLE_OPTIONS_PUBLIC} from "@/services/constants";
import {getTenantByLocalStorage} from "@/utils/function";
import {CreateBtn} from "@/components/CallBackButton/CreateBtn";
import {queryList} from "@/services/api";
import {PermissionsModal} from "@/pages/AuthCenter/RowPermissions/components/PermissionsModal";

const PermissionsProTable: React.FC = () => {

    const [formValues, setFormValues] = useState<Partial<RowPermissions>>({});
    const [modalVisible, handleModalVisible] = useState<boolean>(false);
    const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
    const [loading, setLoading] = useState<boolean>(false);
    const actionRef = useRef<ActionType>();

    const executeAndCallbackRefresh = async (callback: () => void) => {
        setLoading(true);
        await callback();
        setLoading(false);
        actionRef.current?.reload?.();
    }

    /**
     * delete role by id
     * @param id role id
     */
    const handleDeleteSubmit = async (id: number) => {
       await executeAndCallbackRefresh(async () => {
            // TODO: DELETE role interface is use /api/rowPermissions/delete  , because of the backend interface 'DeleteMapping' is repeat , in the future, we need to change the interface to /api/rowPermissions (rowPermissions)
            await handleRemoveById(API_CONSTANTS.ROW_PERMISSIONS_DELETE, id);
        });
    }

    /**
     * add or update role submit callback
     * @param value
     */
    const handleAddOrUpdateSubmit = async (value: any) => {
        await executeAndCallbackRefresh(async () => {
            await handleAddOrUpdate(API_CONSTANTS.ROW_PERMISSIONS, {...value, tenantId: getTenantByLocalStorage()});
            handleModalVisible(false);
        });
    }

    /**
     * edit
     * @param record
     */
    const handleEditVisible = (record: Partial<RowPermissions>) => {
        setFormValues(record);
        handleUpdateModalVisible(true);
    }

    /**
     * cancel
     */
    const handleCancel = () => {
        handleModalVisible(false);
        handleUpdateModalVisible(false);
    }


    /**
     * query list
     * @type {({dataIndex: string, title: any} | {sorter: boolean, dataIndex: string, title: any} | {sorter: boolean, dataIndex: string, title: any} | {hideInSearch: boolean, dataIndex: string, title: any, ellipsis: boolean} | {sorter: boolean, dataIndex: string, valueType: string, title: any} | {sorter: boolean, dataIndex: string, valueType: string, title: any} | {dataIndex: string, valueType: string, width: string, title: any, render: (_, record) => JSX.Element[]})[]}
     */
    const columns: ProColumns<RowPermissions>[] = [
        {
            title: l('role.roleCode'),
            dataIndex: 'roleCode',
        },
        {
            title: l('role.roleName'),
            dataIndex: 'roleName',
        },
        {
            title: l('rowPermissions.tableName'),
            dataIndex: 'tableName',
            copyable: true,
        },

        {
            title: l('rowPermissions.expression'),
            dataIndex: 'expression',
            ellipsis: true,
            copyable: true,
        },
        {
            title: l('global.table.createTime'),
            dataIndex: 'createTime',
            hideInSearch: true,
            sorter: true,
            valueType: 'dateTime',
        },
        {
            title: l('global.table.updateTime'),
            dataIndex: 'updateTime',
            sorter: true,
            hideInSearch: true,
            valueType: 'dateTime',
        },
        {
            title: l('global.table.operate'),
            dataIndex: 'option',
            valueType: 'option',
            width: "10vh",
            render: (_, record) => [
                <EditBtn key={`${record.id}_edit`} onClick={() => handleEditVisible(record)}/>,
                <PopconfirmDeleteBtn key={`${record.id}_delete`} onClick={() => handleDeleteSubmit(record.id)}
                                     description={l('rowPermissions.deleteConfirm')}/>
            ],
        },
    ];

    /**
     * render
     */
    return <>
        <ProTable<RowPermissions>
            {...PROTABLE_OPTIONS_PUBLIC}
            headerTitle={l('rowPermissions.management')}
            actionRef={actionRef}
            loading={loading}
            toolBarRender={() => [<CreateBtn key="createBtn" onClick={() => handleModalVisible(true)}/>]}
            request={(params: any, sorter: any, filter: any) => queryList(API_CONSTANTS.ROW_PERMISSIONS, {
                ...params,
                sorter,
                filter
            })
            }
            columns={columns}
        />

        {/* added row Permissions */}
        <PermissionsModal
            onSubmit={(value) => handleAddOrUpdateSubmit(value)}
            onCancel={() => handleCancel()}
            modalVisible={modalVisible}
            values={{}}
        />

        {/* modify row Permissions */}
        <PermissionsModal
            onSubmit={(value) => handleAddOrUpdateSubmit(value)}
            onCancel={() => handleCancel()}
            modalVisible={updateModalVisible}
            values={formValues}
        />
    </>
}

export default PermissionsProTable;
