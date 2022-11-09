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
import {NameSpaceTableListItem, RoleTableListItem} from "@/pages/AuthenticationCenter/data.d";
import {getStorageTenantId} from "@/components/Common/crud";
import {connect} from "umi";
import {NameSpaceStateType} from "@/pages/AuthenticationCenter/RoleManager/model";
import {buildFormData, getFormData} from "@/pages/AuthenticationCenter/function";
import {l} from "@/utils/intl";


export type TenantFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<RoleTableListItem>) => void;
  modalVisible: boolean;
  values: Partial<RoleTableListItem>;
  nameSpaces: NameSpaceTableListItem[];
};

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const Option = Select.Option;


const RoleForm: React.FC<TenantFormProps> = (props) => {


  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<RoleTableListItem>>({
    id: props.values?.id,
    tenantId: props.values?.tenantId,
    roleCode: props.values?.roleCode,
    namespaceIds: props.values?.namespaceIds,
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
    nameSpaces,
  } = props;

  const getNameSpaceOptions = () => {
    const itemList: JSX.Element[] = [];
    for (const item of nameSpaces) {
      const tag = (
        <>
          <Tag color="processing">
            {item.namespaceCode}
          </Tag>
          {/*{item.namespaceCode}*/}
        </>);
      itemList.push(<Option key={item.namespaceCode} value={item.id?.toString()} label={tag}>
        {tag}
      </Option>)
    }
    return itemList;
  };

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    // fieldsValue.id = formVals.id;
    fieldsValue.tenantId = getStorageTenantId();
    setFormVals(buildFormData(formVals, fieldsValue));
    handleSubmit(buildFormData(formVals, fieldsValue));
  };

  const renderContent = (formVals) => {
    return (
      <>
        <Form.Item
          name="roleCode"
          label={l('pages.role.roleCode')}
          rules={[{required: true, message: l('pages.role.EnterRoleCode')}]}>
          <Input placeholder={l('pages.role.EnterRoleCode')}/>
        </Form.Item>
        <Form.Item
          name="roleName"
          label={l('pages.role.roleName')}
          rules={[{required: true, message: l('pages.role.EnterRoleName')}]}>
          <Input placeholder={l('pages.role.EnterRoleName')}/>
        </Form.Item>
        <Form.Item
          name="namespaceIds"
          label={l('pages.role.namespaceIds')}
          rules={[{required: true, message: l('pages.role.selectNameSpace')}]}
        >
          <Select
            mode="multiple"
            style={{width: '100%'}}
            placeholder={l('pages.role.selectNameSpace')}
            optionLabelProp="label"
          >
            {getNameSpaceOptions()}
          </Select>
        </Form.Item>
        <Form.Item
          name="note"
          label={l('global.table.note')}
        >
          <Input.TextArea placeholder={l('pages.role.EnterNote')} allowClear
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
  nameSpaces: NameSpace.nameSpaces,
}))(RoleForm);
