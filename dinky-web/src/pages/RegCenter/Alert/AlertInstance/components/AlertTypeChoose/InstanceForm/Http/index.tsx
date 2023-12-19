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

import { RequestMethod } from "@/pages/RegCenter/Alert/AlertInstance/constans";
import { Alert } from "@/types/RegCenter/data.d";
import { l } from "@/utils/intl";
import { ProForm,ProFormGroup,ProFormList,ProFormSelect,ProFormText } from "@ant-design/pro-components";
import { Space} from "antd";
import { FormInstance } from "antd/es/form/hooks/useForm";
import { Values } from "async-validator";
import React from "react";

type HttpProps = {
  values: Partial<Alert.AlertInstance>;
  form: FormInstance<Values>;
};
const Http: React.FC<HttpProps> = (props) => {

  const {values, form} = props;

  const params = values.params as Alert.AlertInstanceParamsHttp;

  return (
    <>
      <ProForm.Group>
        <ProFormSelect
          width='sm'
          name={['params', 'method']}
          label={l('rc.ai.http.method')}
          rules={[{ required: true, message: l('rc.ai.http.methodPleaseHolder') }]}
          placeholder={l('rc.ai.http.methodPleaseHolder')}
          options={RequestMethod}
        />
        <ProFormText
          width='xl'
          name={['params', 'url']}
          label={l('rc.ai.http.url')}
          rules={[{ required: true, message: l('rc.ai.http.urlPleaseHolder') }]}
          placeholder={l('rc.ai.http.urlPleaseHolder')}
        />
      </ProForm.Group>

      {/*这有 bug，待解决*/}
      <ProFormList
        name={['params', 'headers']}
        label={l('rc.ai.http.headers')}
        copyIconProps={false}
        required
        initialValue={[{ key: '', value: '' }]}
        deleteIconProps={{
          tooltipText: l('rc.cc.deleteConfig')
        }}
        creatorButtonProps={{
          style: { width: '100%' },
          creatorButtonText: l('rc.cc.addConfig')
        }}
      >
        <ProFormGroup key='headersGroup' style={{ width: '100%' }}>
          <Space key={'config'} style={{ width: '100%' }} align='baseline'>
            <ProFormText width={'md'} name='key' placeholder={l('rc.cc.key')} />
            <ProFormText width={'lg'} name='value' placeholder={l('rc.cc.value')} />
          </Space>
        </ProFormGroup>
      </ProFormList>
    </>
  );
};

export default Http;
