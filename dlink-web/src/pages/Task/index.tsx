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


import {DownOutlined, PlusOutlined} from '@ant-design/icons';
import {Button, Drawer, Modal} from 'antd';
import React, {useRef, useState} from 'react';
import {PageContainer} from '@ant-design/pro-layout';
import type {ActionType, ProColumns} from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import ProDescriptions from '@ant-design/pro-descriptions';
import CreateForm from './components/CreateForm';
import UpdateForm from './components/UpdateForm';
import type {TaskTableListItem} from './data.d';

import Dropdown from "antd/es/dropdown/dropdown";
import Menu from "antd/es/menu";
import {handleAddOrUpdate, handleRemove, handleSubmit, queryData} from "@/components/Common/crud";
import {l} from "@/utils/intl";

const url = '/api/task';

const TaskTableList: React.FC<{}> = () => {
  const [createModalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [formValues, setFormValues] = useState({});
  const actionRef = useRef<ActionType>();
  const [row, setRow] = useState<TaskTableListItem>();
  const [selectedRowsState, setSelectedRows] = useState<TaskTableListItem[]>([]);

  const editAndDelete = (key: string | number, currentItem: TaskTableListItem) => {
    if (key === 'edit') {
      handleUpdateModalVisible(true);
      setFormValues(currentItem);
    } else if (key === 'delete') {
      Modal.confirm({
        title: l('pages.task.delete'),
        content: l('pages.task.deleteConfirm'),
        okText: l('button.confirm'),
        cancelText: l('button.cancel'),
        onOk: async () => {
          await handleRemove(url, [currentItem]);
          actionRef.current?.reloadAndRest?.();
        }
      });
    } else if (key === 'submit') {
      Modal.confirm({
        title: l('pages.task.exec'),
        content: l('pages.task.execConfirm'),
        okText: l('button.confirm'),
        cancelText: l('button.cancel'),
        onOk: async () => {
          await handleSubmit(url + '/submit', l('pages.task.exec'), [currentItem]);
          actionRef.current?.reloadAndRest?.();
        }
      });
    }
  };


  const MoreBtn: React.FC<{
    item: TaskTableListItem;
  }> = ({item}) => (
    <Dropdown
      overlay={
        <Menu onClick={({key}) => editAndDelete(key, item)}>
          <Menu.Item key="submit">{l('button.submit')}</Menu.Item>
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

  const columns: ProColumns<TaskTableListItem>[] = [
    {
      title: l('pages.task.name'),
      dataIndex: 'name',
      sorter: true,
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: l('pages.task.id'),
      dataIndex: 'id',
      hideInTable: true,
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: l('pages.task.alias'),
      sorter: true,
      dataIndex: 'alias',
      hideInTable: false,
    },
    {
      title: l('pages.task.type'),
      sorter: true,
      dataIndex: 'type',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: false,
    },
    {
      title: l('pages.task.checkPoint'),
      sorter: true,
      dataIndex: 'checkPoint',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: l('pages.task.savePointPath'),
      sorter: true,
      dataIndex: 'savePointPath',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: l('pages.task.parallelism'),
      sorter: true,
      dataIndex: 'parallelism',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: l('pages.task.fragment'),
      sorter: true,
      dataIndex: 'fragment',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: l('pages.task.clusterId'),
      sorter: true,
      dataIndex: 'clusterId',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: l('pages.task.clusterName'),
      sorter: true,
      dataIndex: 'clusterName',
      hideInForm: true,
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: l('global.table.note'),
      sorter: true,
      valueType: 'textarea',
      dataIndex: 'note',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: true,
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
      hideInForm: true,
      hideInTable: true,
    },
    {
      title: l('global.table.lastUpdateTime'),
      dataIndex: 'updateTime',
      sorter: true,
      valueType: 'dateTime',
      hideInForm: true,
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
    <PageContainer>
      <ProTable<TaskTableListItem>
        headerTitle={l('pages.task.management')}
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
      <CreateForm onCancel={() => handleModalVisible(false)} modalVisible={createModalVisible}>
        <ProTable<TaskTableListItem, TaskTableListItem>
          onSubmit={async (value) => {
            const success = await handleAddOrUpdate(url, value);
            if (success) {
              handleModalVisible(false);
              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
          }}
          rowKey="id"
          type="form"
          columns={columns}
        />
      </CreateForm>
      {formValues && Object.keys(formValues).length ? (
        <UpdateForm
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
          updateModalVisible={updateModalVisible}
          values={formValues}
        />
      ) : null}

      <Drawer
        width={600}
        visible={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.name && (
          <ProDescriptions<TaskTableListItem>
            column={2}
            title={row?.name}
            request={async () => ({
              data: row || {},
            })}
            params={{
              id: row?.name,
            }}
            columns={columns}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};

export default TaskTableList;
