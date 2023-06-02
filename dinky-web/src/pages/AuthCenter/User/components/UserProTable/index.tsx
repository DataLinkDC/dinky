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


import React, {useEffect, useRef, useState} from "react";
import {LockTwoTone} from "@ant-design/icons";
import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {Button} from "antd";
import {l} from "@/utils/intl";
import {handleAddOrUpdate, handleOption, handlePutData, handleRemoveById, updateEnabled} from "@/services/BusinessCrud";
import {queryList} from "@/services/api";
import {
    API_CONSTANTS,
    PROTABLE_OPTIONS_PUBLIC,
    STATUS_ENUM,
    STATUS_MAPPING,
} from "@/services/constants";
import {useAccess} from "@@/exports";
import {UserBaseInfo} from "@/types/User/data";
import {EnableSwitchBtn} from "@/components/CallBackButton/EnableSwitchBtn";
import {EditBtn} from "@/components/CallBackButton/EditBtn";
import {AssignBtn} from "@/components/CallBackButton/AssignBtn";
import {PopconfirmDeleteBtn} from "@/components/CallBackButton/PopconfirmDeleteBtn";
import {CreateBtn} from "@/components/CallBackButton/CreateBtn";
import PasswordModal from "@/pages/AuthCenter/User/components/PasswordModal";
import RoleModalTransfer from "../RoleModalTransfer";
import UserModalForm from "@/pages/AuthCenter/User/components/UserModalForm";


