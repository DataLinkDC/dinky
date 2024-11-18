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
import { JOB_TYPE } from '@/pages/DataStudio/Toolbar/Project/constants';
import {
  DOCUMENT_CATEGORY_ENUMS,
  DOCUMENT_FUNCTION_TYPE_ENUMS,
  DOCUMENT_TYPE_ENUMS,
  VERSIONS
} from '@/pages/RegCenter/Document/constans';
import { Document } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import {
  ProFormGroup,
  ProFormItem,
  ProFormSegmented,
  ProFormSelect,
  ProFormSwitch,
  ProFormText,
  ProFormTextArea
} from '@ant-design/pro-components';
import { FormInstance } from 'antd/es/form/hooks/useForm';
import { Values } from 'async-validator';
import { DefaultOptionType } from 'rc-select/lib/Select';
import React, { useState } from 'react';

type DocumentFormProps = {
  values: Partial<Document>;
  form: FormInstance<Values>;
};

const CodeEditProps = {
  height: '25vh',
  lineNumbers: 'on'
};

const DocumentForm: React.FC<DocumentFormProps> = (props) => {
  const { values, form } = props;

  /**
   * status
   */
  const [codeFillValue, setCodeFillValue] = useState<string>(values.fillValue || '');
  const { FLINK_OPTIONS, SQL_TEMPLATE, FUN_UDF, OTHER } = DOCUMENT_TYPE_ENUMS;

  const [CATEGORY_LIST] = useState<DefaultOptionType[]>(
    Object.values(DOCUMENT_CATEGORY_ENUMS).map((item) => ({ label: item.text, value: item.value }))
  );

  const [FUNCTION_TYPES] = useState<DefaultOptionType[]>(
    Object.values(DOCUMENT_FUNCTION_TYPE_ENUMS).map((item) => ({
      label: item.text,
      value: item.value
    }))
  );

  const [VERSION_OPTIONS] = useState<DefaultOptionType[]>(
    VERSIONS.map((item) => ({ label: item.text, value: item.value }))
  );

  const [documentType, setDocumentType] = useState<string>('');

  /**
   * form
   * @constructor
   */
  const documentFormRender = () => {
    return (
      <>
        <ProFormGroup>
          <ProFormSegmented
            name='type'
            label={l('rc.doc.functionType')}
            initialValue={SQL_TEMPLATE.value}
            rules={[{ required: true, message: l('rc.doc.typePlaceholder') }]}
            valueEnum={DOCUMENT_TYPE_ENUMS}
            fieldProps={{ onChange: (value) => setDocumentType(value as string) }}
          />

          <ProFormText
            name='name'
            width={'md'}
            label={l('rc.doc.name')}
            placeholder={l('rc.doc.namePlaceholder')}
            rules={[{ required: true, message: l('rc.doc.namePlaceholder') }]}
          />

          <ProFormSwitch
            name='enabled'
            label={l('global.table.isEnable')}
            checkedChildren={l('button.enable')}
            unCheckedChildren={l('button.disable')}
          />
        </ProFormGroup>

        <ProFormGroup>
          <ProFormSelect
            name='subtype'
            width={'sm'}
            label={l('rc.doc.subFunctionType')}
            rules={[{ required: true, message: l('rc.doc.subTypePlaceholder') }]}
            options={documentType == FUN_UDF.value ? FUNCTION_TYPES : JOB_TYPE}
          />

          <ProFormSelect
            name='category'
            width={'sm'}
            label={l('rc.doc.category')}
            rules={[{ required: true, message: l('rc.doc.categoryPlaceholder') }]}
            options={CATEGORY_LIST}
          />
          <ProFormSelect
            name='version'
            width={'sm'}
            label={l('rc.doc.version')}
            rules={[{ required: true, message: l('rc.doc.versionPlaceholder') }]}
            options={VERSION_OPTIONS}
          />
        </ProFormGroup>

        <ProFormItem
          name='fillValue'
          label={l('rc.doc.fillValue')}
          tooltip={l('rc.doc.fillValuePlaceholder')}
          rules={[{ required: true, message: l('rc.doc.fillValueHelp') }]}
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
      </>
    );
  };

  return <>{documentFormRender()}</>;
};
export default DocumentForm;
