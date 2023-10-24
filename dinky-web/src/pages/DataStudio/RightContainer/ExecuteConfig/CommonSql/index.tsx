/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import { TagAlignLeft } from '@/components/StyledComponents';
import { getCurrentData } from '@/pages/DataStudio/function';
import { StateType, STUDIO_MODEL } from '@/pages/DataStudio/model';
import { DataSources } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import { ProForm, ProFormDigit, ProFormGroup, ProFormSelect } from '@ant-design/pro-components';
import { Tag } from 'antd';
import { useForm } from 'antd/es/form/Form';
import React from 'react';
import { connect } from 'umi';

const ExecuteConfigCommonSql = (props: any) => {
  const {
    dispatch,
    tabs: { panes, activeKey },
    databaseData
  } = props;
  const [form] = useForm();
  const current = getCurrentData(panes, activeKey);
  const data: Record<number, React.ReactNode> = {};
  const databaseDataList = databaseData as DataSources.DataSource[];
  databaseDataList
    .filter((x) => x.type.toLowerCase() === current?.dialect.toLowerCase())
    .forEach((item: DataSources.DataSource) => {
      data[item.id] = (
        <TagAlignLeft>
          <Tag key={item.id} color={item.enabled ? 'processing' : 'error'}>
            {item.type}
          </Tag>
          {item.name}
        </TagAlignLeft>
      );
    });

  form.setFieldsValue({ ...current, databaseId: String(current?.databaseId ?? '') });
  const onValuesChange = (change: any, all: any) => {
    for (let i = 0; i < panes.length; i++) {
      if (panes[i].key === activeKey) {
        for (const key in change) {
          if (key === 'databaseId') {
            panes[i].params.taskData[key] = Number(all[key]);
            continue;
          }
          panes[i].params.taskData[key] = all[key];
        }
        break;
      }
    }
    dispatch({
      type: STUDIO_MODEL.saveTabs,
      payload: { ...props.tabs }
    });
  };

  return (
    <>
      <ProForm
        initialValues={{
          maxRowNum: 100
        }}
        style={{ padding: '10px' }}
        form={form}
        submitter={false}
        layout='vertical'
        onValuesChange={onValuesChange}
      >
        <ProFormGroup>
          <ProFormSelect
            width={'lg'}
            name={'databaseId'}
            label={l('pages.datastudio.label.execConfig.selectDatabase')}
            valueEnum={data}
            initialValue={1}
            placeholder='Please select a country'
            rules={[{ required: true, message: 'Please select your country!' }]}
          />
        </ProFormGroup>
        <ProFormGroup>
          <ProFormDigit
            width={'xs'}
            label={l('pages.datastudio.label.execConfig.maxrow')}
            name='maxRowNum'
            tooltip={l('pages.datastudio.label.execConfig.maxrow.tip')}
            min={1}
            max={9999}
          />
        </ProFormGroup>
      </ProForm>
    </>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  tabs: Studio.tabs,
  databaseData: Studio.database.dbData
}))(ExecuteConfigCommonSql);
