import React, {useEffect, useState} from 'react';
import { Form, Button, Input, Modal } from 'antd';

import type { TableListItem } from '../data.d';
import Switch from "antd/es/switch";

export type UpdateFormProps = {
  onCancel: (flag?: boolean, formVals?: Partial<TableListItem>) => void;
  onSubmit: (values: Partial<TableListItem>) => void;
  updateModalVisible: boolean;
  values: Partial<TableListItem>;
};
const FormItem = Form.Item;

const formLayout = {
  labelCol: { span: 7 },
  wrapperCol: { span: 13 },
};

const UpdateForm: React.FC<UpdateFormProps> = (props) => {
  const [formVals, setFormVals] = useState<Partial<TableListItem>>({
    id: props.values.id,
    enabled: props.values.enabled,
    username: props.values.username,
    nickname: props.values.nickname,
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
    setFormVals({ ...formVals, ...fieldsValue });
    handleUpdate({ ...formVals, ...fieldsValue });
  };

  const renderContent = (formVals) => {
    return (
      <>
        <FormItem
          name="username"
          label="登录名"
          rules={[{ required: true, message: '请输入登录名！' }]}
        >
          <Input placeholder="请输入" />
        </FormItem>
        <FormItem
          name="nickname"
          label="昵称"
          rules={[{ required: true, message: '请输入昵称！' }]}
        >
          <Input placeholder="请输入" />
        </FormItem>
        <FormItem
          name="enabled"
          label="状态"
        >
          <Switch checkedChildren="启用" unCheckedChildren="禁用"
                  defaultChecked={formVals.enabled}/>
        </FormItem>
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
      bodyStyle={{ padding: '32px 40px 48px' }}
      destroyOnClose
      title="编辑用户"
      visible={updateModalVisible}
      footer={renderFooter()}
      onCancel={() => handleUpdateModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={{
          username: formVals.username,
          nickname: formVals.nickname,
          enabled: formVals.enabled,
        }}
      >
        {renderContent(formVals)}
      </Form>
    </Modal>
  );
};

export default UpdateForm;
