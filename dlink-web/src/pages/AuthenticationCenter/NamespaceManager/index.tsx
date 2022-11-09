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
import {handleAddOrUpdate, handleRemove, queryData, updateEnabled} from "@/components/Common/crud";
import {NameSpaceTableListItem} from "@/pages/AuthenticationCenter/data.d";
import NameSpaceForm from "@/pages/AuthenticationCenter/NamespaceManager/components/NameSpaceForm";
import {l} from "@/utils/intl";

const url = '/api/namespace';

const NameSpaceFormList: React.FC<{}> = (props: any) => {
  const [row, setRow] = useState<NameSpaceTableListItem>();
  const [formValues, setFormValues] = useState<NameSpaceTableListItem>();
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  const [selectedRowsState, setSelectedRows] = useState<NameSpaceTableListItem[]>([]);

  const editAndDelete = (key: string | number, currentItem: NameSpaceTableListItem) => {
    if (key === 'edit') {
      handleUpdateModalVisible(true);
      setFormValues(currentItem);
    } else if (key === 'delete') {
      Modal.confirm({
        title: l('pages.nameSpace.deleteNameSpace'),
        content: l('pages.nameSpace.deleteNameSpaceConfirm'),
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
    item: NameSpaceTableListItem;
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

  const columns: ProColumns<NameSpaceTableListItem>[] = [
    {
      title: 'ID',
      dataIndex: 'id',
      hideInTable: true,
      hideInSearch: true,
      sorter: true,
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: l('pages.nameSpace.NameSpaceCode'),
      dataIndex: 'namespaceCode',
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: l('pages.nameSpace.belongTenant'),
      hideInSearch: true,
      render: (dom, entity) => {
        return entity.tenant.tenantCode;
      },
    },
    {
      title: l('global.table.isEnable'),
      dataIndex: 'enabled',
      hideInTable: false,
      hideInSearch: true,
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
        <a
          onClick={() => {
            handleUpdateModalVisible(true);
            setFormValues(record);
          }}
        >
          {l('button.config')}
        </a>,
        <MoreBtn key="more" item={record}/>,
      ],
    },
  ];

  return (
    <PageContainer title={false}>
      <ProTable<NameSpaceTableListItem>
        headerTitle={l('pages.nameSpace.NameSpaceManagement')}
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
  {l('pages.nameSpace.disableTotalOf', '', {total: selectedRowsState.length - selectedRowsState.reduce((pre, item) => pre + (item.enabled ? 1 : 0), 0)})}
  </span>
            </div>
          }
        >
          <Button type="primary" danger
                  onClick={() => {
                    Modal.confirm({
                      title: l('pages.nameSpace.deleteNameSpace'),
                      content: l('pages.nameSpace.deleteNameSpaceConfirm'),
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
                      title: l('pages.nameSpace.enableNameSpace'),
                      content: l('pages.nameSpace.enableNameSpaceConfirm'),
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
                      title: l('pages.nameSpace.disableNameSpace'),
                      content: l('pages.nameSpace.disableNameSpaceConfirm'),
                      okText: l('button.confirm'),
                      cancelText: l('button.cancel'),
                      onOk: async () => {
                        await updateEnabled(url, selectedRowsState, false);
                        setSelectedRows([]);
                        actionRef.current?.reloadAndRest?.();
                      }
                    });
                  }}
          > {l('button.batchDisable')}</Button>
        </FooterToolbar>
      )}
      <NameSpaceForm
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
          <NameSpaceForm
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
          <ProDescriptions<NameSpaceTableListItem>
            column={2}
            title={row?.namespaceCode}
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

export default NameSpaceFormList;
