import {DownOutlined, HeartOutlined, PlusOutlined, UserOutlined} from '@ant-design/icons';
import {Button, message, Input, Drawer, Modal} from 'antd';
import React, {useState, useRef} from 'react';
import {PageContainer, FooterToolbar} from '@ant-design/pro-layout';
import type {ProColumns, ActionType} from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import ProDescriptions from '@ant-design/pro-descriptions';
import CreateForm from './components/CreateForm';
import UpdateForm from './components/UpdateForm';
import type {DocumentTableListItem} from './data.d';

import styles from './index.less';

import Dropdown from "antd/es/dropdown/dropdown";
import Menu from "antd/es/menu";
import {
  handleAddOrUpdate, handleOption, handleRemove, queryData,
  updateEnabled
} from "@/components/Common/crud";
import {getFillAllByVersion} from "@/components/Studio/StudioEvent/DDL";

const url = '/api/document';

const DocumentTableList: React.FC<{}> = (props: any) => {

  const {dispatch} = props;
  const [createModalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [formValues, setFormValues] = useState({});
  const actionRef = useRef<ActionType>();
  const [row, setRow] = useState<DocumentTableListItem>();
  const [selectedRowsState, setSelectedRows] = useState<DocumentTableListItem[]>([]);

  const editAndDelete = (key: string | number, currentItem: DocumentTableListItem) => {
    if (key === 'edit') {
      handleUpdateModalVisible(true);
      setFormValues(currentItem);
    } else if (key === 'delete') {
      Modal.confirm({
        title: '删除文档',
        content: '确定删除该文档吗？',
        okText: '确认',
        cancelText: '取消',
        onOk:async () => {
          await handleRemove(url,[currentItem]);
          actionRef.current?.reloadAndRest?.();
        }
      });
    }
  };

  const MoreBtn: React.FC<{
    item: DocumentTableListItem;
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

  const columns: ProColumns<DocumentTableListItem>[] = [
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
      title: '文档ID',
      dataIndex: 'id',
      hideInTable: true,
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: '文档类型',
      sorter: true,
      dataIndex: 'category',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: false,
      filters: [
        {
          text: '函数',
          value: 'function',
        }
      ],
      filterMultiple: false,
      valueEnum: {
        'Method': { text: 'Method'},
        'Function': { text: 'Function'},
        'Constructor': { text: 'Constructor'},
        'Field': { text: 'Field'},
        'Variable': { text: 'Variable'},
        'Class': { text: 'Class'},
        'Struct': { text: 'Struct'},
        'Interface': { text: 'Interface'},
        'Module': { text: 'Module'},
        'Property': { text: 'Property'},
        'Event': { text: 'Event'},
        'Operator': { text: 'Operator'},
        'Unit': { text: 'Unit'},
        'Value': { text: 'Value'},
        'Constant': { text: 'Constant'},
        'Enum': { text: 'Enum'},
        'EnumMember': { text: 'EnumMember'},
        'Keyword': { text: 'Keyword'},
        'Text': { text: 'Text'},
        'Color': { text: 'Color'},
        'File': { text: 'File'},
        'Reference': { text: 'Reference'},
        'Customcolor': { text: 'Customcolor'},
        'Folder': { text: 'Folder'},
        'TypeParameter': { text: 'TypeParameter'},
        'User': { text: 'User'},
        'Issue': { text: 'Issue'},
        'Snippet': { text: 'Snippet'},
      },
    },
    {
      title: '类型',
      sorter: true,
      dataIndex: 'type',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: false,
      /*filters: [
        {
          text: '内置函数',
          value: '内置函数',
        },
        {
          text: 'UDF',
          value: 'UDF',
        },
      ],
      filterMultiple: false,
      valueEnum: {
        '内置函数': { text: '内置函数'},
        'UDF': { text: 'UDF'},
      },*/
    },
    {
      title: '子类型',
      sorter: true,
      dataIndex: 'subtype',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: false,
      /*filters: [
        {
          text: '比较函数',
          value: '比较函数',
        },
        {
          text: '逻辑函数',
          value: '逻辑函数',
        },{
          text: '算术函数',
          value: '算术函数',
        },{
          text: '字符串函数',
          value: '字符串函数',
        },{
          text: '时间函数',
          value: '时间函数',
        },{
          text: '条件函数',
          value: '条件函数',
        },{
          text: '类型转换函数',
          value: '类型转换函数',
        },{
          text: 'Collection 函数',
          value: 'Collection 函数',
        },{
          text: 'Value Collection 函数',
          value: 'Value Collection 函数',
        },{
          text: 'Value Access 函数',
          value: 'Value Access 函数',
        },{
          text: '分组函数',
          value: '分组函数',
        },{
          text: 'hash函数',
          value: 'hash函数',
        },{
          text: '聚合函数',
          value: '聚合函数',
        },{
          text: '列函数',
          value: '列函数',
        },{
          text: '表值聚合函数',
          value: '表值聚合函数',
        },{
          text: '其他函数',
          value: '其他函数',
        },
      ],
      filterMultiple: false,
      valueEnum: {
        '比较函数': { text: '比较函数'},
        '逻辑函数': { text: '逻辑函数'},
        '算术函数': { text: '算术函数'},
        '字符串函数': { text: '字符串函数'},
        '时间函数': { text: '时间函数'},
        '条件函数': { text: '条件函数'},
        '类型转换函数': { text: '类型转换函数'},
        'Collection 函数': { text: 'Collection 函数'},
        'Value Collection 函数': { text: 'Value Collection 函数'},
        'Value Access 函数': { text: 'Value Access 函数'},
        '分组函数': { text: '分组函数'},
        'hash函数': { text: 'hash函数'},
        '聚合函数': { text: '聚合函数'},
        '列函数': { text: '列函数'},
        '表值聚合函数': { text: '表值聚合函数'},
        '其他函数': { text: '其他函数'},
      },*/
    },
    {
      title: '描述',
      sorter: true,
      dataIndex: 'description',
      valueType: 'textarea',
      hideInForm: false,
      hideInSearch: false,
      hideInTable: true,
    },{
      title: '填充值',
      sorter: true,
      dataIndex: 'fillValue',
      valueType: 'textarea',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: '版本',
      sorter: true,
      dataIndex: 'version',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: '是否启用',
      dataIndex: 'enabled',
      hideInForm: false,
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
        true: { text: '已启用', status: 'Success' },
        false: { text: '已禁用', status: 'Error' },
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      sorter: true,
      valueType: 'dateTime',
      hideInForm: true,
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
        <MoreBtn key="more" item={record}/>,
      ],
    },
  ];

  return (
    <PageContainer>
      <ProTable<DocumentTableListItem>
        headerTitle="文档管理"
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
        request={(params, sorter, filter) => queryData(url,{...params, sorter, filter})}
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
                被禁用的文档共 {selectedRowsState.length - selectedRowsState.reduce((pre, item) => pre + (item.enabled ? 1 : 0), 0)} 人
              </span>
              </div>
            }
          >
            <Button type="primary" danger
                    onClick ={()=>{
                      Modal.confirm({
                        title: '删除文档',
                        content: '确定删除选中的文档吗？',
                        okText: '确认',
                        cancelText: '取消',
                        onOk:async () => {
                          await handleRemove(url,selectedRowsState);
                          setSelectedRows([]);
                          actionRef.current?.reloadAndRest?.();
                        }
                      });
                    }}
            >
              批量删除
            </Button>
            <Button type="primary"
                    onClick ={()=>{
                      Modal.confirm({
                        title: '启用文档',
                        content: '确定启用选中的文档吗？',
                        okText: '确认',
                        cancelText: '取消',
                        onOk:async () => {
                          await updateEnabled(url,selectedRowsState, true);
                          setSelectedRows([]);
                          actionRef.current?.reloadAndRest?.();
                        }
                      });
                    }}
            >批量启用</Button>
            <Button danger
                    onClick ={()=>{
                      Modal.confirm({
                        title: '禁用文档',
                        content: '确定禁用选中的文档吗？',
                        okText: '确认',
                        cancelText: '取消',
                        onOk:async () => {
                          await updateEnabled(url,selectedRowsState, false);
                          setSelectedRows([]);
                          actionRef.current?.reloadAndRest?.();
                        }
                      });
                    }}
            >批量禁用</Button>
          </FooterToolbar>
        )}
        <CreateForm onCancel={() => handleModalVisible(false)} modalVisible={createModalVisible}>
          <ProTable<DocumentTableListItem, DocumentTableListItem>
          onSubmit={async (value) => {
          const success = await handleAddOrUpdate(url,value);
          if (success) {
            handleModalVisible(false);
            if (actionRef.current) {
              actionRef.current.reload();
            }
            getFillAllByVersion('',dispatch);
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
              const success = await handleAddOrUpdate(url,value);
              if (success) {
                handleUpdateModalVisible(false);
                setFormValues({});
                if (actionRef.current) {
                  actionRef.current.reload();
                }
                getFillAllByVersion('',dispatch);
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
            <ProDescriptions<DocumentTableListItem>
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

export default DocumentTableList;
