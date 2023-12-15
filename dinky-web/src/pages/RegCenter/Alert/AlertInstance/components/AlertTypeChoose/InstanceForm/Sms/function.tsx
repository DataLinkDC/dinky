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
  AliYunArea,
  TencentArea
} from '@/pages/RegCenter/Alert/AlertInstance/components/AlertTypeChoose/InstanceForm/Sms/constants';
import { SMS_TYPE } from '@/pages/RegCenter/Alert/AlertInstance/constans';
import { l } from '@/utils/intl';
import {
  ProForm,
  ProFormDigit,
  ProFormSelect,
  ProFormSwitch,
  ProFormTextArea
} from '@ant-design/pro-components';
import { ProFormText } from '@ant-design/pro-form';
import { randomStr } from '@antfu/utils';

/**
 * 匹配平台请求地址 | match platform request url
 * @param smsType
 */
export function matchPlatFormRequestUrl(smsType: string): string {
  switch (smsType) {
    case SMS_TYPE.ALIBABA:
      return 'dysmsapi.aliyuncs.com';
    case SMS_TYPE.TENCENT:
      return 'sms.tencentcloudapi.com';
    default:
      return '';
  }
}

/**
 * 匹配平台版本号 | match platform version
 * @param smsType
 */
export function matchPlatVersion(smsType: string): string {
  switch (smsType) {
    case SMS_TYPE.ALIBABA:
      return '2017-05-25';
    case SMS_TYPE.TENCENT:
      return '2021-01-11';
    default:
      return '';
  }
}

export const renderCommonSmsForm = (smsType: string) => {
  return (
    <>
      <ProFormText
        name={['params', 'accessKeyId']}
        label={l('rc.ai.accessKeyId')}
        width={'md'}
        rules={[{ required: true, message: l('rc.ai.accessKeyIdPleaseHolder') }]}
        placeholder={l('rc.ai.accessKeyIdPleaseHolder')}
      />
      <ProFormText.Password
        name={['params', 'accessKeySecret']}
        label={l('rc.ai.accessKeySecret')}
        width={'sm'}
        rules={[{ required: true, message: l('rc.ai.accessKeyIdPleaseHolder') }]}
        placeholder={l('rc.ai.accessKeyIdPleaseHolder')}
      />
      <ProFormText
        name={['params', 'signature']}
        label={l('rc.ai.signature')}
        width={'sm'}
        rules={[{ required: true, message: l('rc.ai.signaturePleaseHolder') }]}
        placeholder={l('rc.ai.signaturePleaseHolder')}
      />
      <ProFormText
        name={['params', 'templateId']}
        label={l('rc.ai.templateId')}
        width={'md'}
        rules={[{ required: true, message: l('rc.ai.templateIdPleaseHolder') }]}
        placeholder={l('rc.ai.templateIdPleaseHolder')}
      />
      <ProFormDigit
        name={['params', 'weight']}
        label={l('rc.ai.weight')}
        width={'xs'}
        initialValue={1}
        rules={[{ required: true, message: l('rc.ai.weightPleaseHolder') }]}
        placeholder={l('rc.ai.weightPleaseHolder')}
      />
      <ProFormDigit
        name={['params', 'retryInterval']}
        label={l('rc.ai.retryInterval')}
        width={'xs'}
        initialValue={5}
        rules={[{ required: true, message: l('rc.ai.retryInterval') }]}
        placeholder={l('rc.ai.retryInterval')}
      />
      <ProFormDigit
        name={['params', 'maxRetries']}
        label={l('rc.ai.maxRetries')}
        width={'xs'}
        initialValue={5}
        rules={[{ required: true, message: l('rc.ai.maxRetriesPleaseHolder') }]}
        placeholder={l('rc.ai.maxRetriesPleaseHolder')}
      />
      <ProFormText
        name={['params', 'configId']}
        label={l('rc.ai.configId')}
        width={'md'}
        disabled
        hidden
        initialValue={randomStr(32)}
        rules={[{ required: true, message: l('rc.ai.configIdPleaseHolder') }]}
        placeholder={l('rc.ai.configIdPleaseHolder')}
      />
    </>
  );
};

