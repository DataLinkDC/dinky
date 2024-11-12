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

import { getCurrentData } from '@/pages/DataStudio/function';
import { StateType, STUDIO_MODEL } from '@/pages/DataStudio/model';
import { SWITCH_OPTIONS } from '@/services/constants';
import { l } from '@/utils/intl';
import { InfoCircleOutlined } from '@ant-design/icons';
import { ProForm, ProFormDigit, ProFormGroup, ProFormSwitch } from '@ant-design/pro-components';
import { useForm } from 'antd/es/form/Form';
import { connect } from 'umi';

const ExecuteConfigFlinkSql = (props: any) => {
  const {
    dispatch,
    tabs: { panes, activeKey }
  } = props;
  const [form] = useForm();
  const current = getCurrentData(panes, activeKey);

  form.setFieldsValue(current);
  const onValuesChange = (change: any, all: any) => {
    for (let i = 0; i < panes.length; i++) {
      if (panes[i].key === activeKey) {
        for (const key in change) {
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
          <ProFormSwitch
            label={l('pages.datastudio.label.execConfig.changelog')}
            name='useChangeLog'
            tooltip={{
              title: l('pages.datastudio.label.execConfig.changelog.tip'),
              icon: <InfoCircleOutlined />
            }}
            {...SWITCH_OPTIONS()}
          />
          <ProFormSwitch
            label={l('pages.datastudio.label.execConfig.autostop')}
            name='useAutoCancel'
            tooltip={{
              title: l('pages.datastudio.label.execConfig.autostop.tip'),
              icon: <InfoCircleOutlined />
            }}
            {...SWITCH_OPTIONS()}
          />
          <ProFormSwitch
            label={l('pages.datastudio.label.execConfig.mocksink')}
            name= 'mockSinkFunction'
            tooltip={{
              title: l('pages.datastudio.label.execConfig.mocksink.tip'),
              icon: <InfoCircleOutlined />
            }}
            {...SWITCH_OPTIONS()}
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
  tabs: Studio.tabs
}))(ExecuteConfigFlinkSql);
