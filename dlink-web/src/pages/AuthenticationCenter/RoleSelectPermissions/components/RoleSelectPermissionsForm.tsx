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
import {
  NameSpaceTableListItem,
  RoleSelectPermissionsTableListItem,
  RoleTableListItem
} from "@/pages/AuthenticationCenter/data.d";
import {connect} from "umi";
import {NameSpaceStateType} from "@/pages/AuthenticationCenter/RoleManager/model";
import {buildFormData, getFormData} from "@/pages/AuthenticationCenter/function";
import {l} from "@/utils/intl";


export type RoleSelectPermissionsFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<RoleSelectPermissionsTableListItem>) => void;
  modalVisible: boolean;
  values: Partial<RoleSelectPermissionsTableListItem>;
  role: RoleTableListItem[];
};

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const Option = Select.Option;


const RoleSelectPermissionsForm: React.FC<RoleSelectPermissionsFormProps> = (props) => {


  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<RoleSelectPermissionsTableListItem>>({
    id: props.values?.id,
    roleId: props.values?.roleId,
    tableName: props.values?.tableName,
    roleName: props.values?.roleName,
    expression: props.values?.expression,
    createTime: props.values?.createTime,
    updateTime: props.values?.updateTime,
  });

  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
    role,
  } = props;

  const getRoleOptions = () => {
    const itemList: JSX.Element[] = [];
    for (const item of role) {
      const tag = (
        <>
          <Tag color="processing">
            {item.roleName}
          </Tag>
        </>);
      itemList.push(<Option key={item.id} value={item.id} label={tag}>
        {tag}
      </Option>)
    }
    return itemList;
  };

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    if(fieldsValue.roleId.length > 0){
      fieldsValue.roleId = fieldsValue.roleId[0];
    }else{
      fieldsValue.roleId = null;
    }
    setFormVals(buildFormData(formVals, fieldsValue));
    handleSubmit(buildFormData(formVals, fieldsValue));
  };

  const renderContent = (formVals) => {
    return (
      <>
        <Form.Item
          name="roleId"
          label={l('pages.role.roleName')}
          rules={[{required: true, message: l('pages.role.selectRole')}]}
        >
          <Select
            mode="multiple"
            style={{width: '100%'}}
            placeholder={l('pages.role.selectRole')}
            optionLabelProp="label"
          >
            {getRoleOptions()}
          </Select>
        </Form.Item>
        <Form.Item
          name="tableName"
          label={l('pages.roleSelectPermissions.tableName')}
          rules={[{required: true, message: l('pages.roleSelectPermissions.EnterTableName')}]}>
          <Input placeholder={l('pages.roleSelectPermissions.EnterTableName')}/>
        </Form.Item>
        <Form.Item
          name="expression"
          label={l('pages.roleSelectPermissions.expression')}
        >
          <Input.TextArea placeholder={l('pages.roleSelectPermissions.EnterExpression')} allowClear
                          autoSize={{minRows: 3, maxRows: 10}}/>
        </Form.Item>
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
      title={formVals.id ? l('pages.role.update') : l('pages.role.create')}
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={getFormData(formVals)}
      >
        {renderContent(getFormData(formVals))}
      </Form>
    </Modal>
  );
};
export default connect(({NameSpace}: { NameSpace: NameSpaceStateType }) => ({
  role: NameSpace.role,
}))(RoleSelectPermissionsForm);
