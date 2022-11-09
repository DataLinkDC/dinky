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
import {DownOutlined, PlusOutlined} from '@ant-design/icons';
import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {Button, Drawer, Dropdown, Menu, Modal} from 'antd';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import ProDescriptions from '@ant-design/pro-descriptions';
import {getStorageTenantId, handleAddOrUpdate, handleRemove, queryData} from "@/components/Common/crud";
import {RoleTableListItem} from "@/pages/AuthenticationCenter/data.d";
import RoleForm from "@/pages/AuthenticationCenter/RoleManager/components/RoleForm";
import {getNameSpaceList} from "@/pages/AuthenticationCenter/service";
import {connect} from "umi";
import {l} from "@/utils/intl";

const url = '/api/role';

const RoleFormList: React.FC<{}> = (props: any) => {
  const {dispatch} = props;
  const [row, setRow] = useState<RoleTableListItem>();
  const [formValues, setFormValues] = useState<RoleTableListItem>();
  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  const [selectedRowsState, setSelectedRows] = useState<RoleTableListItem[]>([]);


  useEffect(() => {
    getNameSpaceList(dispatch);
  }, []);

  const editAndDelete = (key: string | number, currentItem: RoleTableListItem) => {
    if (key === 'edit') {
      setFormValues(currentItem);
      handleUpdateModalVisible(true);
    } else if (key === 'delete') {
      Modal.confirm({
        title: l('pages.role.delete'),
        content: l('pages.role.deleteConfirm'),
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
    item: RoleTableListItem;
  }> = ({item}) => (
    <Dropdown
      overlay={
        <Menu onClick={({key}) => editAndDelete(key, item)}>
          <Menu.Item key="edit">{l('button.edit')}</Menu.Item>
          <Menu.Item key="delete">{l('button.delete')}</Menu.Item>
        </Menu>
      }
    >
      <a>
        {l('button.more')} <DownOutlined/>
      </a>
    </Dropdown>
  );

  const columns: ProColumns<RoleTableListItem>[] = [
    {
      title: 'ID',
      dataIndex: 'id',
      hideInTable: true,
      hideInSearch: true,
      sorter: true,
    },
    {
      title: l('pages.role.roleCode'),
      dataIndex: 'roleCode',
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: l('pages.role.roleName'),
      dataIndex: 'roleName',
    },
    {
      title: l('pages.role.belongTenant'),
      hideInSearch: true,
      render: (dom, entity) => {
        return entity?.tenant?.tenantCode || '';
      },
    },
    // {
    //   title: '是否删除',
    //   dataIndex: 'isDelete',
    //   hideInForm: true,
    //   hideInSearch: true,
    //   hideInTable: false,
    //   filters: [
    //     {
    //       text: '未删除',
    //       value: 0,
    //     },
    //     {
    //       text: '已删除',
    //       value: 1,
    //     },
    //   ],
    //   filterMultiple: false,
    //   valueEnum: {
    //     true: {text: '已删除', status: 'Error'},
    //     false: {text: '未删除', status: 'Success'},
    //   },
    // },
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
      valueType: 'dateTime',
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
        <Button type={"link"}
                onClick={() => {
                  handleUpdateModalVisible(true);
                  setFormValues(record);
                }}
        >
          {l('button.config')}
        </Button>,
        <MoreBtn key="more" item={record}/>,
      ],
    },
  ];

  return (
    <PageContainer title={false}>
      <ProTable<RoleTableListItem>
        headerTitle={l('pages.role.roleManagement')}
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
        request={(params, sorter, filter) => queryData(url, {tenantId: getStorageTenantId(), sorter, filter})}
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
                })}  &nbsp;&nbsp;            </div>
          }
        >
          <Button type="primary" danger
                  onClick={() => {
                    Modal.confirm({
                      title: l('pages.role.delete'),
                      content: l('pages.role.deleteConfirm'),
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
        </FooterToolbar>
      )}
      <RoleForm
        onSubmit={async (value) => {
          const success = await handleAddOrUpdate(url, value);
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
      {
        formValues && Object.keys(formValues).length ? (
          <RoleForm
            onSubmit={async (value) => {
              const success = await handleAddOrUpdate(url, value);
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
          />
        ) : undefined
      }
      <Drawer
        width={600}
        visible={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.id && (
          <ProDescriptions<RoleTableListItem>
            column={2}
            title={row?.roleName}
            request={async () => ({
              data: row || {},
            })}
            params={{
              id: row?.id,
            }}
            columns={columns}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};

export default connect()(RoleFormList);
