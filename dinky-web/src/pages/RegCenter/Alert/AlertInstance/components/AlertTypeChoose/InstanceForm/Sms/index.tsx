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


import React, {useCallback} from 'react';
import {l} from '@/utils/intl';
import {ProForm, ProFormSelect, ProFormSwitch} from '@ant-design/pro-components';
import {SWITCH_OPTIONS} from '@/services/constants';
import {
  renderAlibabaSmsForm,
  renderCloopenSmsForm,
  renderCtyunForm,
  renderEmaySmsForm,
  renderHuaWeiSmsForm,
  renderJDSmsForm,
  renderTencentSmsForm,
  renderUniSmsForm,
  renderYunpianSmsForm
} from '@/pages/RegCenter/Alert/AlertInstance/components/AlertTypeChoose/InstanceForm/Sms/function';
import {MANU_FACTURERS, SMS_TYPE} from '@/pages/RegCenter/Alert/AlertInstance/constans';


const Sms = (props: any) => {
  const {values} = props;
  /**
   * render form by sms type
   */
  const renderFormBySmsType = useCallback((smsType: number) => {
    switch (smsType) {
      case SMS_TYPE.ALIBABA:
        return renderAlibabaSmsForm();
      case SMS_TYPE.HUAWEI:
        return renderHuaWeiSmsForm();
      case SMS_TYPE.YUNPIAN:
        return renderYunpianSmsForm();
      case SMS_TYPE.TENCENT:
        return renderTencentSmsForm();
      case SMS_TYPE.UNI:
        return renderUniSmsForm();
      case  SMS_TYPE.JDCLOUD:
        return renderJDSmsForm();
      case SMS_TYPE.CLOOPEN:
        return renderCloopenSmsForm();
      case SMS_TYPE.EMAY:
        return renderEmaySmsForm();
      case SMS_TYPE.CTYUN:
        return renderCtyunForm();
      default:
        return undefined;
    }
  }, [values.manufacturers]);


  /**
   * render
   */
  return <>
    <ProForm.Group>
      <ProFormSelect
        name="manufacturers"
        label={l('rc.ai.manufacturers')}
        width={'md'}
        options={MANU_FACTURERS}
        rules={[{required: true, message: l('rc.ai.manufacturersPleaseHolder')}]}
        placeholder={l('rc.ai.manufacturersPleaseHolder')}
      />
      <ProFormSwitch
        name="enabled"
        label={l('global.table.isEnable')}
        {...SWITCH_OPTIONS()}
      />
    </ProForm.Group>

    <ProForm.Group>
      {renderFormBySmsType(values.manufacturers)}
    </ProForm.Group>
  </>;
};

export default Sms;