export const renderAlibabaSmsForm = (smsType: string) => {
  // let area = YunArea[0].value;
  return (
    <>
      {renderCommonSmsForm(smsType)}
      <ProFormText
        name={['params', 'requestUrl']}
        label={l('rc.ai.requestUrl')}
        width={'md'}
        rules={[{ required: true, message: l('rc.ai.requestUrlPleaseHolder') }]}
        placeholder={l('rc.ai.requestUrlPleaseHolder')}
      />
      <ProFormSelect
        name={['params', 'regionId']}
        label={l('rc.ai.regionId')}
        width={'sm'}
        options={AliYunArea}
        rules={[{ required: true, message: l('rc.ai.regionIdPleaseHolder') }]}
        placeholder={l('rc.ai.regionIdPleaseHolder')}
      />
      <ProFormText
        name={['params', 'templateName']}
        label={l('rc.ai.templateName')}
        width={'md'}
        initialValue={'content'}
        disabled
        hidden
        rules={[{ required: true, message: l('rc.ai.templateNamePleaseHolder') }]}
        placeholder={l('rc.ai.templateNamePleaseHolder')}
      />
      <ProFormText
        name={['params', 'action']}
        label={l('rc.ai.action')}
        width={'sm'}
        disabled
        hidden
        initialValue={'SendSms'}
        placeholder={l('rc.ai.actionPleaseHolder')}
      />
      <ProFormText
        name={['params', 'version']}
        label={l('rc.ai.version')}
        width={'sm'}
        disabled
        hidden
        initialValue={'2017-05-25'}
        placeholder={l('rc.ai.versionPleaseHolder')}
      />
    </>
  );
};

export const renderTencentSmsForm = (smsType: string) => {
  return (
    <>
      {renderCommonSmsForm(smsType)}
      <ProFormText
        name={['params', 'sdkAppId']}
        label={l('rc.ai.sdkAppId')}
        width={'sm'}
        rules={[{ required: true, message: l('rc.ai.sdkAppIdPleaseHolder') }]}
        placeholder={l('rc.ai.sdkAppIdPleaseHolder')}
      />
      <ProFormSelect
        name={['params', 'territory']}
        label={l('rc.ai.regionId')}
        width={'sm'}
        options={TencentArea}
        rules={[{ required: true, message: l('rc.ai.regionIdPleaseHolder') }]}
        placeholder={l('rc.ai.regionIdPleaseHolder')}
      />
      <ProFormDigit
        name={['params', 'connTimeout']}
        label={l('rc.ai.connTimeout')}
        width={'xs'}
        rules={[{ required: true, message: l('rc.ai.connTimeoutPleaseHolder') }]}
        placeholder={l('rc.ai.connTimeoutPleaseHolder')}
        initialValue={60}
      />
      <ProFormText
        name={['params', 'requestUrl']}
        label={l('rc.ai.requestUrl')}
        width={'sm'}
        rules={[{ required: true, message: l('rc.ai.requestUrlPleaseHolder') }]}
        placeholder={l('rc.ai.requestUrlPleaseHolder')}
      />
      <ProForm.Group>
        <ProFormText
          name={['params', 'action']}
          label={l('rc.ai.action')}
          width={'sm'}
          disabled
          hidden
          initialValue={'SendSms'}
          placeholder={l('rc.ai.actionPleaseHolder')}
        />

        <ProFormText
          name={['params', 'version']}
          label={l('rc.ai.version')}
          width={'sm'}
          disabled
          hidden
          initialValue={'2021-01-11'}
          placeholder={l('rc.ai.versionPleaseHolder')}
        />
      </ProForm.Group>
    </>
  );
};
export const renderHuaWeiSmsForm = () => {
  return (
    <>
      <ProFormText
        name='appKey'
        label={l('rc.ai.appKey')}
        width={'md'}
        rules={[{ required: true, message: l('rc.ai.appKeyPleaseHolder') }]}
        placeholder={l('rc.ai.appKeyPleaseHolder')}
      />
      <ProFormText.Password
        name='appSecret'
        label={l('rc.ai.appSecret')}
        width={'sm'}
        rules={[{ required: true, message: l('rc.ai.appSecretPleaseHolder') }]}
        placeholder={l('rc.ai.appSecretPleaseHolder')}
      />

      <ProFormText
        name='signature'
        label={l('rc.ai.signature')}
        width={'sm'}
        rules={[{ required: true, message: l('rc.ai.signaturePleaseHolder') }]}
        placeholder={l('rc.ai.signaturePleaseHolder')}
      />

      <ProFormText
        name='sender'
        label={l('rc.ai.senders')}
        width={'sm'}
        rules={[{ required: true, message: l('rc.ai.sendersPleaseHolder') }]}
        placeholder={l('rc.ai.sendersPleaseHolder')}
      />
      <ProFormText
        name='templateId'
        label={l('rc.ai.templateId')}
        width={'lg'}
        rules={[{ required: true, message: l('rc.ai.templateIdPleaseHolder') }]}
        placeholder={l('rc.ai.templateIdPleaseHolder')}
      />
      <ProFormTextArea
        name='url'
        label={l('rc.ai.url')}
        width={'md'}
        rules={[{ required: true, message: l('rc.ai.urlPleaseHolder') }]}
        placeholder={l('rc.ai.urlPleaseHolder')}
      />
      <ProFormTextArea
        name='statusCallBack'
        label={l('rc.ai.statusCallBack')}
        width={'md'}
        rules={[{ required: true, message: l('rc.ai.statusCallBackPleaseHolder') }]}
        placeholder={l('rc.ai.statusCallBackPleaseHolder')}
      />
    </>
  );
};

