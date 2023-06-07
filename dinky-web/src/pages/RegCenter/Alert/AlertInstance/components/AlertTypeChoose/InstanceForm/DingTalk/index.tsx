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


import React from 'react';
import {Radio} from 'antd';
import {l} from '@/utils/intl';
import {
  ProForm,
  ProFormDigit,
  ProFormRadio,
  ProFormSwitch,
  ProFormText,
  ProFormTextArea
} from '@ant-design/pro-components';
import {SWITCH_OPTIONS} from '@/services/constants';


const DingTalk = (props: any) => {
  const {values} = props;

  return <>
    {/* base columns */}
    <ProForm.Group>
      <ProFormTextArea
        width="lg"
        allowClear
        name="webhook"
        label={l('rc.ai.webhook')}
        rules={[{required: true, message: l('rc.ai.webhookPleaseHolder')}]}
        placeholder={l('rc.ai.webhookPleaseHolder')}
      />
      <ProFormTextArea
        width="md"
        name="keyword"
        label={l('rc.ai.keyword')}
        placeholder={l('rc.ai.keywordPleaseHolder')}
      />
      <ProFormText.Password
        width="lg"
        allowClear
        name="secret"
        label={l('rc.ai.secret')}
        placeholder={l('rc.ai.secretPleaseHolder')}
      />

      {/* advanced columns */}
      <ProFormRadio.Group
        name="msgtype"
        width={'xs'}
        label={l('rc.ai.msgtype')}
        rules={[{required: true, message: l('rc.ai.msgtypePleaseHolder')}]}
      >
        <Radio.Group>
          <Radio value="markdown">{l('rc.ai.markdown')}</Radio>
          <Radio value="text">{l('rc.ai.text')}</Radio>
        </Radio.Group>
      </ProFormRadio.Group>
    </ProForm.Group>

    <ProForm.Group>
      <ProFormSwitch
        width="xs"
        name="isEnableProxy"
        label={l('rc.ai.isEnableProxy')}
        {...SWITCH_OPTIONS()}
      />
      <ProFormSwitch
        width="xs"
        name="isAtAll"
        label={l('rc.ai.isAtAll')}
        {...SWITCH_OPTIONS()}
      />
      <ProFormSwitch
        width="xs"
        name="enabled"
        label={l('global.table.isEnable')}
        {...SWITCH_OPTIONS()}
      />
      {/* if not Enable At All this group do render */}
      {!values.isAtAll &&
        <>
          <ProFormTextArea
            width="xl"
            name="atMobiles"
            label={l('rc.ai.atMobiles')}
            rules={[{required: true, message: l('rc.ai.atMobilesPleaseHolder')}]}
            placeholder={l('rc.ai.atMobilesPleaseHolder')}
          />
        </>
      }
    </ProForm.Group>

    {/* if Enable Proxy this group do render */}
    <ProForm.Group>
      {values.isEnableProxy && <>
        <ProFormText
          width="md"
          name="proxy"
          label={l('rc.ai.proxy')}
          rules={[{required: true, message: l('rc.ai.proxyPleaseHolder')}]}
          placeholder={l('rc.ai.proxyPleaseHolder')}
        />

        <ProFormDigit
          width="lg"
          name="port"
          label={l('rc.ai.port')}
          rules={[{required: true, message: l('rc.ai.portPleaseHolder')}]}
          placeholder={l('rc.ai.portPleaseHolder')}
        />
        <ProFormText
          width="md"
          name="user"
          label={l('rc.ai.user')}
          rules={[{required: true, message: l('rc.ai.userPleaseHolder')}]}
          placeholder={l('rc.ai.userPleaseHolder')}
        />
        <ProFormText.Password
          width={'lg'}
          name="password"
          label={l('rc.ai.password')}
          rules={[{required: true, message: l('rc.ai.passwordPleaseHolder')}]}
          placeholder={l('rc.ai.passwordPleaseHolder')}
        />
      </>
      }
    </ProForm.Group>
  </>;
};

export default DingTalk;
