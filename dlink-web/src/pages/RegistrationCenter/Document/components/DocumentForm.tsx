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


import React, {useState} from 'react';
import {Button, Form, Input, Modal, Select, Switch} from 'antd';
import {DocumentTableListItem} from "@/pages/RegistrationCenter/data";
import TextArea from "antd/es/input/TextArea";
import {getDocumentFormData,} from "@/pages/RegistrationCenter/Document/function";
import {l} from "@/utils/intl";

export type DocumentFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<DocumentTableListItem>) => void;
  modalVisible: boolean;
  values: Partial<DocumentTableListItem>;
  // instance: DocumentTableListItem[];
};

const FormItem = Form.Item;
const Option = Select.Option;


const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const DocumentForm: React.FC<DocumentFormProps> = (props) => {


  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<DocumentTableListItem>>({
    id: props.values.id,
    name: props.values.name,
    category: props.values.category,
    type: props.values.type,
    subtype: props.values.subtype,
    description: props.values.description,
    fillValue: props.values.fillValue,
    version: props.values.version,
    likeNum: props.values.likeNum,
    enabled: props.values.enabled ? props.values.enabled : true,
  });

  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
  } = props;


  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals({...formVals, ...fieldsValue});
    handleSubmit({...formVals, ...fieldsValue});
  };

  const renderContent = (formVals) => {
    return (
      <>
        <FormItem
          name="name"
          label={l('pages.rc.doc.name')}
          rules={[{required: true, message: l('pages.rc.doc.namePlaceholder')}]}>
          <Input placeholder={l('pages.rc.doc.namePlaceholder')}/>
        </FormItem>
        <FormItem
          name="category"
          label={l('pages.rc.doc.category')}
          rules={[{required: true, message: l('pages.rc.doc.categoryPlaceholder')}]}
        >
          <Select allowClear>
            <Option value="Method">Method</Option>
            <Option value="Function">Function</Option>
            <Option value="Constructor">Constructor</Option>
            <Option value="Field">Field</Option>
            <Option value="Variable">Variable</Option>
            <Option value="Class">Class</Option>
            <Option value="Struct">Struct</Option>
            <Option value="Interface">Interface</Option>
            <Option value="Module">Module</Option>
            <Option value="Property">Property</Option>
            <Option value="Event">Event</Option>
            <Option value="Operator">Operator</Option>
            <Option value="Unit">Unit</Option>
            <Option value="Value">Value</Option>
            <Option value="Constant">Constant</Option>
            <Option value="Enum">Enum</Option>
            <Option value="EnumMember">EnumMember</Option>
            <Option value="Keyword">Keyword</Option>
            <Option value="Text">Text</Option>
            <Option value="Color">Color</Option>
            <Option value="File">File</Option>
            <Option value="Reference">Reference</Option>
            <Option value="Customcolor">Customcolor</Option>
            <Option value="Folder">Folder</Option>
            <Option value="TypeParameter">TypeParameter</Option>
            <Option value="User">User</Option>
            <Option value="Issue">Issue</Option>
            <Option value="Snippet">Snippet</Option>
          </Select>
        </FormItem>
        <FormItem
          name="type"
          label={l('pages.rc.doc.functionType')}
          rules={[{required: true, message: l('pages.rc.doc.typePlaceholder')}]}
        >
          <Select allowClear>
            <Option value="优化参数">优化参数</Option>
            <Option value="建表语句">建表语句</Option>
            <Option value="CataLog">CataLog</Option>
            <Option value="设置参数">设置参数</Option>
            <Option value="内置函数">内置函数</Option>
            <Option value="UDF">UDF</Option>
            <Option value="Other">Other</Option>
          </Select>
        </FormItem>
        <FormItem
          name="subtype"
          label={l('pages.rc.doc.subFunctionType')}
          rules={[{required: true, message: l('pages.rc.doc.subTypePlaceholder')}]}
        >
          <Select allowClear>
            <Option value="比较函数">比较函数</Option>
            <Option value="逻辑函数">逻辑函数</Option>
            <Option value="算术函数">算术函数</Option>
            <Option value="字符串函数">字符串函数</Option>
            <Option value="时间函数">时间函数</Option>
            <Option value="条件函数">条件函数</Option>
            <Option value="类型转换函数">类型转换函数</Option>
            <Option value="Collection 函数">Collection 函数</Option>
            <Option value="Value Collection 函数">Value Collection 函数</Option>
            <Option value="Value Access 函数">Value Access 函数</Option>
            <Option value="分组函数">分组函数</Option>
            <Option value="hash函数">hash函数</Option>
            <Option value="聚合函数">聚合函数</Option>
            <Option value="列函数">列函数</Option>
            <Option value="表值聚合函数">表值聚合函数</Option>
            <Option value="其他函数">其他函数</Option>
          </Select>
        </FormItem>
        <FormItem
          name="description"
          label={l('pages.rc.doc.description')}
        >
          <TextArea placeholder={l('pages.rc.doc.descriptionPlaceholder')} allowClear autoSize={{minRows: 3, maxRows: 10}}/>
        </FormItem>
        <FormItem
          name="fillValue"
          label={l('pages.rc.doc.fillValue')}
          rules={[{required: true, message: l('pages.rc.doc.fillValueHelp')}]}
        >
          <TextArea
            placeholder={ l('pages.rc.doc.fillValuePlaceholder')}
            allowClear
            autoSize={{minRows: 3, maxRows: 10}}/>
        </FormItem>
        <FormItem
          name="version"
          label={l('pages.rc.doc.version')}
          rules={[{required: true, message: l('pages.rc.doc.versionPlaceholder')}]}
        >
          <Select allowClear>
            <Option value="1.11">Flink-1.11</Option>
            <Option value="1.12">Flink-1.12</Option>
            <Option value="1.13">Flink-1.13</Option>
            <Option value="1.14">Flink-1.14</Option>
            <Option value="1.15">Flink-1.15</Option>
            <Option value="1.16">Flink-1.16</Option>
            <Option value="ALL Version">ALL Version</Option>
          </Select>
        </FormItem>
        <FormItem
          name="enabled"
          label={l('global.table.isEnable')}
          rules={[{required: true, message: l('pages.rc.doc.enabledPlaceholder')}]}
        >
          <Switch  checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                  defaultChecked={formVals.enabled}/>
        </FormItem>
      </>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleModalVisible(false)}>{l('button.cancel')}</Button>
        <Button type="primary" onClick={() => submitForm()}>{l('button.finish')}</Button>
      </>
    );
  };

  return (
    <Modal
      width={"40%"}
      bodyStyle={{padding: '32px 40px 48px'}}
      destroyOnClose
      title={formVals.id ? l('pages.rc.doc.modify') : l('pages.rc.doc.create')}
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={getDocumentFormData(formVals as DocumentTableListItem)}
      >
        {renderContent(getDocumentFormData(formVals as DocumentTableListItem))}
      </Form>
    </Modal>
  );
};

export default DocumentForm;
