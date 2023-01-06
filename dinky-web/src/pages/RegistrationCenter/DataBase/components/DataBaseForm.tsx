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
import {Button, Divider, Form, Input, Select, Space} from 'antd';

import Switch from "antd/es/switch";
import TextArea from "antd/es/input/TextArea";
import {DataBaseItem} from "@/pages/RegistrationCenter/data";
import {l} from "@/utils/intl";


export type ClickHouseFormProps = {
  onCancel: (flag?: boolean, formVals?: Partial<DataBaseItem>) => void;
  onSubmit: (values: Partial<DataBaseItem>) => void;
  onTest: (values: Partial<DataBaseItem>) => void;
  modalVisible: boolean;
  values: Partial<DataBaseItem>;
  type?: string;
};
const Option = Select.Option;

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const DataBaseForm: React.FC<ClickHouseFormProps> = (props) => {

  const [formVals, setFormVals] = useState<Partial<DataBaseItem>>({
    id: props.values.id,
    name: props.values.name,
    alias: props.values.alias,
    groupName: props.values.groupName,
    url: props.values.url,
    username: props.values.username,
    password: props.values.password,
    dbVersion: props.values.dbVersion,
    flinkConfig: props.values.flinkConfig,
    flinkTemplate: props.values.flinkTemplate,
    note: props.values.note,
    enabled: props.values.enabled,
  });

  const [form] = Form.useForm();
  const {
    onSubmit: handleUpdate,
    onTest: handleTest,
    onCancel: handleModalVisible,
    modalVisible,
    values,
    type
  } = props;

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals({type, ...formVals, ...fieldsValue});
    handleUpdate({type, ...formVals, ...fieldsValue});
  };

  const testForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals({type, ...formVals, ...fieldsValue});
    handleTest({type, ...formVals, ...fieldsValue});
  };

  const onReset = () => {
    form.resetFields();
  };

  const renderContent = (formVals) => {
    return (
      <>
        <Divider>{type}</Divider>
        <Form.Item
          name="name"
          label={l('pages.rc.db.name')}
          rules={[{required: true, message: l('pages.rc.db.namePlaceholder')}]}>
          <Input placeholder={l('pages.rc.db.namePlaceholder')}/>
        </Form.Item>
        <Form.Item
          name="alias"
          label={l('pages.rc.db.alias')}
        >
          <Input placeholder={l('pages.rc.db.aliasPlaceholder')}/>
        </Form.Item>
        <Form.Item
          name="groupName"
          label={l('pages.rc.db.groupName')}
        >
          <Select>
            <Option value="source">{l('pages.rc.db.source')}</Option>
            <Option value="warehouse">{l('pages.rc.db.warehouse')}</Option>
            <Option value="application">{l('pages.rc.db.application')}</Option>
            <Option value="backup">{l('pages.rc.db.backup')}</Option>
            <Option value="other">{l('pages.rc.db.other')}</Option>
          </Select>
        </Form.Item>
        <Form.Item
          name="url"
          label={l('pages.rc.db.url')}
        >
          <TextArea placeholder={l('pages.rc.db.urlPlaceholder')} allowClear
                    autoSize={{minRows: 3, maxRows: 10}}/>
        </Form.Item>
        <Form.Item
          name="username"
          label={l('pages.rc.db.username')}
        >
          <Input/>
        </Form.Item>
        <Form.Item
          name="password"
          label={l('pages.rc.db.password')}
        >
          <Input.Password/>
        </Form.Item>
        {type !== "Hive" && type !== "Presto" &&
          <>
            <Form.Item
              name="flinkConfig"
              label={l('pages.rc.db.flinkConfig')}
            >
              <TextArea placeholder={l('pages.rc.db.flinkConfigPlaceholder')} allowClear
                        autoSize={{minRows: 3, maxRows: 10}}/>
            </Form.Item>
            <Form.Item
              name="flinkTemplate"
              label={l('pages.rc.db.flinkTemplate')}
            >
              <TextArea placeholder={l('pages.rc.db.flinkTemplatePlaceholder')} allowClear
                        autoSize={{minRows: 3, maxRows: 10}}/>
            </Form.Item>
          </>}
        <Form.Item
          name="note"
          label={l('global.table.note')}
        >
          <Input placeholder={l('global.table.notePlaceholder')}/>
        </Form.Item>
        <Form.Item
          name="enabled"
          label={l('global.table.isEnable')}
        >
          <Switch  checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                  defaultChecked={formVals.enabled}/>
        </Form.Item>
      </>
    );
  };

  return (
    <>{
      modalVisible && (
        <>
          <Form
            {...formLayout}
            form={form}
            initialValues={{
              id: formVals.id,
              name: formVals.name,
              alias: formVals.alias,
              type: formVals.type,
              groupName: formVals.groupName,
              url: formVals.url,
              username: formVals.username,
              password: formVals.password,
              note: formVals.note,
              flinkConfig: formVals.flinkConfig,
              flinkTemplate: formVals.flinkTemplate,
              enabled: formVals.enabled,
            }}
          >
            {renderContent(formVals)}
            <Form.Item wrapperCol={{offset: 8, span: 16}}>
              <Space>
                {!formVals.id ?
                  <Button htmlType="button" onClick={() => {
                    handleModalVisible(false)
                  }}>
                    {l('button.cancel')}
                  </Button> : undefined
                }
                <Button htmlType="button" onClick={onReset}>
                  {l('button.reset')}
                </Button>
                <Button type="primary" htmlType="button" onClick={testForm}>
                  {l('button.test')}
                </Button>
                <Button type="primary" htmlType="button" onClick={submitForm}>
                  {l('button.save')}
                </Button>
              </Space>
            </Form.Item>
          </Form>
        </>
      )
    }</>
  );
};

export default DataBaseForm;
