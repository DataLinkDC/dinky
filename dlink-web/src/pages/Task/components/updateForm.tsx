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
import {Button, Form, Input, Modal} from 'antd';

import {TaskTableListItem} from "@/pages/Task/data";
import Switch from "antd/es/switch";
import {l} from "@/utils/intl";

export type UpdateFormProps = {
  onCancel: (flag?: boolean, formVals?: Partial<TaskTableListItem>) => void;
  onSubmit: (values: Partial<TaskTableListItem>) => void;
  updateModalVisible: boolean;
  values: Partial<TaskTableListItem>;
};
const FormItem = Form.Item;

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const UpdateForm: React.FC<UpdateFormProps> = (props) => {


  const [formVals, setFormVals] = useState<Partial<TaskTableListItem>>({
    id: props.values.id,
    name: props.values.name,
    alias: props.values.alias,
    type: props.values.type,
    checkPoint: props.values.checkPoint,
    savePointPath: props.values.savePointPath,
    parallelism: props.values.parallelism,
    fragment: props.values.fragment,
    clusterId: props.values.clusterId,
    note: props.values.note,
    enabled: props.values.enabled,
  });

  const [form] = Form.useForm();

  const {
    onSubmit: handleUpdate,
    onCancel: handleUpdateModalVisible,
    updateModalVisible,
    values,
  } = props;

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals({...formVals, ...fieldsValue});
    handleUpdate({...formVals, ...fieldsValue});
  };

  const renderContent = (formVals: any) => {
    return (
      <>
        <FormItem
          name="name"
          label="名称"
          rules={[{required: true, message: '请输入名称！'}]}>
          <Input placeholder="请输入"/>
        </FormItem>
        <FormItem
          name="alias"
          label="别名"
        >
          <Input placeholder="请输入"/>
        </FormItem>
        <FormItem
          name="type"
          label="类型"
        >
          <Input placeholder="请输入"/>
        </FormItem>
        <FormItem
          name="note"
          label={l('global.table.note')}
        >
          <Input placeholder="请输入"/>
        </FormItem>
        <FormItem
          name="checkPoint"
          label="CheckPoint"
        >
          <Input placeholder="请输入"/>
        </FormItem>
        <FormItem
          name="savePointPath"
          label="SavePointPath"
        >
          <Input placeholder="请输入"/>
        </FormItem>
        <FormItem
          name="parallelism"
          label="Parallelism"
        >
          <Input placeholder="请输入"/>
        </FormItem>
        <FormItem
          name="fragemnt"
          label="Fragment"
        >
          <Input placeholder="请输入"/>
        </FormItem>
        <FormItem
          name="note"
          label={l('global.table.note')}
        >
          <Input placeholder="请输入"/>
        </FormItem>
        <FormItem
          name="enabled"
          label={l('global.table.isEnable')}
          rules={[{required: true, message: '请输入是否启用！'}]}>
          <Switch  checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                  defaultChecked={formVals.enabled}/>
        </FormItem>
      </>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleUpdateModalVisible(false, values)}> {l('button.cancel')}</Button>
        <Button type="primary" onClick={() => submitForm()}>
          {l('button.finish')}
        </Button>
      </>
    );
  };

  return (
    <Modal
      width={640}
      bodyStyle={{padding: '32px 40px 48px'}}
      destroyOnClose
      title="编辑任务"
      visible={updateModalVisible}
      footer={renderFooter()}
      onCancel={() => handleUpdateModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={{
          id: formVals.id,
          name: formVals.name,
          alias: formVals.alias,
          type: formVals.type,
          note: formVals.note,
          checkPoint: formVals.checkPoint,
          savePointPath: formVals.savePointPath,
          parallelism: formVals.parallelism,
          fragment: formVals.fragment,
          clusterId: formVals.clusterId,
          enabled: formVals.enabled,
        }}
      >
        {renderContent(formVals)}
      </Form>
    </Modal>
  );
};

export default UpdateForm;
