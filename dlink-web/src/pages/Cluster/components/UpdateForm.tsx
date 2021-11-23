import React, {useEffect, useState} from 'react';
import {Form, Button, Input, Modal, Select} from 'antd';

import Switch from "antd/es/switch";
import TextArea from "antd/es/input/TextArea";
import {ClusterTableListItem} from "@/pages/Cluster/data";

export type UpdateFormProps = {
  onCancel: (flag?: boolean, formVals?: Partial<ClusterTableListItem>) => void;
  onSubmit: (values: Partial<ClusterTableListItem>) => void;
  updateModalVisible: boolean;
  values: Partial<ClusterTableListItem>;
};
const Option = Select.Option;

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const UpdateForm: React.FC<UpdateFormProps> = (props) => {
  const [formVals, setFormVals] = useState<Partial<ClusterTableListItem>>({
    id: props.values.id,
    name: props.values.name,
    alias: props.values.alias,
    type: props.values.type,
    hosts: props.values.hosts,
    note: props.values.note,
    enabled: props.values.enabled,
  });

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

  const renderContent = (formVals) => {
    return (
      <>
        <Form.Item
          name="name"
          label="名称"
          rules={[{required: true, message: '请输入名称！'}]}>
          <Input placeholder="请输入"/>
        </Form.Item>
        <Form.Item
          name="alias"
          label="别名"
        >
          <Input placeholder="请输入"/>
        </Form.Item>
        <Form.Item
          name="type"
          label="类型"
        >
          <Select defaultValue="yarn-session" allowClear>
            <Option value="standalone">Standalone</Option>
            <Option value="yarn-session">Yarn Session</Option>
            <Option value="yarn-per-job">Yarn Per-Job</Option>
            <Option value="yarn-application">Yarn Application</Option>
          </Select>
        </Form.Item>
        <Form.Item
          name="hosts"
          label="JobManager HA 地址"
        >
          <TextArea
            placeholder="添加 Flink 集群的 JobManager 的 RestApi 地址。当 HA 模式时，地址间用英文逗号分隔，例如：192.168.123.101:8081,192.168.123.102:8081,192.168.123.103:8081"
            allowClear
            autoSize={{minRows: 3, maxRows: 10}}/>
        </Form.Item>
        <Form.Item
          name="note"
          label="注释"
        >
          <TextArea
            placeholder="请输入"
            allowClear
            autoSize={{minRows: 3, maxRows: 10}}/>
        </Form.Item>
        <Form.Item
          name="enabled"
          label="是否启用"
          rules={[{required: true, message: '请输入是否启用！'}]}>
          <Switch checkedChildren="启用" unCheckedChildren="禁用"
                  defaultChecked={formVals.enabled}/>
        </Form.Item>
      </>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleUpdateModalVisible(false, values)}>取消</Button>
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
      title="编辑集群"
      visible={updateModalVisible}
      footer={renderFooter()}
      onCancel={() => handleUpdateModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={{
          id: formVals.id,
          name: formVals.name,
          alias: formVals.alias,
          type: formVals.type,
          hosts: formVals.hosts,
          note: formVals.note,
          enabled: formVals.enabled,
        }}
      >
        {renderContent(formVals)}
      </Form>
    </Modal>
  );
};

export default UpdateForm;
