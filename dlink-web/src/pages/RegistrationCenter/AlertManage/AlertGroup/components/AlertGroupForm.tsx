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
import {Button, Form, Input, Modal, Select, Switch, Tag} from 'antd';
import {AlertGroupTableListItem, AlertInstanceTableListItem} from "@/pages/RegistrationCenter/data";
import {connect} from "umi";
import {AlertStateType} from "@/pages/RegistrationCenter/AlertManage/AlertInstance/model";
import {buildFormData, getFormData} from "@/pages/RegistrationCenter/AlertManage/AlertGroup/function";
import {l} from "@/utils/intl";

export type AlertGroupFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<AlertGroupTableListItem>) => void;
  modalVisible: boolean;
  values: Partial<AlertGroupTableListItem>;
  instance: AlertInstanceTableListItem[];
};
const Option = Select.Option;

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const AlertGroupForm: React.FC<AlertGroupFormProps> = (props) => {
  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<AlertGroupTableListItem>>({
    id: props.values.id,
    name: props.values.name,
    alertInstanceIds: props.values.alertInstanceIds,
    note: props.values.note,
    enabled: props.values.enabled ? props.values.enabled : true,
  });

  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
    instance,
  } = props;

  const getAlertInstanceOptions = () => {
    const itemList = [];
    for (const item of instance) {
      const tag = (<><Tag color="processing">{item.type}</Tag>{item.name}</>);
      itemList.push(<Option key={item.id} value={item.id.toString()} label={tag}>
        {tag}
      </Option>)
    }
    return itemList;
  };

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals(buildFormData(formVals, fieldsValue));
    handleSubmit(buildFormData(formVals, fieldsValue));
  };

  const renderContent = (formVals) => {
    return (
      <>
        <Form.Item
          name="name"
          label={l('pages.rc.alert.group.name')}
          rules={[{required: true, message: l('pages.rc.alert.group.inputName')}]}>
          <Input placeholder={l('pages.rc.alert.group.inputName')}/>
        </Form.Item>
        <Form.Item
          name="alertInstanceIds"
          label={l('pages.rc.alert.group.alertInstanceIds')}
          rules={[{required: true, message: l('pages.rc.alert.group.chooseAlertInstanceIds')}]}
        >
          <Select
            mode="multiple"
            style={{width: '100%'}}
            placeholder={l('pages.rc.alert.group.chooseAlertInstanceIds')}
            optionLabelProp="label"
          >
            {getAlertInstanceOptions()}
          </Select>
        </Form.Item>
        <Form.Item
          name="note"
          label={l('global.table.note')}
        >
          <Input.TextArea placeholder={l('global.table.notePlaceholder')} allowClear
                          autoSize={{minRows: 3, maxRows: 10}}/>
        </Form.Item>
        <Form.Item
          name="enabled"
          label={l('global.table.isEnable')}>
          <Switch checkedChildren={l("button.enable")} unCheckedChildren={l('button.disable')}
                  defaultChecked={formVals.enabled}/>
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
      width={1200}
      bodyStyle={{padding: '32px 40px 48px'}}
      destroyOnClose
      title={formVals.id ? l('pages.rc.alert.group.modify') : l('pages.rc.alert.group.create')}
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

export default connect(({Alert}: { Alert: AlertStateType }) => ({
  instance: Alert.instance,
}))(AlertGroupForm);
