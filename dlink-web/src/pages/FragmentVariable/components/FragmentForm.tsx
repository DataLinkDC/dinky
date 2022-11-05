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
import {Button, Form, Input, Modal, Switch} from 'antd';
import TextArea from "antd/es/input/TextArea";
import {FragmentVariableTableListItem} from "@/pages/FragmentVariable/data";
import {useIntl} from 'umi';

export type FragmentFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<FragmentVariableTableListItem>) => void;
  modalVisible: boolean;
  values: Partial<FragmentVariableTableListItem>;
  // instance: FragmentVariableTableListItem[];
};

const FormItem = Form.Item;


const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const FragmentForm: React.FC<FragmentFormProps> = (props: any) => {


  const intl = useIntl();
  const l = (id: string, defaultMessage?: string, value?: {}) => intl.formatMessage({id, defaultMessage}, value);


  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<FragmentVariableTableListItem>>({
    id: props.values.id,
    name: props.values.name,
    alias: props.values.alias,
    fragmentValue: props.values.fragmentValue,
    note: props.values.note,
    enabled: props.values.enabled,
    createTime: props.values.createTime,
    updateTime: props.values.updateTime,
  });

  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
  } = props;


  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    fieldsValue.id = formVals.id;
    setFormVals({...formVals, ...fieldsValue});
    handleSubmit({...formVals, ...fieldsValue});
  };

  const renderContent = (formVals: FragmentVariableTableListItem) => {
    return (
      <>
        <FormItem
          name="name"
          label={l('pages.registerCenter.fv.name')}
          rules={[{required: true, message: l('pages.registerCenter.fv.namePlaceholder')}]}>
          <Input placeholder={l('pages.registerCenter.fv.namePlaceholder')}/>
        </FormItem>
        <FormItem
          name="alias"
          label={l('pages.registerCenter.fv.alias')}
          rules={[{required: true, message: l('pages.registerCenter.fv.aliasPlaceholder')}]}>
          <Input placeholder={l('pages.registerCenter.fv.aliasPlaceholder')}/>
        </FormItem>
        <FormItem
          name="note"
          label={l('global.table.note')}
        >
          <TextArea placeholder={l('global.table.notePlaceholder')} allowClear autoSize={{minRows: 3, maxRows: 10}}/>
        </FormItem>
        <FormItem
          name="fragmentValue"
          label={l('pages.registerCenter.fv.fragmentValue')}
          rules={[{required: true, message: l('pages.registerCenter.fv.fragmentValuePlaceholder')}]}
        >
          <TextArea placeholder={l('pages.registerCenter.fv.fragmentValuePlaceholder')}
                    allowClear
                    autoSize={{minRows: 3, maxRows: 10}}/>
        </FormItem>
        <FormItem
          name="enabled"
          label={l('global.table.isEnable')}
          rules={[{required: true, message: l('pages.registerCenter.fv.enabledPlaceholder')}]}>
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
      title={formVals.id ? l('pages.registerCenter.fv.modify') : l('pages.registerCenter.fv.create')}
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={formVals as FragmentVariableTableListItem}
      >
        {renderContent(formVals as FragmentVariableTableListItem)}
      </Form>
    </Modal>
  );
};

export default FragmentForm;
