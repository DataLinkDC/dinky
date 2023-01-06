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
import {Button, Drawer, Dropdown, Menu, message, Modal, Upload} from 'antd';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import ProDescriptions from '@ant-design/pro-descriptions';
import {JarTableListItem} from "@/pages/RegistrationCenter/data";
import {CODE, handleAddOrUpdate, handleRemove, queryData, updateEnabled} from "@/components/Common/crud";
import JarForm from "@/pages/RegistrationCenter/Jar/components/JarForm";
import {l} from "@/utils/intl";

const url = '/api/jar';
const JarTableList: React.FC<{}> = (props: any) => {
  const {dispatch} = props;


  const [row, setRow] = useState<JarTableListItem>();
  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [formValues, setFormValues] = useState({});
  const actionRef = useRef<ActionType>();
  const [selectedRowsState, setSelectedRows] = useState<JarTableListItem[]>([]);

  const editAndDelete = (key: string | number, currentItem: JarTableListItem) => {
    if (key === 'edit') {
      setFormValues(currentItem);
      handleUpdateModalVisible(true);
    } else if (key === 'delete') {
      Modal.confirm({
        title: l('pages.rc.jar.delete'),
        content: l('pages.rc.jar.deleteConfirm'),
        okText: l('button.confirm'),
        cancelText: l('button.cancel'),
        onOk: async () => {
          await handleRemove(url, [currentItem]);
          actionRef.current?.reloadAndRest?.();
        }
      });
    }
  };

  const getUploadProps = (dir: string) => {
    return {
      name: 'files',
      action: '/api/fileUpload',
      accept: 'jar',
      headers: {
        authorization: 'authorization-text',
      },
      data: {
        dir
      },
      showUploadList: true,
      onChange(info) {
        if (info.file.status === 'done') {
          if (info.file.response.code == CODE.SUCCESS) {
            message.success(info.file.response.msg);
          } else {
            message.warn(info.file.response.msg);
          }
        } else if (info.file.status === 'error') {
          message.error(`${info.file.name}` + l('app.request.upload.failed'));
        }
      },
    }
  };

  const MoreBtn: React.FC<{
    item: JarTableListItem;
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

  const columns: ProColumns<JarTableListItem>[] = [
    {
      title: l('pages.rc.jar.name'),
      dataIndex: 'name',
      sorter: true,
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: l('pages.rc.jar.id'),
      dataIndex: 'id',
      hideInTable: true,
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: l('pages.rc.jar.alias'),
      sorter: true,
      dataIndex: 'alias',
      hideInTable: false,
    },
    {
      title: l('pages.rc.jar.type'),
      sorter: true,
      dataIndex: 'type',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: false,
      filters: [
        {
          text: 'UserApp',
          value: 'UserApp',
        }
      ],
      filterMultiple: false,
      valueEnum: {
        'UserApp': {text: 'UserApp'},
      },
    },
    {
      title: l('pages.rc.jar.filePath'),
      sorter: true,
      dataIndex: 'path',
    },
    {
      title: l('pages.rc.jar.mainClass'),
      sorter: true,
      dataIndex: 'mainClass',
    }, {
      title: l('pages.rc.jar.execParams'),
      sorter: true,
      dataIndex: 'paras',
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
      hideInTable: true
    },
    {
      title: l('global.table.lastUpdateTime'),
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
        <Upload {...getUploadProps(record.path)}>
          <a>{l('button.upload')}</a>
        </Upload>,
        <MoreBtn key="more" item={record}/>,
      ],
    },
  ];

  return (
    <PageContainer title={false}>
      <ProTable<JarTableListItem>
        headerTitle={l('pages.rc.jar.Management')}
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
                {l('pages.rc.jar.disableTotalOf', '',
                  {
                    total: (selectedRowsState.length - selectedRowsState.reduce((pre, item) => pre + (item.enabled ? 1 : 0), 0))
                  })}
              </span>
            </div>
          }
        >
          <Button type="primary" danger
                  onClick={() => {
                    Modal.confirm({
                      title: l('pages.rc.jar.delete'),
                      content: l('pages.rc.jar.deleteConfirm'),
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
                      title: l('pages.rc.jar.enable'),
                      content: l('pages.rc.jar.enableConfirm'),
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
                      title: l('pages.rc.jar.disable'),
                      content: l('pages.rc.jar.disableConfirm'),
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
      <JarForm
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
      {formValues && Object.keys(formValues).length ? (
        <JarForm
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
          <ProDescriptions<JarTableListItem>
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

export default JarTableList;
