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

import {
  ActionType,
  ProForm,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
  ProList
} from '@ant-design/pro-components';
import React, { useCallback, useRef, useState } from 'react';
import { DashboardData, EchartsTheme } from '@/pages/Dashboard/data';
import { Link } from '@umijs/max';
import { Button, Modal } from 'antd';
import { ProFormInstance } from '@ant-design/pro-form/lib';
import { addOrUpdate, deleteData, getDataList } from '@/pages/Dashboard/service';
import useHookRequest from '@/hooks/useHookRequest';
import { getMetricsLayout } from '@/pages/Metrics/service';

// const dataSource = [{
//   id: 1,
//   name: "test",
//   remark: "ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd",
//   chartTheme: "roma",
// }, {
//   id: 2,
//   name: "test2",
//   remark: "ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd",
//   chartTheme: "dark"
// }] as DashboardData[]

const echartsThemeOptions = EchartsTheme.map((x) => {
  return { label: x, value: x };
});
export default () => {
  const { data, refresh, loading } = useHookRequest<any, any>(getDataList, { defaultParams: [] });

  const [activeKey, setActiveKey] = useState<React.Key | undefined>('tab1');

  const [openCreate, setOpenCreate] = useState(false);
  const formRef = useRef<ProFormInstance>();

  const onCloseCreate = useCallback(() => {
    setOpenCreate(false);
  }, []);

  const onAddOrUpdate = async () => {
    await addOrUpdate(formRef.current?.getFieldsValue());
    await refresh();
    onCloseCreate();
  };

  const action = useRef<ActionType>();

  return (
    <>
      <ProList<any>
        loading={loading}
        rowKey='name'
        actionRef={action}
        dataSource={data}
        editable={{
          onSave: async (_, row) => {
            await addOrUpdate(row);
            await refresh();
          }
        }}
        metas={{
          title: {
            dataIndex: 'name'
          },
          description: {
            dataIndex: 'remark',
            key: 'desc'
          },
          content: {
            dataIndex: 'chartTheme',
            valueType: 'select',
            fieldProps: {
              showSearch: true,
              placement: 'bottomRight',
              options: echartsThemeOptions
            },
            render: (text) => <> charts Theme: {text}</>
          },
          actions: {
            render: (text, row) => [
              <Link to={`/dashboard/dashboard-layout/${row.id}`}>打开</Link>,
              <a
                href={row.html_url}
                target='_blank'
                rel='noopener noreferrer'
                key='link'
                onClick={() => {
                  action.current?.startEditable(row.name);
                }}
              >
                编辑
              </a>,
              <a
                target='_blank'
                rel='noopener noreferrer'
                key='view'
                onClick={async () => {
                  await deleteData(row.id);
                  await refresh();
                }}
              >
                删除
              </a>
            ]
          }
        }}
        toolbar={{
          menu: {
            activeKey,
            onChange(key) {
              setActiveKey(key);
            }
          },
          actions: [
            <Button type='primary' key='primary' onClick={() => setOpenCreate(true)}>
              Create new layout
            </Button>
          ]
        }}
      />
      <Modal
        open={openCreate}
        onCancel={onCloseCreate}
        onClose={onCloseCreate}
        onOk={onAddOrUpdate}
      >
        <ProForm<DashboardData> params={{}} submitter={false} formRef={formRef}>
          <ProFormText name='name' label='名称' tooltip='最长为 24 位' placeholder='请输入名称' />
          <ProFormTextArea name='remark' label='描述' placeholder='请输入描述' />
          <ProFormSelect name='chartTheme' label='chart主题' options={echartsThemeOptions} />
        </ProForm>
      </Modal>
    </>
  );
};
