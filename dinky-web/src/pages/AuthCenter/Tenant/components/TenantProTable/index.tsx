/*
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
import TenantForm from "@/pages/AuthCenter/Tenant/components/TenantModalForm";
import {l} from "@/utils/intl";
import {handleAddOrUpdate, handleRemoveById} from "@/services/BusinessCrud";
import {queryList} from "@/services/api";
import {API_CONSTANTS, PROTABLE_OPTIONS_PUBLIC} from "@/services/constants";
import {UserBaseInfo} from "@/types/User/data.d";
import {PopconfirmButton} from "@/components/CallBackButton/PopconfirmButton";
import {EditButton} from "@/components/CallBackButton/EditButton";
import {AssignButton} from "@/components/CallBackButton/AssignButton";
import {CreateButton} from "@/components/CallBackButton/CreateButton";
import TenantModalTransfer from "@/pages/AuthCenter/Tenant/components/TenantModalTransfer";

const TenantProTable: React.FC = () => {
    /**
     * status
     */
    const [handleGrantTenant, setHandleGrantTenant] = useState<boolean>(false);
    const [tenantRelFormValues, setTenantRelFormValues] = useState<string[]>([]);
    const [modalVisible, handleModalVisible] = useState<boolean>(false);
    const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
    const [loading, setLoading] = useState<boolean>(false);
    const actionRef = useRef<ActionType>();
    const [formValues, setFormValues] = useState<Partial<UserBaseInfo.Tenant>>({});


    /**
     * add tenant
     * @param value
     */
    const handleAddOrUpdateSubmit = async (value: Partial<UserBaseInfo.Tenant>) => {
        await handleAddOrUpdate(API_CONSTANTS.TENANT, value);
        handleModalVisible(false);
        setFormValues({})
        actionRef?.current?.reload?.();
    };

    /**
     * delete tenant
     * @param id tenant id
     */
    const handleDeleteSubmit = async (id: number) => {
        setLoading(true)
        // TODO: delete tenant interface is use /api/tenant/delete  , because of the backend interface 'DeleteMapping' is repeat , in the future, we need to change the interface to /api/tenant (TENANT)
        await handleRemoveById(API_CONSTANTS.TENANT_DELETE, id);
        setLoading(false)
        actionRef.current?.reload?.();
    };

    /**
     * assign user to tenant
     */
    const handleAssignUserSubmit = async () => {
        // to save
        await handleAddOrUpdate(API_CONSTANTS.ASSIGN_USER_TO_TENANT, {
            tenantId: formValues.id as number,
            userIds: tenantRelFormValues
        });
        setHandleGrantTenant(false);
        actionRef.current?.reload?.();
    }
    const handleCancel = () => {
        handleModalVisible(false);
        handleUpdateModalVisible(false);
        setHandleGrantTenant(false)
    }


    const handleAssignUserChange = (value: string[]) => {
        setTenantRelFormValues(value)
    }

    /**
     * edit visible change
     * @param record
     */
    const handleEditVisible = (record: Partial<UserBaseInfo.Tenant>) => {
        setFormValues(record);
        handleUpdateModalVisible(true);
    }
    /**
     * assign user visible change
     * @param record
     */
    const handleAssignVisible = (record: Partial<UserBaseInfo.Tenant>) => {
        setFormValues(record);
        setHandleGrantTenant(true);
    }


    /**
     * columns
     */
    const columns: ProColumns<UserBaseInfo.Tenant>[] = [
        {
            title: l('tenant.TenantCode'),
            dataIndex: 'tenantCode',
        },
        {
            title: l('global.table.note'),
            dataIndex: 'note',
            ellipsis: true,
        },
        {
            title: l('global.table.createTime'),
            dataIndex: 'createTime',
            valueType: 'dateTime',
            hideInSearch: true,
        },
        {
            title: l('global.table.updateTime'),
            dataIndex: 'updateTime',
            hideInSearch: true,
            valueType: 'dateTime',
        },
        {
            title: l('global.table.operate'),
            valueType: 'option',
            width: "10vh",
            render: (_, record: UserBaseInfo.Tenant) => [
                <EditButton key={record.id} onClick={() => handleEditVisible(record)}/>,
                <AssignButton key={record.id} onClick={() => handleAssignVisible(record)}
                              title={l('tenant.AssignUser')}/>,
                <>{record.id !== 1 &&
                    <PopconfirmButton onClick={() => handleDeleteSubmit(record.id)}
                                      description={l("tenant.deleteConfirm")}/>}</>,
            ],
        },
    ];

    /**
     * render
     */
    return <>
        <ProTable<UserBaseInfo.Tenant>
            {...PROTABLE_OPTIONS_PUBLIC}
            key={"tenantTable"}
            loading={loading}
            headerTitle={l('tenant.TenantManager')}
            actionRef={actionRef}
            toolBarRender={() => [<CreateButton key={"tenantTable"} onClick={() => handleModalVisible(true)}/>]}
            request={(params, sorter, filter: any) => queryList(API_CONSTANTS.TENANT, {...params, sorter, filter})}
            columns={columns}
        />

        {/*add tenant form*/}
        <TenantForm
            key={"tenantFormAdd"}
            onSubmit={(value) => handleAddOrUpdateSubmit(value)}
            onCancel={() => handleCancel()}
            modalVisible={modalVisible}
            values={{}}
        />

        {/*update tenant form*/}
        {(formValues && Object.keys(formValues).length) &&
            <TenantForm
                key={"tenantFormUpdate"}
                onSubmit={(value) => handleAddOrUpdateSubmit(value)}
                onCancel={() => handleCancel()}
                modalVisible={updateModalVisible}
                values={formValues}
            />
        }
        {/* assign user to tenant */}
        <TenantModalTransfer
            tenant={formValues}
            modalVisible={handleGrantTenant}
            onChange={(values) => handleAssignUserChange(values)}
            onCancel={() => handleCancel()}
            onSubmit={() => handleAssignUserSubmit()}
        />
    </>;
};

export default TenantProTable;
