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
import {Button, Form, Input, Modal, Select, Tag} from 'antd';
import {l} from "@/utils/intl";
import {FORM_LAYOUT_PUBLIC, NORMAL_MODAL_OPTIONS} from "@/services/constants";
import {ProForm, ProFormText, ProFormTextArea} from "@ant-design/pro-components";


export type TenantFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<UserBaseInfo.Role>) => void;
  modalVisible: boolean;
  values: Partial<UserBaseInfo.Role>;
};

const RoleForm: React.FC<TenantFormProps> = (props) => {


  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<UserBaseInfo.Role>>({
    id: props.values?.id,
    tenantId: props.values?.tenantId,
    roleCode: props.values?.roleCode,
    roleName: props.values?.roleName,
    isDelete: props.values?.isDelete,
    note: props.values?.note,
    createTime: props.values?.createTime,
    updateTime: props.values?.updateTime,
  });

  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
  } = props;


  /**
   * submit form
   */
  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    // fieldsValue.id = formVals.id;
    setFormVals({...formVals, ...fieldsValue});
    handleSubmit({...formVals, ...fieldsValue});
  };

  /**
   * construct role form
   * @constructor
   */
  const ConstructRoleForm = () => {
    return (
      <>
        <ProFormText
          name="roleCode"
          label={l('role.roleCode')}
          placeholder={l('role.EnterRoleCode')}
          rules={[{required: true, message: l('role.EnterRoleCode')}]}
        />

        <ProFormText
          name="roleName"
          label={l('role.roleName')}
          placeholder={l('role.EnterRoleName')}
          rules={[{required: true, message: l('role.EnterRoleName')}]}
        />

        <ProFormTextArea
          name="note"
          label={l('global.table.note')}
          placeholder={l('role.EnterNote')}
          allowClear
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
      title={formVals.id ? l('role.update') : l('role.create')}
      open={modalVisible}
      onCancel={() => handleModalVisible(false)}
      onOk={() => submitForm()}
    >
      <ProForm
        {...FORM_LAYOUT_PUBLIC}
        form={form}
        initialValues={formVals}
        submitter={false}
        layout={'horizontal'}
      >
        {ConstructRoleForm()}
      </ProForm>
    </Modal>
  );
};
export default RoleForm;
