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
import {NameSpaceTableListItem} from "@/pages/ResourceCenter/NamespaceManager/data";

export type TenantFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<NameSpaceTableListItem>) => void;
  modalVisible: boolean;
  values: Partial<NameSpaceTableListItem>;
};

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const NameSpaceForm: React.FC<TenantFormProps> = (props) => {

  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<NameSpaceTableListItem>>({
    id: props?.values?.id,
    tenantId: props?.values?.tenantId,
    namespaceCode: props?.values?.namespaceCode,
    enabled: props?.values?.enabled,
    note: props?.values?.note,
    createTime: props?.values?.createTime,
    updateTime: props?.values?.updateTime,
  });

  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
  } = props;

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    // fieldsValue.id = formVals.id;
    setFormVals(fieldsValue);
    handleSubmit(fieldsValue);
  };

  const renderContent = (formValsPara: Partial<NameSpaceTableListItem>) => {
    return (
      <>
        <Form.Item
          name="roleCode"
          label="角色编号"
          rules={[{required: true, message: '请输入角色唯一编码！'}]}>
          <Input placeholder="请输入角色唯一编码"/>
        </Form.Item>
        <Form.Item
          name="roleName"
          label="角色名称"
          rules={[{required: true, message: '请输入角色名称！'}]}>
          <Input placeholder="请输入角色名称"/>
        </Form.Item>
        <Form.Item
          name="note"
          label="注释"
        >
          <Input.TextArea placeholder="请输入文本注释" allowClear
                          autoSize={{minRows: 3, maxRows: 10}}/>
        </Form.Item>
      </>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleModalVisible(false)}>取消</Button>
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
      title={formVals.id ? "修改角色" : "创建角色"}
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={formVals}
      >
        {renderContent(formVals)}
      </Form>
    </Modal>
  );
};

export default NameSpaceForm;
