import React, {useEffect, useState} from 'react';
import {Form, Button, Input, Modal, Select,Switch} from 'antd';

import {ClusterTableListItem} from "@/pages/Cluster/data";

export type ClusterFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<ClusterTableListItem>) => void;
  modalVisible: boolean;
};
const Option = Select.Option;

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const ClusterForm: React.FC<ClusterFormProps> = (props) => {

  const [form] = Form.useForm();

  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
  } = props;

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    handleSubmit(fieldsValue);
  };

  const renderContent = () => {
    return (
      <>
        <Form.Item
          name="name"
          label="标识"
          rules={[{required: true, message: '请输入名称！'}]}>
          <Input placeholder="请输入唯一英文标识"/>
        </Form.Item>
        <Form.Item
          name="alias"
          label="名称"
        >
          <Input placeholder="请输入名称"/>
        </Form.Item>
        <Form.Item
          name="type"
          label="类型"
        >
          <Select defaultValue="Yarn" allowClear>
            <Option value="Standalone">Standalone</Option>
            <Option value="Yarn">Yarn</Option>
            <Option value="Others">Others</Option>
          </Select>
        </Form.Item>
        <Form.Item
          name="hosts"
          label="Hosts"
        >
          <Input.TextArea placeholder="添加 Flink Hosts...例如：127.0.0.1:8081,127.0.0.1:8091" allowClear
                    autoSize={{minRows: 3, maxRows: 10}}/>
        </Form.Item>
        <Form.Item
          name="note"
          label="注释"
        >
          <Input.TextArea placeholder="请输入文本注释" allowClear
                          autoSize={{minRows: 3, maxRows: 10}}/>
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
      width={640}
      bodyStyle={{padding: '32px 40px 48px'}}
      destroyOnClose
      title="创建集群"
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
      >
        {renderContent()}
      </Form>
    </Modal>
  );
};

export default ClusterForm;
