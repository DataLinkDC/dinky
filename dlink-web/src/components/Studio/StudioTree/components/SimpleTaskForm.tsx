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
import {Button, Form, Input, Modal, Select} from 'antd';

import type {TaskTableListItem} from '../data.d';
import {DIALECT} from "@/components/Studio/conf";

const {Option} = Select;

export type UpdateFormProps = {
  onCancel: (flag?: boolean, formVals?: Partial<TaskTableListItem>) => void;
  onSubmit: (values: Partial<TaskTableListItem>) => void;
  updateModalVisible: boolean;
  isCreate: boolean;
  values: Partial<TaskTableListItem>;
};

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const SimpleTaskForm: React.FC<UpdateFormProps> = (props) => {
  const [formVals, setFormVals] = useState<Partial<TaskTableListItem>>({
    id: props.values.id,
    name: props.values.name,
    alias: props.values.alias,
    parentId: props.values.parentId,
  });

  const [form] = Form.useForm();

  const {
    onSubmit: handleUpdate,
    onCancel: handleUpdateModalVisible,
    updateModalVisible,
    values,
    isCreate,
  } = props;

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals({...formVals, ...fieldsValue});
    handleUpdate({...formVals, ...fieldsValue});
  };

  const renderContent = () => {
    return (
      <>
        {isCreate?(<Form.Item
          label="作业类型" name="dialect"
          tooltip='指定作业类型，默认为 FlinkSql'
        >
          <Select defaultValue={DIALECT.FLINKSQL} value={DIALECT.FLINKSQL}>
            <Option value={DIALECT.FLINKSQL}>{DIALECT.FLINKSQL}</Option>
            <Option value={DIALECT.FLINKJAR}>{DIALECT.FLINKJAR}</Option>
            <Option value={DIALECT.FLINKSQLENV}>{DIALECT.FLINKSQLENV}</Option>
            <Option value={DIALECT.MYSQL}>{DIALECT.MYSQL}</Option>
            <Option value={DIALECT.ORACLE}>{DIALECT.ORACLE}</Option>
            <Option value={DIALECT.SQLSERVER}>{DIALECT.SQLSERVER}</Option>
            <Option value={DIALECT.POSTGRESQL}>{DIALECT.POSTGRESQL}</Option>
            <Option value={DIALECT.CLICKHOUSE}>{DIALECT.CLICKHOUSE}</Option>
            <Option value={DIALECT.DORIS}>{DIALECT.DORIS}</Option>
            <Option value={DIALECT.HIVE}>{DIALECT.HIVE}</Option>
            <Option value={DIALECT.PHOENIX}>{DIALECT.PHOENIX}</Option>
            <Option value={DIALECT.STARROCKS}>{DIALECT.STARROCKS}</Option>
            <Option value={DIALECT.JAVA}>{DIALECT.JAVA}</Option>
            <Option value={DIALECT.SQL}>{DIALECT.SQL}</Option>
          </Select>
        </Form.Item>):undefined}
        <Form.Item
          name="name"
          label="名称"
          rules={[{required: true, message: '请输入唯一名称！'}]}>
          <Input placeholder="请输入"/>
        </Form.Item>
        <Form.Item
          name="alias"
          label="别名"
          rules={[{required: true, message: '请输入别名！'}]}>
          <Input placeholder="请输入"/>
        </Form.Item>
      </>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleUpdateModalVisible(false, values)}>取消</Button>
        <Button type="primary" onClick={() => submitForm()}>
          完成
        </Button>
      </>
    );
  };

  return (
    <Modal
      width={640}
      bodyStyle={{padding: '32px 40px 48px'}}
      destroyOnClose
      title={isCreate ? '创建新作业' : ('重命名作业-' + formVals.name)}
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
          dialect: formVals.dialect,
          parentId: formVals.parentId,
        }}
      >
        {renderContent()}
      </Form>
    </Modal>
  );
};

export default SimpleTaskForm;
