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
import {AlertGroupTableListItem} from "@/pages/AlertGroup/data";
import {connect, useIntl} from "umi";
import {AlertStateType} from "@/pages/AlertInstance/model";
import {AlertInstanceTableListItem} from "@/pages/AlertInstance/data";
import {buildFormData, getFormData} from "@/pages/AlertGroup/function";

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

  const l = (key: string, defaultMsg?: string) => useIntl().formatMessage({id: key, defaultMessage: defaultMsg})


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
          label="名称"
          rules={[{required: true, message: '请输入名称！'}]}>
          <Input placeholder="请输入唯一英文标识"/>
        </Form.Item>
        <Form.Item
          name="alertInstanceIds"
          label="报警实例"
          rules={[{required: true, message: '请选择报警组实例！'}]}
        >
          <Select
            mode="multiple"
            style={{width: '100%'}}
            placeholder="请选择报警实例"
            optionLabelProp="label"
          >
            {getAlertInstanceOptions()}
          </Select>
        </Form.Item>
        <Form.Item
          name="note"
          label="注释"
        >
          <Input.TextArea placeholder="请输入文本注释" allowClear
                          autoSize={{minRows: 3, maxRows: 10}}/>
        </Form.Item>
        <Form.Item
          name="enabled"
          label="是否启用">
          <Switch checkedChildren="启用" unCheckedChildren="禁用"
                  defaultChecked={formVals.enabled}/>
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
      width={1200}
      bodyStyle={{padding: '32px 40px 48px'}}
      destroyOnClose
      title={formVals.id ? "维护报警组" : "创建报警组"}
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
