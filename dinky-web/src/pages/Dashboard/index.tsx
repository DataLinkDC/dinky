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
import { l } from '@/utils/intl';
import { history } from '@umijs/max';
import { PermissionConstants } from '@/types/Public/constants';
import { Authorized } from '@/hooks/useAccess';
import { Layout } from 'react-grid-layout';
import DashboardLayout from '@/pages/Dashboard/DashboardLayout';
import MetricsFilter from '@/components/Flink/MetricsFilter/MetricsFilter';

const echartsThemeOptions = EchartsTheme.map((x) => {
  return { label: l(`dashboard.theme.${x}`), value: x };
});
export default () => {
  const { data, refresh, loading } = useHookRequest<any, any>(getDataList, { defaultParams: [] });

  const [activeKey, setActiveKey] = useState<React.Key | undefined>('tab1');

  const [openDetailPage, setOpenDetailPage] = useState(false);
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

  const handleDelete = async (id: number) => {
    Modal.confirm({
      title: l('dashboard.delete'),
      content: l('dashboard.deleteConfirm'),
      okText: l('button.delete'),
      cancelText: l('button.cancel'),
      async onOk() {
        await deleteData(id);
        await refresh();
      }
    });
  };

  return (
    <>
      <ProList<any>
        loading={loading}
        rowKey='name'
        actionRef={action}
        dataSource={data}
        bordered
        editable={{
          onSave: async (_, row) => {
            await addOrUpdate(row);
            await refresh();
          }
        }}
        metas={{
          title: {
            dataIndex: 'name',
            title: l('dashboard.name'),
            key: 'title',
            fieldProps: {
              width: '10%'
            },
            render: (text, row) => {
              return (
                <Link to={`/dashboard/dashboard-layout?layoutId=${row.id}`}>
                  {l('dashboard.name')}: {text}
                </Link>
              );
            }
          },
          description: {
            dataIndex: 'remark',
            title: l('dashboard.remark'),
            key: 'desc',
            fieldProps: {
              width: '10%'
            }
          },
          content: {
            dataIndex: 'chartTheme',
            valueType: 'select',
            fieldProps: {
              width: '10%',
              showSearch: true,
              placement: 'bottomRight',
              options: echartsThemeOptions
            },
            render: (text) => (
              <>
                {' '}
                {l('dashboard.chartTheme')}: {text}
              </>
            )
          },
          actions: {
            render: (text, row) => [
              <Authorized key={`added_auth`} path={PermissionConstants.DASHBOARD_VIEW}>
                <a
                  target='_blank'
                  rel='noopener noreferrer'
                  key='link'
                  onClick={() => {
                    history.push(`/dashboard/dashboard-layout?layoutId=${row.id}`);
                    setOpenDetailPage(true);
                  }}
                >
                  {l('button.open')}
                </a>
              </Authorized>,
              <Authorized key={`added_auth`} path={PermissionConstants.DASHBOARD_EDIT}>
                <a
                  href={row.html_url}
                  target='_blank'
                  rel='noopener noreferrer'
                  key='link'
                  onClick={() => {
                    action.current?.startEditable(row.name);
                  }}
                >
                  {l('button.edit')}
                </a>
              </Authorized>,
              <Authorized key={`added_auth`} path={PermissionConstants.DASHBOARD_DELETE}>
                <a
                  target='_blank'
                  rel='noopener noreferrer'
                  key='delete'
                  onClick={async () => handleDelete(row.id)}
                >
                  {l('button.delete')}
                </a>
              </Authorized>
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
            <Authorized key={`added_auth`} path={PermissionConstants.DASHBOARD_ADD}>
              <Button type='primary' key='primary' onClick={() => setOpenCreate(true)}>
                {l('dashboard.create')}
              </Button>
            </Authorized>
          ]
        }}
      />
      <Modal
        open={openCreate}
        width={'30%'}
        onCancel={onCloseCreate}
        onClose={onCloseCreate}
        onOk={onAddOrUpdate}
      >
        <ProForm<DashboardData> params={{}} submitter={false} formRef={formRef}>
          <ProFormText
            name='name'
            label={l('dashboard.name')}
            tooltip={l('dashboard.name.maxLength')}
            placeholder={l('dashboard.namePlaceholder')}
            rules={[{ required: true, message: l('dashboard.namePlaceholder') }]}
          />
          <ProFormTextArea
            name='remark'
            label={l('dashboard.remark')}
            placeholder={l('dashboard.remarkPlaceholder')}
          />
          <ProFormSelect
            name='chartTheme'
            label={l('dashboard.chartTheme')}
            rules={[{ required: true, message: l('dashboard.selectChartTheme') }]}
            options={echartsThemeOptions}
          />
        </ProForm>
      </Modal>
      {openDetailPage && (
        <>
          <DashboardLayout />
        </>
      )}
    </>
  );
};
