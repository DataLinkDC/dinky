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
import {NameSpaceTableListItem} from "@/pages/AuthenticationCenter/data.d";
import {getStorageTenantId} from "@/components/Common/crud";
import {l} from "@/utils/intl";

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
    id: props.values?.id,
    tenantId: props.values?.tenantId,
    namespaceCode: props.values?.namespaceCode,
    enabled: props.values?.enabled,
    note: props.values?.note,
    createTime: props.values?.createTime,
    updateTime: props.values?.updateTime,
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

  const renderContent = (formValsPara: Partial<NameSpaceTableListItem>) => {
    return (
      <>
        <Form.Item
          name="namespaceCode"
          label={l('pages.nameSpace.NameSpaceCode')}
          rules={[{required: true, message: l('pages.nameSpace.EnterNameSpaceCode')}]}>
          <Input placeholder={l('pages.nameSpace.EnterNameSpaceCode')}/>
        </Form.Item>
        <Form.Item
          hidden={true}
          name="tenantId"
          label={l('pages.nameSpace.belongTenant')}
        >
          <Input disabled defaultValue={getStorageTenantId()}/>
        </Form.Item>
        <Form.Item
          name="note"
          label={l('global.table.note')}
        >
          <Input.TextArea placeholder={l('pages.nameSpace.EnterNameSpaceNote')} allowClear
                          autoSize={{minRows: 3, maxRows: 10}}/>
        </Form.Item>
        <Form.Item
          name="enabled"
          label={l('global.table.isEnable')}>
          <Switch checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                  defaultChecked={formValsPara.enabled}/>
        </Form.Item>
      </>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleModalVisible(false)}> {l('button.cancel')}</Button>
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
      title={formVals.id ? l('pages.nameSpace.update') : l('pages.nameSpace.create')}
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