export const renderYunpianSmsForm = () => {
  return (
    <>
      <ProFormText
        name='apikey'
        label={l('rc.ai.apikey')}
        width={'lg'}
        rules={[{ required: true, message: l('rc.ai.apikeyPleaseHolder') }]}
        placeholder={l('rc.ai.apikeyPleaseHolder')}
      />

      <ProFormText
        name='templateId'
        label={l('rc.ai.templateId')}
        width={'md'}
        rules={[{ required: true, message: l('rc.ai.templateIdPleaseHolder') }]}
        placeholder={l('rc.ai.templateIdPleaseHolder')}
      />

      <ProFormTextArea
        name='templateName'
        label={l('rc.ai.templateName')}
        width={'lg'}
        rules={[{ required: true, message: l('rc.ai.templateNamePleaseHolder') }]}
        placeholder={l('rc.ai.templateNamePleaseHolder')}
      />

      <ProFormTextArea
        name='callbackUrl'
        label={l('rc.ai.callbackUrl')}
        width={'md'}
        placeholder={l('rc.ai.callbackUrlPleaseHolder')}
      />
    </>
  );
};

export const renderUniSmsForm = (smsType: string) => {
  return (
    <>
      {renderCommonSmsForm(smsType)}
      <ProFormTextArea
        name='templateName'
        label={l('rc.ai.templateName')}
        width={'md'}
        rules={[{ required: true, message: l('rc.ai.templateNamePleaseHolder') }]}
        placeholder={l('rc.ai.templateNamePleaseHolder')}
      />

      <ProFormTextArea
        name='signature'
        label={l('rc.ai.signature')}
        width={'md'}
        rules={[{ required: true, message: l('rc.ai.signaturePleaseHolder') }]}
        placeholder={l('rc.ai.signaturePleaseHolder')}
      />

      <ProFormSwitch
        name='is-simple'
        label={l('rc.ai.isSimple')}
        unCheckedChildren={l('rc.ai.isSimple.no')}
        checkedChildren={l('rc.ai.isSimple.yes')}
        initialValue={true}
      />
    </>
  );
};

