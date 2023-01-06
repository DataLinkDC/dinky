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

import {SessionItem} from '../data.d';
import {connect} from "umi";
import {StateType} from "@/pages/DataStudio/model";
import {l} from "@/utils/intl";

export type UpdateFormProps = {
  onCancel: (flag?: boolean, formVals?: Partial<SessionItem>) => void;
  onSubmit: (values: Partial<SessionItem>) => void;
  updateModalVisible: boolean;
  values: Partial<SessionItem>;
};

const {Item} = Form;
const {Option} = Select;

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const SessionForm: React.FC<UpdateFormProps> = (props) => {

  const [formVals, setFormVals] = useState<Partial<SessionItem>>({
    session: props.values.session,
    type: props.values.type,
    useRemote: props.values.useRemote,
    clusterId: props.values.clusterId,
  });

  const {cluster} = props;

  const [form] = Form.useForm();

  const {
    onSubmit: handleUpdate,
    onCancel: handleUpdateModalVisible,
    updateModalVisible,
    values,
  } = props;

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals({...formVals, ...fieldsValue});
    handleUpdate({...formVals, ...fieldsValue});
  };

  const getClusterOptions = () => {
    let itemList = [(<Option value={0} label={(<><Tag color="default">Local</Tag>本地环境</>)}>
      <Tag color="default">Local</Tag>
      本地环境
    </Option>)];
    for (let item of cluster) {
      let tag = (<><Tag
        color={item.enabled ? "processing" : "error"}>{item.type}</Tag>{item.alias === "" ? item.name : item.alias}</>);
      itemList.push(<Option value={item.id} label={tag}>
        {tag}
      </Option>)
    }
    return itemList;
  };

  const renderContent = () => {
    return (
      <>
        <Item
          name="session"
          label="名称"
          rules={[{required: true, message: '请输入唯一名称！'}]}>
          <Input placeholder="请输入"/>
        </Item>
        <Item
          name="type"
          label="访问权限"
        >
          <Select defaultValue="PUBLIC">
            <Option value="PUBLIC">共享</Option>
            <Option value="PRIVATE">私密</Option>
          </Select>
        </Item>
        <Item
          name="useRemote"
          label="是否远程"
        >
          <Switch  checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                  defaultChecked={formVals.useRemote}/>
        </Item>
        <Item
          name="clusterId"
          label="集群"
        >
          <Select
            style={{width: '100%'}}
            placeholder="选择Flink集群"
            optionLabelProp="label"
          >
            {getClusterOptions()}
          </Select>
        </Item>
      </>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleUpdateModalVisible(false, values)}>{l('button.cancel')}</Button>
        <Button type="primary" onClick={() => submitForm()}>
          {l('button.finish')}
        </Button>
      </>
    );
  };

  return (
    <Modal
      width={640}
      bodyStyle={{padding: '32px 40px 48px'}}
      destroyOnClose
      title={'创建新会话'}
      visible={updateModalVisible}
      footer={renderFooter()}
      onCancel={() => handleUpdateModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={{
          session: formVals.session,
          type: formVals.type,
          useRemote: formVals.useRemote,
          clusterId: formVals.clusterId,
        }}
      >
        {renderContent()}
      </Form>
    </Modal>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  cluster: Studio.cluster,
}))(SessionForm);
