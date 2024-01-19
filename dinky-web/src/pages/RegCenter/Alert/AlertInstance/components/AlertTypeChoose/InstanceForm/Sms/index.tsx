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
  matchPlatFormRequestUrl,
  matchPlatVersion,
  renderAlibabaSmsForm,
  renderTencentSmsForm
} from '@/pages/RegCenter/Alert/AlertInstance/components/AlertTypeChoose/InstanceForm/Sms/function';
import { MANU_FRACTURES, SMS_TYPE } from '@/pages/RegCenter/Alert/AlertInstance/constans';
import { SWITCH_OPTIONS } from '@/services/constants';
import { Alert } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';

import { useCallback, useEffect, useState } from 'react';

import { FormSingleColumnList } from '@/components/FormSingleColumnList';
import { ProForm, ProFormSelect, ProFormSwitch } from '@ant-design/pro-components';
import { randomStr } from '@antfu/utils';
import { FormInstance } from 'antd/es/form/hooks/useForm';
import { Values } from 'async-validator';
import { Rule } from 'rc-field-form/lib/interface';

type SmsProps = {
  values: Partial<Alert.AlertInstance>;
  form: FormInstance<Values>;
};
const Sms = (props: SmsProps) => {
  const { values, form } = props;

  const params = values.params as Alert.AlertInstanceParamsSms;

  const [suppliers, setSuppliers] = useState<string>(params.suppliers ?? SMS_TYPE.ALIBABA);

  useEffect(() => {
    if (suppliers) {
      // 厂商改变时 重新设置requestUrl, version, configId 等一些参数  | if suppliers change, reset some params like requestUrl, version, configId
      form.setFieldsValue({
        params: {
          ...params,
          suppliers: suppliers,
          requestUrl: matchPlatFormRequestUrl(suppliers),
          configId: randomStr(32),
          version: matchPlatVersion(suppliers)
        }
      });
    }
  }, [suppliers]);

  /**
   * render form by sms type
   */
  const renderFormBySmsType = useCallback(() => {
    switch (suppliers) {
      case SMS_TYPE.ALIBABA:
        return renderAlibabaSmsForm(suppliers);
      case SMS_TYPE.TENCENT:
        return renderTencentSmsForm(suppliers);
      // case SMS_TYPE.HUAWEI:
      //   return renderHuaWeiSmsForm();
      // case SMS_TYPE.YUNPIAN:
      //   return renderYunpianSmsForm();
      //
      // case SMS_TYPE.UNISMS:
      //   return renderUniSmsForm();
      // case SMS_TYPE.JDCLOUD:
      //   return renderJDSmsForm();
      // case SMS_TYPE.CLOOPEN:
      //   return renderCloopenSmsForm();
      // case SMS_TYPE.EMAY:
      //   return renderEmaySmsForm();
      // case SMS_TYPE.CTYUN:
      //   return renderCtyunForm();
      default:
        return <></>;
    }
  }, [suppliers]);

  const validateSmsPhoneRules = [
    {
      required: true,
      validator: async (rule: Rule, value: string) => {
        if (!value) {
          return Promise.reject(l('rc.ai.atMobilesPleaseHolder'));
        }
        const fieldValue = form.getFieldValue(['params', 'phoneNumbers']);
        const filterField = fieldValue.filter((item: string) => item === value);
        if (filterField.length > 1) {
          return Promise.reject(l('rc.ai.atMobilesRepeat'));
        }
      }
    },
    {
      pattern: /^1[3456789]\d{9}$/,
      message: l('rc.ai.atMobilesFormat')
    }
  ];

  /**
   * render
   */
  return (
    <>
      <ProForm.Group>
        <ProFormSelect
          name={['params', 'suppliers']}
          label={l('rc.ai.suppliers')}
          width={'md'}
          options={MANU_FRACTURES}
          initialValue={suppliers}
          rules={[{ required: true, message: l('rc.ai.suppliersPleaseHolder') }]}
          onChange={(value: string) => setSuppliers(value)}
          placeholder={l('rc.ai.suppliersPleaseHolder')}
        />
        <ProFormSwitch name='enabled' label={l('global.table.isEnable')} {...SWITCH_OPTIONS()} />
      </ProForm.Group>
      <ProForm.Group>{renderFormBySmsType()}</ProForm.Group>

      <FormSingleColumnList
        form={form}
        namePath={['params', 'phoneNumbers']}
        rules={validateSmsPhoneRules}
        inputPlaceholder={l('rc.ai.atMobilesPleaseHolder')}
        title={l('rc.ai.atMobilesMax', '', { max: 10 })}
        max={10}
        min={1}
        plain={true}
        phonePrefix={'+86'}
      />
    </>
  );
};

export default Sms;
