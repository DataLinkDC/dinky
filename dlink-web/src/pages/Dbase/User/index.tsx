import {DownOutlined, PlusOutlined, UserOutlined } from '@ant-design/icons';
import {Button, message, Input, Drawer, Modal} from 'antd';
import React, { useState, useRef } from 'react';
import { PageContainer, FooterToolbar } from '@ant-design/pro-layout';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import ProDescriptions from '@ant-design/pro-descriptions';
import CreateForm from './components/CreateForm';
import UpdateForm from './components/UpdateForm';
import type { UserTableListItem } from './data.d';
import { addOrUpdateUser, removeUser, queryUser} from './service';

import styles from './index.less';
import Avatar from "antd/es/avatar";
import Image from "antd/es/image";
import Dropdown from "antd/es/dropdown/dropdown";
import Menu from "antd/es/menu";


const handleAddOrUpdate = async (fields: UserTableListItem) => {
  const tipsTitle = fields.id?"修改":"添加";
  const hide = message.loading(`正在${tipsTitle}`);
  try {
    const { msg } = await addOrUpdateUser({ ...fields });
    hide();
    message.success(msg);
    return true;
  } catch (error) {
    hide();
    message.error(error);
    return false;
  }
};

const handleRemove = async (selectedRows: UserTableListItem[]) => {
  const hide = message.loading('正在删除');
  if (!selectedRows) return true;
  try {
    const { msg } = await removeUser(selectedRows.map((row) => row.id));
    hide();
    message.success(msg);
    return true;
  } catch (error) {
    hide();
    message.error('删除失败，请重试');
    return false;
  }
};


const TableList: React.FC<{}> = () => {
  const [createModalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [formValues, setFormValues] = useState({});
  const actionRef = useRef<ActionType>();
  const [row, setRow] = useState<UserTableListItem>();
  const [selectedRowsState, setSelectedRows] = useState<UserTableListItem[]>([]);

  const editAndDelete = (key: string | number, currentItem: UserTableListItem) => {
    if (key === 'edit') {
      handleUpdateModalVisible(true);
      setFormValues(currentItem);
    } else if (key === 'delete') {
      Modal.confirm({
        title: '删除任务',
        content: '确定删除该任务吗？',
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          handleRemove([currentItem])
        }
      });
    }
  };

  const updateEnabled = (selectedRows: UserTableListItem[],enabled:boolean) =>{
    selectedRows.forEach((item)=>{
      handleAddOrUpdate({id:item.id,username:item.username,enabled:enabled})
    })
  };

  const MoreBtn: React.FC<{
    item: UserTableListItem;
  }> = ({ item }) => (
    <Dropdown
      overlay={
        <Menu onClick={({ key }) => editAndDelete(key, item)}>
          <Menu.Item key="edit">编辑</Menu.Item>
          <Menu.Item key="delete">删除</Menu.Item>
        </Menu>
      }
    >
      <a>
        更多 <DownOutlined />
      </a>
    </Dropdown>
  );

  const columns: ProColumns<UserTableListItem>[] = [
    {
      title: '用户名',
      dataIndex: 'username',
      tip: '用户名是唯一的',
      sorter: true,
      formItemProps: {
        rules: [
          {
            required: true,
            message: '用户名为必填项',
          },
        ],
      },
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: '昵称',
      sorter: true,
      dataIndex: 'nickname',
    },
    {
      title: '头像',
      dataIndex: 'avatar',
      hideInForm: true,
      hideInSearch:true,
      render: (val:string) => {
        return (val!='-'&&val!=null)?<Avatar
          src={<Image src={val} />}
        />:<Avatar icon={<UserOutlined />} />;
        },
    },
    {
      title: '状态',
      dataIndex: 'enabled',
      hideInForm: true,
      //hideInSearch:true,
      filters: [
        {
          text: '正常',
          value: 1,
        },
        {
          text: '禁用',
          value: 0,
        },
      ],
      filterMultiple: false,
      //onFilter: (value, record) => record.address.indexOf(value) === 0,
      valueEnum: {
        true: { text: '正常', status: 'Success' },
        false: { text: '禁用', status: 'Error' },
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      sorter: true,
      valueType: 'dateTime',
      hideInForm: true,
      //hideInSearch:true,
      hideInTable:true,
      renderFormItem: (item, { defaultRender, ...rest }, form) => {
        const status = form.getFieldValue('status');
        if (`${status}` === '0') {
          return false;
        }
        if (`${status}` === '3') {
          return <Input {...rest} placeholder="请输入异常原因！" />;
        }
        return defaultRender(item);
      },
    },
    {
      title: '最近更新时间',
      dataIndex: 'updateTime',
      sorter: true,
      valueType: 'dateTime',
      hideInForm: true,
      hideInSearch:true,
      renderFormItem: (item, { defaultRender, ...rest }, form) => {
        const status = form.getFieldValue('status');
        if (`${status}` === '0') {
          return false;
        }
        if (`${status}` === '3') {
          return <Input {...rest} placeholder="请输入异常原因！" />;
        }
        return defaultRender(item);
      },
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => [
        <a
          onClick={() => {
            handleUpdateModalVisible(true);
            setFormValues(record);
          }}
        >
          配置
        </a>,
        <MoreBtn key="more" item={record} />,
      ],
    },
  ];

  return (
    <PageContainer>
      <ProTable<UserTableListItem>
        headerTitle="用户管理"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Button type="primary" onClick={() => handleModalVisible(true)}>
            <PlusOutlined /> 新建
          </Button>,
        ]}
        request={(params, sorter, filter) => queryUser({ ...params, sorter, filter })}
        columns={columns}
        rowSelection={{
          onChange: (_, selectedRows) => setSelectedRows(selectedRows),
        }}
      />
      {selectedRowsState?.length > 0 && (
        <FooterToolbar
          extra={
            <div>
              已选择 <a style={{ fontWeight: 600 }}>{selectedRowsState.length}</a> 项&nbsp;&nbsp;
              <span>
                被禁用的用户共 {selectedRowsState.length - selectedRowsState.reduce((pre, item) => pre + (item.enabled?1:0), 0)} 人
              </span>
            </div>
          }
        >
          <Button type="primary" danger
            onClick={async () => {
              await handleRemove(selectedRowsState);
              setSelectedRows([]);
              actionRef.current?.reloadAndRest?.();
            }}
          >
            批量删除
          </Button>
          <Button type="primary"
                  onClick={async () => {
                    await updateEnabled(selectedRowsState,true);
                    setSelectedRows([]);
                    actionRef.current?.reloadAndRest?.();
                  }}
          >批量启用</Button>
          <Button danger
                  onClick={async () => {
                    await updateEnabled(selectedRowsState,false);
                    setSelectedRows([]);
                    actionRef.current?.reloadAndRest?.();
                  }}
          >批量禁用</Button>
        </FooterToolbar>
      )}
      <CreateForm onCancel={() => handleModalVisible(false)} modalVisible={createModalVisible}>
        <ProTable<UserTableListItem, UserTableListItem>
          onSubmit={async (value) => {
            const success = await handleAddOrUpdate(value);
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
            const success = await handleAddOrUpdate(value);
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
    </PageContainer>
  );
};

export default TableList;
