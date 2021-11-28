import React, {useRef, useState} from "react";
import {DownOutlined, PlusOutlined} from '@ant-design/icons';
import {ActionType, ProColumns} from "@ant-design/pro-table";
import {Button, Drawer, Modal, Dropdown, Menu} from 'antd';
import {FooterToolbar} from '@ant-design/pro-layout';
import ProTable from '@ant-design/pro-table';
import ProDescriptions from '@ant-design/pro-descriptions';
import {UserTableListItem} from "@/pages/user/data";
import {handleAddOrUpdate, handleRemove, queryData, updateEnabled} from "@/components/Common/crud";
import UserForm from "@/pages/user/components/UserForm";

const url = '/api/user';
const UserTableList: React.FC<{}> = (props: any) => {
  const {dispatch} = props;
  const [row, setRow] = useState<UserTableListItem>();
  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [formValues, setFormValues] = useState({});
  const actionRef = useRef<ActionType>();
  const [selectedRowsState, setSelectedRows] = useState<UserTableListItem[]>([]);

  const editAndDelete = (key: string | number, currentItem: UserTableListItem) => {
    if (key === 'edit') {
      setFormValues(currentItem);
      handleUpdateModalVisible(true);
    } else if (key === 'delete') {
      Modal.confirm({
        title: '删除用户',
        content: '确定删除该用户吗？',
        okText: '确认',
        cancelText: '取消',
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
          <Menu.Item key="edit">编辑</Menu.Item>
          {item.username=='admin'?'':(<Menu.Item key="delete">删除</Menu.Item>)}
        </Menu>
      }
    >
      <a>
        更多 <DownOutlined/>
      </a>
    </Dropdown>
  );

  const columns: ProColumns<UserTableListItem>[] = [
    {
      title: '用户名',
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
      title: '昵称',
      sorter: true,
      dataIndex: 'nickname',
      hideInTable: false,
    },
    {
      title: '工号',
      sorter: true,
      dataIndex: 'worknum',
      hideInTable: false,
    },
    {
      title: '手机号',
      sorter: true,
      dataIndex: 'mobile',
      hideInTable: false,
    },
    {
      title: '是否启用',
      dataIndex: 'enabled',
      hideInForm: true,
      hideInSearch: true,
      hideInTable: false,
      filters: [
        {
          text: '已启用',
          value: 1,
        },
        {
          text: '已禁用',
          value: 0,
        },
      ],
      filterMultiple: false,
      valueEnum: {
        true: {text: '已启用', status: 'Success'},
        false: {text: '已禁用', status: 'Error'},
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      sorter: true,
      valueType: 'dateTime',
      hideInTable: true,
    },
    {
      title: '最近更新时间',
      dataIndex: 'updateTime',
      sorter: true,
      valueType: 'dateTime',
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
        <MoreBtn key="more" item={record}/>,
      ],
    },
  ];

  return (
    <>
      <ProTable<UserTableListItem>
        headerTitle="用户管理"
        actionRef={actionRef}
        rowKey="id"
        search={{
        labelWidth: 120,
      }}
        toolBarRender={() => [
        <Button type="primary" onClick={() => handleModalVisible(true)}>
          <PlusOutlined/> 新建
        </Button>,
      ]}
        request={(params, sorter, filter) => queryData(url, {...params, sorter, filter})}
        columns={columns}
        rowSelection={{
        onChange: (_, selectedRows) => setSelectedRows(selectedRows),
      }}
        />
        {selectedRowsState?.length > 0 && (
          <FooterToolbar
            extra={
              <div>
                已选择 <a style={{fontWeight: 600}}>{selectedRowsState.length}</a> 项&nbsp;&nbsp;
                <span>
                被禁用的用户共 {selectedRowsState.length - selectedRowsState.reduce((pre, item) => pre + (item.enabled ? 1 : 0), 0)} 人
              </span>
              </div>
            }
          >
            <Button type="primary" danger
                    onClick={() => {
                      Modal.confirm({
                        title: '删除用户',
                        content: '确定删除选中的用户吗？',
                        okText: '确认',
                        cancelText: '取消',
                        onOk: async () => {
                          await handleRemove(url, selectedRowsState);
                          setSelectedRows([]);
                          actionRef.current?.reloadAndRest?.();
                        }
                      });
                    }}
            >
              批量删除
            </Button>
            <Button type="primary"
                    onClick={() => {
                      Modal.confirm({
                        title: '启用用户',
                        content: '确定启用选中的用户吗？',
                        okText: '确认',
                        cancelText: '取消',
                        onOk: async () => {
                          await updateEnabled(url, selectedRowsState, true);
                          setSelectedRows([]);
                          actionRef.current?.reloadAndRest?.();
                        }
                      });
                    }}
            >批量启用</Button>
            <Button danger
                    onClick={() => {
                      Modal.confirm({
                        title: '禁用用户',
                        content: '确定禁用选中的用户吗？',
                        okText: '确认',
                        cancelText: '取消',
                        onOk: async () => {
                          await updateEnabled(url, selectedRowsState, false);
                          setSelectedRows([]);
                          actionRef.current?.reloadAndRest?.();
                        }
                      });
                    }}
            >批量禁用</Button>
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
        />
          ): null}
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
    </>
);
};

export default UserTableList;
