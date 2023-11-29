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

import CodeEdit from '@/components/CustomEditor/CodeEdit';
import { FORM_LAYOUT_PUBLIC } from '@/services/constants';
import { GlobalVar } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import {
  ProForm,
  ProFormItem,
  ProFormSwitch,
  ProFormText,
  ProFormTextArea
} from '@ant-design/pro-components';
import { FormInstance } from 'antd/es/form/hooks/useForm';
import { Values } from 'async-validator';
import React, { useState } from 'react';

type GlobalVarModalProps = {
  values: Partial<GlobalVar>;
  form: FormInstance<Values>;
};

/**
 * global variable code edit props
 */
const CodeEditProps = {
  height: '25vh',
  lineNumbers: 'on'
};
const GlobalVarForm: React.FC<GlobalVarModalProps> = (props) => {
  const { values, form } = props;

  const [globalVarValue, setGlobalVarValue] = useState<string>(values.fragmentValue ?? '');

  const handleGlobalVarChange = (value: string) => {
    setGlobalVarValue(value);
  };

  const globalVarRender = () => {
    return (
      <>
        <ProFormText
          name='name'
          label={l('rc.gv.name')}
          placeholder={l('rc.gv.namePlaceholder')}
          rules={[{ required: true, message: l('rc.gv.namePlaceholder') }]}
        />

        <ProFormTextArea
          name='note'
          label={l('global.table.note')}
          placeholder={l('global.table.notePlaceholder')}
          allowClear
        />

        <ProFormSwitch
          name='enabled'
          label={l('global.table.isEnable')}
          checkedChildren={l('button.enable')}
          unCheckedChildren={l('button.disable')}
        />

        <ProFormItem
          name='fragmentValue'
          label={l('rc.gv.value')}
          rules={[{ required: true, message: l('rc.gv.valuePlaceholder') }]}
        >
          <CodeEdit
            onChange={(value) => handleGlobalVarChange(value ?? '')}
            code={globalVarValue}
            language={'sql'}
            {...CodeEditProps}
          />
        </ProFormItem>
      </>
    );
  };

  return (
    <>
      <ProForm
        {...FORM_LAYOUT_PUBLIC}
        layout={'horizontal'}
        submitter={false}
        form={form}
        initialValues={values}
      >
        {globalVarRender()}
      </ProForm>
    </>
  );
};

export default GlobalVarForm;
