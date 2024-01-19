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
import { CODE_TYPE, FUNCTION_TYPE_FILTER } from '@/pages/RegCenter/UDF/constants';
import { UDFTemplate } from '@/types/RegCenter/data';
import { renderLanguage } from '@/utils/function';
import { l } from '@/utils/intl';
import { ProForm, ProFormSelect, ProFormText } from '@ant-design/pro-components';
import { Select } from 'antd';
import { FormInstance } from 'antd/es/form/hooks/useForm';
import { DefaultOptionType } from 'antd/es/select';
import { Values } from 'async-validator';
import { lowerCase, toLower } from 'lodash';
import React, { useState } from 'react';

/**
 * code edit props
 */
const CodeEditProps = {
  height: '40vh',
  width: '40vw',
  lineNumbers: 'on',
  language: 'java',
  showFloatButton: true
};

/**
 * props
 */
type TemplateFormProps = {
  values: Partial<UDFTemplate>;
  form: FormInstance<Values>;
};
const { Option } = Select;

const TemplateProFrom: React.FC<TemplateFormProps> = (props) => {
  const { values, form } = props;

  const [codeValue, setCodeValue] = useState<string>(values.templateCode || '');
  const [codeLanguage, setCodeLanguage] = useState<string>(lowerCase(values.codeType) || 'java');

  const handleCodeTypeChange = (value: string) => {
    setCodeLanguage(renderLanguage(toLower(value), ''));
  };

  const renderFunctionType = () => {
    let functionTypes: DefaultOptionType[] = [];
    FUNCTION_TYPE_FILTER.map((item) => {
      return functionTypes.push({
        value: item.value,
        label: item.text
      });
    });
    return functionTypes;
  };

  const renderCodeEdit = () => {
    return (
      <>
        <ProForm.Group>
          <ProFormText
            width={'xl'}
            name='name'
            label={l('rc.template.name')}
            placeholder={l('rc.template.namePlaceholder')}
            rules={[{ required: true, message: l('rc.template.namePlaceholder') }]}
          />
        </ProForm.Group>
        <ProForm.Group>
          <ProForm.Item
            name='codeType'
            label={l('rc.template.codeType')}
            rules={[{ required: true, message: l('rc.template.codeTypePlaceholder') }]}
          >
            <Select
              onSelect={handleCodeTypeChange}
              style={{ width: 280 }}
              placeholder={l('rc.template.codeTypePlaceholder')}
            >
              <Option value={CODE_TYPE.JAVA}>{CODE_TYPE.JAVA}</Option>
              <Option value={CODE_TYPE.SCALA}>{CODE_TYPE.SCALA}</Option>
              <Option value={CODE_TYPE.PYTHON}>{CODE_TYPE.PYTHON}</Option>
            </Select>
          </ProForm.Item>
          <ProFormSelect
            width={'md'}
            name='functionType'
            label={l('rc.template.functionType')}
            rules={[
              {
                required: true,
                message: l('rc.template.functionTypePlaceholder')
              }
            ]}
            placeholder={l('rc.template.functionTypePlaceholder')}
            options={renderFunctionType()}
          />
        </ProForm.Group>
        <ProForm.Group>
          <ProForm.Item
            name='templateCode'
            label={l('rc.template.templateCodeLabel', '', {
              language: codeLanguage
            })}
            rules={[
              {
                required: true,
                message: l('rc.template.templateCodePlaceholder')
              }
            ]}
          >
            <CodeEdit
              {...CodeEditProps}
              code={codeValue}
              onChange={(value: string) => setCodeValue(value ?? '')}
              language={codeLanguage}
            />
          </ProForm.Item>
        </ProForm.Group>
      </>
    );
  };

  return (
    <>
      <ProForm
        form={form}
        initialValues={{
          ...values,
          codeType: values.codeType || CODE_TYPE.JAVA
        }}
        submitter={false}
      >
        {renderCodeEdit()}
      </ProForm>
    </>
  );
};

export default TemplateProFrom;
