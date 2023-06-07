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


import React, {useRef, useState} from "react";
import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {Tag} from 'antd';
import {l} from "@/utils/intl";
import {handleAddOrUpdate, handleRemoveById} from "@/services/BusinessCrud";
import {queryList} from "@/services/api";
import {API_CONSTANTS, PROTABLE_OPTIONS_PUBLIC} from "@/services/constants";
import {getTenantByLocalStorage} from "@/utils/function";
import {UserBaseInfo} from "@/types/User/data.d";
import RoleModalForm from "../RoleModalForm";
import {CreateBtn} from "@/components/CallBackButton/CreateBtn";
import {PopconfirmDeleteBtn} from "@/components/CallBackButton/PopconfirmDeleteBtn";
import {EditBtn} from "@/components/CallBackButton/EditBtn";


const RoleProTable: React.FC = () => {
        /**
         * status
         */
        const [formValues, setFormValues] = useState<Partial<UserBaseInfo.Role>>({});
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
                // TODO: DELETE role interface is use /api/role  , because of the backend interface 'DeleteMapping' is repeat , in the future, we need to change the interface to /api/role (ROLE)
                await handleRemoveById(API_CONSTANTS.ROLE_DELETE, id);
            });
        }

        /**
         * add or update role submit callback
         * @param value
         */
        const handleAddOrUpdateSubmit = async (value: any) => {
            await executeAndCallbackRefresh(async () => {
                // TODO: added or update role interface is use /api/role/addedOrUpdateRole  , because of the backend interface 'saveOrUpdate' is repeat , in the future, we need to change the interface to /api/role (ROLE)
                await handleAddOrUpdate(API_CONSTANTS.ROLE_ADDED_OR_UPDATE, {...value, tenantId: getTenantByLocalStorage()});
                handleModalVisible(false);
            });


        }

        /**
         * edit role status
         * @param record
         */
        const handleEditVisible = (record: Partial<UserBaseInfo.Role>) => {
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
         * columns
         */
        const columns: ProColumns<UserBaseInfo.Role>[] = [
            {
                title: l('role.roleCode'),
                dataIndex: 'roleCode',
            },
            {
                title: l('role.roleName'),
                dataIndex: 'roleName',
            },
            {
                title: l('role.belongTenant'),
                hideInSearch: true,
                render: (_:any, record: UserBaseInfo.Role) => {
                    return <Tag color="blue">{record.tenant.tenantCode}</Tag>
                }
            },
            {
                title: l('global.table.note'),
                dataIndex: 'note',
                hideInSearch: true,
                ellipsis: true,
            },
            {
                title: l('global.table.createTime'),
                dataIndex: 'createTime',
                sorter: true,
                hideInSearch: true,
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
                valueType: 'option',
                width: "10vh",
                render: (_:any, record: UserBaseInfo.Role) => [
                    <EditBtn key={`${record.id}_edit`} onClick={() => handleEditVisible(record)}/>,
                    <>{record.id !== 1 &&
                        <PopconfirmDeleteBtn key={`${record.id}_delete`} onClick={() => handleDeleteSubmit(record.id)}
                                             description={l("role.deleteConfirm")}/>}</>
                ],
            },
        ];


        /**
         * render
         */
        return <>
            <ProTable<UserBaseInfo.Role>
                {...PROTABLE_OPTIONS_PUBLIC}
                headerTitle={l('role.roleManagement')}
                actionRef={actionRef}
                loading={loading}
                toolBarRender={() => [<CreateBtn key={"toolBarRender"} onClick={() => handleModalVisible(true)}/>,]}
                request={(params, sorter, filter: any) => queryList(API_CONSTANTS.ROLE, {...params, sorter, filter})}
                columns={columns}
            />
            {/* create  */}
            <RoleModalForm
                onSubmit={(value: any) => handleAddOrUpdateSubmit(value)}
                onCancel={() => handleCancel()}
                modalVisible={modalVisible}
                values={{}}
            />
            {/* modify */}
            <RoleModalForm
                onSubmit={(value: any) => handleAddOrUpdateSubmit(value)}
                onCancel={() => handleCancel()}
                modalVisible={updateModalVisible}
                values={formValues}
            />
        </>
    }
;

export default RoleProTable;
