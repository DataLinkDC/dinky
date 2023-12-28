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
import {
  DOCUMENT_TYPE_ENUMS, VERSIONS, DOCUMENT_FUNCTION_TYPE_ENUMS, DOCUMENT_CATEGORY_ENUMS
} from '@/pages/RegCenter/Document/constans';
import {FORM_LAYOUT_PUBLIC} from '@/services/constants';
import {Document} from '@/types/RegCenter/data';
import {l} from '@/utils/intl';
import {
  ProForm,
  ProFormItem, ProFormSegmented,
  ProFormSelect,
  ProFormSwitch,
  ProFormText,
  ProFormTextArea
} from '@ant-design/pro-components';
import {FormInstance} from 'antd/es/form/hooks/useForm';
import {Values} from 'async-validator';
import {DefaultOptionType} from 'rc-select/lib/Select';
import React, {useState} from 'react';
import {ProFormDependency} from "@ant-design/pro-form";
import {JOB_TYPE} from "@/pages/DataStudio/LeftContainer/Project/constants";

type DocumentFormProps = {
  values: Partial<Document>;
  form: FormInstance<Values>;
};

const CodeEditProps = {
  height: '25vh',
  lineNumbers: 'on'
};

const DocumentForm: React.FC<DocumentFormProps> = (props) => {
  const {values, form} = props;

  /**
   * status
   */
  const [codeFillValue, setCodeFillValue] = useState<string>(values.fillValue || '');
  const {
    FLINK_OPTIONS,
    SQL_TEMPLATE,
    FUN_UDF,
    OTHER
  } = DOCUMENT_TYPE_ENUMS;

  const [CATEGORY_LIST] = useState<DefaultOptionType[]>(
    Object.values(DOCUMENT_CATEGORY_ENUMS).map((item) => ({label: item.text, value: item.value}))
  );

  const [FUNCTION_TYPES] = useState<DefaultOptionType[]>(
    Object.values(DOCUMENT_FUNCTION_TYPE_ENUMS).map((item) => ({label: item.text, value: item.value}))
  );

  const [VERSION_OPTIONS] = useState<DefaultOptionType[]>(
    VERSIONS.map((item) => ({label: item.text, value: item.value}))
  );

  const onTypeAndNameChange = (type: string, name: string) => {
    if (type == FLINK_OPTIONS.value) {
      const fillValue = "set '" + name + "' = '${1:}'";
      form.setFieldsValue({category: "Varible"});
      form.setFieldsValue({fillValue: fillValue});
      setCodeFillValue(fillValue);
    }
  }

  /**
   * form
   * @constructor
   */
  const documentFormRender = () => {
    return (
      <>
        <ProFormSegmented
          name='type'
          label={l('rc.doc.functionType')}
          rules={[{required: true, message: l('rc.doc.typePlaceholder')}]}
          valueEnum={DOCUMENT_TYPE_ENUMS}
        />

        <ProFormText
          name='name'
          label={l('rc.doc.name')}
          placeholder={l('rc.doc.namePlaceholder')}
          rules={[{required: true, message: l('rc.doc.namePlaceholder')}]}
        />

        <ProFormDependency name={['type', 'name']}>
          {
            ({type, name}) => {
              onTypeAndNameChange(type, name);
              return <>
                {type != FLINK_OPTIONS.value && type != OTHER.value &&
                  <ProFormSelect
                    name='subtype'
                    label={l('rc.doc.subFunctionType')}
                    rules={[{required: true, message: l('rc.doc.subTypePlaceholder')}]}
                    options={type == FUN_UDF.value ? FUNCTION_TYPES : JOB_TYPE}
                  />}

                <ProFormSelect
                  disabled={type == FLINK_OPTIONS.value}
                  name='category'
                  label={l('rc.doc.category')}
                  rules={[{required: true, message: l('rc.doc.categoryPlaceholder')}]}
                  options={CATEGORY_LIST}
                />
              </>
            }
          }
        </ProFormDependency>

        <ProFormItem
          name='fillValue'
          label={l('rc.doc.fillValue')}
          tooltip={l('rc.doc.fillValuePlaceholder')}
          rules={[{required: true, message: l('rc.doc.fillValueHelp')}]}
        >
          <CodeEdit
            code={codeFillValue}
            language={'sql'}
            enableSuggestions={false}
            {...CodeEditProps}
          />
        </ProFormItem>

        <ProFormTextArea
          name='description'
          label={l('rc.doc.description')}
          placeholder={l('rc.doc.descriptionPlaceholder')}
        />

        <ProFormSelect
          name='version'
          label={l('rc.doc.version')}
          rules={[{required: true, message: l('rc.doc.versionPlaceholder')}]}
          options={VERSION_OPTIONS}
        />

        <ProFormSwitch
          name='enabled'
          label={l('global.table.isEnable')}
          checkedChildren={l('button.enable')}
          unCheckedChildren={l('button.disable')}
        />
      </>
    );
  };

  return (
    <>
      <ProForm
        {...FORM_LAYOUT_PUBLIC}
        form={form}
        submitter={false}
        layout={'horizontal'}
        initialValues={values}
      >
        {documentFormRender()}
      </ProForm>
    </>
  );
};
export default DocumentForm;