export const renderJDSmsForm = (smsType: string) => {
  return (
    <>
      {renderCommonSmsForm(smsType)}
      <ProFormText
        name='signature'
        label={l('rc.ai.signature')}
        width={'md'}
        rules={[{ required: true, message: l('rc.ai.signaturePleaseHolder') }]}
        placeholder={l('rc.ai.signaturePleaseHolder')}
      />
      <ProFormText
        name='region'
        label={l('rc.ai.regionId')}
        width={'lg'}
        rules={[{ required: true, message: l('rc.ai.regionIdPleaseHolder') }]}
        placeholder={l('rc.ai.regionIdPleaseHolder')}
      />
    </>
  );
};

export const renderCloopenSmsForm = () => {
  return (
    <>
      <ProFormText
        name='accessKeyId'
        label={l('rc.ai.accessKeyId')}
        width={'md'}
        rules={[{ required: true, message: l('rc.ai.accessKeyIdPleaseHolder') }]}
        placeholder={l('rc.ai.accessKeyIdPleaseHolder')}
      />
      <ProFormText
        name='accessKeySecret'
        label={l('rc.ai.accessKeySecret')}
        width={'sm'}
        rules={[{ required: true, message: l('rc.ai.accessKeySecretPleaseHolder') }]}
        placeholder={l('rc.ai.accessKeySecretPleaseHolder')}
      />
      <ProFormText
        name='appId'
        label={l('rc.ai.appId')}
        width={'sm'}
        rules={[{ required: true, message: l('rc.ai.appIdPleaseHolder') }]}
        placeholder={l('rc.ai.appIdPleaseHolder')}
      />
      <ProFormTextArea
        name='baseUrl'
        label={l('rc.ai.baseUrl')}
        width={'xl'}
        rules={[{ required: true, message: l('rc.ai.baseUrlPleaseHolder') }]}
        placeholder={l('rc.ai.baseUrlPleaseHolder')}
      />
    </>
  );
};

export const renderEmaySmsForm = () => {
  return (
    <>
      <ProFormText
        name='appid'
        label={l('rc.ai.appId')}
        width={'lg'}
        rules={[{ required: true, message: l('rc.ai.appIdPleaseHolder') }]}
        placeholder={l('rc.ai.appIdPleaseHolder')}
      />
      <ProFormText.Password
        name='secretKey'
        label={l('rc.ai.secretKey')}
        width={'md'}
        rules={[{ required: true, message: l('rc.ai.secretKeyPleaseHolder') }]}
        placeholder={l('rc.ai.secretKeyPleaseHolder')}
      />
      <ProFormTextArea
        name='requestUrl'
        label={l('rc.ai.requestUrl')}
        width={'lg'}
        rules={[{ required: true, message: l('rc.ai.requestUrlPleaseHolder') }]}
        placeholder={l('rc.ai.requestUrlPleaseHolder')}
      />
    </>
  );
};

export const renderCtyunForm = (smsType: string) => {
  return (
    <>
      {renderCommonSmsForm(smsType)}
      <ProFormText
        name='signature'
        label={l('rc.ai.signature')}
        width={'md'}
        rules={[{ required: true, message: l('rc.ai.signaturePleaseHolder') }]}
        placeholder={l('rc.ai.signaturePleaseHolder')}
      />
      <ProFormText
        name='action'
        label={l('rc.ai.action')}
        width={'sm'}
        disabled
        initialValue={'SendSms'}
        placeholder={l('rc.ai.actionPleaseHolder')}
      />
      <ProFormTextArea
        name='templateName'
        label={l('rc.ai.templateName')}
        width={'md'}
        rules={[{ required: true, message: l('rc.ai.templateNamePleaseHolder') }]}
        placeholder={l('rc.ai.templateNamePleaseHolder')}
      />

      <ProFormTextArea
        name='requestUrl'
        label={l('rc.ai.requestUrl')}
        width={'lg'}
        initialValue={'https://sms-global.ctapi.ctyun.cn/sms/api/v1'}
        rules={[{ required: true, message: l('rc.ai.requestUrlPleaseHolder') }]}
        placeholder={l('rc.ai.requestUrlPleaseHolder')}
      />
    </>
  );
};
