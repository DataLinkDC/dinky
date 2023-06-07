/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {Alert, ALERT_TYPE} from '@/types/RegCenter/data.d';
import DingTalk from './DingTalk';
import {buildJSONData, getJSONData} from '@/pages/RegCenter/Alert/AlertInstance/function';
import {ProForm, ProFormSelect, ProFormText} from '@ant-design/pro-components';
import React, {useState} from 'react';
import {FormInstance} from 'antd/es/form/hooks/useForm';
import {Values} from 'async-validator';
import {MODAL_FORM_OPTIONS} from '@/services/constants';
import {l} from '@/utils/intl';
import {ALERT_TYPE_LIST_OPTIONS} from '@/pages/RegCenter/Alert/AlertInstance/constans';
import FeiShu from '@/pages/RegCenter/Alert/AlertInstance/components/AlertTypeChoose/InstanceForm/FeiShu';
import WeChat from '@/pages/RegCenter/Alert/AlertInstance/components/AlertTypeChoose/InstanceForm/WeChat';
import Email from '@/pages/RegCenter/Alert/AlertInstance/components/AlertTypeChoose/InstanceForm/Email';
import Sms from '@/pages/RegCenter/Alert/AlertInstance/components/AlertTypeChoose/InstanceForm/Sms';

type InstanceFormProps = {
  values: Partial<Alert.AlertInstance>;
  form: FormInstance<Values>
}

const InstanceForm: React.FC<InstanceFormProps> = (props) => {
  const {values, form} = props;

  const [alertType, setAlertType] = useState<string>(values.type || ALERT_TYPE.DINGTALK);
  const [data, setData] = useState<any>(
    values ? getJSONData(values) : {}
  );// 保存表单数据

  const renderPreForm = () => {
    return <>
      <ProFormText
        width="lg"
        name="name"
        label={l('rc.ai.name')}
        rules={[{required: true, message: l('rc.ai.namePleaseHolder')}]}
        placeholder={l('rc.ai.namePleaseHolder')}
      />
      <ProFormSelect
        width="md"
        name="type"
        label={l('rc.ai.type')}
        rules={[{required: true, message: l('rc.ai.choosetype')}]}
        placeholder={l('rc.ai.choosetype')}
        options={ALERT_TYPE_LIST_OPTIONS}
        initialValue={alertType}
      />

    </>;
  };

  const renderFormByType = () => {
    switch (alertType) {
      case ALERT_TYPE.DINGTALK:
        return <DingTalk values={getJSONData(data)}/>;
      case ALERT_TYPE.FEISHU:
        return <FeiShu values={getJSONData(data)}/>;
      case ALERT_TYPE.WECHAT:
        return <WeChat values={getJSONData(data)}/>;
      case ALERT_TYPE.EMAIL:
        return <Email values={getJSONData(data)}/>;
      case ALERT_TYPE.SMS:
        return <Sms values={getJSONData(data)}/>;
    }
  };

  const handleValuesChange = async (changedValues: any) => {
    const fields = await form.getFieldsValue();
    setData(fields);
    if (changedValues.type) {
      setAlertType(changedValues.type);
    }
  };


  return <>
    <ProForm
      {...MODAL_FORM_OPTIONS}
      form={form}
      submitter={false}
      initialValues={getJSONData(values)}
      onValuesChange={handleValuesChange}
    >
      <ProForm.Group>
        {renderPreForm()}
        {renderFormByType()}
      </ProForm.Group>
    </ProForm>

  </>;
};

export default InstanceForm;
