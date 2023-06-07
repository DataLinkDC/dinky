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
import {Form, Radio} from 'antd';
import {l} from '@/utils/intl';
import {ProForm, ProFormDigit, ProFormSwitch, ProFormText, ProFormTextArea} from '@ant-design/pro-components';
import {SWITCH_OPTIONS} from '@/services/constants';


const Email = (props: any) => {
  const {values} = props;

  return <>
    <>
      {/* base columns */}
      <ProForm.Group>
        <ProFormText
          width="lg"
          name="serverHost"
          label={l('rc.ai.serverHost')}
          rules={[{required: true, message: l('rc.ai.serverHostPleaseHolder')}]}
          placeholder={l('rc.ai.serverHostPleaseHolder')}
        />

        <ProFormDigit
          width="md"
          name="serverPort"
          label={l('rc.ai.serverPort')}
          rules={[{required: true, message: l('rc.ai.serverPortPleaseHolder')}]}
          placeholder={l('rc.ai.serverPortPleaseHolder')}
        />
        <ProFormTextArea
          width="xs"
          name="sender"
          label={l('rc.ai.sender')}
          rules={[{required: true, message: l('rc.ai.senderPleaseHolder')}]}
          placeholder={l('rc.ai.senderPleaseHolder')}
        />

        <ProFormTextArea
          width="md"
          name="receivers"
          label={l('rc.ai.receivers')}
          rules={[{required: true, message: l('rc.ai.receiversPleaseHolder')}]}
          placeholder={l('rc.ai.receiversPleaseHolder')}
        />
        <ProFormTextArea
          width="md"
          name="receiverCcs"
          label={l('rc.ai.receiverCcs')}
          placeholder={l('rc.ai.receiverCcsPleaseHolder')}
        />


      </ProForm.Group>

      {/* switch */}
      <ProForm.Group>
        <ProFormSwitch
          width="xs"
          name="enableSmtpAuth"
          label={l('rc.ai.enableSmtpAuth')}
          {...SWITCH_OPTIONS()}
        />
        <ProFormSwitch
          width="xs"
          name="starttlsEnable"
          label={l('rc.ai.starttlsEnable')}
          {...SWITCH_OPTIONS()}
        />
        <ProFormSwitch
          width="xs"
          name="sslEnable"
          label={l('rc.ai.sslEnable')}
          {...SWITCH_OPTIONS()}
        />
        <ProFormSwitch
          width="xs"
          name="enabled"
          label={l('global.table.isEnable')}
          {...SWITCH_OPTIONS()}
        />

        {/* msgtype */}
        <Form.Item
          name="msgtype"
          label={l('rc.ai.msgtype')}
          rules={[{required: true, message: l('rc.ai.msgtypePleaseHolder')}]}
        >
          <Radio.Group>
            <Radio value="text">{l('rc.ai.text')}</Radio>
            <Radio value="table">{l('rc.ai.table')}</Radio>
            <Radio value="attachment">{l('rc.ai.attachment')}</Radio>
            <Radio value="table attachment">{l('rc.ai.tableAttachment')}</Radio>
          </Radio.Group>
        </Form.Item>
      </ProForm.Group>

      {/* proxy */}
      <ProForm.Group>
        {values.enableSmtpAuth &&
          <>
            <ProFormText
              name="User"
              width={'md'}
              label={l('rc.ai.emailUser')}
              rules={[{required: true, message: l('rc.ai.emailUserPleaseHolder')}]}
              allowClear placeholder={l('rc.ai.emailUserPleaseHolder')}
            />
            <ProFormText.Password
              name="Password"
              width={'lg'}
              label={l('rc.ai.emailPassword')}
              rules={[{required: true, message: l('rc.ai.emailPasswordPleaseHolder')}]}
              allowClear placeholder={l('rc.ai.emailPasswordPleaseHolder')}
            />
          </>}
      </ProForm.Group>

      <ProForm.Group>
        {/* if choose attachment || table attachment , this input is render */}
        {(values.msgtype === 'attachment' || values.msgtype === 'table attachment') &&
          <ProFormText
            name="xls.file.path"
            width={'md'}
            label={l('rc.ai.xls.file.path')}
            placeholder={l('rc.ai.xls.file.pathPleaseHolder')} allowClear
          />}

        {/* ssl  */}
        {(values.sslEnable) &&
          <ProFormText
            name="smtpSslTrust"
            width={'lg'}
            label={l('rc.ai.smtpSslTrust')}
            rules={[{required: true, message: l('rc.ai.smtpSslTrustPleaseHolder')}]}
            placeholder={l('rc.ai.smtpSslTrustPleaseHolder')}
          />
        }
      </ProForm.Group>
    </>
  </>;
};

export default Email;
