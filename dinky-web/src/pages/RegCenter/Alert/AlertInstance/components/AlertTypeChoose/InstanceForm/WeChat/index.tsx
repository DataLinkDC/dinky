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
  ProFormRadio,
  ProFormSwitch,
  ProFormText,
  ProFormTextArea
} from '@ant-design/pro-components';
import { FormInstance } from 'antd/es/form/hooks/useForm';
import { Values } from 'async-validator';
import { Rule } from 'rc-field-form/lib/interface';
import { useState } from 'react';

type WeChatProps = {
  values: Partial<Alert.AlertInstance>;
  form: FormInstance<Values>;
};
const WeChat = (props: WeChatProps) => {
  const { values, form } = props;

  const params = values.params as Alert.AlertInstanceParamsWeChat;

  const [sendType, setSendType] = useState<string>(params.sendType ?? 'app');

  const validateRulesWeChat = [
    {
      required: true,
      validator: async (rule: Rule, value: string) => {
        if (!value) {
          return Promise.reject(l('rc.ai.atUsersPleaseHolder'));
        }
        const fieldValue = form.getFieldValue(['params', 'atUsers']);
        const filterField = fieldValue.filter((item: string) => item === value);
        if (filterField.length > 1) {
          return Promise.reject(l('rc.ai.atUsersRepeat'));
        }
      }
    }
  ];

  const validateRulesWeChatSendUrl = [
    {
      required: true,
      validator: async (rule: Rule, value: string) => {
        if (!value) {
          return Promise.reject(l('rc.ai.sendUrlPleaseHolder'));
        }
        if (value.endsWith('/')) {
          return Promise.reject(l('rc.ai.sendUrlValidate'));
        }
      }
    }
  ];

  /**
   * render
   */
  return (
    <>
      <ProForm.Group>
        <ProFormRadio.Group
          name={['params', 'sendType']}
          label={l('rc.ai.sendType')}
          initialValue={sendType ?? 'app'}
          fieldProps={{
            onChange: (e) => setSendType(e.target.value)
          }}
          options={[
            { label: l('rc.ai.sendType.app'), value: 'app' },
            { label: l('rc.ai.sendType.wechat'), value: 'wechat' }
          ]}
          rules={[{ required: true, message: l('rc.ai.sendTypePleaseHolder') }]}
        />

        <ProFormSwitch
          key={'enabled'}
          name='enabled'
          label={l('global.table.isEnable')}
          {...SWITCH_OPTIONS()}
        />

        <ProFormSwitch
          key={'isAtAll'}
          name={['params', 'isAtAll']}
          label={l('rc.ai.isAtAll')}
          {...SWITCH_OPTIONS()}
        />
      </ProForm.Group>

      <ProForm.Group>
        {sendType === 'wechat' ? (
          // if sendType is wechat
          <>
            <ProFormTextArea
              width='xl'
              allowClear
              name={['params', 'webhook']}
              label={l('rc.ai.webhook')}
              rules={[{ required: true, message: l('rc.ai.webhookPleaseHolder') }]}
              placeholder={l('rc.ai.webhookPleaseHolder')}
            />
            <ProFormTextArea
              width='sm'
              name={['params', 'keyword']}
              label={l('rc.ai.keyword')}
              placeholder={l('rc.ai.keywordPleaseHolder')}
            />
          </>
        ) : (
          sendType === 'app' && (
            // if sendType is app
            <>
              <ProFormText
                width='sm'
                name={['params', 'corpId']}
                label={l('rc.ai.corpId')}
                rules={[{ required: true, message: l('rc.ai.corpIdPleaseHolder') }]}
                placeholder={l('rc.ai.corpIdPleaseHolder')}
              />
              <ProFormText.Password
                width='xl'
                name={['params', 'secret']}
                label={l('rc.ai.secret')}
                rules={[{ required: true, message: l('rc.ai.secretPleaseHolder') }]}
                placeholder={l('rc.ai.secretPleaseHolder')}
              />
              <ProFormDigit
                width='sm'
                name={['params', 'agentId']}
                label={l('rc.ai.agentId')}
                rules={[{ required: true, message: l('rc.ai.agentIdPleaseHolder') }]}
                placeholder={l('rc.ai.agentIdPleaseHolder')}
              />
              <ProFormText
                width='xl'
                name={['params', 'sendUrl']}
                label={l('rc.ai.sendUrl')}
                tooltip={l('rc.ai.sendUrlTooltip')}
                initialValue={'https://qyapi.weixin.qq.com/cgi-bin'}
                rules={validateRulesWeChatSendUrl}
                placeholder={l('rc.ai.sendUrlPleaseHolder')}
              />
            </>
          )
        )}
      </ProForm.Group>
      {/* if not Enable At All this group do render */}
      {!params.isAtAll && (
        <>
          <FormSingleColumnList
            form={form}
            namePath={['params', 'atUsers']}
            rules={validateRulesWeChat}
            inputPlaceholder={l('rc.ai.atUsersPleaseHolder')}
            title={l('rc.ai.atUsersMax', '', { max: 10 })}
            max={10}
            min={1}
            plain={true}
          />
        </>
      )}
    </>
  );
};

export default WeChat;