const UserProTable = () => {

    /**
     * open or close status
     */
    const [modalOpen, handleModalOpen] = useState<boolean>(false);
    const [updateModalOpen, handleUpdateModalOpen] = useState<boolean>(false);
    const [assignRoleTransferOpen, handleAssignRoleTransferOpen] = useState<boolean>(false);
    const [passwordModalOpen, handlePasswordModalOpen] = useState<boolean>(false);
    const [loading, setLoading] = useState<boolean>(false);
    /**
     * form values
     */
    const [formValues, setFormValues] = useState<any>({});
    const [roleList, setRoleList] = useState<string[]>([]);
    const actionRef = useRef<ActionType>(); // table action
    const access = useAccess(); // access control

    const executeAndCallbackRefresh = async (callback: () => void) => {
        setLoading(true);
        await callback();
        setLoading(false);
        actionRef.current?.reload?.();
    }

    /**
     * edit user
     * @param value
     */
    const handleEditVisible = (value: UserBaseInfo.User) => {
        setFormValues(value);
        handleUpdateModalOpen(true);
    };

    /**
     * assign role
     * @param value
     */
    const handleAssignRole = (value: UserBaseInfo.User) => {
        setFormValues(value);
        handleAssignRoleTransferOpen(true);
    };

    /**
     * change password
     * @param value
     */
    const handleChangePassword = (value: UserBaseInfo.User) => {
        setFormValues(value);
        handlePasswordModalOpen(true);
    };

    /**
     * delete user
     * @param value
     */
    const handleDeleteUser = async (value: UserBaseInfo.User) => {
        await executeAndCallbackRefresh(async () => {
            // TODO: delete user interface is use /api/users/delete  , because of the backend interface 'DeleteMapping' is repeat , in the future, we need to change the interface to /api/users (USER)
            await handleRemoveById(API_CONSTANTS.USER_DELETE, value.id)
        })
    };

    /**
     * user add role to submit
     */
    const handleGrantRoleSubmit = async () => {
        await executeAndCallbackRefresh(async () => {
            await handlePutData(API_CONSTANTS.USER_ASSIGN_ROLE, {userId: formValues.id, roleIds: roleList});
            handleAssignRoleTransferOpen(false);
        })

    };

    /**
     * user enable or disable
     * @param value
     */
    const handleChangeEnable = async (value: UserBaseInfo.User) => {
        await executeAndCallbackRefresh(async () => {
            await updateEnabled(API_CONSTANTS.USER_ENABLE, {id: value.id})
        })
    };

    /**
     * change password submit
     * @param value
     */
    const handlePasswordChangeSubmit = async (value: any) => {
        await executeAndCallbackRefresh(async () => {
            await handleOption(API_CONSTANTS.USER_MODIFY_PASSWORD, l("button.changePassword"), value);
            await handlePasswordModalOpen(false);
        })

    };

    /**
     * edit user submit
     * @param value
     */
    const handleSubmitUser = async (value: Partial<UserBaseInfo.User>) => {
        await executeAndCallbackRefresh(async () => {
            await handleAddOrUpdate(API_CONSTANTS.USER, value);
            handleModalOpen(false);
        })
    };


    /**
     * table columns
     */
    const columns: ProColumns<UserBaseInfo.User>[] = [
        {
            title: l("user.username"),
            dataIndex: "username",
        },
        {
            title: l("user.nickname"),
            dataIndex: "nickname",
        },
        {
            title: l("user.jobnumber"),
            dataIndex: "worknum",
        },
        {
            title: l("user.phone"),
            dataIndex: "mobile",
            hideInSearch: true,
        },
        {
            title: l("global.table.isEnable"),
            dataIndex: "enabled",
            hideInSearch: true,
            render: (_: any, record: UserBaseInfo.User) => {
                return <EnableSwitchBtn key={`${record.id}_enable`} record={record}
                                        onChange={() => handleChangeEnable(record)}/>;
            },
            filters: STATUS_MAPPING(),
            filterMultiple: false,
            valueEnum: STATUS_ENUM(),
        },
        {
            title: l("global.table.createTime"),
            dataIndex: "createTime",
            sorter: true,
            valueType: "dateTime",
            hideInTable: true,
            hideInSearch: true,
        },
        {
            title: l("global.table.updateTime"),
            dataIndex: "updateTime",
            hideInSearch: true,
            sorter: true,
            valueType: "dateTime",
        },
        {
            title: l("global.table.operate"),
            valueType: "option",
            width: "12vh",
            render: (_: any, record: UserBaseInfo.User) => [
                <EditBtn key={`${record.id}_edit`} onClick={() => handleEditVisible(record)}/>,
                <AssignBtn key={`${record.id}_delete`} onClick={() => handleAssignRole(record)}
                           title={l('user.assignRole')}/>,
                <Button
                    className={"options-button"}
                    key={`${record.id}_change`}
                    icon={<LockTwoTone/>}
                    title={l("button.changePassword")}
                    onClick={() => {
                        handleChangePassword(record);
                    }}
                />,
                <>
                    {(access.canAdmin && record.username !== "admin") &&
                        <PopconfirmDeleteBtn key={`${record.id}_delete`} onClick={() => handleDeleteUser(record)}
                                             description={l("user.deleteConfirm")}/>
                    }
                </>
                ,
            ],
        },
    ];


    /**
     * render
     */
    return <>
        <ProTable<UserBaseInfo.User>
            {...PROTABLE_OPTIONS_PUBLIC}
            headerTitle={l("user.manager")}
            actionRef={actionRef}
            loading={loading}
            toolBarRender={() => [<CreateBtn key={"CreateUser"} onClick={() => handleModalOpen(true)}/>]}
            request={(params, sorter, filter: any) => (queryList(API_CONSTANTS.USER, {
                ...params,
                sorter,
                filter
            }))}
            columns={columns}
        />

        <UserModalForm
            key={"handleSubmitUser"}
            onSubmit={handleSubmitUser}
            onCancel={() => handleModalOpen(false)}
            modalVisible={modalOpen}
            values={{}}
        />
        <PasswordModal
            key={"handlePasswordChangeSubmit"}
            onSubmit={handlePasswordChangeSubmit}
            onCancel={() => handlePasswordModalOpen(false)}
            modalVisible={passwordModalOpen}
            values={formValues}
        />
        <UserModalForm
            key={"handleUpdateUser"}
            onSubmit={handleSubmitUser}
            onCancel={() => {
                handleUpdateModalOpen(false);
                setFormValues({});
            }}
            modalVisible={updateModalOpen}
            values={formValues}
        />
        {/* assign role to user */}
        <RoleModalTransfer
            user={formValues}
            modalVisible={assignRoleTransferOpen}
            onChange={(value) => setRoleList(value)}
            onCancel={() => handleAssignRoleTransferOpen(false)}
            onSubmit={() => handleGrantRoleSubmit()}
        />
    </>
};

export default UserProTable;
