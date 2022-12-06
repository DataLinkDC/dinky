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
import {Button, Divider, Form, Input, Modal, Select, Switch} from 'antd';
import {JarTableListItem} from "@/pages/RegistrationCenter/data";
import {l} from "@/utils/intl";

export type JarFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<JarTableListItem>) => void;
  modalVisible: boolean;
  values: Partial<JarTableListItem>;
};
const Option = Select.Option;

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const JarForm: React.FC<JarFormProps> = (props) => {


  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<JarTableListItem>>({
    id: props.values.id,
    name: props.values.name,
    alias: props.values.alias,
    type: props.values.type ? props.values.type : 'UserApp',
    path: props.values.path,
    mainClass: props.values.mainClass,
    paras: props.values.paras,
    note: props.values.note,
    enabled: props.values.enabled ? props.values.enabled : true,
  });

  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
  } = props;

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals({...formVals, ...fieldsValue});
    handleSubmit({...formVals, ...fieldsValue});
  };

  const renderContent = (formVals) => {
    return (
      <>
        <Form.Item
          name="type"
          label={l('pages.rc.jar.type')}
        >
          <Select defaultValue="UserApp" value="UserApp">
            <Option value="UserApp">User App</Option>
          </Select>
        </Form.Item>
        <Divider>{l('pages.rc.jar.config')}</Divider>
        <Form.Item
          name="path"
          label={l('pages.rc.jar.filePath')}
          help={l('pages.rc.jar.filePathHelp')}
          rules={[{required: true, message: l('pages.rc.jar.filePathHelp')}]}
        >
          <Input placeholder={l('pages.rc.jar.filePathPleaseHolder')}/>
        </Form.Item>
        <Form.Item
          name="mainClass"
          label={l('pages.rc.jar.mainClass')}
          help={l('pages.rc.jar.mainClassHelp')}
        >
          <Input placeholder={l('pages.rc.jar.mainClassPleaseHolder')}/>
        </Form.Item>
        <Form.Item
          name="paras"
          label={l('pages.rc.jar.execParams')}
          help={l('pages.rc.jar.execParamsHelp')}
        >
          <Input placeholder={l('pages.rc.jar.execParamsPleaseHolder')}/>
        </Form.Item>
        <Divider>{l('pages.rc.jar.baseConfig')}</Divider>
        <Form.Item
          name="name"
          label={l('pages.rc.jar.name')}
          rules={[{required: true, message: l('pages.rc.jar.namePlaceholder')}]}>
          <Input placeholder={l('pages.rc.jar.namePlaceholder')}/>
        </Form.Item>
        <Form.Item
          name="alias"
          label={l('pages.rc.jar.alias')}
        >
          <Input placeholder={l('pages.rc.jar.aliasPlaceholder')}/>
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
          <Switch  checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                  defaultChecked={formVals.enabled}/>
        </Form.Item>
      </>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleModalVisible(false)}>{l('button.cancel')}</Button>
        <Button type="primary" onClick={() => submitForm()}>{l('button.finish')}</Button>
      </>
    );
  };

  return (
    <Modal
      width={"40%"}
      bodyStyle={{padding: '32px 40px 48px'}}
      destroyOnClose
      title={formVals.id ? l('pages.rc.jar.modify') : l('pages.rc.jar.create')}
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

export default JarForm;
