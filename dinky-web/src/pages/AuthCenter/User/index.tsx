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
import {EditTwoTone, LockTwoTone, PlusOutlined, UserSwitchOutlined} from "@ant-design/icons";
import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {Button, Form, Modal, Popconfirm, Space, Switch} from "antd";
import {PageContainer} from "@ant-design/pro-layout";
import UserForm from "@/pages/AuthCenter/User/components/UserForm";
import PasswordForm from "@/pages/AuthCenter/User/components/PasswordForm";
import TableTransferFrom from "@/pages/AuthCenter/User/components/UserTransfer";
import {l} from "@/utils/intl";
import {handleAddOrUpdate, handleOption, handlePutData, handleRemoveById, updateEnabled} from "@/services/BusinessCrud";
import {queryList} from "@/services/api";
import {API_CONSTANTS, PROTABLE_OPTIONS_PUBLIC, STATUS_ENUM, STATUS_MAPPING} from "@/services/constants";
import {useAccess} from "@@/exports";
import {DangerDeleteIcon} from "@/components/Icons/CustomIcons";


const UserTableList: React.FC = () => {

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
  const [form] = Form.useForm(); // form instance
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
      form.resetFields();
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
    const success = await handleOption(API_CONSTANTS.USER_MODIFY_PASSWORD, l("button.changePassword"), value);
    if (success) {
      handlePasswordModalOpen(false);
      setFormValues({});
    }
  };

  /**
   * edit user submit
   * @param value
   */
  const handleSubmitUser = async (value: Partial<UserBaseInfo.User>) => {
    const success = await handleAddOrUpdate(API_CONSTANTS.USER, value);
    if (success) {
      handleModalOpen(false);
      setFormValues({});
      if (actionRef.current) {
        actionRef.current.reload();
      }
    }
  };


  /**
   * assign role modal
   */
  const AssignRoleTransfer = () => {
    return (
      <Modal
        title={l("user.AssignRole")}
        open={assignRoleTransferOpen}
        destroyOnClose
        key={"AssignRole"}
        width={"75%"}
        onCancel={() => {handleAssignRoleTransferOpen(false)}}
        onOk={handleGrantRoleSubmit}
        >
        <TableTransferFrom user={formValues} onChange={(value) => {setRoleList(value)}}/>
      </Modal>
    );
  };


  /**
   * table columns
   */
  const columns: ProColumns<UserBaseInfo.User>[] = [
    {
      title: l("user.UserName"),
      dataIndex: "username",
    },
    {
      title: l("user.UserNickName"),
      dataIndex: "nickname",
    },
    {
      title: l("user.UserJobNumber"),
      dataIndex: "worknum",
    },
    {
      title: l("user.UserPhoneNumber"),
      dataIndex: "mobile",
      hideInSearch: true,
    },
    {
      title: l("global.table.isEnable"),
      dataIndex: "enabled",
      hideInSearch: true,
      render: (_, record) => {
        return <>
          <Space>
            <Switch
              key={record.id}
              checkedChildren={l("status.enabled")}
              unCheckedChildren={l("status.disabled")}
              checked={record.enabled}
              onChange={() => handleChangeEnable(record)}/>
          </Space>
        </>;
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
      width: "20vh",
      render: (_: any, record: UserBaseInfo.User) => [
        <Button
          className={"options-button"}
          key={"edit"}
          icon={<EditTwoTone/>}
          title={l("button.edit")}
          onClick={() => {
            handleEditVisible(record);
          }}
        />,
        <Button
          className={"options-button"}
          key={"assignRole"}
          title={l("user.AssignRole")}
          icon={<UserSwitchOutlined className={"blue-icon"}/>}
          onClick={() => {
            handleAssignRole(record);
          }}
        />,
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
            <Popconfirm
              placement="topRight"
              title={l("button.delete")}
              key={'userDelete'}
              description={l("user.deleteConfirm")}
              onConfirm={() => {handleDeleteUser(record);}}
              okText={l("button.confirm")}
              cancelText={l("button.cancel")}
            >
              <Button key={'deleteUserIcon'} icon={<DangerDeleteIcon/>}/>
            </Popconfirm>
          }
        </>
        ,
      ],
    },
  ];


  /**
   * render
   */
  return (
    <>
      <PageContainer
        key={"user list"}
        title={false}>
        <ProTable<UserBaseInfo.User>
          {...PROTABLE_OPTIONS_PUBLIC}
          headerTitle={l("user.UserManger")}
          actionRef={actionRef}
          loading={loading}
          toolBarRender={() => [
            <Button
              icon={<PlusOutlined/>}
              key={"CreateUser"}
              type="primary"
              onClick={() => {
                form.resetFields();
                handleModalOpen(true)
              }}>
              {l("button.create")}
            </Button>
          ]}
          request={(params, sorter, filter: any) => (queryList(API_CONSTANTS.USER, {...params, sorter,  filter}))}
          columns={columns}
        />
        <UserForm key={"handleSubmitUser"} onSubmit={handleSubmitUser} onCancel={() => {handleModalOpen(false)}} modalVisible={modalOpen} values={{}}/>
        {(formValues && formValues.id !== undefined) && (
          <>
            <PasswordForm
              onSubmit={handlePasswordChangeSubmit}
              onCancel={() => {handlePasswordModalOpen(false);}}
              modalVisible={passwordModalOpen}
              values={formValues}
             />
            <UserForm key={"handleUpdateUser"}
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
        {AssignRoleTransfer()}
      </PageContainer>
    </>
  );
};

export default UserTableList;
