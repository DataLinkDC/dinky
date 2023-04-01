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


import React, {useEffect, useState} from 'react';
import {Form, Modal, Select} from 'antd';
import {l} from "@/utils/intl";
import {Document} from "@/types/RegCenter/data";
import {FORM_LAYOUT_PUBLIC, NORMAL_MODAL_OPTIONS} from "@/services/constants";
import {ProForm, ProFormItem, ProFormSwitch, ProFormText, ProFormTextArea} from "@ant-design/pro-components";
import CodeEdit from "@/components/CustomMonacoEditor/CodeEdit";
import {
  DOCUMENT_CATEGORY,
  DOCUMENT_FUNCTION_TYPE,
  DOCUMENT_SUBTYPE,
  VERSIONS
} from "@/pages/RegCenter/Document/constans";

export type DocumentFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<Document>) => void;
  modalVisible: boolean;
  values: Partial<Document>;
};

const FormItem = Form.Item;
const Option = Select.Option;

const CodeEditProps ={
  height : "25vh",
  lineNumbers: "on",
}

const DocumentForm: React.FC<DocumentFormProps> = (props) => {


  /**
   * status
   */
  const [form] = Form.useForm();
  const [codeFillValue, setCodeFillValue] = useState<string>(props.values.fillValue || '');
  const [categoryList, setCategoryList] = useState<JSX.Element[]>([]);
  const [typeList, setTypeList] = useState<JSX.Element[]>([]);
  const [subTypeList, setSubTypeList] = useState<JSX.Element[]>([]);
  const [versionOptions, setVersionOptions] = useState<JSX.Element[]>([]);

  const [formVals, setFormVals] = useState<Partial<Document>>({
    id: props.values.id,
    name: props.values.name,
    category: props.values.category,
    type: props.values.type,
    subtype: props.values.subtype,
    description: props.values.description,
    fillValue: props.values.fillValue,
    version: props.values.version,
    likeNum: props.values.likeNum,
    enabled: props.values.enabled,
  });

  useEffect(() => {
    let versionOptions: JSX.Element[] =[]
    let categoryOptions: JSX.Element[] =[]
    let typeOptions: JSX.Element[] =[]
    let subTypeOptions: JSX.Element[] =[]
    /**
     * generate version options
     */
    VERSIONS.map((item) => {
      versionOptions.push(<Option value={item.value}>{item.text}</Option>)
    })
    setVersionOptions(versionOptions)

    /**
     * generate category options
     */
    DOCUMENT_CATEGORY.map((item) => {
      categoryOptions.push(<Option value={item.value}>{item.value}</Option>)
    })
    setCategoryList(categoryOptions)

    /**
     * generate subType options
     */
    DOCUMENT_SUBTYPE.map((item) => {
      subTypeOptions.push(<Option value={item.value}>{item.value}</Option>)
    })
    setSubTypeList(subTypeOptions)

    /**
     * generate type options
     */
    DOCUMENT_FUNCTION_TYPE.map((item) => {
      typeOptions.push(<Option value={item.value}>{item.value}</Option>)
    })
    setTypeList(typeOptions)
  },[])

  /**
   * 1. onSubmit: submit callback
   * 2. onCancel: cancel callback
   * 3. modalVisible: modal visible
   */
  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
  } = props;


  /**
   * code editor change callback
   * @param value
   */
  const handleFillValueChange =(value: string) => {
    setCodeFillValue(value);
  }


  /**
   * form submit callback
   */
  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals({...formVals, ...fieldsValue});
    handleSubmit({...formVals, ...fieldsValue});
  };

  /**
   * form
   * @constructor
   */
  const DocumentFormRender = () => {
    return (
      <>
        <ProFormText
          name="name"
          label={l('rc.doc.name')}
          placeholder={l('rc.doc.namePlaceholder')}
          rules={[{required: true, message: l('rc.doc.namePlaceholder')}]}
        />

        <ProFormItem
          name="category"
          label={l('rc.doc.category')}
          rules={[{required: true, message: l('rc.doc.categoryPlaceholder')}]}
        >
          <Select autoClearSearchValue showSearch  allowClear>
            {categoryList}
          </Select>
        </ProFormItem>

        <ProFormItem
          name="type"
          label={l('rc.doc.functionType')}
          rules={[{required: true, message: l('rc.doc.typePlaceholder')}]}
        >
          <Select autoClearSearchValue showSearch  allowClear>
            {typeList}
          </Select>
        </ProFormItem>


        <ProFormItem
          name="subtype"
          label={l('rc.doc.subFunctionType')}
          rules={[{required: true, message: l('rc.doc.subTypePlaceholder')}]}
        >
          <Select autoClearSearchValue showSearch  allowClear>
            {subTypeList}
          </Select>
        </ProFormItem>

        <ProFormTextArea
          name="description"
          label={l('rc.doc.description')}
          placeholder={l('rc.doc.descriptionPlaceholder')}
        />

        <ProFormItem
          name="fillValue"
          label={l('rc.doc.fillValue')}
          rules={[{required: true, message: l('rc.doc.fillValueHelp')}]}
        >
          <CodeEdit
            onChange={(value =>{handleFillValueChange(value)})}
            code={codeFillValue}
            language={"sql"}
            {...CodeEditProps}
          />
        </ProFormItem>

        <FormItem
          name="version"
          label={l('rc.doc.version')}
          rules={[{required: true, message: l('rc.doc.versionPlaceholder')}]}
        >
          <Select autoClearSearchValue showSearch  allowClear>
            {versionOptions}
          </Select>
        </FormItem>
        <ProFormSwitch
          name="enabled"
          label={l('global.table.isEnable')}
          rules={[{required: true, message: l('rc.doc.enabledPlaceholder')}]}
          checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
        />
      </>
    );
  };


  /**
   * render
   */
  return (
    <Modal
      {...NORMAL_MODAL_OPTIONS}
      title={formVals.id ? l('rc.doc.modify') : l('rc.doc.create')}
      open={modalVisible}
      onOk={() => submitForm()}
      onCancel={() => handleModalVisible()}
    >
      <ProForm
        {...FORM_LAYOUT_PUBLIC}
        form={form}
        submitter={false}
        layout={'horizontal'}
        initialValues={formVals}
      >
        {DocumentFormRender()}
      </ProForm>
    </Modal>
  );
};

export default DocumentForm;
