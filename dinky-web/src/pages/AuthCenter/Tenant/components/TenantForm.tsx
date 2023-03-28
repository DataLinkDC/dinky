/*
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
import {Form, Modal} from 'antd';
import {l} from "@/utils/intl";
import {ProForm, ProFormText, ProFormTextArea} from "@ant-design/pro-components";
import {FORM_LAYOUT_PUBLIC, NORMAL_MODAL_OPTIONS} from "@/services/constants";

export type TenantFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<UserBaseInfo.Tenant>) => void;
  modalVisible: boolean;
  values: Partial<UserBaseInfo.Tenant>;
};


const TenantForm: React.FC<TenantFormProps> = (props) => {

  const [form] = Form.useForm();
  /**
   * form values
   */
  const [formVals, setFormVals] = useState<Partial<UserBaseInfo.Tenant>>({
    id: props.values.id,
    tenantCode: props.values.tenantCode,
    isDelete: props.values.isDelete,
    note: props.values.note,
    createTime: props.values.createTime,
    updateTime: props.values.updateTime,
  });

  /**
   * props
   */
  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
  } = props;

  /**
   * submit
   */
  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    fieldsValue.id = formVals.id;
    setFormVals({...formVals, ...fieldsValue});
    handleSubmit({...formVals, ...fieldsValue});
    form.resetFields();
  };

  /**
   * render form
   * @constructor
   */
  const TenantFormRender = () => {
    return (
      <>
        <ProFormText
          name="tenantCode"
          label={l('tenant.TenantCode')}
          placeholder={l('tenant.EnterTenantCode')}
          rules={[{required: true, message: l('tenant.EnterTenantCode')}]}
        />
        <ProFormTextArea
          name="note"
          allowClear
          label={l('global.table.note')}
          placeholder={l('tenant.EnterTenantNote')}
          rules={[{required: true, message: l('tenant.EnterTenantNote')}]}
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
      title={formVals.id ? l('tenant.update') : l('tenant.create')}
      open={modalVisible}
      onOk={submitForm}
      onCancel={() => handleModalVisible()}
    >
      <ProForm
        {...FORM_LAYOUT_PUBLIC}
        form={form}
        initialValues={formVals}
        submitter={false}
        layout={"horizontal"}
      >
        {TenantFormRender()}
      </ProForm>
    </Modal>
  );
};

export default TenantForm;
