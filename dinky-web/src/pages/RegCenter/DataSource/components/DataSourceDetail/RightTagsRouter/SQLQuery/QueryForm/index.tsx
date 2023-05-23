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

import {ProForm, ProFormText} from '@ant-design/pro-components';
import React from 'react';
import {FormInstance} from 'antd/es/form/hooks/useForm';
import {Values} from 'async-validator';
import {AutoComplete} from 'antd';
import {DefaultOptionType} from 'rc-select/lib/Select';
import {l} from '@/utils/intl';

type QueryFormProps = {
  form: FormInstance<Values>;
  onSubmit: (values: any) => void;
  autoCompleteColumns: DefaultOptionType[];
}

const QueryForm: React.FC<QueryFormProps> = (props) => {

  const {form, onSubmit: submitHandle, autoCompleteColumns} = props;


  /**
   * search form submit
   */
  const onSubmitHandle = () => {
    const values = form.validateFields();
    submitHandle(values);
  };

  const renderForm = () => {
    return <>
      <AutoComplete
        options={autoCompleteColumns}
        onSelect={(value: string) => {
          form.setFieldsValue({whereInput: value});
        }}
      >
        <ProFormText
          addonBefore={'WHERE'}
          key="whereInput"
          name="whereInput"
          required
        />
      </AutoComplete>

      <AutoComplete
        options={autoCompleteColumns}
        onSelect={(value: string) => {
          form.setFieldsValue({orderInput: value});
        }}
      >
        <ProFormText
          addonBefore={'ORDER BY'}
          key="ORDER BY"
          name="orderInput"
          required
        />
      </AutoComplete>
    </>;
  };

  /**
   * submit config
   */
  const submitConfig = {
    onSubmit: onSubmitHandle,
    searchConfig: {
      submitText: l('button.search'),
    },
    resetButtonProps: {
      style: {
        display: 'none',
      },
    }
  };

  /**
   * render
   */
  return <>
    <ProForm
      layout={'inline'}
      size={'middle'}
      form={form}
      isKeyPressSubmit autoFocusFirstInput
      className={'query-form'}
      submitter={{...submitConfig}}
    >
      {renderForm()}
    </ProForm>
  </>;
};

export default QueryForm;
