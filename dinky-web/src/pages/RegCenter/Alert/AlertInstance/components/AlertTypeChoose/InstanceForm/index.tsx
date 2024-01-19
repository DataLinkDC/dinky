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

import Email from '@/pages/RegCenter/Alert/AlertInstance/components/AlertTypeChoose/InstanceForm/Email';
import FeiShu from '@/pages/RegCenter/Alert/AlertInstance/components/AlertTypeChoose/InstanceForm/FeiShu';
import Http from '@/pages/RegCenter/Alert/AlertInstance/components/AlertTypeChoose/InstanceForm/Http';
import Sms from '@/pages/RegCenter/Alert/AlertInstance/components/AlertTypeChoose/InstanceForm/Sms';
import WeChat from '@/pages/RegCenter/Alert/AlertInstance/components/AlertTypeChoose/InstanceForm/WeChat';
import { ALERT_TYPE_LIST_OPTIONS } from '@/pages/RegCenter/Alert/AlertInstance/constans';
import { Alert, ALERT_TYPE } from '@/types/RegCenter/data.d';
import { l } from '@/utils/intl';
import { ProForm, ProFormSelect, ProFormText } from '@ant-design/pro-components';
import { FormInstance } from 'antd/es/form/hooks/useForm';
import { Values } from 'async-validator';
import React, { useState } from 'react';
import DingTalk from './DingTalk';

type InstanceFormProps = {
  values: Partial<Alert.AlertInstance>;
  form: FormInstance<Values>;
};

const InstanceForm: React.FC<InstanceFormProps> = (props) => {
  const { values, form } = props;

  const [alertType, setAlertType] = useState<string>(values.type ?? ALERT_TYPE.DINGTALK);

  const [formValues, setFormValues] = useState<Partial<Alert.AlertInstance>>(values);

  const renderPreForm = () => {
    return (
      <>
        <ProFormText
          width='lg'
          name='name'
          label={l('rc.ai.name')}
          rules={[{ required: true, message: l('rc.ai.namePleaseHolder') }]}
          placeholder={l('rc.ai.namePleaseHolder')}
        />
        <ProFormSelect
          width='md'
          name='type'
          allowClear={false}
          label={l('rc.ai.type')}
          rules={[{ required: true, message: l('rc.ai.choosetype') }]}
          placeholder={l('rc.ai.choosetype')}
          options={ALERT_TYPE_LIST_OPTIONS}
          onChange={(value: string) => setAlertType(value)}
          initialValue={values.type ?? ALERT_TYPE.DINGTALK}
        />
      </>
    );
  };

  const renderFormByType = (value: Partial<Alert.AlertInstance>, platform: string) => {
    switch (platform) {
      case ALERT_TYPE.DINGTALK:
        return <DingTalk values={value} form={form} />;
      case ALERT_TYPE.FEISHU:
        return <FeiShu values={value} form={form} />;
      case ALERT_TYPE.WECHAT:
        return <WeChat values={value} form={form} />;
      case ALERT_TYPE.EMAIL:
        return <Email values={value} form={form} />;
      case ALERT_TYPE.SMS:
        return <Sms values={value} form={form} />;
      case ALERT_TYPE.HTTP:
        return <Http values={value} form={form} />;
      default:
        return <></>;
    }
  };

  return (
    <>
      <ProForm
        form={form}
        onValuesChange={(changedValues, allValues) => setFormValues(allValues)}
        submitter={false}
      >
        <ProForm.Group>
          {renderPreForm()}
          {renderFormByType(formValues, alertType)}
        </ProForm.Group>
      </ProForm>
    </>
  );
};

export default InstanceForm;
