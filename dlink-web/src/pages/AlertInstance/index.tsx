import React, {useRef, useState} from "react";
import {DownOutlined, PlusOutlined} from '@ant-design/icons';
import {ActionType, ProColumns} from "@ant-design/pro-table";
import {Button, message, Input, Drawer, Modal, Dropdown, Menu} from 'antd';
import {PageContainer, FooterToolbar} from '@ant-design/pro-layout';
import ProTable from '@ant-design/pro-table';
import ProDescriptions from '@ant-design/pro-descriptions';
import {AlertInstanceTableListItem} from "@/pages/AlertInstance/data";
import {handleAddOrUpdate, handleRemove, queryData, updateEnabled} from "@/components/Common/crud";
import AlertInstanceChooseForm from "@/pages/AlertInstance/components/AlertInstanceChooseForm";

const url = '/api/alertInstance';
const AlertInstanceTableList: React.FC<{}> = (props: any) => {
  const {dispatch} = props;
  const [row, setRow] = useState<AlertInstanceTableListItem>();
  const [values, setValues] = useState<AlertInstanceTableListItem>();
  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  const [selectedRowsState, setSelectedRows] = useState<AlertInstanceTableListItem[]>([]);

  const editAndDelete = (key: string | number, currentItem: AlertInstanceTableListItem) => {
    if (key === 'edit') {
      setValues(currentItem);
      handleModalVisible(true);
    } else if (key === 'delete') {
      Modal.confirm({
        title: '删除报警实例',
        content: '确定删除该报警实例吗？',
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
    item: AlertInstanceTableListItem;
  }> = ({item}) => (
    <Dropdown
      overlay={
      <Menu onClick={({key}) => editAndDelete(key, item)}>
  <Menu.Item key="edit">编辑</Menu.Item>
    <Menu.Item key="delete">删除</Menu.Item>
    </Menu>
}
>
  <a>
    更多 <DownOutlined/>
  </a>
  </Dropdown>
);

  const columns: ProColumns<AlertInstanceTableListItem>[] = [
    {
      title: '名称',
      dataIndex: 'name',
      tip: '名称是唯一的',
      sorter: true,
      formItemProps: {
        rules: [
          {
            required: true,
            message: '名称为必填项',
          },
        ],
      },
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: '实例ID',
      dataIndex: 'id',
      hideInTable: true,
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: '类型',
      sorter: true,
      dataIndex: 'type',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: false,
      filters: [
        {
          text: 'DingTalk',
          value: 'DingTalk',
        },{
          text: 'WeChat',
          value: 'WeChat',
        }
      ],
      filterMultiple: false,
      valueEnum: {
        'DingTalk': {text: 'DingTalk'},
        'WeChat': {text: 'WeChat'},
      },
    },
    {
      title: '配置',
      dataIndex: 'params',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: true,
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
      hideInTable: true
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
    handleModalVisible(true);
            setValues(record);
  }}
>
  配置
  </a>,
  <MoreBtn key="more" item={record}/>,
],
},
];

  return (
    <PageContainer>
      <ProTable<AlertInstanceTableListItem>
        headerTitle="报警实例管理"
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
  被禁用的报警实例共 {selectedRowsState.length - selectedRowsState.reduce((pre, item) => pre + (item.enabled ? 1 : 0), 0)} 人
  </span>
  </div>
  }
  >
    <Button type="primary" danger
    onClick={() => {
    Modal.confirm({
      title: '删除报警实例',
      content: '确定删除选中的报警实例吗？',
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
      title: '启用报警实例',
      content: '确定启用选中的报警实例吗？',
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
      title: '禁用报警实例',
      content: '确定禁用选中的报警实例吗？',
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
  <AlertInstanceChooseForm onCancel={() => {
    handleModalVisible(false);
    setValues(undefined);
  }}
  modalVisible={modalVisible}
  onSubmit={() => {
    actionRef.current?.reloadAndRest?.();
  }}
  values={values}
  />
  <Drawer
  width={600}
  visible={!!row}
  onClose={() => {
    setRow(undefined);
  }}
  closable={false}
    >
    {row?.name && (
      <ProDescriptions<AlertInstanceTableListItem>
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

export default AlertInstanceTableList;
