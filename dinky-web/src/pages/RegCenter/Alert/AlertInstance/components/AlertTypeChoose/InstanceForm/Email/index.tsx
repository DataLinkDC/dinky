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

import { FormSingleColumnList } from '@/components/FormSingleColumnList';
import { SWITCH_OPTIONS } from '@/services/constants';
import { Alert } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import {
  ProCard,
  ProForm,
  ProFormDigit,
  ProFormSwitch,
  ProFormText
} from '@ant-design/pro-components';
import { FormInstance } from 'antd/es/form/hooks/useForm';
import { Values } from 'async-validator';
import { Rule } from 'rc-field-form/lib/interface';

type EmailProps = {
  values: Partial<Alert.AlertInstance>;
  form: FormInstance<Values>;
};
const Email = (props: EmailProps) => {
  const { values, form } = props;
  const params = values.params as Alert.AlertInstanceParamsEmail;

  const validateEmailRules = (
    namePath: string | string[],
    nullTips: string,
    repeatTips: string
  ) => [
    {
      required: true,
      validator: async (rule: Rule, value: string) => {
        if (!value) {
          return Promise.reject(nullTips);
        }
        const fieldValue = form.getFieldValue(namePath);
        const filterField = fieldValue.filter((item: string) => item === value);
        if (filterField.length > 1) {
          return Promise.reject(repeatTips);
        }
      }
    },
    {
      pattern: /^([A-Za-z0-9_\-.])+@([A-Za-z0-9_\-.])+\.([A-Za-z]{2,4})$/,
      message: l('rc.ai.emailPleaseHolderFormat')
    }
  ];

  return (
    <>
      <>
        {/* base columns */}
        <ProForm.Group>
          <ProFormText
            width='sm'
            name={['params', 'serverHost']}
            label={l('rc.ai.serverHost')}
            rules={[{ required: true, message: l('rc.ai.serverHostPleaseHolder') }]}
            placeholder={l('rc.ai.serverHostPleaseHolder')}
          />

          <ProFormDigit
            width='sm'
            name={['params', 'serverPort']}
            label={l('rc.ai.serverPort')}
            rules={[{ required: true, message: l('rc.ai.serverPortPleaseHolder') }]}
            placeholder={l('rc.ai.serverPortPleaseHolder')}
          />
          <ProFormText
            width='md'
            name={['params', 'sender']}
            label={l('rc.ai.sender')}
            rules={[{ required: true, message: l('rc.ai.senderPleaseHolder') }]}
            placeholder={l('rc.ai.senderPleaseHolder')}
          />
        </ProForm.Group>

        <ProCard ghost size={'small'} wrap={false} split={'vertical'}>
          <ProCard ghost>
            <FormSingleColumnList
              form={form}
              namePath={['params', 'receivers']}
              rules={validateEmailRules(
                ['params', 'receivers'],
                l('rc.ai.receiversPleaseHolder'),
                l('rc.ai.receiversRepeat')
              )}
              inputPlaceholder={l('rc.ai.receiversPleaseHolder')}
              title={l('rc.ai.receiversMax', '', { max: 5 })}
              max={5}
              min={1}
              plain={true}
            />
          </ProCard>
          <ProCard.Divider type={'vertical'} />
          <ProCard ghost>
            <FormSingleColumnList
              form={form}
              namePath={['params', 'receiverCcs']}
              rules={validateEmailRules(
                ['params', 'receiverCcs'],
                l('rc.ai.receiverCcsPleaseHolder'),
                l('rc.ai.receiverCcsRepeat')
              )}
              inputPlaceholder={l('rc.ai.receiverCcsPleaseHolder')}
              title={l('rc.ai.receiverCcsMax', '', { max: 10 })}
              max={10}
              min={0}
              plain={true}
            />
          </ProCard>
        </ProCard>

        {/* switch */}
        <ProForm.Group>
          <ProFormSwitch
            width='xs'
            name={['params', 'enableSmtpAuth']}
            label={l('rc.ai.enableSmtpAuth')}
            {...SWITCH_OPTIONS()}
          />
          <ProFormSwitch
            width='xs'
            name={['params', 'starttlsEnable']}
            label={l('rc.ai.starttlsEnable')}
            {...SWITCH_OPTIONS()}
          />
          <ProFormSwitch
            width='xs'
            name={['params', 'sslEnable']}
            label={l('rc.ai.sslEnable')}
            {...SWITCH_OPTIONS()}
          />
          <ProFormSwitch
            width='xs'
            name='enabled'
            label={l('global.table.isEnable')}
            {...SWITCH_OPTIONS()}
          />
        </ProForm.Group>

        {/* proxy */}
        <ProForm.Group>
          {params.enableSmtpAuth && (
            <>
              <ProFormText
                name={['params', 'user']}
                width={'md'}
                label={l('rc.ai.emailUser')}
                rules={[{ required: true, message: l('rc.ai.emailUserPleaseHolder') }]}
                allowClear
                placeholder={l('rc.ai.emailUserPleaseHolder')}
              />
              <ProFormText.Password
                name={['params', 'password']}
                width={'lg'}
                label={l('rc.ai.emailPassword')}
                rules={[
                  {
                    required: true,
                    message: l('rc.ai.emailPasswordPleaseHolder')
                  }
                ]}
                allowClear
                placeholder={l('rc.ai.emailPasswordPleaseHolder')}
              />
            </>
          )}
        </ProForm.Group>

        <ProForm.Group>
          {/* ssl  */}
          {params.sslEnable && (
            <ProFormText
              name={['params', 'smtpSslTrust']}
              width={'lg'}
              label={l('rc.ai.smtpSslTrust')}
              rules={[
                {
                  required: true,
                  message: l('rc.ai.smtpSslTrustPleaseHolder')
                }
              ]}
              placeholder={l('rc.ai.smtpSslTrustPleaseHolder')}
            />
          )}
        </ProForm.Group>
      </>
    </>
  );
};

export default Email;
