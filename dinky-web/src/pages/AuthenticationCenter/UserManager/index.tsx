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
import {DownOutlined, PlusOutlined} from '@ant-design/icons';
import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {Button, Drawer, Dropdown, Menu, Modal} from 'antd';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import ProDescriptions from '@ant-design/pro-descriptions';
import {handleAddOrUpdate, handleOption, handleRemove, queryData, updateEnabled} from "@/components/Common/crud";
import {UserTableListItem} from "@/pages/AuthenticationCenter/data.d";
import UserForm from "@/pages/AuthenticationCenter/UserManager/components/UserForm";
import PasswordForm from "@/pages/AuthenticationCenter/UserManager/components/PasswordForm";
import TableTransferFrom from "@/pages/AuthenticationCenter/UserManager/components/TableTransfer";
import {l} from "@/utils/intl";

const url = '/api/user';
const UserTableList: React.FC<{}> = (props: any) => {
  const {dispatch} = props;
  const [row, setRow] = useState<UserTableListItem>();
  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [handleGrantRole, setHandleGrantRole] = useState<boolean>(false);
  const [passwordModalVisible, handlePasswordModalVisible] = useState<boolean>(false);
  const [formValues, setFormValues] = useState({});
  const [roleRelFormValues, setRoleRelFormValues] = useState({});
  const actionRef = useRef<ActionType>();
  const [selectedRowsState, setSelectedRows] = useState<UserTableListItem[]>([]);


  const editAndDelete = (key: string | number, currentItem: UserTableListItem) => {
    if (key === 'edit') {
      setFormValues(currentItem);
      handleUpdateModalVisible(true);
    } else if (key === 'password') {
      setFormValues(currentItem);
      handlePasswordModalVisible(true);
    } else if (key === 'delete') {
      Modal.confirm({
        title: l('pages.user.delete'),
        content: l('pages.user.deleteConfirm'),
        okText: l('button.confirm'),
        cancelText: l('button.cancel'),
        onOk: async () => {
          await handleRemove(url, [currentItem]);
          actionRef.current?.reloadAndRest?.();
        }
      });
    }
  };

  const MoreBtn: React.FC<{
    item: UserTableListItem;
  }> = ({item}) => (
    <Dropdown
      overlay={
        <Menu onClick={({key}) => editAndDelete(key, item)}>
          <Menu.Item key="edit">{l('button.edit')}</Menu.Item>
          <Menu.Item key="password">{l('button.changePassword')}</Menu.Item>
          {item.username == 'admin' ? '' : (<Menu.Item key="delete">{l('button.delete')}</Menu.Item>)}
        </Menu>
      }
    >
      <a>
        {l('button.more')} <DownOutlined/>
      </a>
    </Dropdown>
  );


  const handleGrantRoleForm = () => {
    return (
      <Modal title={l('pages.user.AssignRole')} visible={handleGrantRole} destroyOnClose={true} width={"90%"}
             onCancel={() => {
               setHandleGrantRole(false);
             }}
             footer={[
               <Button key="back" onClick={() => {
                 setHandleGrantRole(false);
               }}>
                 {l('button.close')}
               </Button>,
               <Button type="primary" onClick={async () => {
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

  const columns: ProColumns<UserTableListItem>[] = [
    {
      title: l('pages.user.UserName'),
      dataIndex: 'username',
      sorter: true,
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: '用户ID',
      dataIndex: 'id',
      hideInTable: true,
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: l('pages.user.UserNickName'),
      sorter: true,
      dataIndex: 'nickname',
      hideInTable: false,
    },
    {
      title: l('pages.user.UserJobNumber'),
      sorter: true,
      dataIndex: 'worknum',
      hideInTable: false,
    },
    {
      title: l('pages.user.UserPhoneNumber'),
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
      render: (_, record) => [
        <a
          onClick={() => {
            handleUpdateModalVisible(true);
            setFormValues(record);
          }}
        >
          {l('button.config')}
        </a>,
        <a
          onClick={() => {
            setHandleGrantRole(true);
            setFormValues(record);
          }}
        >
          {l('pages.user.AssignRole')}
        </a>,
        <MoreBtn key="more" item={record}/>,
      ],
    },
  ];

  return (
    <>
      <PageContainer title={false}>
        <ProTable<UserTableListItem>
          headerTitle={l('pages.user.UserManger')}
          actionRef={actionRef}
          rowKey="id"
          search={{
            labelWidth: 120,
          }}
          toolBarRender={() => [
            <Button type="primary" onClick={() => handleModalVisible(true)}>
              <PlusOutlined/> {l('button.create')}
            </Button>,
          ]}
          request={(params, sorter, filter) => queryData(url, {...params, sorter, filter})}
          columns={columns}
          rowSelection={{
            onChange: (_, selectedRows) => setSelectedRows(selectedRows),
          }}
          pagination={{
            defaultPageSize: 10,
            showSizeChanger: true,
          }}
        />
        {selectedRowsState?.length > 0 && (
          <FooterToolbar
            extra={
              <div>
                {l('tips.selected', '',
                  {
                    total: <a
                      style={{fontWeight: 600}}>{selectedRowsState.length}</a>
                  })}  &nbsp;&nbsp;
                <span>
                    {l('pages.user.disableTotalOf', '', {total: selectedRowsState.length - selectedRowsState.reduce((pre, item) => pre + (item.enabled ? 1 : 0), 0)})}
              </span>
              </div>
            }
          >
            <Button type="primary" danger
                    onClick={() => {
                      Modal.confirm({
                        title: l('pages.user.delete'),
                        content: l('pages.user.deleteConfirm'),
                        okText: l('button.confirm'),
                        cancelText: l('button.cancel'),
                        onOk: async () => {
                          await handleRemove(url, selectedRowsState);
                          setSelectedRows([]);
                          actionRef.current?.reloadAndRest?.();
                        }
                      });
                    }}
            >
              {l('button.batchDelete')}
            </Button>
            <Button type="primary"
                    onClick={() => {
                      Modal.confirm({
                        title: l('pages.user.enable'),
                        content: l('pages.user.enableConfirm'),
                        okText: l('button.confirm'),
                        cancelText: l('button.cancel'),
                        onOk: async () => {
                          await updateEnabled(url, selectedRowsState, true);
                          setSelectedRows([]);
                          actionRef.current?.reloadAndRest?.();
                        }
                      });
                    }}
            >{l('button.batchEnable')}</Button>
            <Button danger
                    onClick={() => {
                      Modal.confirm({
                        title: l('pages.user.disable'),
                        content: l('pages.user.disableConfirm'),
                        okText: l('button.confirm'),
                        cancelText: l('button.cancel'),
                        onOk: async () => {
                          await updateEnabled(url, selectedRowsState, false);
                          setSelectedRows([]);
                          actionRef.current?.reloadAndRest?.();
                        }
                      });
                    }}
            >{l('button.batchDisable')}</Button>
          </FooterToolbar>
        )}
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
        <Drawer
          width={600}
          visible={!!row}
          onClose={() => {
            setRow(undefined);
          }}
          closable={false}
        >
          {row?.username && (
            <ProDescriptions<UserTableListItem>
              column={2}
              title={row?.username}
              request={async () => ({
                data: row || {},
              })}
              params={{
                id: row?.username,
              }}
              columns={columns}
            />
          )}
        </Drawer>
        {handleGrantRoleForm()}
      </PageContainer>
    </>
  );
};

export default UserTableList;
