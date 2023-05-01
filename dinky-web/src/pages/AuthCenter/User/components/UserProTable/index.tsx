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
import {EnableSwitch} from "@/components/CallBackButton/EnableSwitch";
import {EditButton} from "@/components/CallBackButton/EditButton";
import {AssignButton} from "@/components/CallBackButton/AssignButton";
import {PopconfirmDeleteButton} from "@/components/CallBackButton/PopconfirmDeleteButton";
import {CreateButton} from "@/components/CallBackButton/CreateButton";
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


    /**
     * user add role to submit
     */
    const handleGrantRoleSubmit = async () => {
        setLoading(true);
        const success = await handlePutData(
            API_CONSTANTS.USER_ASSIGN_ROLE,
            {userId: formValues.id, roleIds: roleList}
        );
        if (success) {
            handleAssignRoleTransferOpen(false);
            actionRef.current?.reload?.();
        }
        setLoading(false);

    };

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
        setLoading(true);
        // TODO: delete user interface is use /api/users/delete  , because of the backend interface 'DeleteMapping' is repeat , in the future, we need to change the interface to /api/users (USER)
        await handleRemoveById(API_CONSTANTS.USER_DELETE, value.id);
        setLoading(false);
        actionRef.current?.reload?.();
    };


    /**
     * user enable or disable
     * @param value
     */
    const handleChangeEnable = async (value: UserBaseInfo.User) => {
        setLoading(true);
        await updateEnabled(API_CONSTANTS.USER_ENABLE, {id: value.id});
        setLoading(false);
        actionRef.current?.reload?.();
    };

    /**
     * change password submit
     * @param value
     */
    const handlePasswordChangeSubmit = async (value: any) => {
        await handleOption(API_CONSTANTS.USER_MODIFY_PASSWORD, l("button.changePassword"), value);
        await handlePasswordModalOpen(false);
    };

    /**
     * edit user submit
     * @param value
     */
    const handleSubmitUser = async (value: Partial<UserBaseInfo.User>) => {
        await handleAddOrUpdate(API_CONSTANTS.USER, value);
        await handleModalOpen(false);
        actionRef.current?.reload?.();

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
            render: (_, record) => {
                return <EnableSwitch record={record} onChange={() => handleChangeEnable(record)}/>;
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
            width: "10vh",
            render: (_: any, record: UserBaseInfo.User) => [
                <EditButton key={record.id} onClick={() => handleEditVisible(record)}/>,
                <AssignButton key={record.id} onClick={() => handleAssignRole(record)} title={l('user.AssignRole')}/>,
                <Button
                    className={"options-button"}
                    key={"changePassword"}
                    icon={<LockTwoTone/>}
                    title={l("button.changePassword")}
                    onClick={() => {
                        handleChangePassword(record);
                    }}
                />,
                <>
                    {(access.canAdmin && record.username !== "admin") &&
                        <PopconfirmDeleteButton onClick={() => handleDeleteUser(record)}
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
            toolBarRender={() => [<CreateButton key={"CreateUser"} onClick={() => handleModalOpen(true)}/>]}
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
        {(formValues && Object.keys(formValues).length > 0) && (
            <>
                <PasswordModal
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
            </>
        )}
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
