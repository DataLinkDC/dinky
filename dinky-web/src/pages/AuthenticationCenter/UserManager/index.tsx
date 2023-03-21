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
import {EditTwoTone, PlusOutlined} from '@ant-design/icons';
import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {Button, Modal} from 'antd';
import {PageContainer} from '@ant-design/pro-layout';
import UserForm from "@/pages/AuthenticationCenter/UserManager/components/UserForm";
import PasswordForm from "@/pages/AuthenticationCenter/UserManager/components/PasswordForm";
import TableTransferFrom from "@/pages/AuthenticationCenter/UserManager/components/TableTransfer";
import {l} from "@/utils/intl";
import {handleAddOrUpdate, handleOption, handleRemove} from "@/services/BusinessCrud";
import {queryData} from "@/services/api";
import {CustomDeleteIcon} from "@/components/Icons/CustomIcons";
import {CgAssign, RiLockPasswordFill} from "react-icons/all";

const url = '/api/user';
const UserTableList: React.FC = () => {
  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [handleGrantRole, setHandleGrantRole] = useState<boolean>(false);
  const [passwordModalVisible, handlePasswordModalVisible] = useState<boolean>(false);
  const [formValues, setFormValues] = useState<UserBaseInfo.User>({});
  const [roleRelFormValues, setRoleRelFormValues] = useState({});
  const actionRef = useRef<ActionType>();


  const handleGrantRoleForm = () => {
    return (
      <Modal title={l('user.AssignRole')} open={handleGrantRole} destroyOnClose={true} width={"90%"}
             onCancel={() => {
               setHandleGrantRole(false);
             }}
             footer={[
               <Button key="back" onClick={() => {
                 setHandleGrantRole(false);
               }}>
                 {l('button.close')}
               </Button>,
               <Button key={"grantRole"} type="primary" onClick={async () => {
                 // to save
                 const success = await handleAddOrUpdate("api/user/grantRole", {
                   userId: formValues.id,
                   roles: roleRelFormValues
                 });
                 if (success) {
                   setHandleGrantRole(false);
                   setFormValues({});
                   if (actionRef.current) {
                     actionRef.current.reload();
                   }
                 }
               }}
               >
                 {l('button.confirm')}
               </Button>,
             ]}>
        <TableTransferFrom user={formValues} onChange={(value) => {
          setRoleRelFormValues(value);
        }}/>
      </Modal>
    )
  }

  const columns: ProColumns<UserBaseInfo.User>[] = [
    {
      title: l('user.UserName'),
      dataIndex: 'username',
      sorter: true,
    },
    {
      title: l('user.UserNickName'),
      sorter: true,
      dataIndex: 'nickname',
      hideInTable: false,
    },
    {
      title: l('user.UserJobNumber'),
      sorter: true,
      dataIndex: 'worknum',
      hideInTable: false,
    },
    {
      title: l('user.UserPhoneNumber'),
      sorter: true,
      dataIndex: 'mobile',
      hideInTable: false,
    },
    {
      title: l('global.table.isEnable'),
      dataIndex: 'enabled',
      hideInForm: true,
      hideInSearch: true,
      hideInTable: false,
      filters: [
        {
          text: l('status.enabled'),
          value: 1,
        },
        {
          text: l('status.disabled'),
          value: 0,
        },
      ],
      filterMultiple: false,
      valueEnum: {
        true: {text: l('status.enabled'), status: 'Success'},
        false: {text: l('status.disabled'), status: 'Error'},
      },
    },
    {
      title: l('global.table.createTime'),
      dataIndex: 'createTime',
      sorter: true,
      valueType: 'dateTime',
      hideInTable: true,
    },
    {
      title: l('global.table.updateTime'),
      dataIndex: 'updateTime',
      sorter: true,
      valueType: 'dateTime',
    },
    {
      title: l('global.table.operate'),
      dataIndex: 'option',
      valueType: 'option',
      width: '20vh',
      render: (_: any, record: UserBaseInfo.User) => [
        <Button
          className={'options-button'}
          key={"edit"}
          icon={<EditTwoTone/>}
          title={l('button.edit')}
          onClick={() => {
            setFormValues(record);
            handleUpdateModalVisible(true);
          }}
        />,
        <Button
          className={'options-button'}
          key={"assignRole"}
          title={l('user.AssignRole')}
          icon={<CgAssign className={'blue-icon'}/>}
          onClick={() => {
            setHandleGrantRole(true);
            setFormValues(record);
          }}
        />,
        <Button
          className={'options-button'}
          key={"password"}
          icon={<RiLockPasswordFill className={'blue-icon'}/>}
          title={l('button.changePassword')}
          onClick={() => {
            setFormValues(record);
            handlePasswordModalVisible(true);
          }}
        />,
        <>
          {!record.isAdmin &&
            <Button
              className={'options-button'}
              key={"delete"}
              icon={<CustomDeleteIcon/>}
              title={l('button.delete')}
              onClick={async () => {
                await handleRemove(url, record)
                actionRef.current?.reloadAndRest?.();
              }}
            />
          }
        </>
        ,
      ],
    },
  ];

  return (
    <>
      <PageContainer  title={false}>
        <ProTable<UserBaseInfo.User>
          tableStyle={{height: '50vh'}}
          headerTitle={l('user.UserManger')}
          actionRef={actionRef}
          rowKey="id"
          search={{
            labelWidth: 120,
          }}
          toolBarRender={() => [
            <Button key={'create'} type="primary" onClick={() => handleModalVisible(true)}>
              <PlusOutlined/> {l('button.create')}
            </Button>
          ]}
          request={(params, sorter, filter) => {
            return queryData(url, {...params, ...sorter, ...filter});
          }}
          columns={columns}
          pagination={{
            defaultPageSize: 10,
            showSizeChanger: true,
          }}
        />
        <UserForm
          onSubmit={async (value) => {
            const success = await handleAddOrUpdate("api/user", value);
            if (success) {
              handleModalVisible(false);
              setFormValues({});
              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
          }}
          onCancel={() => {
            handleModalVisible(false);
          }}
          modalVisible={modalVisible}
          values={{}}
        />
        {formValues && Object.keys(formValues).length ? (
          <>
            <PasswordForm
              onSubmit={async (value) => {
                const success = await handleOption(url + "/modifyPassword", l('button.changePassword'), value);
                if (success) {
                  handlePasswordModalVisible(false);
                  setFormValues({});
                }
              }}
              onCancel={() => {
                handlePasswordModalVisible(false);
              }}
              modalVisible={passwordModalVisible}
              values={formValues}
            />
            <UserForm
              onSubmit={async (value) => {
                const success = await handleAddOrUpdate("api/user", value);
                if (success) {
                  handleUpdateModalVisible(false);
                  setFormValues({});
                  if (actionRef.current) {
                    actionRef.current.reload();
                  }
                }
              }}
              onCancel={() => {
                handleUpdateModalVisible(false);
                setFormValues({});
              }}
              modalVisible={updateModalVisible}
              values={formValues}
            /></>
        ) : null}
        {handleGrantRoleForm()}
      </PageContainer>
    </>
  );
};

export default UserTableList;
