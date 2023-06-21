/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import {FormInstance} from "antd/es/form/hooks/useForm";
import {Values} from "async-validator";
import {
  ProForm,
  ProFormItem,
  ProFormSelect,
  ProFormSwitch,
  ProFormText,
  ProFormTextArea
} from "@ant-design/pro-components";
import {FORM_LAYOUT_PUBLIC} from "@/services/constants";
import React, {useState} from "react";
import {
  DOCUMENT_CATEGORY,
  DOCUMENT_FUNCTION_TYPE,
  DOCUMENT_SUBTYPE,
  VERSIONS
} from "@/pages/RegCenter/Document/constans";
import {l} from "@/utils/intl";
import CodeEdit from "@/components/CustomEditor/CodeEdit";
import {Document} from "@/types/RegCenter/data";
import {DefaultOptionType} from "rc-select/lib/Select";

type DocumentFormProps = {
  values: Partial<Document>;
  form: FormInstance<Values>
}

const CodeEditProps = {
  height: "25vh",
  lineNumbers: "on",
}

const DocumentForm: React.FC<DocumentFormProps> = (props) => {

  const {values, form} = props;

  /**
   * status
   */
  const [codeFillValue, setCodeFillValue] = useState<string>(values.fillValue || '');

  const [categoryList] = useState<DefaultOptionType []>(
    DOCUMENT_CATEGORY.map((item) => ({label: item.text, value: item.value})));

  const [typeList] = useState<DefaultOptionType []>(
    DOCUMENT_FUNCTION_TYPE.map((item) => ({label: item.text, value: item.value})));

  const [subTypeList] = useState<DefaultOptionType []>(
    DOCUMENT_SUBTYPE.map((item) => ({label: item.text, value: item.value})));

  const [versionOptions] = useState<DefaultOptionType []>(
    VERSIONS.map((item) => ({label: item.text, value: item.value})));

  /**
   * code editor change callback
   * @param value
   */
  const handleFillValueChange = (value: string) => {
    setCodeFillValue(value);
  }

  /**
   * form
   * @constructor
   */
  const documentFormRender = () => {
    return (
      <>
        <ProFormText
          name="name"
          label={l('rc.doc.name')}
          placeholder={l('rc.doc.namePlaceholder')}
          rules={[{required: true, message: l('rc.doc.namePlaceholder')}]}
        />

        <ProFormSelect
          name="category"
          label={l('rc.doc.category')}
          rules={[{required: true, message: l('rc.doc.categoryPlaceholder')}]}
          options={categoryList}
        />


        <ProFormSelect
          name="type"
          label={l('rc.doc.functionType')}
          rules={[{required: true, message: l('rc.doc.typePlaceholder')}]}
          options={typeList}
        />

        <ProFormSelect
          name="subtype"
          label={l('rc.doc.subFunctionType')}
          rules={[{required: true, message: l('rc.doc.subTypePlaceholder')}]}
          options={subTypeList}
        />

        <ProFormTextArea
          name="description"
          label={l('rc.doc.description')}
          placeholder={l('rc.doc.descriptionPlaceholder')}
        />

        <ProFormItem
          name="fillValue"
          label={l('rc.doc.fillValue')}
          tooltip={l('rc.doc.fillValuePlaceholder')}
          rules={[{required: true, message: l('rc.doc.fillValueHelp')}]}
        >
          <CodeEdit
            onChange={(value => {
              handleFillValueChange(value)
            })}
            code={codeFillValue}
            language={"sql"}
            {...CodeEditProps}
          />
        </ProFormItem>

        <ProFormSelect
          name="version"
          label={l('rc.doc.version')}
          rules={[{required: true, message: l('rc.doc.versionPlaceholder')}]}
          options={versionOptions}
        />

        <ProFormSwitch
          name="enabled"
          label={l('global.table.isEnable')}
          checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
        />

      </>
    );
  };

  return <>
    <ProForm
      {...FORM_LAYOUT_PUBLIC}
      form={form}
      submitter={false}
      layout={'horizontal'}
      initialValues={values}
    >
      {documentFormRender()}
    </ProForm>
  </>;
};
export default DocumentForm;
