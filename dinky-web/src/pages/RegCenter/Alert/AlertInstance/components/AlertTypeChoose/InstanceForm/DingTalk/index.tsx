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
  ProForm,
  ProFormDigit,
  ProFormSwitch,
  ProFormText,
  ProFormTextArea
} from '@ant-design/pro-components';
import { FormInstance } from 'antd/es/form/hooks/useForm';
import { Values } from 'async-validator';
import { Rule } from 'rc-field-form/lib/interface';

type DingTalkProps = {
  values: Partial<Alert.AlertInstance>;
  form: FormInstance<Values>;
};
const DingTalk = (props: DingTalkProps) => {
  const { values, form } = props;

  const params = values.params as Alert.AlertInstanceParamsDingTalk;

  const validateDingTalkRules = [
    {
      required: true,
      validator: async (rule: Rule, value: string) => {
        if (!value) {
          return Promise.reject(l('rc.ai.atMobiles'));
        }
        const fieldValue = form.getFieldValue(['params', 'atMobiles']);
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

  return (
    <>
      {/* base columns */}
      <ProForm.Group>
        <ProFormTextArea
          width='lg'
          allowClear
          name={['params', 'webhook']}
          label={l('rc.ai.webhook')}
          rules={[{ required: true, message: l('rc.ai.webhookPleaseHolder') }]}
          placeholder={l('rc.ai.webhookPleaseHolder')}
        />
        <ProFormTextArea
          width='md'
          name={['params', 'keyword']}
          label={l('rc.ai.keyword')}
          placeholder={l('rc.ai.keywordPleaseHolder')}
        />
        <ProFormText.Password
          width='lg'
          allowClear
          name={['params', 'secret']}
          label={l('rc.ai.secret')}
          placeholder={l('rc.ai.secretPleaseHolder')}
        />
      </ProForm.Group>

      <ProForm.Group>
        <ProFormSwitch
          width='xs'
          name={['params', 'isEnableProxy']}
          label={l('rc.ai.isEnableProxy')}
          {...SWITCH_OPTIONS()}
        />
        <ProFormSwitch
          width='xs'
          name={['params', 'isAtAll']}
          label={l('rc.ai.isAtAll')}
          {...SWITCH_OPTIONS()}
        />
        <ProFormSwitch
          width='xs'
          name='enabled'
          label={l('global.table.isEnable')}
          {...SWITCH_OPTIONS()}
        />
      </ProForm.Group>

      {/* if Enable Proxy this group do render */}
      <ProForm.Group>
        {params.isEnableProxy && (
          <>
            <ProFormText
              width='md'
              name={['params', 'proxy']}
              label={l('rc.ai.proxy')}
              rules={[{ required: true, message: l('rc.ai.proxyPleaseHolder') }]}
              placeholder={l('rc.ai.proxyPleaseHolder')}
            />

            <ProFormDigit
              width='lg'
              name={['params', 'port']}
              label={l('rc.ai.port')}
              rules={[{ required: true, message: l('rc.ai.portPleaseHolder') }]}
              placeholder={l('rc.ai.portPleaseHolder')}
            />
            <ProFormText
              width='md'
              name={['params', 'user']}
              label={l('rc.ai.user')}
              rules={[{ required: true, message: l('rc.ai.userPleaseHolder') }]}
              placeholder={l('rc.ai.userPleaseHolder')}
            />
            <ProFormText.Password
              width={'lg'}
              name={['params', 'password']}
              label={l('rc.ai.password')}
              rules={[{ required: true, message: l('rc.ai.passwordPleaseHolder') }]}
              placeholder={l('rc.ai.passwordPleaseHolder')}
            />
          </>
        )}
      </ProForm.Group>
      {/* if not Enable At All this group do render */}
      {!params.isAtAll && (
        <>
          <FormSingleColumnList
            form={form}
            namePath={['params', 'atMobiles']}
            rules={validateDingTalkRules}
            inputPlaceholder={l('rc.ai.atMobilesPleaseHolder')}
            title={l('rc.ai.atMobilesMax', '', { max: 10 })}
            max={10}
            min={1}
            plain={true}
            phonePrefix={'+86'}
          />
        </>
      )}
    </>
  );
};

export default DingTalk;
