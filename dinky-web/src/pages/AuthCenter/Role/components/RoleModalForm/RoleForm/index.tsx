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


import React from 'react';
import {l} from "@/utils/intl";
import {FORM_LAYOUT_PUBLIC} from "@/services/constants";
import {ProForm, ProFormText, ProFormTextArea} from "@ant-design/pro-components";
import {UserBaseInfo} from "@/types/User/data.d";
import {FormInstance} from "antd/es/form/hooks/useForm";
import {Values} from "async-validator";

type RoleProFormProps = {
  values: Partial<UserBaseInfo.Role>;
  form: FormInstance<Values>;
};

const RoleProForm: React.FC<RoleProFormProps> = (props) => {

  const {values, form} = props;

  /**
   * construct role form
   * @constructor
   */
  const renderRoleForm = () => {
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
    <ProForm
      {...FORM_LAYOUT_PUBLIC}
      form={form}
      initialValues={values}
      submitter={false}
      layout={'horizontal'}
    >
        {renderRoleForm()}
    </ProForm>
  );
};
export default RoleProForm;
