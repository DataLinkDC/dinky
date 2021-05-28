import {DownOutlined, PlusOutlined, UserOutlined } from '@ant-design/icons';
import {Button, message, Input, Drawer, Modal} from 'antd';
import React, { useState, useRef } from 'react';
import { PageContainer, FooterToolbar } from '@ant-design/pro-layout';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import ProDescriptions from '@ant-design/pro-descriptions';
import CreateForm from './components/CreateForm';
import UpdateForm from './components/UpdateForm';
import type { FlinkSqlTableListItem } from './data.d';

import styles from './index.less';
import Dropdown from "antd/es/dropdown/dropdown";
import Menu from "antd/es/menu";
import {handleAddOrUpdate, handleRemove, handleSubmit, queryData, updateEnabled} from "@/components/Common/crud";

const url = '/api-dlink/flinkSql';

const FlinkSqlTableList: React.FC<{}> = () => {
    const [createModalVisible, handleModalVisible] = useState<boolean>(false);
    const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
    const [formValues, setFormValues] = useState({});
    const actionRef = useRef<ActionType>();
    const [row, setRow] = useState<FlinkSqlTableListItem>();
    const [selectedRowsState, setSelectedRows] = useState<FlinkSqlTableListItem[]>([]);

    const editAndDelete = (key: string | number, currentItem: FlinkSqlTableListItem) => {
        if (key === 'edit') {
            handleUpdateModalVisible(true);
            setFormValues(currentItem);
        } else if (key === 'delete') {
            Modal.confirm({
                title: '删除FlinkSql',
                content: '确定删除该FlinkSql吗？',
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
        item: FlinkSqlTableListItem;
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
    const columns: ProColumns<FlinkSqlTableListItem>[] = [
        {
            title: '名称',
            dataIndex: 'name',
            tip: 'name是唯一的',
            sorter: true,
            formItemProps: {
                rules: [
                    {
                        required: true,
                        message: 'name为必填项',
                    },
                ],
            },
            render: (dom, entity) => {
                return <a onClick={() => setRow(entity)}>{dom}</a>;
            },
        },
        {
            title: '表名',
            sorter: true,
            dataIndex: 'alias',
            hideInForm: false,
            hideInTable:false,
        },
        {
            title: '类型',
            sorter: true,
            dataIndex: 'type',
            hideInForm: false,
            hideInSearch:true,
            hideInTable:false,
            filters: [
              {
                text: 'CREATE TABLE',
                value: 'CREATE TABLE',
              },
              {
                text: 'INSERT INTO',
                value: 'INSERT INTO',
              },
              {
                text: 'CREATE VIEW',
                value: 'CREATE VIEW',
              },
              {
                text: 'CREATE AGGTABLE',
                value: 'CREATE AGGTABLE',
              },
            ],
            filterMultiple: false,
            valueEnum: {
              'CREATE TABLE': { text: 'CREATE TABLE'},
              'INSERT INTO': { text: 'INSERT INTO'},
              'CREATE VIEW': { text: 'CREATE VIEW' },
              'CREATE AGGTABLE': { text: 'CREATE AGGTABLE' },
            },
        },
        {
            title: '次序',
            sorter: true,
            dataIndex: 'sqlIndex',
            hideInForm: false,
            hideInSearch:true,
            hideInTable:false,
        },
        {
            title: '语句',
            sorter: true,
            valueType: 'textarea',
            dataIndex: 'statement',
            hideInForm: false,
            hideInSearch:true,
            hideInTable:true,
        },
        {
            title: '备注',
            sorter: true,
            dataIndex: 'note',
            valueType: 'textarea',
            hideInForm: false,
            hideInSearch:true,
            hideInTable:true,
        },
        {
            title: '是否启用',
            sorter: true,
            dataIndex: 'enabled',
            hideInForm: false,
            hideInSearch:true,
            hideInTable:false,
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
            valueEnum: {
                true: { text: '正常', status: 'Success' },
                false: { text: '禁用', status: 'Error' },
            },
        },
        {
            title: '创建人ID',
            sorter: true,
            dataIndex: 'createUser',
            hideInForm: true,
            hideInSearch:true,
            hideInTable:true,
            hideInDescriptions:true,
        },
        {
          title: '创建人',
          sorter: true,
          dataIndex: 'createNickName',
          hideInForm: true,
          hideInSearch: true,
          hideInTable:true,
        },
        {
            title: '创建时间',
            sorter: true,
            dataIndex: 'createTime',
            hideInForm: true,
            hideInTable:true,
            valueType: 'dateTime',
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
            title: '更新人ID',
            sorter: true,
            dataIndex: 'updateUser',
            hideInForm: true,
            hideInSearch:true,
            hideInTable:true,
            hideInDescriptions:true,
        },
        {
          title: '更新人',
          sorter: true,
          dataIndex: 'updateNickName',
          hideInForm: true,
          hideInSearch: true,
          hideInTable: false,
        },
        {
            title: '更新时间',
            sorter: true,
            dataIndex: 'updateTime',
            hideInForm: true,
            hideInTable:false,
            valueType: 'dateTime',
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
            title: '任务ID',
            sorter: true,
            dataIndex: 'taskId',
            hideInForm: false,
            hideInSearch:true,
            hideInTable:false,
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
            <ProTable<FlinkSqlTableListItem>
                headerTitle="FlinkSql管理"
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
                request={(params, sorter, filter) => queryData(url,{ ...params, sorter, filter })}
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
                被禁用的FlinkSql共 {selectedRowsState.length - selectedRowsState.reduce((pre, item) => pre + (item.enabled?1:0), 0)} 人
              </span>
                            </div>
                        }
                    >
                        <Button type="primary" danger
                                onClick ={()=>{
                                    Modal.confirm({
                                        title: '禁用FlinkSql',
                                        content: '确定禁用选中的FlinkSql吗？',
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
                                        title: '禁用FlinkSql',
                                        content: '确定禁用选中的FlinkSql吗？',
                                        okText: '确认',
                                        cancelText: '取消',
                                        onOk:async () => {
                                            await updateEnabled(url,selectedRowsState,true);
                                            setSelectedRows([]);
                                            actionRef.current?.reloadAndRest?.();
                                        }
                                    });
                                }}
                        >批量启用</Button>
                        <Button danger
                                onClick ={()=>{
                                    Modal.confirm({
                                        title: '禁用FlinkSql',
                                        content: '确定禁用选中的FlinkSql吗？',
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
                    <ProTable<FlinkSqlTableListItem, FlinkSqlTableListItem>
                    onSubmit={async (value) => {
                    const success = await handleAddOrUpdate(url,value);
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
                            const success = await handleAddOrUpdate(url,value);
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
                        <ProDescriptions<FlinkSqlTableListItem>
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

export default FlinkSqlTableList;
