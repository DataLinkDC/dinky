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
import {Button, Form, Input, Modal, Select, Switch} from 'antd';

import {ClusterTableListItem} from "@/pages/RegistrationCenter/data";
import {RUN_MODE} from "@/components/Studio/conf";
import {l} from "@/utils/intl";

export type ClusterFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<ClusterTableListItem>) => void;
  modalVisible: boolean;
  values: Partial<ClusterTableListItem>;
};
const Option = Select.Option;

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const ClusterForm: React.FC<ClusterFormProps> = (props) => {

  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<ClusterTableListItem>>({
    id: props.values.id,
    name: props.values.name,
    alias: props.values.alias,
    type: props.values.type,
    hosts: props.values.hosts,
    note: props.values.note,
    enabled: props.values.enabled,
  });

  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
  } = props;

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();

    fieldsValue.id = formVals.id;
    if (!fieldsValue.alias || fieldsValue.alias.length == 0) {
      fieldsValue.alias = fieldsValue.name;
    }

    setFormVals(fieldsValue);
    handleSubmit(fieldsValue);
  };

  const renderContent = (formValsPara: Partial<ClusterTableListItem>) => {
    form.resetFields();
    return (
      <>
        <Form.Item
          name="name"
          label={l('pages.rc.cluster.instanceName')}
          rules={[{required: true, message: l('pages.rc.cluster.namePlaceholder') }]}>
          <Input placeholder={l('pages.rc.cluster.namePlaceholder') }/>
        </Form.Item>

        <Form.Item
          name="alias"
          label={l('pages.rc.cluster.alias')}
        >
          <Input placeholder={l('pages.rc.cluster.aliasPlaceholder') }/>
        </Form.Item>
        <Form.Item
          name="type"
          label={l('pages.rc.cluster.type')}
          rules={[{required: true, message: l('pages.rc.cluster.typePlaceholder')}]}
        >
          <Select>
            <Option value={RUN_MODE.STANDALONE}>Standalone</Option>
            <Option value={RUN_MODE.YARN_SESSION}>Yarn Session</Option>
            <Option value={RUN_MODE.YARN_PER_JOB}>Yarn Per-Job</Option>
            <Option value={RUN_MODE.YARN_APPLICATION}>Yarn Application</Option>
            <Option value={RUN_MODE.KUBERNETES_SESSION}>Kubernetes Session</Option>
            <Option value={RUN_MODE.KUBERNETES_APPLICATION}>Kubernetes Application</Option>
          </Select>
        </Form.Item>
        <Form.Item
          name="hosts"
          label={l('pages.rc.cluster.jobManagerHaAddress')}
          validateTrigger={['onChange']}
          rules={[
            {
              required: true,
              validator(_, hostsValue) {
                let hostArray = [];
                if (hostsValue.trim().length === 0) {
                  return Promise.reject(new Error(l('pages.rc.cluster.jobManagerHaAddressPlaceholder')));
                } else {
                  hostArray = hostsValue.split(',')
                  for (let i = 0; i < hostArray.length; i++) {
                    if (hostArray[i].includes('/')) {
                      return Promise.reject(new Error(l('pages.rc.cluster.jobManagerHaAddress.validate.slash')));
                    }
                    if (parseInt(hostArray[i].split(':')[1]) >= 65535) {
                      return Promise.reject(new Error(l('pages.rc.cluster.jobManagerHaAddress.validate.port')));
                    }
                  }
                  return Promise.resolve();
                }
              },
            },
          ]}
        >
          <Input.TextArea
            placeholder={l('pages.rc.cluster.jobManagerHaAddressPlaceholderText')}
            allowClear
            autoSize={{minRows: 3, maxRows: 10}}/>
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
          <Switch checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                  defaultChecked={formValsPara.enabled}/>
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
      title={formVals.id ? l('pages.rc.cluster.modify') : l('pages.rc.cluster.create')}
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

export default ClusterForm;
