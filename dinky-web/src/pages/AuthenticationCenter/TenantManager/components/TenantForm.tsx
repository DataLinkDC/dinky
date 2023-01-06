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
import {TenantTableListItem} from "@/pages/AuthenticationCenter/data.d";
import {l} from "@/utils/intl";

export type TenantFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<TenantTableListItem>) => void;
  modalVisible: boolean;
  values: Partial<TenantTableListItem>;
};

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};
const FormItem = Form.Item;

const TenantForm: React.FC<TenantFormProps> = (props) => {

  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<TenantTableListItem>>({
    id: props.values.id,
    tenantCode: props.values.tenantCode,
    isDelete: props.values.isDelete,
    note: props.values.note,
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

  const renderContent = (formVals: Partial<TenantTableListItem>) => {
    return (
      <>
        <FormItem
          name="tenantCode"
          label={l('pages.tenant.TenantCode')}
          rules={[{required: true, message: l('pages.tenant.EnterTenantCode')}]}>
          <Input allowClear placeholder={l('pages.tenant.EnterTenantCode')}/>
        </FormItem>
        <FormItem
          name="note"
          label={l('global.table.note')}
          rules={[{required: true, message: l('pages.tenant.EnterTenantNote')}]}
        >
          <Input.TextArea placeholder={l('pages.tenant.EnterTenantNote')} allowClear
                          autoSize={{minRows: 3, maxRows: 10}}/>
        </FormItem>
      </>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleModalVisible(false)}>{l('button.cancel')}</Button>
        <Button type="primary" onClick={() => submitForm()}>
          {l('button.finish')}
        </Button>
      </>
    );
  };

  return (
    <Modal
      width={"40%"}
      bodyStyle={{padding: '32px 40px 48px'}}
      destroyOnClose
      title={formVals.id ? l('pages.tenant.update') : l('pages.tenant.create')}
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

export default TenantForm;
